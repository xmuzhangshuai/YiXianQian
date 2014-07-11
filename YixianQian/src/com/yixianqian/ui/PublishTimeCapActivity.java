package com.yixianqian.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.config.DefaultKeys;
import com.yixianqian.utils.ImageTools;

public class PublishTimeCapActivity extends BaseActivity {
	private ImageView capsuleImage;
	private String photoUri;//ͼƬ��ַ
	private ImageView photo;//���հ�ť
	private ImageView tape;//¼��
	private View tapeView;//��ת�Ŵ�
	private ImageView progressImage1;
	private ImageView progressImage2;
	private ImageView topNavLeftBtn;//��������߰�ť
	private View right_btn_bg;
	private int count = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_publish_time_cap);

		//��ȡͼƬ��ַ
		photoUri = getIntent().getStringExtra(DefaultKeys.PHOTO_URI);

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
		photo = (ImageView) findViewById(R.id.photo);
		tape = (ImageView) findViewById(R.id.tape);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
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

		//¼��
		tape.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (count % 2 == 0) {
					showRecordingTape();
				} else {
					shutRecordingTape();
				}
				count++;
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
