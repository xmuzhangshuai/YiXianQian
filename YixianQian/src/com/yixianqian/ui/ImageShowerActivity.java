package com.yixianqian.ui;

import android.os.Bundle;
import android.view.Window;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.photoview.PhotoView;

/**
 * �����ƣ�ImageShowerActivity
 * �����������ͼƬ��ȫ����ʾͷ��
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��7��23�� ����9:39:47
 *
 */
public class ImageShowerActivity extends BaseActivity {
	public static final String SHOW_BIG_IMAGE = "showBigImage";
	public static final String CACHE = "cache_on_memory";
	private PhotoView imageView;
	private String url;
	private boolean cacahe = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_image_shower);

		url = getIntent().getStringExtra(SHOW_BIG_IMAGE);
		cacahe = getIntent().getBooleanExtra(CACHE, true);
		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		imageView = (PhotoView) findViewById(R.id.imageview);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		imageLoader.displayImage(url, imageView, getImageOptions());
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
				.cacheInMemory(cacahe) // �������ص�ͼƬ�Ƿ񻺴����ڴ���
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build(); // �������ù���DisplayImageOption����
		return options;
	}

}
