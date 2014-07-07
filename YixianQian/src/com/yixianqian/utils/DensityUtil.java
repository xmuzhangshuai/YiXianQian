package com.yixianqian.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

/**
 * 
 * ��Ŀ���ƣ�ExamHelper 
 * �����ƣ�DensityUtil 
 * �������� �����࣬��ȡ��Ļ�ֱ��ʡ�����dp��px��װ�� 
 * �����ˣ���˧  
 * ����ʱ�䣺2013-12-5 ����18:45:21 
 * �޸��ˣ���˧   
 * �޸�ʱ�䣺2013-12-15 ����3:56:28 
 * �޸ı�ע��
 * 
 * @version
 * 
 */
public class DensityUtil {
	private static DisplayMetrics dm;
	private static WindowManager wm;
	private static int ScreenWidth;// ��Ļ���
	private static int ScreenHeight;// ��Ļ�߶�

	/**
	 * �����ֻ��ķֱ��ʴ� dp �ĵ�λ ת��Ϊ px(����)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * �����ֻ��ķֱ��ʴ� dp �ĵ�λ ת��Ϊ px(����)
	 */
	public static int dpToPx(Resources res, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
	}

	/**
	 * �����ֻ��ķֱ��ʴ� px(����) �ĵ�λ ת��Ϊ dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * ��ȡ�ֻ���Ļ���ֵ����λpx
	 */
	public static int getScreenWidthforPX(Context context) {
		dm = new DisplayMetrics();
		wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		ScreenWidth = dm.widthPixels;// ��Ļ���
		return ScreenWidth;
	}

	/**
	 * ��ȡ�ֻ���Ļ�߶�ֵ����λpx
	 */
	public static int getScreenHeightforPX(Context context) {
		dm = new DisplayMetrics();
		wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		ScreenHeight = dm.heightPixels;// ��Ļ�߶�
		return ScreenHeight;
	}

	/**
	 * ��ȡ�ֻ���Ļ���ֵ����λdp
	 */
	public static int getScreenWidthforDP(Context context) {
		ScreenWidth = getScreenWidthforPX(context);
		ScreenWidth = DensityUtil.px2dip(context, ScreenWidth);
		return ScreenWidth;
	}

	/**
	 * ��ȡ�ֻ���Ļ�߶�ֵ����λdp
	 */
	public static int getScreenHeightforDP(Context context) {
		ScreenHeight = getScreenHeightforPX(context);
		ScreenHeight = DensityUtil.px2dip(context, ScreenHeight);
		return ScreenHeight;
	}

	/**
	 * ��ȡ�ֻ�״̬���߶�
	 * 
	 * @param context
	 * @return
	 */
	public static int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		java.lang.reflect.Field field = null;
		int x = 0;
		int statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
			return statusBarHeight;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusBarHeight;
	}
}
