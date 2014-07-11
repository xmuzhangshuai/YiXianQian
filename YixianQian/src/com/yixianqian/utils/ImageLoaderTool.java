package com.yixianqian.utils;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.yixianqian.R;

public class ImageLoaderTool {
	/**
	 * ͷ������
	 * @return
	 */
	public static DisplayImageOptions getHeadImageOptions(int corner) {
		DisplayImageOptions options = null;
		// ʹ��DisplayImageOptions.Builder()����DisplayImageOptions  
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.photoconor)// ����ͼƬ�����ڼ���ʾ��ͼƬ  
				.showImageForEmptyUri(R.drawable.default_hedad_iamge) // ����ͼƬUriΪ�ջ��Ǵ����ʱ����ʾ��ͼƬ  
				.showImageOnFail(R.drawable.default_hedad_iamge) // ����ͼƬ���ػ��������з���������ʾ��ͼƬ      
				.cacheInMemory(true) // �������ص�ͼƬ�Ƿ񻺴����ڴ���  
				.cacheOnDisk(true) // �������ص�ͼƬ�Ƿ񻺴���SD����  
				.displayer(new RoundedBitmapDisplayer(corner)) // ���ó�Բ��ͼƬ  
				.build(); // �������ù���DisplayImageOption���� 
		return options;
	}

	/**
	 * ͷ������
	 * @return
	 */
	public static DisplayImageOptions getHeadImageOptions() {
		DisplayImageOptions options = null;
		// ʹ��DisplayImageOptions.Builder()����DisplayImageOptions  
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.photoconor)// ����ͼƬ�����ڼ���ʾ��ͼƬ  
				.showImageForEmptyUri(R.drawable.default_hedad_iamge) // ����ͼƬUriΪ�ջ��Ǵ����ʱ����ʾ��ͼƬ  
				.showImageOnFail(R.drawable.default_hedad_iamge) // ����ͼƬ���ػ��������з���������ʾ��ͼƬ      
				.cacheInMemory(true) // �������ص�ͼƬ�Ƿ񻺴����ڴ���  
				.cacheOnDisk(true) // �������ص�ͼƬ�Ƿ񻺴���SD����  
				.build(); // �������ù���DisplayImageOption���� 
		return options;
	}

	/**
	 * ��ͨͼƬ����
	 * @return
	 */
	public static DisplayImageOptions getImageOptions() {
		DisplayImageOptions options = null;
		// ʹ��DisplayImageOptions.Builder()����DisplayImageOptions  
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.loading)// ����ͼƬ�����ڼ���ʾ��ͼƬ
				.showImageForEmptyUri(R.drawable.image_error) // ����ͼƬUriΪ�ջ��Ǵ����ʱ����ʾ��ͼƬ
				.showImageOnFail(R.drawable.image_error) // ����ͼƬ���ػ��������з���������ʾ��ͼƬ
				.cacheInMemory(true) // �������ص�ͼƬ�Ƿ񻺴����ڴ���
				.cacheOnDisk(true) // �������ص�ͼƬ�Ƿ񻺴���SD����
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build(); // �������ù���DisplayImageOption����
		return options;
	}

	/**
	 * ��ͨͼƬ����
	 * @return
	 */
	public static DisplayImageOptions getChooseOptions() {
		DisplayImageOptions options = null;
		// ʹ��DisplayImageOptions.Builder()����DisplayImageOptions  
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.loading)// ����ͼƬ�����ڼ���ʾ��ͼƬ
				.showImageForEmptyUri(R.drawable.image_error) // ����ͼƬUriΪ�ջ��Ǵ����ʱ����ʾ��ͼƬ
				.showImageOnFail(R.drawable.image_error) // ����ͼƬ���ػ��������з���������ʾ��ͼƬ
				.build(); // �������ù���DisplayImageOption����
		return options;
	}
}
