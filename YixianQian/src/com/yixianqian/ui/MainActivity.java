package com.yixianqian.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.baidu.android.pushservice.PushManager;
import com.yixianqian.R;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.base.BaseFragmentActivity;
import com.yixianqian.customewidget.MyAlertDialog;
import com.yixianqian.utils.UserPreference;

/**
 * �����ƣ�MainActivity
 * ����������ҳ�棬��������Fragment��һ���������Fragment��һ���Ǹ������ĵ�Fragment
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��7��16�� ����3:25:58
 *
 */
public class MainActivity extends BaseFragmentActivity {
	private FragmentTabHost mTabHost;
	private View indicator = null;
	private UserPreference userPreference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		userPreference = BaseApplication.getInstance().getUserPreference();

		//���ñ�ǩ
		List<String> tags = new ArrayList<String>();
		tags.add(userPreference.getU_gender());
		PushManager.setTags(this, tags);

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

		// ���tab���ƺ�ͼ��
		indicator = getLayoutInflater().inflate(R.layout.home_indicator, null);
		mTabHost.addTab(mTabHost.newTabSpec("home").setIndicator(indicator), HomeFragment.class, null);
		indicator = getLayoutInflater().inflate(R.layout.personal_indicator, null);
		mTabHost.addTab(mTabHost.newTabSpec("personal").setIndicator(indicator), PersonalFragment.class, null);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		final MyAlertDialog myAlertDialog = new MyAlertDialog(this);
		myAlertDialog.setTitle("��ʾ");
		myAlertDialog.setMessage("�Ƿ��˳�һ��ǣ�ͻ��ˣ�");
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
		myAlertDialog.setPositiveButton("ȷ��", comfirm);
		myAlertDialog.setNegativeButton("ȡ��", cancle);
		myAlertDialog.show();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mTabHost = null;
	}

}
