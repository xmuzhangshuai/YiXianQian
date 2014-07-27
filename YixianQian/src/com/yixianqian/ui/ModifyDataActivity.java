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
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.ImageLoaderTool;
import com.yixianqian.utils.ImageTools;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

/**
 * �����ƣ�ModifyDataActivity
 * ���������޸ĸ�������ҳ��
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��7��26�� ����8:47:52
 *
 */
public class ModifyDataActivity extends BaseActivity implements OnClickListener {
	private TextView topNavigation;//����������
	private View leftImageButton;//��������ఴť
	private View rightImageButton;//�������Ҳఴť
	private EditText nameEditText;//����
	private EditText nickNameEditText;//�ǳ�
	private EditText emailEditText;//����
	private TextView telEditText;//�绰
	private UserPreference userPreference;
	private View passView;
	private View nameView;
	private View nicknameView;
	private View emailView;
	private View phoneView;
	private TextView waitCheckView;
	private ImageView headImage;
	private InputMethodManager imm;
	private String realname;
	private String nickname;
	private String email;

	private File picFile;
	private Uri photoUri;

	private final int activity_result_camara_with_data = 1006;
	private final int activity_result_cropimage_with_data = 1007;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_modify_data);

		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		userPreference = BaseApplication.getInstance().getUserPreference();
		findViewById();
		initView();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		telEditText.setText(userPreference.getU_tel());
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		topNavigation = (TextView) findViewById(R.id.nav_text);
		leftImageButton = (View) findViewById(R.id.left_btn_bg);
		rightImageButton = (View) findViewById(R.id.right_btn_bg);
		nameEditText = (EditText) findViewById(R.id.name);
		nickNameEditText = (EditText) findViewById(R.id.nickname);
		emailEditText = (EditText) findViewById(R.id.email);
		telEditText = (TextView) findViewById(R.id.phone);
		passView = findViewById(R.id.passview);
		nameView = findViewById(R.id.realnameview);
		nicknameView = findViewById(R.id.nicknameview);
		emailView = findViewById(R.id.emailview);
		phoneView = findViewById(R.id.phoneview);
		headImage = (ImageView) findViewById(R.id.headimage);
		waitCheckView = (TextView) findViewById(R.id.waitcheck);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavigation.setText("�༭����");
		leftImageButton.setOnClickListener(this);
		rightImageButton.setOnClickListener(this);
		passView.setOnClickListener(this);
		headImage.setOnClickListener(this);
		nameView.setOnClickListener(this);
		nicknameView.setOnClickListener(this);
		emailView.setOnClickListener(this);
		phoneView.setOnClickListener(this);
		nameEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					if (nameEditText.getText().toString().equals("δ���")) {
						nameEditText.setText("");
					}
				} else {
					if (TextUtils.isEmpty(nameEditText.getText().toString())) {
						nameEditText.setText("δ���");
					}
				}
			}
		});
		emailEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					if (emailEditText.getText().toString().equals("δ��")) {
						emailEditText.setText("");
					}
				} else {
					if (TextUtils.isEmpty(emailEditText.getText().toString())) {
						emailEditText.setText("δ��");
					}
				}
			}
		});

		nameEditText.setText(userPreference.getU_realname());
		if (TextUtils.isEmpty(userPreference.getU_realname())) {
			nameEditText.setText("δ���");
		}
		nickNameEditText.setText(userPreference.getU_nickname());
		emailEditText.setText(userPreference.getU_email());
		if (TextUtils.isEmpty(userPreference.getU_email())) {
			emailEditText.setText("δ��");
		}

		ServerUtil.getInstance(ModifyDataActivity.this).disPlayHeadImage(headImage, waitCheckView);
