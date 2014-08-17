package com.yixianqian.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

import com.yixianqian.R;
import com.yixianqian.base.BaseFragmentActivity;

/**
 * �����ƣ�SettingActivity
 * ������������ҳ��
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��8��17�� ����4:30:12
 *
 */
public class SettingActivity extends BaseFragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setting);

		findViewById();
		initView();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		/**********����������ҳ��**********/
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		SettingMainFragment settingMainFragment = new SettingMainFragment();
		fragmentTransaction.add(R.id.container, settingMainFragment);
		fragmentTransaction.commit();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub

	}

}
