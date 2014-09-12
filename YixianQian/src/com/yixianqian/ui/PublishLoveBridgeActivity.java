package com.yixianqian.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.http.Header;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.config.Constants;
import com.yixianqian.customewidget.MyAlertDialog;
import com.yixianqian.table.LoveBridgeItemTable;
import com.yixianqian.utils.AsyncHttpClientImageSound;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.ImageTools;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

/**
 * 类名称：PublishLoveBridgeActivity
 * 类描述：发布鹊桥信息
 * 创建人： 张帅
 * 创建时间：2014年9月12日 上午9:15:54
 *
 */
public class PublishLoveBridgeActivity extends BaseActivity implements OnClickListener {
	public static final int REQUEST_CODE_PREVIEW_PICTURE = 1000;//预览图库图片后发送

	private TextView topNavText;//导航条文字
	private View right_btn_bg;
	private View left_btn_bg;
	private View choosePhotoBtn;
	private View takePhotoBtn;
	private ImageView publishImageView;
	private EditText publishEditeEditText;
	private TextView wordCountTextView;//字数

	/**************用户变量**************/
	public static final int NUM = 250;
	private String photoUri;//图片地址
	private UserPreference userPreference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_publish_love_bridge);
		userPreference = BaseApplication.getInstance().getUserPreference();

		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		right_btn_bg = (View) findViewById(R.id.right_btn_bg);
		topNavText = (TextView) findViewById(R.id.nav_text);
		left_btn_bg = findViewById(R.id.left_btn_bg);
		choosePhotoBtn = findViewById(R.id.choose_image);
		takePhotoBtn = findViewById(R.id.takephoto);
		publishImageView = (ImageView) findViewById(R.id.publish_image);
		publishEditeEditText = (EditText) findViewById(R.id.publish_content);
		wordCountTextView = (TextView) findViewById(R.id.count);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavText.setText("发布鹊桥信息");
		right_btn_bg.setEnabled(false);
		right_btn_bg.setOnClickListener(this);
		left_btn_bg.setOnClickListener(this);
		choosePhotoBtn.setOnClickListener(this);
		takePhotoBtn.setOnClickListener(this);
		publishImageView.setOnClickListener(this);

		//设置编辑框事件
		publishEditeEditText.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int selectionStart;
			private int selectionEnd;

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				temp = s;
				if (s.length() > 14) {
					right_btn_bg.setEnabled(true);
				} else {
					right_btn_bg.setEnabled(false);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				int number = NUM - s.length();
				wordCountTextView.setText("" + number + "字");
				selectionStart = publishEditeEditText.getSelectionStart();
				selectionEnd = publishEditeEditText.getSelectionEnd();
				if (temp.length() > NUM) {
					s.delete(selectionStart - 1, selectionEnd);
					int tempSelection = selectionStart;
					publishEditeEditText.setText(s);
					publishEditeEditText.setSelection(tempSelection);//设置光标在最后
				}
			}
		});
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		giveUpPublish();
	}

	/**
	 * 放弃发布
	 */
	private void giveUpPublish() {
		final MyAlertDialog myAlertDialog = new MyAlertDialog(this);
		myAlertDialog.setTitle("提示");
		myAlertDialog.setMessage("放弃发布？  ");
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
		myAlertDialog.setPositiveButton("确定", comfirm);
		myAlertDialog.setNegativeButton("取消", cancle);
		myAlertDialog.show();
	}

	/**
	 * 显示图片
	 */
	public void showPicture() {
		String tempPath = Environment.getExternalStorageDirectory() + "/yixianqian/image";
		String photoName = "loveBridge.jpeg";
		File file = ImageTools.compressForFile(tempPath, photoName, photoUri, 100);

		Bitmap uploadBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
		publishImageView.setImageBitmap(uploadBitmap);

		if (file != null) {
			photoUri = file.getAbsolutePath();
		}
		publishImageView.setVisibility(View.VISIBLE);
	}

	/**
	 * 从相册选择图片
	 */
	private void choosePhoto() {
		Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, 2);
	}

	/**
	 * 拍照
	 */
	private void takePhoto() {
		try {
			File uploadFileDir = new File(Environment.getExternalStorageDirectory(), "/yixianqian/image");
			Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			if (!uploadFileDir.exists()) {
				uploadFileDir.mkdirs();
			}
			File picFile = new File(uploadFileDir, "yixianqian.jpeg");

			if (!picFile.exists()) {
				picFile.createNewFile();
			}

			photoUri = picFile.getAbsolutePath();
			Uri takePhotoUri = Uri.fromFile(picFile);
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, takePhotoUri);
			startActivityForResult(cameraIntent, 1);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK)
			return;

		switch (requestCode) {
		case 1://拍照
			showPicture();
			break;
		case 2://从相册选择
			try {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = PublishLoveBridgeActivity.this.getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				photoUri = cursor.getString(columnIndex);
				cursor.close();
				showPicture();
			} catch (Exception e) {
				// TODO: handle exception   
				e.printStackTrace();
			}
			break;
		case REQUEST_CODE_PREVIEW_PICTURE:
			photoUri = null;
			publishImageView.setVisibility(View.GONE);
			break;
		}
	}

	/**
	 * 发布
	 */
	private void publish() {
		File photoFile = null;
		if (!TextUtils.isEmpty(photoUri)
				&& photoUri.equals(Environment.getExternalStorageDirectory() + "/yixianqian/image/loveBridge.jpeg")) {
			photoFile = new File(photoUri);
			//			LogTool.e("鹊桥", "" + FileSizeUtil.getFileOrFilesSize(photoUri, FileSizeUtil.SIZETYPE_KB) + "KB");
		}

		RequestParams params = new RequestParams();
		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {
			Dialog dialog;

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
				dialog = showProgressDialog("正在发布，请稍后...");
				dialog.setCancelable(false);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					ToastTool.showShort(PublishLoveBridgeActivity.this, "发布成功！");
					finish();
					overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				ToastTool.showShort(PublishLoveBridgeActivity.this, "失败！");
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
		params.put(LoveBridgeItemTable.N_USERID, userPreference.getU_id());
		params.put(LoveBridgeItemTable.N_CONTENT, publishEditeEditText.getText().toString().trim());
		if (userPreference.getU_gender().equals(Constants.Gender.MALE)) {
			params.put(LoveBridgeItemTable.N_GENDER, 1);
		} else {
			params.put(LoveBridgeItemTable.N_GENDER, 2);
		}

		params.put(LoveBridgeItemTable.N_SCHOOLID, userPreference.getU_schoolid());
		params.put(LoveBridgeItemTable.N_NAME, userPreference.getName());

		if (photoFile != null && photoFile.exists()) {
			try {
				params.put(LoveBridgeItemTable.N_IAMGE, photoFile);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			AsyncHttpClientImageSound.post("lovebridgeitemimage", params, responseHandler);
		} else {
			AsyncHttpClientTool.post("addlovebridgeitemrecord", params, responseHandler);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.left_btn_bg:
			giveUpPublish();
			break;
		case R.id.right_btn_bg:
			publish();
			break;
		case R.id.choose_image:
			choosePhoto();
			break;
		case R.id.takephoto:
			takePhoto();
			break;
		case R.id.publish_image:
			if (photoUri != null) {
				startActivityForResult(
						new Intent(PublishLoveBridgeActivity.this, PublishLBPriviewActivity.class).putExtra(
								ChatImagePreviewActivity.SHOW_BIG_IMAGE, "file://" + photoUri),
						REQUEST_CODE_PREVIEW_PICTURE);
			}
			break;
		default:
			break;
		}
	}
}
