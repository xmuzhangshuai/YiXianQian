package com.yixianqian.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.HttpUtil;
import com.yixianqian.utils.SharePreferenceUtil;

/**
 * �����ƣ�HeadImageActivity
 * ���������ϴ�ͷ��ҳ��
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��7��7�� ����3:39:55
 *
 */
public class HeadImageActivity extends BaseActivity {

	private View getHeadImageBtn;
	private TextView topNavigation;//����������
	private View leftImageButton;//��������ఴť
	private View rightImageButton;//�������Ҳఴť
	private ImageView headImage;// ͷ��
	//	private Bitmap headBitmap;// ͷ��
	private ImageView camera_image;//���ͼ��
	private SharePreferenceUtil sharePreferenceUtil;

	private File picFile;
	private Uri photoUri;

	private final int activity_result_camara_with_data = 1006;
	private final int activity_result_cropimage_with_data = 1007;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_head_image);
		sharePreferenceUtil = new SharePreferenceUtil(this, SharePreferenceUtil.USER_SHAREPREFERENCE);

		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		getHeadImageBtn = (View) findViewById(R.id.gethead_btn);
		topNavigation = (TextView) findViewById(R.id.nav_text);
		leftImageButton = (View) findViewById(R.id.left_btn_bg);
		rightImageButton = (View) findViewById(R.id.right_btn_bg);
		headImage = (ImageView) findViewById(R.id.headimage);
		camera_image = (ImageView) findViewById(R.id.camera_image);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		rightImageButton.setEnabled(false);
		leftImageButton.setVisibility(View.GONE);
		topNavigation.setText("ͷ��");

		getHeadImageBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showPicturePicker(HeadImageActivity.this);
			}
		});

		headImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showPicturePicker(HeadImageActivity.this);
			}
		});

		rightImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(HeadImageActivity.this, MainActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				finish();
			}
		});
	}

	/**
	* ��ʾ�Ի��򣬴����պ����ѡ��ͼƬ��Դ
	* 
	* @param context
	* @param isCrop
	*/
	public void showPicturePicker(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("ͼƬ��Դ");
		builder.setItems(new String[] { "����", "���" }, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					String status = Environment.getExternalStorageState();
					if (status.equals(Environment.MEDIA_MOUNTED)) {// �ж��Ƿ���SD��
						doTakePhoto();// �û�����˴��������ȡ
					}
					break;
				case 1:
					doCropPhoto();// �������ȥ��ȡ
					break;

				default:
					break;
				}
			}
		});
		builder.create().show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case activity_result_camara_with_data: // ����
			try {
				cropImageUriByTakePhoto();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case activity_result_cropimage_with_data:
			try {
				if (photoUri != null) {
					Bitmap bitmap = decodeUriAsBitmap(photoUri);
					camera_image.setVisibility(View.GONE);
					headImage.setImageBitmap(bitmap);
					headImage.setVisibility(View.VISIBLE);
					rightImageButton.setEnabled(true);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		}
	}

	/**
	 * ���ջ�ȡͼƬ
	 * 
	 */
	protected void doTakePhoto() {
		try {
			File uploadFileDir = new File(Environment.getExternalStorageDirectory(), "/yixianqian");
			Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			if (!uploadFileDir.exists()) {
				uploadFileDir.mkdirs();
			}
			picFile = new File(uploadFileDir, "yixianqian.jpeg");
			if (!picFile.exists()) {
				picFile.createNewFile();
			}
			photoUri = Uri.fromFile(picFile);
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
			startActivityForResult(cameraIntent, activity_result_camara_with_data);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ������л�ȡ
	 */
	protected void doCropPhoto() {
		try {
			File pictureFileDir = new File(Environment.getExternalStorageDirectory(), "/yixianqian/image");
			if (!pictureFileDir.exists()) {
				pictureFileDir.mkdirs();
			}
			picFile = new File(pictureFileDir, "yixianqian.jpeg");
			if (!picFile.exists()) {
				picFile.createNewFile();
			}
			photoUri = Uri.fromFile(picFile);
			final Intent intent = getCropImageIntent();
			startActivityForResult(intent, activity_result_cropimage_with_data);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *  ����ͼƬ��������
	 */
	public Intent getCropImageIntent() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 800);
		intent.putExtra("outputY", 800);
		intent.putExtra("noFaceDetection", true);
		intent.putExtra("scale", true);
		intent.putExtra("scaleUpIfNeeded", true);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		return intent;
	}

	private void cropImageUriByTakePhoto() {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(photoUri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 800);
		intent.putExtra("outputY", 800);
		intent.putExtra("scale", true);
		intent.putExtra("scaleUpIfNeeded", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, activity_result_cropimage_with_data);
	}

	private Bitmap decodeUriAsBitmap(Uri uri) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

	/**
	 * �ϴ�ͷ��
	 */
	public void uploadImage(String filePath) {
		String uploadHost = HttpUtil.BASE_URL + "UploadServlet";
		RequestParams params = new RequestParams();
		int userId = sharePreferenceUtil.getU_id();
		if (userId > -1) {
			params.addBodyParameter(UserTable.U_ID, String.valueOf(userId));
		}
		params.addBodyParameter(filePath.replace("/", ""), new File(filePath));
		uploadMethod(params, uploadHost);
	}

	/**
	 * �ϴ��ļ�
	 * 
	 * @param params
	 * @param uploadHost
	 */
	public void uploadMethod(final RequestParams params, final String uploadHost) {
		HttpUtils http = new HttpUtils();
		http.send(HttpMethod.POST, uploadHost, params, new RequestCallBack<String>() {
			@Override
			public void onStart() {
				Toast.makeText(HeadImageActivity.this, "��ʼ�ϴ�...", 1).show();
			}

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				if (isUploading) {
					System.out.println("upload: " + current + "/" + total);
				} else {
					System.out.println("reply: " + current + "/" + total);
				}
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				Toast.makeText(HeadImageActivity.this, "ͷ���ϴ��ɹ���", 1).show();
				// ���¸����û�����
				//				new GetUserTask().execute();
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				Toast.makeText(HeadImageActivity.this, "ͷ���ϴ�ʧ�ܣ�" + msg, 1).show();
			}
		});
	}

}
