package com.yixianqian.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 
 * ��Ŀ���ƣ�Yixianqian
 * �����ƣ�BaseActivity 
 *  �������� �Զ���ļ̳���Activity�ĳ�����࣬createʱ��ӵ���ջ�ڡ�
 * ��������ĿActionBar�Ļ�����ʽ �Զ�����findViewById()��initView()�������󷽷��������б��븲��ʵ�֣�ȷ������淶
 * �Զ����˼�����ݴ�Activity�ķ��� �Զ�����ShowDialog��������Activity�������֮ǰ����ʾ �����ˣ���˧
 * ����ʱ�䣺2014-1-5 ����10:17:18 
 * �޸��ˣ���˧ 
 * �޸�ʱ�䣺2014-1-5 ����10:17:18 
 * �޸ı�ע��
 * 
 * @version
 * 
 */
public abstract class BaseActivity extends Activity {
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	public static final String TAG = BaseActivity.class.getSimpleName();

	protected Handler mHandler = null;

	// дһ���㲥���ڲ��࣬���յ�����ʱ������activity  
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			close();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		AppManager.getInstance().addActivity(this);
		// PushAgent.getInstance(this).onAppStart();

		// ��onCreate��ע��㲥  
		IntentFilter filter = new IntentFilter();
		filter.addAction("close");
		registerReceiver(this.broadcastReceiver, filter); // ע��  
	}

	/** 
	 * �ر� 
	 */
	public void close() {
		Intent intent = new Intent();
		intent.setAction("close"); // ˵������  
		sendBroadcast(intent);// �ú������ڷ��͹㲥  
		finish();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(broadcastReceiver);// ��onDestroyע���㲥��  
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// MobclickAgent.onResume(this);
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	/**
	 * ����ActionBar��ʽ
	 */
	//	@Override
	//	public ActionBar getActionBar() {
	//		// TODO Auto-generated method stub
	//		ActionBar actionBar = super.getActionBar();
	//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	//			// Show the Up button in the action bar.
	////			actionBar.setDisplayHomeAsUpEnabled(true);
	//		}
	//
	//		// actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.title_bar));
	//		// actionBar.setStackedBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_stacked_bg));
	//		// actionBar.setSplitBackgroundDrawable(getResources().getDrawable(R.drawable.title_bar));
	//		actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.system_male));
	//		return actionBar;
	//	}

	/**
	 * �󶨿ؼ�id
	 */
	protected abstract void findViewById();

	/**
	 * ��ʼ���ؼ�
	 */
	protected abstract void initView();

	/**
	 * ͨ����������Activity
	 * 
	 * @param pClass
	 */
	protected void openActivity(Class<?> pClass) {
		openActivity(pClass, null);
	}

	/**
	 * ͨ����������Activity�����Һ���Bundle����
	 * 
	 * @param pClass
	 * @param pBundle
	 */
	protected void openActivity(Class<?> pClass, Bundle pBundle) {
		Intent intent = new Intent(this, pClass);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}
		startActivity(intent);
	}

	/**
	 * ͨ��Action����Activity
	 * 
	 * @param pAction
	 */
	protected void openActivity(String pAction) {
		openActivity(pAction, null);
	}

	/**
	 * ͨ��Action����Activity�����Һ���Bundle����
	 * 
	 * @param pAction
	 * @param pBundle
	 */
	protected void openActivity(String pAction, Bundle pBundle) {
		Intent intent = new Intent(pAction);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}
		startActivity(intent);
	}

	protected void DisPlay(String content) {
		Toast.makeText(this, content, 1).show();
	}

	/** ���ؽ����� **/
	@SuppressLint("ShowToast")
	public Dialog showProgressDialog(String msg) {
		ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage(msg);
		dialog.show();
		return dialog;
	}

}
