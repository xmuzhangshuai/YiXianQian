package com.yixianqian.utils;

import android.view.View;
import android.view.ViewGroup;

/**
 * 类名称：ViewTools
 * 类描述：视图工具类
 * 创建人： 张帅
 * 创建时间：2014年7月5日 下午4:55:24
 *
 */
public class ViewTools {

	/**
	 * 设置视图margin
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