//		imageLoader.displayImage(AsyncHttpClientImageSound.getAbsoluteUrl(userPreference.getU_small_avatar()),
//				headImage, ImageLoaderTool.getHeadImageOptions(10));
//		if (userPreference.getHeadImagePassed() == 0) {
//			waitCheckView.setVisibility(View.VISIBLE);
//		} else if (userPreference.getHeadImagePassed() == -1) {
//			waitCheckView.setVisibility(View.VISIBLE);
//			waitCheckView.setText("δͨ��");
//		}
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
					uploadImage(photoUri.getPath());
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
						ToastTool.showLong(ModifyDataActivity.this, "ͷ���ϴ��ɹ�����ȴ����");
						largeAvatar.recycle();
						smallBitmap.recycle();
						//����ͷ���Ѹı�
						userPreference.setHeadImageChanged(true);
						//��ȡ��ͷ���ַ
						ServerUtil.getInstance(ModifyDataActivity.this).getHeadImage(headImage, waitCheckView);
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
					// TODO Auto-generated method stub
					ToastTool.showLong(ModifyDataActivity.this, "ͷ���ϴ�ʧ�ܣ�" + errorResponse);
				}
			};
			AsyncHttpClientImageSound.post(AsyncHttpClientImageSound.HEADIMAGE_URL, params, responseHandler);
		}
	}

	/**
	 * ȷ���޸�
	 */
	private void attemptModify() {
		boolean realNameChanged = false;
		boolean nickNameChanged = false;
		boolean emailNameChanged = false;
		// ���ô���
		nameEditText.setError(null);
		nickNameEditText.setError(null);
		emailEditText.setError(null);
		//		telEditText.setError(null);

		// �洢�û�ֵ
		realname = nameEditText.getText().toString();
		nickname = nickNameEditText.getText().toString();
		email = emailEditText.getText().toString();
		//		phone = telEditText.getText().toString();
		boolean cancel = false;
		View focusView = null;

		//�������
		if (!realname.equals(userPreference.getU_realname()) && !realname.equals("δ���") && !TextUtils.isEmpty(realname)) {
			realNameChanged = true;
		}

		//����ǳ�
		if (!nickname.equals(userPreference.getU_nickname()) && !TextUtils.isEmpty(nickname)) {
			nickNameChanged = true;
		}

		//�������
		if ((!email.equals(userPreference.getU_email())) && (!TextUtils.isEmpty(email)) && !email.equals("δ��")) {
			if (!email.contains("@")) {
				emailEditText.setError("�����ʽ����");
				focusView = emailEditText;
				cancel = true;
			} else {
				emailNameChanged = true;
			}
		}

		if (cancel) {
			// �����������ʾ����
			focusView.requestFocus();
		} else {
			if (realNameChanged) {
				RequestParams params = new RequestParams();
				params.put(UserTable.U_ID, userPreference.getU_id());
				params.put(UserTable.U_REALNAME, realname);
				TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {

					@Override
					public void onSuccess(int arg0, Header[] arg1, String arg2) {
						// TODO Auto-generated method stub
						if (arg0 == 200) {
							userPreference.setU_realname(realname);
						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
						// TODO Auto-generated method stub

					}
				};
				AsyncHttpClientTool.post("updateuserrealname", params, responseHandler);
			}
			if (nickNameChanged) {
				RequestParams params = new RequestParams();
				params.put(UserTable.U_ID, userPreference.getU_id());
				params.put(UserTable.U_NICKNAME, nickname);
				TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {

					@Override
					public void onSuccess(int arg0, Header[] arg1, String arg2) {
						// TODO Auto-generated method stub
						if (arg0 == 200) {
							userPreference.setU_nickname(nickname);
						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
						// TODO Auto-generated method stub

					}
				};
				AsyncHttpClientTool.post("updateusernickname", params, responseHandler);
			}
			if (emailNameChanged) {
				RequestParams params = new RequestParams();
				params.put(UserTable.U_ID, userPreference.getU_id());
				params.put(UserTable.U_EMAIL, email);
				TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {

					@Override
					public void onSuccess(int arg0, Header[] arg1, String arg2) {
						// TODO Auto-generated method stub
						if (arg0 == 200) {
							userPreference.setU_email(email);
						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
						// TODO Auto-generated method stub

					}
				};
				AsyncHttpClientTool.post("updateusermail", params, responseHandler);
			}
			finish();
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.left_btn_bg:
			finish();
			break;
		case R.id.right_btn_bg:
			attemptModify();
			break;
		case R.id.passview:
			intent = new Intent(ModifyDataActivity.this, ModifyPassActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.headimage:
			showPicturePicker(ModifyDataActivity.this);
			break;
		case R.id.realnameview:
			nameEditText.setFocusable(true);
			nameEditText.setFocusableInTouchMode(true);
			nameEditText.requestFocus();
			nameEditText.requestFocusFromTouch();
			imm.showSoftInput(nameEditText, 0);
			break;
		case R.id.nicknameview:
			nickNameEditText.setFocusable(true);
			nickNameEditText.setFocusableInTouchMode(true);
			nickNameEditText.requestFocus();
			nickNameEditText.requestFocusFromTouch();
			imm.showSoftInput(nickNameEditText, 0);
			break;
		case R.id.emailview:
			emailEditText.setFocusable(true);
			emailEditText.setFocusableInTouchMode(true);
			emailEditText.requestFocus();
			emailEditText.requestFocusFromTouch();
			imm.showSoftInput(emailEditText, 0);
			break;
		case R.id.phoneview:
			intent = new Intent(ModifyDataActivity.this, ModifyPhoneActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		default:
			break;
		}
	}
}
