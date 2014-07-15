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
	public SharedPreferences locationPreferences;// 记录用户位置
	SharedPreferences.Editor locationEditor;
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	private String province;//省份
	private String city;//城市
	private String detailLocation;//详细地址

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
	 * 初始化定位
	 */
	public void initLocation() {
		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开GPS
		option.setIsNeedAddress(true);//返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式
		option.setScanSpan(5 * 60 * 60 * 1000);// 设置发起定位请求的间隔时间为3000ms
		mLocationClient.setLocOption(option);// 使用设置
		mLocationClient.start();// 开启定位SDK
		mLocationClient.requestLocation();// 开始请求位置

		locationPreferences = getSharedPreferences("location", Context.MODE_PRIVATE);
		locationEditor = locationPreferences.edit();
	}

	/**
	 * 位置监听类
	 */
	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location != null) {
				province = location.getProvince();
				city = location.getCity();
				detailLocation = location.getAddrStr();//详细地址
				locationEditor.putString(DefaultKeys.USER_PROVINCE, province);
				locationEditor.putString(DefaultKeys.USER_CITY, city);
				locationEditor.putString(DefaultKeys.USER_DETAIL_LOCATION, detailLocation);
				locationEditor.commit();
			}

		}
	}

	/**
	 * 停止，减少资源消耗
	 */
	public void stopListener() {
		if (mLocationClient != null && mLocationClient.isStarted()) {
			mLocationClient.stop();// 关闭定位SDK
			mLocationClient = null;
		}
	}

}
