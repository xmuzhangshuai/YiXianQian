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
	private String photoUri;//图片地址
	private ImageView photo;//拍照按钮
	private ImageView tape;//录音
	private View tapeView;//旋转磁带
	private ImageView progressImage1;
	private ImageView progressImage2;
	private ImageView topNavLeftBtn;//导航条左边按钮
	private View right_btn_bg;
	private int count = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_publish_time_cap);

		//获取图片地址
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

		//录音
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
