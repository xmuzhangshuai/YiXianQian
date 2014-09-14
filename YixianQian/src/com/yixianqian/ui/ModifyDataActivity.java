package com.yixianqian.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.http.Header;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.base.BaseFragmentActivity;
import com.yixianqian.config.Constants;
import com.yixianqian.server.ServerUtil;
import com.yixianqian.table.UserTable;
import com.yixianqian.ui.ConstellDialogFragment.OnConstellChangedListener;
import com.yixianqian.utils.AsyncHttpClientImageSound;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.ConstellUtil;
import com.yixianqian.utils.DateTimeTools;
import com.yixianqian.utils.ImageTools;
import com.yixianqian.utils.LogTool;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

/**
 * 类名称：ModifyDataActivity
 * 类描述：修改个人资料页面
 * 创建人： 张帅
 * 创建时间：2014年7月26日 下午8:47:52
 *
 */
public class ModifyDataActivity extends BaseFragmentActivity implements OnClickListener, OnConstellChangedListener {
	private TextView topNavigation;//导航栏文字
	private View leftImageButton;//导航栏左侧按钮
	private View rightImageButton;//导航栏右侧按钮
	//	private EditText nameEditText;//姓名
	private EditText nickNameEditText;//昵称
	//	private EditText emailEditText;//邮箱
	private TextView telEditText;//电话
	private View genderView;
	private View ageView;
	private View birthdayView;
	private View heightView;
	private View weightView;
	private View constellView;
	private View introView;
	private ImageView vertifyView;
	private EditText ageEditText;
	private EditText heightEditText;
	private EditText weightEditText;
	private TextView constellEditText;
	private EditText introEditText;
	private TextView genderText;
	private TextView birthdayTextView;
	private UserPreference userPreference;
	private View passView;
	//	private View nameView;
	private View nicknameView;
	//	private View emailView;
	private View phoneView;
	private TextView provinceTextView;
	private TextView cityTextView;
	private TextView schoolTextView;
	private TextView waitCheckView;
	private ImageView headImage;
	private InputMethodManager imm;
	//	private String realname;
	private String nickname;
	//	private String email;
	private String age;
	private String weight;
	private String height;
	private String constell;
	private String personIntro;

	private File picFile;
	private Uri photoUri;

	private final int activity_result_camara_with_data = 1006;
	private final int activity_result_cropimage_with_data = 1007;
	boolean nickNameChanged = false;
	boolean ageChanged = false;
	boolean weightChanged = false;
	boolean heightChanged = false;
	boolean constellChanged = false;
	boolean personIntroChanged = false;

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
		//		nameEditText = (EditText) findViewById(R.id.name);
		nickNameEditText = (EditText) findViewById(R.id.nickname);
		//		emailEditText = (EditText) findViewById(R.id.email);
		telEditText = (TextView) findViewById(R.id.phone);
		passView = findViewById(R.id.passview);
		//		nameView = findViewById(R.id.realnameview);
		nicknameView = findViewById(R.id.nicknameview);
		//		emailView = findViewById(R.id.emailview);
		phoneView = findViewById(R.id.phoneview);
		headImage = (ImageView) findViewById(R.id.headimage);
		waitCheckView = (TextView) findViewById(R.id.waitcheck);
		genderView = findViewById(R.id.genderview);
		genderText = (TextView) findViewById(R.id.gender);
		ageView = findViewById(R.id.ageview);
		birthdayView = findViewById(R.id.birthdayview);
		heightView = findViewById(R.id.heightview);
		weightView = findViewById(R.id.weightview);
		constellView = findViewById(R.id.constellview);
		introView = findViewById(R.id.personIntroview);
		ageEditText = (EditText) findViewById(R.id.age);
		heightEditText = (EditText) findViewById(R.id.height);
		weightEditText = (EditText) findViewById(R.id.weight);
		constellEditText = (TextView) findViewById(R.id.constell);
		introEditText = (EditText) findViewById(R.id.personIntro);
		birthdayTextView = (TextView) findViewById(R.id.birthday);
		vertifyView = (ImageView) findViewById(R.id.vertify);
		provinceTextView = (TextView) findViewById(R.id.province);
		cityTextView = (TextView) findViewById(R.id.city);
		schoolTextView = (TextView) findViewById(R.id.school);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavigation.setText("编辑资料");
		leftImageButton.setOnClickListener(this);
		rightImageButton.setOnClickListener(this);
		passView.setOnClickListener(this);
		headImage.setOnClickListener(this);
		//		nameView.setOnClickListener(this);
		nicknameView.setOnClickListener(this);
		//		emailView.setOnClickListener(this);
		phoneView.setOnClickListener(this);
		genderView.setOnClickListener(this);
		ageView.setOnClickListener(this);
		birthdayView.setOnClickListener(this);
		heightView.setOnClickListener(this);
		weightView.setOnClickListener(this);
		constellView.setOnClickListener(this);
		introView.setOnClickListener(this);

