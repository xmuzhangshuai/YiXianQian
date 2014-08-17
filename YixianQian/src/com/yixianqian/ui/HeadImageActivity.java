package com.yixianqian.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.http.Header;

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

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.server.ServerUtil;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.AsyncHttpClientImageSound;
import com.yixianqian.utils.ImageTools;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

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
	private ImageView camera_image;//���ͼ��
	private UserPreference userPreference;

	private File picFile;
	private Uri photoUri;

	private final int activity_result_camara_with_data = 1006;
	private final int activity_result_cropimage_with_data = 1007;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_head_image);
		userPreference = BaseApplication.getInstance().getUserPreference();

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
				uploadImage(photoUri.getPath());
				ServerUtil.getInstance(HeadImageActivity.this).getTodayRecommend(HeadImageActivity.this, true);
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
					if (bitmap != null) {
						camera_image.setVisibility(View.GONE);
						headImage.setImageBitmap(bitmap);
						headImage.setVisibility(View.VISIBLE);
						rightImageButton.setEnabled(true);
					}
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
			File uploadFileDir = new File(Environment.getExternalStorageDirectory(), "/yixianqian/image");
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
	 * @param filePath
	 */
	public void uploadImage(String filePath) {
		final Bitmap largeAvatar = BitmapFactory.decodeFile(filePath);
		final Bitmap smallBitmap = ImageTools.zoomBitmap(largeAvatar, largeAvatar.getWidth() / 4,
				largeAvatar.getHeight() / 4);
		String smallAvatarPath = Environment.getExternalStorageDirectory() + "/yixianqian/image";

		RequestParams params = new RequestParams();
		int userId = userPreference.getU_id();
		if (userId > -1) {
			params.put(UserTable.U_ID, String.valueOf(userId));
			try {
				params.put("large_avatar", picFile);
				params.put("small_avatar",
						ImageTools.savePhotoToSDCard(smallBitmap, smallAvatarPath, "smallAvatar.jpeg", 100));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, String response) {
					// TODO Auto-generated method stub
					if (statusCode == 200) {
						ToastTool.showLong(HeadImageActivity.this, "ͷ���ϴ��ɹ�����ȴ����");
						largeAvatar.recycle();
						smallBitmap.recycle();
						//����ͷ���Ѹı�
						userPreference.setHeadImageChanged(true);
						//ɾ������ͷ��
						ImageTools.deleteImageByPath(photoUri.getPath());
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
					// TODO Auto-generated method stub
					ToastTool.showLong(HeadImageActivity.this, "ͷ���ϴ�ʧ�ܣ�" + errorResponse);
				}
			};
			AsyncHttpClientImageSound.post(AsyncHttpClientImageSound.HEADIMAGE_URL, params, responseHandler);
		}
	}
}
