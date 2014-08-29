package com.yixianqian.ui;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.http.Header;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.customewidget.MyAlertDialog;
import com.yixianqian.table.ReportTable;
import com.yixianqian.utils.AsyncHttpClientImageSound;
import com.yixianqian.utils.FileSizeUtil;
import com.yixianqian.utils.FriendPreference;
import com.yixianqian.utils.ImageTools;
import com.yixianqian.utils.LogTool;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

/**
 * �����ƣ�HandleComplainActivity
 * ������������Ͷ��ҳ��
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��8��28�� ����4:48:09
 *
 */
public class HandleComplainActivity extends BaseActivity {
	public static final String COMPLAIN_IMAGE_PAHT = "complain_iamge_path";
	private ImageView comImage;
	private String imagePath;
	private View right_btn_bg;
	private View left_btn_bg;
	private TextView navTextView;
	private UserPreference userPreference;
	private FriendPreference friendPreference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_handle_complain);

		imagePath = getIntent().getStringExtra(COMPLAIN_IMAGE_PAHT);
		userPreference = BaseApplication.getInstance().getUserPreference();
		friendPreference = BaseApplication.getInstance().getFriendPreference();

		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		comImage = (ImageView) findViewById(R.id.com_image);
		left_btn_bg = findViewById(R.id.left_btn_bg);
		right_btn_bg = (View) findViewById(R.id.right_btn_bg);
		navTextView = (TextView) findViewById(R.id.nav_text);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		navTextView.setText("Ͷ������");
		if (!TextUtils.isEmpty(imagePath)) {
			showPicture();
		}
		right_btn_bg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendComplain();
			}
		});
		left_btn_bg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				giveUpPublish();
			}
		});
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		giveUpPublish();
	}

	/**
	 * ��������
	 */
	private void giveUpPublish() {
		final MyAlertDialog myAlertDialog = new MyAlertDialog(this);
		myAlertDialog.setTitle("��ʾ");
		myAlertDialog.setMessage("����Ͷ�ߣ�");
		View.OnClickListener comfirm = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myAlertDialog.dismiss();
				finish();
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			}
		};
		View.OnClickListener cancle = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myAlertDialog.dismiss();
			}
		};
		myAlertDialog.setPositiveButton("ȷ��", comfirm);
		myAlertDialog.setNegativeButton("ȡ��", cancle);
		myAlertDialog.show();
	}

	/**
	 * ��ʾͼƬ
	 */
	public void showPicture() {
		String tempPath = Environment.getExternalStorageDirectory() + "/yixianqian/image";
		String photoName = "complain.jpeg";
		File file = ImageTools.compressForFile(tempPath, photoName, imagePath, 100);

		Bitmap uploadBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
		comImage.setImageBitmap(uploadBitmap);

		if (file != null) {
			imagePath = file.getAbsolutePath();
		}
	}

	/**
	 * ����Ͷ��
	 */
	private void sendComplain() {
		File photoFile = null;
		if (!TextUtils.isEmpty(imagePath)) {
			photoFile = new File(imagePath);
			LogTool.e("Ͷ��ͼƬ��С", "" + FileSizeUtil.getFileOrFilesSize(imagePath, FileSizeUtil.SIZETYPE_KB) + "KB");
		}

		RequestParams params = new RequestParams();
		params.put(ReportTable.R_USERID, userPreference.getU_id());
		params.put(ReportTable.R_REPORTERID, friendPreference.getF_id());
		try {
			if (photoFile != null && photoFile.exists()) {
				params.put(ReportTable.R_IMG, photoFile);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
			Dialog dialog;

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
				dialog = showProgressDialog("���ڷ���...");
				dialog.setCancelable(false);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					ToastTool.showShort(HandleComplainActivity.this, "Ͷ�߳ɹ������ǻᾡ�촦��");
					finish();
					overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				ToastTool.showShort(HandleComplainActivity.this, "����ʧ�ܣ�");
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				super.onFinish();
				if (dialog != null) {
					dialog.dismiss();
				}
			}

			@Override
			public void onCancel() {
				// TODO Auto-generated method stub
				super.onCancel();
				if (dialog != null) {
					dialog.dismiss();
				}
			}
		};
		AsyncHttpClientImageSound.post("reportimage", params, responseHandler);
	}
}
