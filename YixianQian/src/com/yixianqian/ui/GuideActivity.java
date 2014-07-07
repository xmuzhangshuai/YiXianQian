package com.yixianqian.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.yixianqian.R;

/**
 * �����ƣ�GuideActivity
 * ������������ҳ�棬�״����н����״�����ҳ�棬GuidePagerActivity;�û�û�е�¼������¼/ע��ҳ�棻�û��Ѿ���¼���������ҳ�����ҳ��
 * �ڼ���ɳ���ĳ�ʼ�������� �����ˣ� ��˧ ����ʱ�䣺2014-7-4 ����8:32:52
 * 
 */
public class GuideActivity extends Activity {
	public static SharedPreferences countPreferences;// ��¼���ʹ�ô���

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);

		// ��ȡ�����Ĵ���
		countPreferences = getSharedPreferences("start_count", 0);
		int count = countPreferences.getInt("count", 0);
		SharedPreferences.Editor localEditor = countPreferences.edit();

		if (count == 0) {// ����ǵ�һ�ε�½����������ҳ��
			startActivity(new Intent(GuideActivity.this, GuidePagerActivity.class));

			// ��һ�����п������ݿ��ļ��������ݽ��з���
//			new initDataBase().execute();
//			initDeviceInfo();// ��ʼ���豸��Ϣ

			localEditor.putInt("count", ++count);// ������1
			localEditor.commit();
			finish();
		} else {// ������ǵ�һ��ʹ��,��������ҳ�棬��ʾ��ӭҳ�档
			if (false) {//������Ѿ���½��
				setContentView(R.layout.activity_guide);
//				initDeviceInfo();// ��ʼ���豸��Ϣ
//				mHandler = new Handler(getMainLooper());
//				findViewById();
//				initView();
			} else {//����û�û�е�¼�������Ѿ�ע��
				startActivity(new Intent(GuideActivity.this, LoginOrRegisterActivity.class));
			}
			localEditor.putInt("count", ++count);
			localEditor.commit();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

}
