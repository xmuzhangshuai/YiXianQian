package com.yixianqian.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**   
*    
* ��Ŀ���ƣ�ExamHelper   
* �����ƣ�NetworkUtils   
* ��������   ���緽��Ĺ�����
* �����ˣ���˧  
* ����ʱ�䣺2013-12-22 ����7:26:43   
* �޸��ˣ���˧    
* �޸�ʱ�䣺2013-12-22 ����7:26:43   
* �޸ı�ע��   
* @version    
*    
*/
public class NetworkUtils {
	/**
	 * ������������IP
	 * @param host
	 * @return
	 */
	public static String getInetAddress(String host) {
		String IPAddress = "";
		InetAddress ReturnStr1 = null;
		try {
			ReturnStr1 = java.net.InetAddress.getByName(host);
			IPAddress = ReturnStr1.getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return IPAddress;
		}
		return IPAddress;
	}

	/**
	 * �������ʹ��״������ʾ
	 * @param context
	 */
	public static boolean checkNetAndTip(Context context) {
		boolean flag = isNetworkAvailable(context);
		if (flag) {
			return true;
		} else {
			networkStateTips(context);
			return false;
		}
	}

	/**
	 * �����Ƿ����
	 * 
	 * @param activity
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * ��������Ƿ����
	 * @param context
	 * @return
	 */
	public static boolean checkNet(Context context) {

		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {

				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {

					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	/**
	 * ����������ʾ
	 * 
	 * @param context
	 * @return
	 */
	public static void networkStateTips(Context context) {
		if (!isNetworkAvailable(context)) {
			CommonTools.showShortToast(context, "������ϣ����ȼ����������");
		}
	}

	/**
	 * Gps�Ƿ��
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isGpsEnabled(Context context) {
		LocationManager locationManager = ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));
		List<String> accessibleProviders = locationManager.getProviders(true);
		return accessibleProviders != null && accessibleProviders.size() > 0;
	}

	/**
	 * wifi�Ƿ��
	 */
	public static boolean isWifiEnabled(Context context) {
		ConnectivityManager mgrConn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		TelephonyManager mgrTel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return ((mgrConn.getActiveNetworkInfo() != null && mgrConn.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel
				.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
	}

	/**
	 * �жϵ�ǰ�����Ƿ���wifi����
	 * if(activeNetInfo.getType()==ConnectivityManager.TYPE_MOBILE) { //�ж�3G��
	 * 
	 * @param context
	 * @return boolean
	 */
	public static boolean isWifi(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	/**
	 * �жϵ�ǰ�����Ƿ���3G����
	 * 
	 * @param context
	 * @return boolean
	 */
	public static boolean is3G(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
			return true;
		}
		return false;
	}
}