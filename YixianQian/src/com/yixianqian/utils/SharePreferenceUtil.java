package com.yixianqian.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.yixianqian.table.UserTable;

/**
 * �����ƣ�SharePreferenceUtil
 * ��������SharedPreferences��һ�������࣬����setParam���ܱ���String, Integer, Boolean, Float, Long���͵Ĳ��� 
 * 		     ͬ������getParam���ܻ�ȡ���������ֻ���������� 
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��7��13�� ����9:00:37
 *
 */
public class SharePreferenceUtil {
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	public static final String USE_COUNT = "count";// ��¼���ʹ�ô���
//	public static final String TODAY_RECOMMEND = "today_recommend";//��¼�����Ƿ��Ѿ��Ƽ���

	public SharePreferenceUtil(Context context, String file) {
		sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
		editor = sp.edit();
	}

	//��¼�����Ƿ��Ѿ��Ƽ���
	public void setTodayRecommend(String todayString) {
		editor.putString("today", todayString);
		editor.commit();
	}

	public String getTodayRecommend() {
		return sp.getString("today", "");
	}

	//��¼���ʹ�ô���
	public int getUseCount() {
		return sp.getInt("count", 0);
	}

	public void setUseCount(int count) {
		editor.putInt("count", count);
		editor.commit();
	}

}
