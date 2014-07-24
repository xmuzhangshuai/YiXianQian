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
 * �����ƣ�QrCodeActivity
 * ���������ҵĶ�ά��
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��7��24�� ����9:34:37
 *
 */
public class QrCodeActivity extends BaseActivity {
	private ImageView qrImageView;
	private UserPreference userPreference;
	Bitmap qrCodeBitmap = null;
	private ImageView topNavLeftBtn;//��������߰�ť
	private TextView topNavText;//����������
	private View right_btn_bg;
	private ImageView headImageView;//ͷ��
	private TextView nameTextView;//����
	private TextView provinceTextView;//ʡ��
	private TextView schoolTextView;//ѧУ

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
		topNavText.setText("�ҵĶ�ά��");

		if (!TextUtils.isEmpty(userPreference.getU_tel())) {

			try {
				qrCodeBitmap = EncodingHandler.createQRCode(userPreference.getU_tel(), 800);
				qrImageView.setImageBitmap(qrCodeBitmap);
			} catch (WriterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		//����ͷ��
		if (!TextUtils.isEmpty(userPreference.getU_small_avatar())) {
			imageLoader.displayImage(AsyncHttpClientImageSound.getAbsoluteUrl(userPreference.getU_small_avatar()),
					headImageView, ImageLoaderTool.getHeadImageOptions(10));
			//�����ʾ����ͷ��
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
		//����������ʡ�ݡ���ѧУ
		//������ʾ��ʵ����
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
