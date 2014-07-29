package com.yixianqian.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.http.Header;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.config.DefaultKeys;
import com.yixianqian.table.LoverTimeCapsuleTable;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.AsyncHttpClientImageSound;
import com.yixianqian.utils.FriendPreference;
import com.yixianqian.utils.ImageTools;
import com.yixianqian.utils.LogTool;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

public class PublishTimeCapActivity extends BaseActivity {
	private ImageView capsuleImage;
	private ImageView tape;//录音
	private View tapeView;//旋转磁带
	private ImageView progressImage1;
	private ImageView progressImage2;
	private ImageView topNavLeftBtn;//导航条左边按钮
	private View nullImage;
	private ImageView play;
	private TextView tip;
	private View record_view;//录音
	private TextView recordTime;//音频时间
	private View right_btn_bg;
	private String type;//类型，用于判断先拍照还是先录音
	private String audioPath;//音频路径
	private String photoUri;//图片地址
	private File soundFileDir;//文件目录
	private File soundFile;//录音文件
	private String soundName = "audio";//文件名称
	private MediaRecorder mRecorder;
	private MediaPlayer mPlayer;
	private boolean recording = false;//是否正在录音
	private int playCount = 0;
	private UserPreference userPreference;
	private FriendPreference friendPreference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_publish_time_cap);
		mPlayer = new MediaPlayer();
		mRecorder = new MediaRecorder();
		userPreference = BaseApplication.getInstance().getUserPreference();
		friendPreference = BaseApplication.getInstance().getFriendPreference();

		type = getIntent().getStringExtra("type");
		if (type.equals("audio")) {
			//获取音频地址
			audioPath = getIntent().getStringExtra(DefaultKeys.PHOTO_URI);
		} else if (type.equals("picture")) {
			//获取图片地址
			photoUri = getIntent().getStringExtra(DefaultKeys.PHOTO_URI);
		}

		findViewById();
		initView();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		recording = false;
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		capsuleImage = (ImageView) findViewById(R.id.capsule_image);
		topNavLeftBtn = (ImageView) findViewById(R.id.nav_left_btn);
		progressImage1 = (ImageView) findViewById(R.id.progressimage1);
		progressImage2 = (ImageView) findViewById(R.id.progressimage2);
		right_btn_bg = (View) findViewById(R.id.right_btn_bg);
		tapeView = (View) findViewById(R.id.tape_view);
		tape = (ImageView) findViewById(R.id.tape);
		tip = (TextView) findViewById(R.id.add_record);
		record_view = (View) findViewById(R.id.record_view);
		nullImage = (View) findViewById(R.id.gethead_btn);
		play = (ImageView) findViewById(R.id.play);
		recordTime = (TextView) findViewById(R.id.timespan);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub

		/****************如果是已经拍照********************/
		if (type.equals("picture")) {
			record_view.setVisibility(View.INVISIBLE);
			tip.setText("添加一段录音");
			showPicture();

			//录音
			tape.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (!recording) {
						initRecorder();
						showRecordingTape();
						mRecorder.start();
						recording = true;
					} else {
						shutRecordingTape();
						mRecorder.stop();
						recording = false;
						if (soundFile != null && soundFile.exists()) {
							audioPath = soundFile.getAbsolutePath();
						}
						showRecord();
					}
				}
			});
		}

		/****************如果是已经录音********************/
		else if (type.equals("audio")) {
			capsuleImage.setVisibility(View.GONE);
			showRecord();
			tape.setImageResource(R.drawable.sel_photo_btn);
			nullImage.setVisibility(View.VISIBLE);
			tip.setText("添加一张图片");

			//拍照
			tape.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showPicturePicker(PublishTimeCapActivity.this);
				}
			});

		}

		//点击播放
		record_view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					if (audioPath != null) {
						if (playCount % 2 == 0) {
							mPlayer.reset();
							mPlayer.setDataSource(audioPath);
							mPlayer.prepare();
							mPlayer.start();
							play.setImageResource(R.drawable.suspend);
						} else {
							mPlayer.stop();
							play.setImageResource(R.drawable.play);
						}
						playCount++;
					}
				} catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		mPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				mPlayer.stop();
				play.setImageResource(R.drawable.play);
				playCount++;
			}
		});

		topNavLeftBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		right_btn_bg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				publish();
			}
		});
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if (mRecorder != null) {
			mRecorder.release();
			mRecorder = null;
		}
		super.onDestroy();
	}

	/**
	 * 初始化录音
	 */
	void initRecorder() {
		try {
			soundFileDir = new File(Environment.getExternalStorageDirectory(), "/yixianqian/audio");
			if (!soundFileDir.exists()) {
				soundFileDir.mkdirs();
			}

			soundFile = new File(soundFileDir, soundName + ".amr");
			if (!soundFile.exists()) {
				soundFile.createNewFile();
			}

			//设置录音来源
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

			//设置录音的输出格式
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

			//设置声音编码格式
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

			mRecorder.setOutputFile(soundFile.getAbsolutePath());

			mRecorder.prepare();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 显示图片
	 */
	public void showPicture() {
		/** 
		 * 获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转 
		 */
		int degree = ImageTools.readPictureDegree(photoUri);

		//获取缩略图显示到屏幕上
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 2;
		Bitmap cbitmap = BitmapFactory.decodeFile(photoUri, opts);

		/** 
		 * 把图片旋转为正的方向 
		 */
		Bitmap newbitmap = ImageTools.rotaingImageView(degree, cbitmap);
		capsuleImage.setImageBitmap(newbitmap);
		capsuleImage.setVisibility(View.VISIBLE);
		nullImage.setVisibility(View.GONE);
	}

	/**
	 * 显示录音
	 */
	public void showRecord() {
		record_view.setVisibility(View.VISIBLE);
		int length = getRecordLength();
		recordTime.setText(length + "\"");
	}

	/**
	 * 获取录音长度
	 * @return
	 */
	private int getRecordLength() {
		MediaPlayer mediaPlayer = MediaPlayer.create(PublishTimeCapActivity.this, Uri.parse(audioPath));
		int length = 0;
		if (mediaPlayer != null) {
			length = mediaPlayer.getDuration() / 1000;
			mediaPlayer.release();
		}
		mediaPlayer = null;
		return length;
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
						takePhoto();// 用户点击了从照相机获取
					}
					break;
				case 1:
					choosePhoto();// 从相册中去获取
					break;

				default:
					break;
				}
			}
		});
		builder.create().show();
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
			tip.setText("更换图片");
			break;
		case 2://从相册选择
			try {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = PublishTimeCapActivity.this.getContentResolver().query(selectedImage, filePathColumn,
						null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				photoUri = cursor.getString(columnIndex);
				cursor.close();
				showPicture();
				tip.setText("更换图片");
			} catch (Exception e) {
				// TODO: handle exception   
				e.printStackTrace();
			}
			break;
		}
	}

	/**
	 * 显示正在旋转的磁带
	 */
	private void showRecordingTape() {
		tapeView.setVisibility(View.VISIBLE);
		tape.setImageResource(R.drawable.sel_tape_btn_recording);

		final Animation operatingAnim = AnimationUtils.loadAnimation(PublishTimeCapActivity.this, R.anim.record);
		operatingAnim.setInterpolator(new LinearInterpolator());

		if (operatingAnim != null) {
			progressImage1.startAnimation(operatingAnim);
			progressImage2.startAnimation(operatingAnim);

		}
	}

	/**
	 * 关闭正在旋转的磁带
	 */
	private void shutRecordingTape() {
		tapeView.setVisibility(View.GONE);
		tape.setImageResource(R.drawable.sel_tabp_btn);
		progressImage1.clearAnimation();
		progressImage2.clearAnimation();
	}

	/**
	 * 上传时间胶囊记录
	 */
	private void publish() {
		File audioFile = null;
		File photoFile = null;
		if (!TextUtils.isEmpty(audioPath)) {
			audioFile = new File(audioPath);
		}
		if (!TextUtils.isEmpty(photoUri)) {
			photoFile = new File(photoUri);
		}

		RequestParams params = new RequestParams();
		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {
			Dialog dialog;

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
				dialog = showProgressDialog("正在上传...");
				dialog.setCancelable(false);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					ToastTool.showShort(PublishTimeCapActivity.this, "成功！");
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				ToastTool.showShort(PublishTimeCapActivity.this, "失败！");
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
		params.put(UserTable.U_STATEID, userPreference.getU_stateid());
		params.put(LoverTimeCapsuleTable.LTC_LOCATION, userPreference.getU_address());
		params.put(LoverTimeCapsuleTable.LTC_USERID, userPreference.getU_id());
		params.put(LoverTimeCapsuleTable.LTC_LOVERID, friendPreference.getF_id());
		if (!TextUtils.isEmpty(audioPath)) {
			params.put(LoverTimeCapsuleTable.LTC_VOICE_LENGTH, getRecordLength());
		}

		try {
			if (photoFile != null && photoFile.exists()) {
				params.put(LoverTimeCapsuleTable.LTC_PHOTO, photoFile);
			}
			if (audioFile != null && audioFile.exists()) {
				params.put(LoverTimeCapsuleTable.LTC_VOICE, audioFile);
			}
		} catch (FileNotFoundException e) {
			// TODO: handle exception
			LogTool.d("发布时间胶囊文件不存在！" + e.getMessage());

		}
		AsyncHttpClientImageSound.post("capsuleimagesound", params, responseHandler);
	}
}
