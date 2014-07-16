package com.yixianqian.config;

import java.util.ArrayList;
import java.util.List;

public class Constants {
	/**
	 ******************************************* 参数设置信息结束 ******************************************
	 */

	public static class Config {
		public static final boolean DEVELOPER_MODE = false;
		public final static String API_KEY = "bcB8dCkZh0GVtMTdpyTTabj3";
		public final static String SECRIT_KEY = "y75ge4lbEEGth1nwbTveiGr7yHARKnm2";
	}

	public static class Extra {
		public static final String IMAGES = "com.nostra13.example.universalimageloader.IMAGES";
		public static final String IMAGE_POSITION = "com.nostra13.example.universalimageloader.IMAGE_POSITION";
	}

	public static class Tags {
		public static final String MALE = "男";
		public static final String FEMALE = "女";
	}

	public static List<String> getTags() {
		List<String> tList = new ArrayList<String>();
		tList.add(Tags.MALE);
		tList.add(Tags.FEMALE);
		return tList;
	}
}
