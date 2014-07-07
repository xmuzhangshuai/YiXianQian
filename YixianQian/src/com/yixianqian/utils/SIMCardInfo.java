package com.yixianqian.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

public class SIMCardInfo {
	/** 
	* TelephonyManager�ṩ�豸�ϻ�ȡͨѶ������Ϣ����ڡ� Ӧ�ó������ʹ������෽��ȷ���ĵ��ŷ����̺͹��� �Լ�ĳЩ���͵��û�������Ϣ�� 
	* Ӧ�ó���Ҳ����ע��һ�����������绰��״̬�ı仯������Ҫֱ��ʵ��������� 
	* ʹ��Context.getSystemService(Context.TELEPHONY_SERVICE)����ȡ������ʵ���� 
	*/
	private TelephonyManager telephonyManager;
	/** 
	 * �����ƶ��û�ʶ���� 
	 */
	private String IMSI;

	public SIMCardInfo(Context context) {
		telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	}

	/** 
	 * Role:��ȡ��ǰ���õĵ绰���� 
	 * <BR>Date:2012-3-12 
	 * <BR>@author CODYY) 
	 */
	public String getNativePhoneNumber() {
		String NativePhoneNumber = null;
		NativePhoneNumber = telephonyManager.getLine1Number();
		if (NativePhoneNumber != null) {
			if (NativePhoneNumber.length() >= 11)
				NativePhoneNumber = NativePhoneNumber.substring(NativePhoneNumber.length() - 11,
						NativePhoneNumber.length());
		}
		return NativePhoneNumber;
	}

	/** 
	 * Role:Telecom service providers��ȡ�ֻ���������Ϣ <BR> 
	 * Date:2012-3-12 <BR> 
	 *  
	 * @author 
	 */
	public String getProvidersName() {
		String ProvidersName = null;
		// ����Ψһ���û�ID;�������ſ��ı�������  
		IMSI = telephonyManager.getSubscriberId();
		// IMSI��ǰ��3λ460�ǹ��ң������ź���2λ00 02���й��ƶ���01���й���ͨ��03���й����š�  
		if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
			ProvidersName = "�й��ƶ�";
		} else if (IMSI.startsWith("46001")) {
			ProvidersName = "�й���ͨ";
		} else if (IMSI.startsWith("46003")) {
			ProvidersName = "�й�����";
		}
		return ProvidersName;
	}
}
