package com.yixianqian.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.photoview.PhotoView;
import com.yixianqian.utils.LogTool;

/**
 * �����ƣ�PublishLBPriviewActivity
 * ������������ȵ�Ź㳡ͼƬԤ��
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��9��12�� ����11:01:55
 *
 */
public class PublishLBPriviewActivity extends BaseActivity {

	private PhotoView imageView;
	private String url;
	public static final String SHOW_BIG_IMAGE = "showBigImage";
	private View right_btn_bg;
	private View left_btn_bg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_publish_lb_preview);
		url = getIntent().getStringExtra(SHOW_BIG_IMAGE);
		LogTool.i(url);

		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		imageView = (PhotoView) findViewById(R.id.imageview);
		left_btn_bg = findViewById(R.id.left_btn_bg);
		right_btn_bg = (View) findViewById(R.id.right_btn_bg);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		imageLoader.displayImage(url, imageView, getImageOptions());
		left_btn_bg.setOnClickListener(new OnClickListener() {

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
				setResult(Activity.RESULT_OK);
				finish();
			}
		});
	}

	/**
	 * ��ͨͼƬ����
	 * @return
	 */
	protected DisplayImageOptions getImageOptions() {
		DisplayImageOptions options = null;
		// ʹ��DisplayImageOptions.Builder()����DisplayImageOptions  
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.loading)// ����ͼƬ�����ڼ���ʾ��ͼƬ
				.showImageForEmptyUri(R.drawable.image_error) // ����ͼƬUriΪ�ջ��Ǵ����ʱ����ʾ��ͼƬ
				.showImageOnFail(R.drawable.image_error) // ����ͼƬ���ػ��������з���������ʾ��ͼƬ
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build(); // �������ù���DisplayImageOption����
		return options;
	}

}