		//如果通过认证
		if (userPreference.getVertify() == Constants.VertifyState.PASSED) {
			vertifyView.setImageResource(R.drawable.already_vertify);
		} else {
			vertifyView.setImageResource(R.drawable.sel_apply_vertify_btn);
			vertifyView.setOnClickListener(this);
		}

		if (userPreference.getU_age() > 0) {
			ageEditText.setText("" + userPreference.getU_age());
		}

		if (userPreference.getU_height() > 0) {
			heightEditText.setText("" + userPreference.getU_height());
		}

		if (userPreference.getU_weight() > 0) {
			weightEditText.setText("" + userPreference.getU_weight());
		}

		//设置生日
		if (userPreference.getU_birthday() != null) {
			birthdayTextView.setText(DateTimeTools.getDateString(userPreference.getU_birthday()));
		}

		//设置星座
		if (!TextUtils.isEmpty(userPreference.getU_constell())) {
			constellEditText.setText(userPreference.getU_constell());
		}

		if (!TextUtils.isEmpty(userPreference.getU_introduce())) {
			introEditText.setText(userPreference.getU_introduce());
		}

		provinceTextView.setText(userPreference.getProvinceName());
		cityTextView.setText(userPreference.getCityName());
		schoolTextView.setText(userPreference.getSchoolName());

		genderText.setText(userPreference.getU_gender());
		//		nameEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
		//
		//			@Override
		//			public void onFocusChange(View v, boolean hasFocus) {
		//				// TODO Auto-generated method stub
		//				if (hasFocus) {
		//					if (nameEditText.getText().toString().equals("未添加")) {
		//						nameEditText.setText("");
		//					}
		//				} else {
		//					if (TextUtils.isEmpty(nameEditText.getText().toString())) {
		//						nameEditText.setText("未添加");
		//					}
		//				}
		//			}
		//		});
		//		emailEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
		//
		//			@Override
		//			public void onFocusChange(View v, boolean hasFocus) {
		//				// TODO Auto-generated method stub
		//				if (hasFocus) {
		//					if (emailEditText.getText().toString().equals("未绑定")) {
		//						emailEditText.setText("");
		//					}
		//				} else {
		//					if (TextUtils.isEmpty(emailEditText.getText().toString())) {
		//						emailEditText.setText("未绑定");
		//					}
		//				}
		//			}
		//		});

		//		nameEditText.setText(userPreference.getU_realname());
		//		if (TextUtils.isEmpty(userPreference.getU_realname())) {
		//			nameEditText.setText("未添加");
		//		}
		nickNameEditText.setText(userPreference.getU_nickname());
		//		emailEditText.setText(userPreference.getU_email());
		//		if (TextUtils.isEmpty(userPreference.getU_email())) {
		//			emailEditText.setText("未绑定");
		//		}

