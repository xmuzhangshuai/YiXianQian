package com.yixianqian.customewidget;

import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.yixianqian.R;

public class MyAlertDialog {
	Context context;
	android.app.AlertDialog ad;
	TextView titleView;
	TextView messageView;
	TextView positiveButton;
	TextView negativeButton;

	public MyAlertDialog(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		ad = new android.app.AlertDialog.Builder(context).create();
		ad.show();
		//�ؼ������������,ʹ��window.setContentView,�滻�����Ի��򴰿ڵĲ���
		Window window = ad.getWindow();
		window.setContentView(R.layout.alert_dialog);
		titleView = (TextView) window.findViewById(R.id.title);
		messageView = (TextView) window.findViewById(R.id.message);
		negativeButton = (TextView) window.findViewById(R.id.cancle);
		positiveButton = (TextView) window.findViewById(R.id.confirm);
	}

	public void setTitle(int resId) {
		titleView.setText(resId);
	}

	public void setTitle(String title) {
		titleView.setText(title);
	}

	public void setMessage(int resId) {
		messageView.setText(resId);
	}

	public void setMessage(String message) {
		messageView.setText(message);
	}

	/**
	 * ���ð�ť
	 * @param text
	 * @param listener
	 */
	public void setPositiveButton(String text, final View.OnClickListener listener) {
		positiveButton.setText(text);
		positiveButton.setOnClickListener(listener);
	}

	/**
	 * ���ð�ť
	 * @param text
	 * @param listener
	 */
	public void setNegativeButton(String text, final View.OnClickListener listener) {
		negativeButton.setText(text);
		negativeButton.setOnClickListener(listener);
	}

	/**
	 * �رնԻ���
	 */
	public void dismiss() {
		ad.dismiss();
	}

	public void show() {
		ad.show();
	}
}
