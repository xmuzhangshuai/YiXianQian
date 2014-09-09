package com.yixianqian.config;

import java.util.ArrayList;
import java.util.List;

public class Constants {

	//����
	public static final String PACKAGENAME = "com.yixianqian";

	//����������IP
	public static String DoMainName = "http://192.168.1.102/";

	public static class Config {
		//�Ƿ��ڿ���ģʽ
		public static final boolean DEVELOPER_MODE = true;

		//Ĭ��ÿ���Ƽ��û�����
		public static int RECOMMEND_COUNT = 4;

		//������֤��ʱ��Ϊ120s
		public static int AUTN_CODE_TIME = 120;

		//��Ƭ��С����
		public static final int SCALE = 5;

		// �ܹ��ж���ҳ
		public static final int NUM_PAGE = 6;

		// ÿҳ20������,�������һ��ɾ��button
		public static int NUM = 20;

		//ʱ�佺��ÿҳ��ʾ����
		public static int PAGE_NUM = 20;

		//����ÿ��ˢ�¼�¼����
		public static int LOAD_MESSAGE_COUNT = 20;
	}

	public static class UserStateType {
		//����
		public static final int SINGLE = 4;
		//�Ķ�
		public static final int FLIPPER = 3;
		//����
		public static final int LOVER = 2;
		//����
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
		public static final int MESSAGE_TYPE_FLIPPER_REQUEEST = 1;
		//ͬ���Ķ�
		public static final int MESSAGE_TYPE_FLIPPER_TO = 2;
		//����
		public static final int MESSAGE_TYPE_LOVE_REQUEST = 3;
	}

	public static class FlipperStatus {

		/**������**/
		public static final String INVITE = "INVITE";

		/**������*/
		public static final String BEINVITEED = "BEINVITEED";

		/**�Ҿܾ��˶Է�������*/
		public static final String REFUSED = "REFUSED";

		/**���ܾ�*/
		public static final String BEREFUSED = "BEREFUSED";

		/**��ͬ���˶Է�������*/
		public static final String AGREED = "AGREED";

		/**�Է�ͬ��*/
		public static final String BEAGREED = "BEAGREED";

	}

	public static class FlipperType {
		public static final int FROM = 0;//������
		public static final int TO = 1;//���������
	}
}
