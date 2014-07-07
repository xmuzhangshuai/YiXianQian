package com.yixianqian.utils;

import android.view.View;
import android.view.ViewGroup;

/**
 * �����ƣ�ViewTools
 * ����������ͼ������
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��7��5�� ����4:55:24
 *
 */
public class ViewTools {

	/**
	 * ������ͼmargin
	 * @param v
	 * @param l
	 * @param t
	 * @param r
	 * @param b
	 */
	public static void setMargins(View v, int l, int t, int r, int b) {
		if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
			ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
			p.setMargins(l, t, r, b);
			v.requestLayout();
		}
	}
}
