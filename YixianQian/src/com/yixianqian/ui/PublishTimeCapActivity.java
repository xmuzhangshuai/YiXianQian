package com.yixianqian.ui;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.config.DefaultKeys;
import com.yixianqian.customewidget.MyRecorder;
import com.yixianqian.utils.ImageTools;

public class PublishTimeCapActivity extends BaseActivity {
	private ImageView capsuleImage;
	private String photoUri;//图片地址
	//	private ImageView photo;//拍照按钮
	private ImageView tape;//录音
	private View tapeView;//旋转磁带
	private ImageView progressImage1;
	private ImageView progressImage2;
	private ImageView topNavLeftBtn;//导航条左边按钮
	private View nullImage;
	private ImageView play;
	private TextView tip;
	private View record_view;
	private View right_btn_bg;
	private int count = 0;
	private String type;//类型，用于判断先拍照还是先录音
	private String audioPath;
	private MyRecorder myRecorder;
	private MediaPlayer mPlayer;
	private int playCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_publish_time_cap);
		mPlayer = new MediaPlayer();
		myRecorder = new MyRecorder("audio");

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
					if (count % 2 == 0) {
						showRecordingTape();
						myRecorder.beginRecord();
					} else {
						shutRecordingTape();
						myRecorder.stopRecord();
						audioPath = myRecorder.getPath();
						showRecord();
					}
					count++;
				}
			});
		}

		/****************如果是已经录音********************/
		else if (type.equals("audio")) {
			capsuleImage.setVisibility(View.GONE);
			record_view.setVisibility(View.VISIBLE);
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
			}
		});
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
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
}