		ServerUtil.getInstance().disPlayHeadImage(headImage, waitCheckView);
	}

	/**
	* 显示对话框，从拍照和相册选择图片来源
	* 
	* @param context
	* @param isCrop
	*/
	public void showPicturePicker(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("图片来源");
		builder.setItems(new String[] { "拍照", "相册" }, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					String status = Environment.getExternalStorageState();
					if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
						doTakePhoto();// 用户点击了从照相机获取
					}
					break;
				case 1:
					doCropPhoto();// 从相册中去获取
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
		case activity_result_camara_with_data: // 拍照
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
	 * 拍照获取图片
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
	 * 从相册中获取
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
	 *  调用图片剪辑程序
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
	 * 上传头像
	 * @param filePath
	 */
	public void uploadImage(final String filePath) {
		final Bitmap largeAvatar = BitmapFactory.decodeFile(filePath);
		if (largeAvatar != null) {
			final Bitmap smallBitmap = ImageTools.zoomBitmap(largeAvatar, largeAvatar.getWidth() / 4,
					largeAvatar.getHeight() / 4);
			final String smallAvatarPath = Environment.getExternalStorageDirectory() + "/yixianqian/image";

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
							ToastTool.showLong(ModifyDataActivity.this, "头像上传成功！请等待审核");
							largeAvatar.recycle();
							smallBitmap.recycle();
							//删除本地头像
							ImageTools.deleteImageByPath(filePath);
							ImageTools.deletePhotoAtPathAndName(smallAvatarPath, "smallAvatar.jpeg");
							//获取新头像地址
							ServerUtil.getInstance().getHeadImage(headImage, waitCheckView);
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
						// TODO Auto-generated method stub
						ToastTool.showLong(ModifyDataActivity.this, "头像上传失败！" + errorResponse);
						//删除本地头像
						ImageTools.deleteImageByPath(filePath);
						ImageTools.deletePhotoAtPathAndName(smallAvatarPath, "smallAvatar.jpeg");
					}
				};
				AsyncHttpClientImageSound.post(AsyncHttpClientImageSound.HEADIMAGE_URL, params, responseHandler);
			}
		} else {
			ImageTools.deleteImageByPath(filePath);
		}
	}

	/**
	 * 确认修改
	 */
	private void attemptModify() {
		//		boolean realNameChanged = false;
		//		boolean emailNameChanged = false;
		//		boolean nickNameChanged = false;
		//		boolean ageChanged = false;
		//		boolean weightChanged = false;
		//		boolean heightChanged = false;
		//		boolean constellChanged = false;
		//		boolean personIntroChanged = false;

		// 重置错误
		//		nameEditText.setError(null);
		nickNameEditText.setError(null);
		//		emailEditText.setError(null);
		ageEditText.setError(null);
		weightEditText.setError(null);
		heightEditText.setError(null);
		constellEditText.setError(null);
		introEditText.setError(null);
		birthdayTextView.setError(null);

		// 存储用户值
		//		realname = nameEditText.getText().toString();
		nickname = nickNameEditText.getText().toString();
		//		email = emailEditText.getText().toString();
		age = ageEditText.getText().toString();
		height = heightEditText.getText().toString();
		weight = weightEditText.getText().toString();
		constell = constellEditText.getText().toString();
		personIntro = introEditText.getText().toString();

		boolean cancel = false;
		View focusView = null;

		//检查真名
		//		if (!realname.equals(userPreference.getU_realname()) && !realname.equals("未添加") && !TextUtils.isEmpty(realname)) {
		//			realNameChanged = true;
		//		}

		//检查昵称
		if (!nickname.equals(userPreference.getU_nickname()) && !TextUtils.isEmpty(nickname)) {
			nickNameChanged = true;
		}

		//检查邮箱
		//		if ((!email.equals(userPreference.getU_email())) && (!TextUtils.isEmpty(email)) && !email.equals("未绑定")) {
		//			if (!email.contains("@")) {
		//				emailEditText.setError("邮箱格式不对");
		//				focusView = emailEditText;
		//				cancel = true;
		//			} else {
		//				emailNameChanged = true;
		//			}
		//		}

		//检查年龄
		if (!TextUtils.isEmpty(age)) {
			int a = Integer.parseInt(age);
			if (a < 16 || a > 30) {
				ageEditText.setError("年龄必须在16到30岁之间");
				focusView = ageEditText;
				cancel = true;
			} else if (a != userPreference.getU_age()) {
				ageChanged = true;
			}
		}

		//检查身高
		if (!TextUtils.isEmpty(height)) {
			int h = Integer.parseInt(height);
			if (h < 80 || h > 230) {
				heightEditText.setError("身高必须在80cm~230cm之间");
				focusView = heightEditText;
				cancel = true;
			} else if (Integer.parseInt(height) != userPreference.getU_height()) {
				heightChanged = true;
			}
		}

		//检查体重
		if (!TextUtils.isEmpty(weight)) {
			int w = Integer.parseInt(weight);
			if (w < 30 || w > 300) {
				weightEditText.setError("体重必须在30~300Kg之间");
				focusView = weightEditText;
				cancel = true;
			} else if (w != userPreference.getU_weight()) {
				weightChanged = true;
			}
		}

		//检查星座
		if (!constell.equals(userPreference.getU_constell()) && !TextUtils.isEmpty(constell)) {
			constellChanged = true;
		}

		//检查个人说明
		if (!personIntro.equals(userPreference.getU_introduce()) && !TextUtils.isEmpty(personIntro)) {
			personIntroChanged = true;
		}

		if (cancel) {
			// 如果错误，则提示错误
			focusView.requestFocus();
		} else {

			if (!nickNameChanged && !ageChanged && !weightChanged && !heightChanged && !constellChanged
					&& !personIntroChanged) {
				ToastTool.showShort(ModifyDataActivity.this, "没有做任何修改");
				finish();
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			}

			final ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage("正在修改,请稍后...");
			dialog.setCancelable(false);

			//昵称有变化
			if (nickNameChanged) {
				RequestParams params = new RequestParams();
				params.put(UserTable.U_ID, userPreference.getU_id());
				params.put(UserTable.U_NICKNAME, nickname);
				TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();
						if (!dialog.isShowing()) {
							dialog.show();
						}
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						super.onFinish();
						nickNameChanged = false;
						if (!nickNameChanged && !ageChanged && !weightChanged && !heightChanged && !constellChanged
								&& !personIntroChanged && dialog.isShowing()) {
							dialog.dismiss();
							ToastTool.showLong(ModifyDataActivity.this, "修改成功！");
							finish();
							overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
						}
					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, String arg2) {
						// TODO Auto-generated method stub
						if (arg0 == 200) {
							userPreference.setU_nickname(nickname);
//							loginHuanXin();
						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
						// TODO Auto-generated method stub
					}
				};
				AsyncHttpClientTool.post("updateusernickname", params, responseHandler);
			}

			//真名有变化
			//			if (realNameChanged) {
			//				RequestParams params = new RequestParams();
			//				params.put(UserTable.U_ID, userPreference.getU_id());
			//				params.put(UserTable.U_REALNAME, realname);
			//				TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {
			//
			//					@Override
			//					public void onSuccess(int arg0, Header[] arg1, String arg2) {
			//						// TODO Auto-generated method stub
			//						if (arg0 == 200) {
			//							userPreference.setU_realname(realname);
			//							LogTool.e(userPreference.getName());
			//							//更新环信昵称
			//							if (EMChatManager.getInstance().updateCurrentUserNick(userPreference.getName())) {
			//								LogTool.i("ModifyDataActivity", "更新环信昵称成功");
			//							} else {
			//								LogTool.e("ModifyDataActivity", "更新环信昵称失败");
			//							}
			//						}
			//					}
			//
			//					@Override
			//					public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
			//						// TODO Auto-generated method stub
			//
			//					}
			//				};
			//				AsyncHttpClientTool.post("updateuserrealname", params, responseHandler);
			//			}

			//邮箱有变化
			//			if (emailNameChanged) {
			//				RequestParams params = new RequestParams();
			//				params.put(UserTable.U_ID, userPreference.getU_id());
			//				params.put(UserTable.U_EMAIL, email);
			//				TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {
			//
			//					@Override
			//					public void onSuccess(int arg0, Header[] arg1, String arg2) {
			//						// TODO Auto-generated method stub
			//						if (arg0 == 200) {
			//							userPreference.setU_email(email);
			//						}
			//					}
			//
			//					@Override
			//					public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
			//						// TODO Auto-generated method stub
			//					}
			//				};
			//				AsyncHttpClientTool.post("updateusermail", params, responseHandler);
			//			}
			//年龄有变化
			if (ageChanged) {
				RequestParams params = new RequestParams();
				params.put(UserTable.U_ID, userPreference.getU_id());
				params.put(UserTable.U_AGE, age);
				TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {
					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();
						if (!dialog.isShowing()) {
							dialog.show();
						}
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						super.onFinish();
						ageChanged = false;
						if (!nickNameChanged && !ageChanged && !weightChanged && !heightChanged && !constellChanged
								&& !personIntroChanged && dialog.isShowing()) {
							dialog.dismiss();
							ToastTool.showLong(ModifyDataActivity.this, "修改成功！");
							finish();
							overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
						}
					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, String arg2) {
						// TODO Auto-generated method stub
						if (arg0 == 200) {
							userPreference.setU_age(Integer.parseInt(age));
						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
						// TODO Auto-generated method stub
					}
				};
				AsyncHttpClientTool.post("updateuserage", params, responseHandler);
			}
			//体重有变化
			if (weightChanged) {
				RequestParams params = new RequestParams();
				params.put(UserTable.U_ID, userPreference.getU_id());
				params.put(UserTable.U_WEIGHT, weight);
				TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {
					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();
						if (!dialog.isShowing()) {
							dialog.show();
						}
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						super.onFinish();
						weightChanged = false;
						if (!nickNameChanged && !ageChanged && !weightChanged && !heightChanged && !constellChanged
								&& !personIntroChanged && dialog.isShowing()) {
							dialog.dismiss();
							ToastTool.showLong(ModifyDataActivity.this, "修改成功！");
							finish();
							overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
						}
					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, String arg2) {
						// TODO Auto-generated method stub
						if (arg0 == 200) {
							userPreference.setU_weight(Integer.parseInt(weight));
						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
						// TODO Auto-generated method stub
					}
				};
				AsyncHttpClientTool.post("updateuserweight", params, responseHandler);
			}
			//身高有变化
			if (heightChanged) {
				RequestParams params = new RequestParams();
				params.put(UserTable.U_ID, userPreference.getU_id());
				params.put(UserTable.U_HEIGHT, height);
				TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {
					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();
						if (!dialog.isShowing()) {
							dialog.show();
						}
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						super.onFinish();
						heightChanged = false;
						if (!nickNameChanged && !ageChanged && !weightChanged && !heightChanged && !constellChanged
								&& !personIntroChanged && dialog.isShowing()) {
							dialog.dismiss();
							ToastTool.showLong(ModifyDataActivity.this, "修改成功！");
							finish();
							overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
						}
					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, String arg2) {
						// TODO Auto-generated method stub
						if (arg0 == 200) {
							userPreference.setU_height(Integer.parseInt(height));
						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
						// TODO Auto-generated method stub
					}
				};
				AsyncHttpClientTool.post("updateuserheight", params, responseHandler);
			}
			//星座有变化
			if (constellChanged) {
				RequestParams params = new RequestParams();
				params.put(UserTable.U_ID, userPreference.getU_id());
				params.put(UserTable.U_CONSTELL, constell);
				TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {
					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();
						if (!dialog.isShowing()) {
							dialog.show();
						}
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						super.onFinish();
						constellChanged = false;
						if (!nickNameChanged && !ageChanged && !weightChanged && !heightChanged && !constellChanged
								&& !personIntroChanged && dialog.isShowing()) {
							dialog.dismiss();
							ToastTool.showLong(ModifyDataActivity.this, "修改成功！");
							finish();
							overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
						}
					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, String arg2) {
						// TODO Auto-generated method stub
						if (arg0 == 200) {
							userPreference.setU_constell(constell);
						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
						// TODO Auto-generated method stub
					}
				};
				AsyncHttpClientTool.post("updateuserconstell", params, responseHandler);
			}

			//个人说明有变化
			if (personIntroChanged) {
				RequestParams params = new RequestParams();
				params.put(UserTable.U_ID, userPreference.getU_id());
				params.put(UserTable.U_INTRODUCE, personIntro);
				TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {
					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();
						if (!dialog.isShowing()) {
							dialog.show();
						}
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						super.onFinish();
						personIntroChanged = false;
						if (!nickNameChanged && !ageChanged && !weightChanged && !heightChanged && !constellChanged
								&& !personIntroChanged && dialog.isShowing()) {
							dialog.dismiss();
							ToastTool.showLong(ModifyDataActivity.this, "修改成功！");
							finish();
							overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
						}
					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, String arg2) {
						// TODO Auto-generated method stub
						if (arg0 == 200) {
							userPreference.setU_introduce(personIntro);
						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
						// TODO Auto-generated method stub
					}
				};
				AsyncHttpClientTool.post("updateuserintro", params, responseHandler);
			}

		}
	}

	/**
	 * 登录环信
	 */
