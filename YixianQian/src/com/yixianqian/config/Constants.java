package com.yixianqian.config;

import java.util.ArrayList;
import java.util.List;

public class Constants {

	//包名
	public static final String PACKAGENAME = "com.yixianqian";

	//域名或者是IP
	public static String DoMainName = "http://192.168.1.102/";

	public static class Config {
		//是否处于开发模式
		public static final boolean DEVELOPER_MODE = true;

		//默认每天推荐用户数量
		public static int RECOMMEND_COUNT = 4;

		//接受验证码时间为120s
		public static int AUTN_CODE_TIME = 120;

		//照片缩小比例
		public static final int SCALE = 5;

		// 总共有多少页
		public static final int NUM_PAGE = 6;

		// 每页20个表情,还有最后一个删除button
		public static int NUM = 20;

		//时间胶囊每页显示条数
		public static int PAGE_NUM = 20;

		//聊天每次刷新纪录条数
		public static int LOAD_MESSAGE_COUNT = 20;
	}

	public static class UserStateType {
		//单身
		public static final int SINGLE = 4;
		//心动
		public static final int FLIPPER = 3;
		//情侣
		public static final int LOVER = 2;
		//冻结
		public static final int FREEZE = 1;
	}

	public static class BaiduPushConfig {
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

	public static class Gender {
		public static final String MALE = "男";
		public static final String FEMALE = "女";
	}

	public static List<String> getTags() {
		List<String> tList = new ArrayList<String>();
		tList.add(Tags.MALE);
		tList.add(Tags.FEMALE);
		return tList;
	}

	public static class PersonDetailType {
		//心动
		public static final int SINGLE = 1;
		//同意心动
		public static final int FLIPPER = 2;
		//情侣
		public static final int LOVER = 3;
	}

	public static class MessageType {
		//心动
		public static final int MESSAGE_TYPE_FLIPPER_REQUEEST = 1;
		//同意心动
		public static final int MESSAGE_TYPE_FLIPPER_TO = 2;
		//情侣
		public static final int MESSAGE_TYPE_LOVE_REQUEST = 3;
	}

	public static class FlipperStatus {

		/**我邀请**/
		public static final String INVITE = "INVITE";

		/**被邀请*/
		public static final String BEINVITEED = "BEINVITEED";

		/**我拒绝了对方的请求*/
		public static final String REFUSED = "REFUSED";

		/**被拒绝*/
		public static final String BEREFUSED = "BEREFUSED";

		/**我同意了对方的请求*/
		public static final String AGREED = "AGREED";

		/**对方同意*/
		public static final String BEAGREED = "BEAGREED";

	}

	public static class FlipperType {
		public static final int FROM = 0;//被邀请
		public static final int TO = 1;//我邀请别人
	}
}
