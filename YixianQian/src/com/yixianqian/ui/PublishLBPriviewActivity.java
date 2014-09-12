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
 * 类名称：PublishLBPriviewActivity
 * 类描述：发布鹊桥广场图片预览
 * 创建人： 张帅
 * 创建时间：2014年9月12日 上午11:01:55
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
	 * 普通图片设置
	 * @return
	 */
	protected DisplayImageOptions getImageOptions() {
		DisplayImageOptions options = null;
		// 使用DisplayImageOptions.Builder()创建DisplayImageOptions  
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.loading)// 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.image_error) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.image_error) // 设置图片加载或解码过程中发生错误显示的图片
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build(); // 创建配置过得DisplayImageOption对象
		return options;
	}

}
