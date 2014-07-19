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
 * �����ƣ�MainActivity
 * ����������ҳ�棬��������Fragment��һ���������Fragment��һ���Ǹ������ĵ�Fragment
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��7��16�� ����3:25:58
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

		//�����ٶ����ͷ���
		PushManager.startWork(MainActivity.this, PushConstants.LOGIN_TYPE_API_KEY, Constants.BaiduPushConfig.API_KEY);
		// ���ڵ���λ�����ͣ����Դ�֧�ֵ���λ�õ����͵Ŀ���
		PushManager.enableLbs(getApplicationContext());
		//���ñ�ǩ
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
	 * ��ʼ��֪ͨ��ʽ
	 */
	private void initNotification() {
		// Push: �����Զ����֪ͨ��ʽ������API���ܼ��û��ֲᣬ�����ʹ��ϵͳĬ�ϵĿ��Բ�����δ���
		// ����֪ͨ���ͽ����У��߼�����->֪ͨ����ʽ->�Զ�����ʽ��ѡ�в�����дֵ��1��
		// ���·������� PushManager.setNotificationBuilder(this, 1, cBuilder)�еĵڶ���������Ӧ
	}
}
