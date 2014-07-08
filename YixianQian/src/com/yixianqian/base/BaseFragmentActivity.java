package com.yixianqian.base;


import com.yixianqian.R;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;


/**   
*    
* ��Ŀ���ƣ�ExamHelper   
* �����ƣ�BaseFragmentActivity   
* ��������   �Զ���ļ̳���FragmentActivity�ĳ�����࣬createʱ���ӵ���ջ�ڡ�
*          ��������ĿActionBar�Ļ�����ʽ
*          �Զ�����findViewById()��initView()�������󷽷��������б��븲��ʵ�֣�ȷ������淶
*          �Զ����˼�����ݴ�Activity�ķ���
*          �Զ�����ShowDialog��������Activity�������֮ǰ����ʾ
* �����ˣ���˧  
* ����ʱ�䣺2014-1-5 ����10:31:59   
* �޸��ˣ���˧   
* �޸�ʱ�䣺2014-1-5 ����10:31:59   
* �޸ı�ע��   
* @version    
*    
*/
public abstract class BaseFragmentActivity extends FragmentActivity {
	public static final String TAG = BaseFragmentActivity.class.getSimpleName();
	protected Handler mHandler = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		AppManager.getInstance().addActivity(this);
//		PushAgent.getInstance(this).onAppStart();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
//		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		MobclickAgent.onResume(this);
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
//			actionBar.setDisplayHomeAsUpEnabled(true);
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

	/**���ؽ�����
	 * @return 
	 * */
	@SuppressLint("ShowToast")
	public Dialog showProgressDialog() {
		ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage("���Ժ�����Ŭ������...");
		// dialog.setCancelable(false);
		dialog.show();
		return dialog;
	}

}