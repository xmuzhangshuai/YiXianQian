package com.yixianqian.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.yixianqian.R;
import com.yixianqian.base.BaseFragmentActivity;
import com.yixianqian.config.Constants;
import com.yixianqian.customewidget.MyAlertDialog;

/**
 * 类名称：MainActivity
 * 类描述：主页面，包括两个Fragment，一个是聊天的Fragment，一个是个人中心的Fragment
 * 创建人： 张帅
 * 创建时间：2014年7月16日 下午3:25:58
 *
 */
public class MainActivity extends BaseFragmentActivity {
	private FragmentTabHost mTabHost;
	private View indicator = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		//开启百度推送服务
		PushManager.startWork(MainActivity.this, PushConstants.LOGIN_TYPE_API_KEY, Constants.BaiduPushConfig.API_KEY);
		// 基于地理位置推送，可以打开支持地理位置的推送的开关
		PushManager.enableLbs(getApplicationContext());
		//设置标签
		PushManager.setTags(this, Constants.getTags());

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
	public void onBackPressed() {
		// TODO Auto-generated method stub
		final MyAlertDialog myAlertDialog = new MyAlertDialog(this);
		myAlertDialog.setTitle("提示");
		myAlertDialog.setMessage("是否退出一线牵客户端？");
		View.OnClickListener comfirm = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myAlertDialog.dismiss();
				close();
			}
		};
		View.OnClickListener cancle = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myAlertDialog.dismiss();
			}
		};
		myAlertDialog.setPositiveButton("确定", comfirm);
		myAlertDialog.setNegativeButton("取消", cancle);
		myAlertDialog.show();

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		PushManager.activityStarted(this);
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		PushManager.activityStoped(this);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mTabHost = null;
	}

	/**
	 * 初始化通知样式
	 */
	private void initNotification() {
		// Push: 设置自定义的通知样式，具体API介绍见用户手册，如果想使用系统默认的可以不加这段代码
		// 请在通知推送界面中，高级设置->通知栏样式->自定义样式，选中并且填写值：1，
		// 与下方代码中 PushManager.setNotificationBuilder(this, 1, cBuilder)中的第二个参数对应
	}
}
