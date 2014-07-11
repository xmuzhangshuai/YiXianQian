package com.yixianqian.utils;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.yixianqian.R;

public class ImageLoaderTool {
	/**
	 * 头像设置
	 * @return
	 */
	public static DisplayImageOptions getHeadImageOptions(int corner) {
		DisplayImageOptions options = null;
		// 使用DisplayImageOptions.Builder()创建DisplayImageOptions  
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.photoconor)// 设置图片下载期间显示的图片  
				.showImageForEmptyUri(R.drawable.default_hedad_iamge) // 设置图片Uri为空或是错误的时候显示的图片  
				.showImageOnFail(R.drawable.default_hedad_iamge) // 设置图片加载或解码过程中发生错误显示的图片      
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中  
				.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中  
				.displayer(new RoundedBitmapDisplayer(corner)) // 设置成圆角图片  
				.build(); // 创建配置过得DisplayImageOption对象 
		return options;
	}

	/**
	 * 头像设置
	 * @return
	 */
	public static DisplayImageOptions getHeadImageOptions() {
		DisplayImageOptions options = null;
		// 使用DisplayImageOptions.Builder()创建DisplayImageOptions  
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.photoconor)// 设置图片下载期间显示的图片  
				.showImageForEmptyUri(R.drawable.default_hedad_iamge) // 设置图片Uri为空或是错误的时候显示的图片  
				.showImageOnFail(R.drawable.default_hedad_iamge) // 设置图片加载或解码过程中发生错误显示的图片      
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中  
				.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中  
				.build(); // 创建配置过得DisplayImageOption对象 
		return options;
	}

	/**
	 * 普通图片设置
	 * @return
	 */
	public static DisplayImageOptions getImageOptions() {
		DisplayImageOptions options = null;
		// 使用DisplayImageOptions.Builder()创建DisplayImageOptions  
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.loading)// 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.image_error) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.image_error) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build(); // 创建配置过得DisplayImageOption对象
		return options;
	}

	/**
	 * 普通图片设置
	 * @return
	 */
	public static DisplayImageOptions getChooseOptions() {
		DisplayImageOptions options = null;
		// 使用DisplayImageOptions.Builder()创建DisplayImageOptions  
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.loading)// 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.image_error) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.image_error) // 设置图片加载或解码过程中发生错误显示的图片
				.build(); // 创建配置过得DisplayImageOption对象
		return options;
	}
}
