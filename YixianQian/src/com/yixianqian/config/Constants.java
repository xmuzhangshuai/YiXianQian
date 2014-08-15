package com.yixianqian.config;

import java.util.ArrayList;
import java.util.List;

public class Constants {

	/**
	 ******************************************* ����������Ϣ���� ******************************************
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
		public static final String MALE = "��";
		public static final String FEMALE = "Ů";
	}

	public static class Gender {
		public static final String MALE = "��";
		public static final String FEMALE = "Ů";
	}

	public static List<String> getTags() {
		List<String> tList = new ArrayList<String>();
		tList.add(Tags.MALE);
		tList.add(Tags.FEMALE);
		return tList;
	}


	public static class PersonDetailType {
		//�Ķ�
		public static final int SINGLE = 1;
		//ͬ���Ķ�
		public static final int FLIPPER = 2;
		//����
		public static final int LOVER = 3;
	}

	
	public static class MessageType {
		//�Ķ�
		public static final int MESSAGE_TYPE_FLIPPER_REQUEEST = 6;
		//ͬ���Ķ�
		public static final int MESSAGE_TYPE_FLIPPER_TO = 7;
		//����
		public static final int MESSAGE_TYPE_LOVER = 8;
	}

	public static class FlipperStatus {
		/**������**/
		public static final String INVITE = "INVITE";

		/**������*/
		public static final String BEINVITEED = "BEINVITEED";

		/**���ܾ�*/
		public static final String BEREFUSED = "BEREFUSED";

		/**�Է�ͬ��*/
		public static final String BEAGREED = "BEAGREED";

		/**�Է�����*/
		public static final String BEAPPLYED = "BEAPPLYED";

		/**��ͬ���˶Է�������*/
		public static final String AGREED = "AGREED";

		/**�Ҿܾ��˶Է�������*/
		public static final String REFUSED = "REFUSED";

	}

	public static class FlipperType {
		public static final int FROM = 0;//������
		public static final int TO = 1;//���������
	}

	//����
	public static class Constell {
		public static final String Aries = "������";
		public static final String Taurus = "��ţ��";
		public static final String Gemini = "˫����";
		public static final String Cancer = "��з��";
		public static final String Leo = "ʨ����";
		public static final String Virgo = "��Ů��";
		public static final String Libra = "�����";
		public static final String Scorpius = "��Ы��";
		public static final String Sagittarius = "������";
		public static final String Capricorn = "Ħ����";
		public static final String Aquarius = "ˮƿ��";
		public static final String Pisces = "˫����";

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
