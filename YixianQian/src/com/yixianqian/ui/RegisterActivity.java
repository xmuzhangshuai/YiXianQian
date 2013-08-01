package com.yixianqian.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.yixianqian.R;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.base.BaseFragmentActivity;
import com.yixianqian.customewidget.MyAlertDialog;

/**
 * �����ƣ�RegisterActivity
 * ��������ע��ҳ��
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��7��6�� ����7:24:39
 *
 */
public class RegisterActivity extends BaseFragmentActivity {

	private FragmentTransaction fragmentTransaction;
	private FragmentManager fragmentManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);

		findViewById();
		initView();

		Fragment fragment = new RegGenderFragment();
		fragmentManager = getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out, R.anim.push_right_in,
				R.anim.push_right_out);
		fragmentTransaction.replace(R.id.fragment_container, fragment);
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

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		vertifyToTerminate();
	}

	/**
	 * ȷ����ֹע��
	 */
	private void vertifyToTerminate() {
		final MyAlertDialog dialog = new MyAlertDialog(RegisterActivity.this);
		dialog.setTitle("��ʾ");
		dialog.setMessage("ע��������˳�����Ϣ�����ܱ��档�Ƿ�����˳���");
		View.OnClickListener comfirm = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				finish();
				BaseApplication.getInstance().getUserPreference().clear();
			}
		};
		View.OnClickListener cancle = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		};
		dialog.setPositiveButton("ȷ��", comfirm);
		dialog.setNegativeButton("ȡ��", cancle);
		dialog.show();
	}
}