//	private void loginHuanXin() {
//		EMChatManager.getInstance().login(userPreference.getHuanXinUserName(), userPreference.getHuanXinPassword(),
//				new EMCallBack() {
//					@Override
//					public void onSuccess() {
//						LogTool.i("ModifyDataActivity", "登录环信成功");
//						//更新环信昵称
//						if (EMChatManager.getInstance().updateCurrentUserNick(userPreference.getName())) {
//							LogTool.i("ModifyDataActivity", "更新环信昵称成功");
//						} else {
//							LogTool.e("ModifyDataActivity", "更新环信昵称失败");
//						}
//					}
//
//					@Override
//					public void onProgress(int progress, String status) {
//					}
//
//					@Override
//					public void onError(int code, final String message) {
//						LogTool.e("ModifyDataActivity", "登录环信失败：code:" + code + "   message:" + message);
//					}
//				});
//	}

	/**
	 * 显示星座菜单
	 */
	void showConstellDialog() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Fragment fragment = getSupportFragmentManager().findFragmentByTag("constell");
		if (fragment != null) {
			ft.remove(fragment);
		}
		ft.addToBackStack(null);

		// Create and show the dialog.
		ConstellDialogFragment newFragment = ConstellDialogFragment.newInstance();
		newFragment.show(ft, "constell");
	}

	/**
	 * 显示生日菜单
	 */
	private void showDatePicker() {
		final Calendar calendar = Calendar.getInstance(Locale.CHINA);
		OnDateSetListener callBack = new OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, final int year, int monthOfYear, int dayOfMonth) {
				// TODO Auto-generated method stub
				calendar.set(year, monthOfYear, dayOfMonth);
				final Date date = calendar.getTime();

				RequestParams params = new RequestParams();
				params.put(UserTable.U_ID, userPreference.getU_id());
				params.put(UserTable.U_BIRTHDAY, date.getTime());
				TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {

					@Override
					public void onSuccess(int arg0, Header[] arg1, String arg2) {
						// TODO Auto-generated method stub
						if (arg0 == 200) {
							userPreference.setU_birthday(date);
							birthdayTextView.setText(DateTimeTools.getDateString(date));
							constellEditText.setText(ConstellUtil.date2Constellation(calendar));
							int tempAge = Calendar.getInstance().get(Calendar.YEAR) - year;
							ageEditText.setText("" + tempAge);
						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
						// TODO Auto-generated method stub
					}
				};
				AsyncHttpClientTool.post("updateuserbirthday", params, responseHandler);
			}
		};
		DatePickerDialog datePickerDialog;
		if (userPreference.getU_birthday() != null) {
			Date date = userPreference.getU_birthday();
			calendar.setTime(date);
			datePickerDialog = new DatePickerDialog(ModifyDataActivity.this, callBack, calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		} else {
			datePickerDialog = new DatePickerDialog(ModifyDataActivity.this, callBack, 1993, 6, 15);
		}
		datePickerDialog.show();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.left_btn_bg:
			finish();
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
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
		//		case R.id.realnameview:
		//			nameEditText.setFocusable(true);
		//			nameEditText.setFocusableInTouchMode(true);
		//			nameEditText.requestFocus();
		//			nameEditText.requestFocusFromTouch();
		//			imm.showSoftInput(nameEditText, 0);
		//			break;
		case R.id.nicknameview:
			nickNameEditText.setFocusable(true);
			nickNameEditText.setFocusableInTouchMode(true);
			nickNameEditText.requestFocus();
			nickNameEditText.requestFocusFromTouch();
			imm.showSoftInput(nickNameEditText, 0);
			break;
		//		case R.id.emailview:
		//			emailEditText.setFocusable(true);
		//			emailEditText.setFocusableInTouchMode(true);
		//			emailEditText.requestFocus();
		//			emailEditText.requestFocusFromTouch();
		//			imm.showSoftInput(emailEditText, 0);
		//			break;
		case R.id.ageview:
			ageEditText.setFocusable(true);
			ageEditText.setFocusableInTouchMode(true);
			ageEditText.requestFocus();
			ageEditText.requestFocusFromTouch();
			imm.showSoftInput(ageEditText, 0);
			break;
		case R.id.birthdayview:
			showDatePicker();
			break;
		case R.id.heightview:
			heightEditText.setFocusable(true);
			heightEditText.setFocusableInTouchMode(true);
			heightEditText.requestFocus();
			heightEditText.requestFocusFromTouch();
			imm.showSoftInput(heightEditText, 0);
			break;
		case R.id.weightview:
			weightEditText.setFocusable(true);
			weightEditText.setFocusableInTouchMode(true);
			weightEditText.requestFocus();
			weightEditText.requestFocusFromTouch();
			imm.showSoftInput(weightEditText, 0);
			break;
		case R.id.constellview:
			showConstellDialog();
			break;
		case R.id.personIntroview:
			introEditText.setFocusable(true);
			introEditText.setFocusableInTouchMode(true);
			introEditText.requestFocus();
			introEditText.requestFocusFromTouch();
			imm.showSoftInput(introEditText, 0);
			break;
		case R.id.phoneview:
			intent = new Intent(ModifyDataActivity.this, ModifyPhoneActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.genderview:
			ToastTool.showShort(ModifyDataActivity.this, "性别不可修改");
			break;
		case R.id.vertify:
			intent = new Intent(ModifyDataActivity.this, ApplyVertifyActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			finish();
		default:
			break;
		}
	}

	@Override
	public void onConstellChaged(String constell) {
		// TODO Auto-generated method stub
		//设置星座
		if (!TextUtils.isEmpty(constell)) {
			constellEditText.setText(constell);
		}
	}
}
