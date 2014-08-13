package com.yixianqian.ui;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;

import com.easemob.chat.EMChatConfig;
import com.easemob.cloud.CloudOperationCallback;
import com.easemob.cloud.HttpFileManager;
import com.easemob.util.ImageUtils;
import com.easemob.util.PathUtil;
import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.photoview.PhotoView;
import com.yixianqian.task.LoadLocalBigImgTask;
import com.yixianqian.utils.ImageCache;

/**
 * œ¬‘ÿœ‘ æ¥ÛÕº
 * 
 */
public class ShowBigImage extends BaseActivity {

	private ProgressDialog pd;
	private PhotoView image;
	private int default_res = R.drawable.photoconor;
	// flag to indicate if need to delete image on server after download
	private boolean deleteAfterDownload;
	private boolean showAvator;
	private String localFilePath;
	private String username;
	private Bitmap bitmap;
	private boolean isDownloaded;
	private ProgressBar loadLocalPb;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_show_big_image);

		image = (PhotoView) findViewById(R.id.image);
		loadLocalPb = (ProgressBar) findViewById(R.id.pb_load_local);

		default_res = getIntent().getIntExtra("default_image", R.drawable.photoconor);
		showAvator = getIntent().getBooleanExtra("showAvator", false);
		username = getIntent().getStringExtra("username");
		deleteAfterDownload = getIntent().getBooleanExtra("delete", false);

		Uri uri = getIntent().getParcelableExtra("uri");
		String remotepath = getIntent().getExtras().getString("remotepath");
		String secret = getIntent().getExtras().getString("secret");
		System.err.println("show big image uri:" + uri + " remotepath:" + remotepath);

		if (uri != null && new File(uri.getPath()).exists()) {
			System.err.println("showbigimage file exists. directly show it");
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			// int screenWidth = metrics.widthPixels;
			// int screenHeight =metrics.heightPixels;
			bitmap = ImageCache.getInstance().get(uri.getPath());
			if (bitmap == null) {
				LoadLocalBigImgTask task = new LoadLocalBigImgTask(this, uri.getPath(), image, loadLocalPb,
						ImageUtils.SCALE_IMAGE_WIDTH, ImageUtils.SCALE_IMAGE_HEIGHT);
				if (android.os.Build.VERSION.SDK_INT > 10) {
					task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					task.execute();
				}
			} else {
				image.setImageBitmap(bitmap);
			}
		} else if (remotepath != null) {
			System.err.println("download remote image");
			Map<String, String> maps = new HashMap<String, String>();
			maps.put("Authorization", "Bearer " + EMChatConfig.getInstance().AccessToken);
			if (!TextUtils.isEmpty(secret)) {
				maps.put("share-secret", secret);
			}
			maps.put("Accept", "application/octet-stream");
			downloadImage(remotepath, maps);
		} else {
			image.setImageResource(default_res);
		}

		image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	/**
	 * œ¬‘ÿÕº∆¨
	 * 
	 * @param remoteFilePath
	 */
	private void downloadImage(final String remoteFilePath, final Map<String, String> headers) {
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setCanceledOnTouchOutside(false);
		pd.setMessage("œ¬‘ÿÕº∆¨: 0%");
		pd.show();
		if (!showAvator) {
			if (remoteFilePath.contains("/"))
				localFilePath = PathUtil.getInstance().getImagePath().getAbsolutePath() + "/"
						+ remoteFilePath.substring(remoteFilePath.lastIndexOf("/") + 1);
			else
				localFilePath = PathUtil.getInstance().getImagePath().getAbsolutePath() + "/" + remoteFilePath;
		} else {
			if (remoteFilePath.contains("/"))
				localFilePath = PathUtil.getInstance().getImagePath().getAbsolutePath() + "/"
						+ remoteFilePath.substring(remoteFilePath.lastIndexOf("/") + 1);
			else
				localFilePath = PathUtil.getInstance().getImagePath().getAbsolutePath() + "/" + remoteFilePath;

		}
		final HttpFileManager httpFileMgr = new HttpFileManager(this, EMChatConfig.getInstance().getStorageUrl());
		final CloudOperationCallback callback = new CloudOperationCallback() {
			public void onSuccess(String resultMsg) {

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						DisplayMetrics metrics = new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(metrics);
						int screenWidth = metrics.widthPixels;
						int screenHeight = metrics.heightPixels;

						bitmap = ImageUtils.decodeScaleImage(localFilePath, screenWidth, screenHeight);
						if (bitmap == null) {
							image.setImageResource(default_res);
						} else {
							image.setImageBitmap(bitmap);
							ImageCache.getInstance().put(localFilePath, bitmap);
							isDownloaded = true;

						}
						if (pd != null) {
							pd.dismiss();
						}
					}
				});
			}

			public void onError(String msg) {
				Log.e("###", "offline file transfer error:" + msg);
				File file = new File(localFilePath);
				if (file.exists()) {
					file.delete();
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						pd.dismiss();
						image.setImageResource(default_res);
					}
				});
			}

			public void onProgress(final int progress) {
				Log.d("ease", "Progress: " + progress);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						pd.setMessage("œ¬‘ÿÕº∆¨: " + progress + "%");
					}
				});
			}
		};

		new Thread(new Runnable() {
			@Override
			public void run() {
				httpFileMgr.downloadFile(remoteFilePath, localFilePath, EMChatConfig.getInstance().APPKEY, headers,
						callback);
			}
		}).start();

	}

	@Override
	public void onBackPressed() {
		if (isDownloaded)
			setResult(RESULT_OK);
		finish();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub

	}
}
