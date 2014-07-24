package com.yixianqian.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.utils.AsyncHttpClientImageSound;
import com.yixianqian.utils.ImageLoaderTool;
import com.yixianqian.utils.UserPreference;
import com.yixianqian.zxing.EncodingHandler;

/**
 * 类名称：QrCodeActivity
 * 类描述：我的二维码
 * 创建人： 张帅
 * 创建时间：2014年7月24日 上午9:34:37
 *
 */
public class QrCodeActivity extends BaseActivity {
	private ImageView qrImageView;
	private UserPreference userPreference;
	Bitmap qrCodeBitmap = null;
	private ImageView topNavLeftBtn;//导航条左边按钮
	private TextView topNavText;//导航条文字
	private View right_btn_bg;
	private ImageView headImageView;//头像
	private TextView nameTextView;//姓名
	private TextView provinceTextView;//省份
	private TextView schoolTextView;//学校

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_qrcode);

		userPreference = BaseApplication.getInstance().getUserPreference();

		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		qrImageView = (ImageView) findViewById(R.id.qrcode);
		topNavLeftBtn = (ImageView) findViewById(R.id.nav_left_btn);
		right_btn_bg = (View) findViewById(R.id.right_btn_bg);
		topNavText = (TextView) findViewById(R.id.nav_text);
		headImageView = (ImageView) findViewById(R.id.head_image);
		nameTextView = (TextView) findViewById(R.id.name);
		provinceTextView = (TextView) findViewById(R.id.province);
		schoolTextView = (TextView) findViewById(R.id.school);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		right_btn_bg.setVisibility(View.GONE);
		topNavText.setText("我的二维码");

		if (!TextUtils.isEmpty(userPreference.getU_tel())) {

			try {
				qrCodeBitmap = EncodingHandler.createQRCode(userPreference.getU_tel(), 800);
				qrImageView.setImageBitmap(qrCodeBitmap);
			} catch (WriterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		//设置头像
		if (!TextUtils.isEmpty(userPreference.getU_small_avatar())) {
			imageLoader.displayImage(AsyncHttpClientImageSound.getAbsoluteUrl(userPreference.getU_small_avatar()),
					headImageView, ImageLoaderTool.getHeadImageOptions(10));
			//点击显示高清头像
			headImageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(QrCodeActivity.this, ImageShowerActivity.class);
					intent.putExtra(ImageShowerActivity.SHOW_BIG_IMAGE,
							AsyncHttpClientImageSound.getAbsoluteUrl(userPreference.getU_large_avatar()));
					startActivity(intent);
					QrCodeActivity.this.overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
				}
			});
		}
		//设置姓名、省份、及学校
		//优先显示真实姓名
		String name = userPreference.getU_nickname();
		if (userPreference.getU_realname() != null) {
			if (userPreference.getU_realname().length() > 0) {
				name = userPreference.getU_realname();
			}
		}
		nameTextView.setText(name);
		provinceTextView.setText(userPreference.getProvinceName());
		schoolTextView.setText(userPreference.getSchoolName());

		topNavLeftBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (qrCodeBitmap != null) {
			qrCodeBitmap.recycle();
		}
		super.onDestroy();
	}
}
