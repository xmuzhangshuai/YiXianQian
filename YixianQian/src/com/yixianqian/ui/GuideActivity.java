package com.yixianqian.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.yixianqian.R;

/**
 * 类名称：GuideActivity
 * 类描述：引导页面，首次运行进入首次引导页面，GuidePagerActivity;用户没有登录则进入登录/注册页面；用户已经登录过则进入主页面加载页，
 * 期间完成程序的初始化工作。 创建人： 张帅 创建时间：2014-7-4 上午8:32:52
 * 
 */
public class GuideActivity extends Activity {
	public static SharedPreferences countPreferences;// 记录软件使用次数

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);

		// 获取启动的次数
		countPreferences = getSharedPreferences("start_count", 0);
		int count = countPreferences.getInt("count", 0);
		SharedPreferences.Editor localEditor = countPreferences.edit();

		if (count == 0) {// 如果是第一次登陆，则启动向导页面
			startActivity(new Intent(GuideActivity.this, GuidePagerActivity.class));

			// 第一次运行拷贝数据库文件并对数据进行分组
//			new initDataBase().execute();
//			initDeviceInfo();// 初始化设备信息

			localEditor.putInt("count", ++count);// 次数加1
			localEditor.commit();
			finish();
		} else {// 如果不是第一次使用,则不启动向导页面，显示欢迎页面。
			if (false) {//如果是已经登陆过
				setContentView(R.layout.activity_guide);
//				initDeviceInfo();// 初始化设备信息
//				mHandler = new Handler(getMainLooper());
//				findViewById();
//				initView();
			} else {//如果用户没有登录过或者已经注销
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
