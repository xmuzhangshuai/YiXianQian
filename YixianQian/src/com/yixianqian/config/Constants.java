package com.yixianqian.config;

import java.util.ArrayList;
import java.util.List;

public class Constants {

	/**
	 ******************************************* 参数设置信息结束 ******************************************
	 */

	public static class Config {
		public static final boolean DEVELOPER_MODE = false;

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
		public static final int MESSAGE_TYPE_FLIPPER_REQUEEST = 6;
		//同意心动
		public static final int MESSAGE_TYPE_FLIPPER_TO = 7;
		//情侣
		public static final int MESSAGE_TYPE_LOVER = 8;
	}

	public static class FlipperStatus {
		/**我邀请**/
		public static final String INVITE = "INVITE";

		/**被邀请*/
		public static final String BEINVITEED = "BEINVITEED";

		/**被拒绝*/
		public static final String BEREFUSED = "BEREFUSED";

		/**对方同意*/
		public static final String BEAGREED = "BEAGREED";

		/**对方申请*/
		public static final String BEAPPLYED = "BEAPPLYED";

		/**我同意了对方的请求*/
		public static final String AGREED = "AGREED";

		/**我拒绝了对方的请求*/
		public static final String REFUSED = "REFUSED";

	}

	public static class FlipperType {
		public static final int FROM = 0;//被邀请
		public static final int TO = 1;//我邀请别人
	}

	//星座
	public static class Constell {
		public static final String Aries = "白羊座";
		public static final String Taurus = "金牛座";
		public static final String Gemini = "双子座";
		public static final String Cancer = "巨蟹座";
		public static final String Leo = "狮子座";
		public static final String Virgo = "处女座";
		public static final String Libra = "天秤座";
		public static final String Scorpius = "天蝎座";
		public static final String Sagittarius = "射手座";
		public static final String Capricorn = "摩羯座";
		public static final String Aquarius = "水瓶座";
		public static final String Pisces = "双鱼座";

		public static List<String> getConstellList() {
			List<String> list = new ArrayList<String>();
			list.add(Aries);
			list.add(Taurus);
			list.add(Gemini);
			list.add(Cancer);
			list.add(Leo);
			list.add(Virgo);
			list.add(Libra);
			list.add(Scorpius);
			list.add(Sagittarius);
			list.add(Capricorn);
			list.add(Aquarius);
			list.add(Pisces);
			return list;
		}
	}

}
