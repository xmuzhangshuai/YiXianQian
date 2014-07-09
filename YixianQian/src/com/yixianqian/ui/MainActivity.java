package com.yixianqian.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.View;
import android.view.Window;

import com.yixianqian.R;
import com.yixianqian.base.BaseFragmentActivity;

public class MainActivity extends BaseFragmentActivity {
	private FragmentTabHost mTabHost;
	private View indicator = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		findViewById();
		initView();

	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub

		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

		// 添加tab名称和图标
		indicator = getLayoutInflater().inflate(R.layout.home_indicator, null);
		mTabHost.addTab(mTabHost.newTabSpec("home").setIndicator(indicator), HomeFragment.class, null);

		indicator = getLayoutInflater().inflate(R.layout.personal_indicator, null);
		mTabHost.addTab(mTabHost.newTabSpec("personal").setIndicator(indicator), PersonalFragment.class, null);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mTabHost = null;
	}
}
