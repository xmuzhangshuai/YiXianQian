package com.yixianqian.service;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.yixianqian.config.DefaultKeys;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

public class LocationService extends Service {
	public SharedPreferences locationPreferences;// ��¼�û�λ��
	SharedPreferences.Editor locationEditor;
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	private String province;//ʡ��
	private String city;//����
	private String detailLocation;//��ϸ��ַ

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		initLocation();
		return null;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		stopListener();
		super.onDestroy();
	}

	/**
	 * ��ʼ����λ
	 */
	public void initLocation() {
		mLocationClient = new LocationClient(getApplicationContext()); // ����LocationClient��
		mLocationClient.registerLocationListener(myListener); // ע���������
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// ��GPS
		option.setIsNeedAddress(true);//���صĶ�λ���������ַ��Ϣ
		option.setCoorType("bd09ll");// ���صĶ�λ����ǰٶȾ�γ��,Ĭ��ֵgcj02
		option.setLocationMode(LocationMode.Hight_Accuracy);//���ö�λģʽ
		option.setScanSpan(5 * 60 * 60 * 1000);// ���÷���λ����ļ��ʱ��Ϊ3000ms
		mLocationClient.setLocOption(option);// ʹ������
		mLocationClient.start();// ������λSDK
		mLocationClient.requestLocation();// ��ʼ����λ��

		locationPreferences = getSharedPreferences("location", Context.MODE_PRIVATE);
		locationEditor = locationPreferences.edit();
	}

	/**
	 * λ�ü�����
	 */
	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location != null) {
				province = location.getProvince();
				city = location.getCity();
				detailLocation = location.getAddrStr();//��ϸ��ַ
				locationEditor.putString(DefaultKeys.USER_PROVINCE, province);
				locationEditor.putString(DefaultKeys.USER_CITY, city);
				locationEditor.putString(DefaultKeys.USER_DETAIL_LOCATION, detailLocation);
				locationEditor.commit();
			}

		}
	}

	/**
	 * ֹͣ��������Դ����
	 */
	public void stopListener() {
		if (mLocationClient != null && mLocationClient.isStarted()) {
			mLocationClient.stop();// �رն�λSDK
			mLocationClient = null;
		}
	}

}
