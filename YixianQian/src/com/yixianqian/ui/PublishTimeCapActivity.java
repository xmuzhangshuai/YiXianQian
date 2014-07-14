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
	private String photoUri;//ͼƬ��ַ
	//	private ImageView photo;//���հ�ť
	private ImageView tape;//¼��
	private View tapeView;//��ת�Ŵ�
	private ImageView progressImage1;
	private ImageView progressImage2;
	private ImageView topNavLeftBtn;//��������߰�ť
	private View nullImage;
	private ImageView play;
	private TextView tip;
	private View record_view;
	private View right_btn_bg;
	private int count = 0;
	private String type;//���ͣ������ж������ջ�����¼��
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
			//��ȡ��Ƶ��ַ
			audioPath = getIntent().getStringExtra(DefaultKeys.PHOTO_URI);

		} else if (type.equals("picture")) {
			//��ȡͼƬ��ַ
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

		/****************������Ѿ�����********************/
		if (type.equals("picture")) {
			record_view.setVisibility(View.INVISIBLE);
			tip.setText("���һ��¼��");
			showPicture();

			//¼��
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

		/****************������Ѿ�¼��********************/
		else if (type.equals("audio")) {
			capsuleImage.setVisibility(View.GONE);
			record_view.setVisibility(View.VISIBLE);
			tape.setImageResource(R.drawable.sel_photo_btn);
			nullImage.setVisibility(View.VISIBLE);
			tip.setText("���һ��ͼƬ");

			//����
			tape.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showPicturePicker(PublishTimeCapActivity.this);
				}
			});

		}

		//�������
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
	 * ��ʾͼƬ
	 */
	public void showPicture() {
		/** 
		 * ��ȡͼƬ����ת�Ƕȣ���Щϵͳ�����յ�ͼƬ��ת�ˣ��е�û����ת 
		 */
		int degree = ImageTools.readPictureDegree(photoUri);

		//��ȡ����ͼ��ʾ����Ļ��
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 2;
		Bitmap cbitmap = BitmapFactory.decodeFile(photoUri, opts);

		/** 
		 * ��ͼƬ��תΪ���ķ��� 
		 */
		Bitmap newbitmap = ImageTools.rotaingImageView(degree, cbitmap);
		capsuleImage.setImageBitmap(newbitmap);
		capsuleImage.setVisibility(View.VISIBLE);
		nullImage.setVisibility(View.GONE);
	}

	/**
	 * ��ʾ¼��
	 */
	public void showRecord() {
		record_view.setVisibility(View.VISIBLE);
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
						takePhoto();// �û�����˴��������ȡ
					}
					break;
				case 1:
					choosePhoto();// �������ȥ��ȡ
					break;

				default:
					break;
				}
			}
		});
		builder.create().show();
	}

	/**
	 * �����ѡ��ͼƬ
	 */
	private void choosePhoto() {
		Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, 2);
	}

	/**
	 * ����
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
		case 1://����
			showPicture();
			tip.setText("����ͼƬ");
			break;
		case 2://�����ѡ��
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
				tip.setText("����ͼƬ");
			} catch (Exception e) {
				// TODO: handle exception   
				e.printStackTrace();
			}
			break;
		}
	}

	/**
	 * ��ʾ������ת�ĴŴ�
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
	 * �ر�������ת�ĴŴ�
	 */
	private void shutRecordingTape() {
		tapeView.setVisibility(View.GONE);
		tape.setImageResource(R.drawable.sel_tabp_btn);
		progressImage1.clearAnimation();
		progressImage2.clearAnimation();
	}
}
