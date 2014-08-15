package com.yixianqian.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.umeng.analytics.MobclickAgent;
import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.config.Constants;
import com.yixianqian.config.DefaultKeys;
import com.yixianqian.db.CopyDataBase;
import com.yixianqian.server.ServerUtil;
import com.yixianqian.utils.NetworkUtils;
import com.yixianqian.utils.SharePreferenceUtil;
import com.yixianqian.utils.UserPreference;

/**
 * 类名称：GuideActivity
 * 类描述：引导页面，首次运行进入首次引导页面，GuidePagerActivity;用户没有登录则进入登录/注册页面；用户已经登录过则进入主页面加载页，
 * 期间完成程序的初始化工作。 创建人： 张帅 创建时间：2014-7-4 上午8:32:52
 * 
 */
/**
 * 类名称：GuideActivity
 * 类描述：首次安装运行的activity，欢迎页面。并且在这其间将数据库拷贝到手机存储中。
 * 创建人： 张帅
 * 创建时间：2014年7月8日 下午4:51:53
 *
 */
public class GuideActivity extends BaseActivity {
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	public SharedPreferences locationPreferences;// 记录用户位置
	SharedPreferences.Editor locationEditor;
	private SharePreferenceUtil sharePreferenceUtil;
	private UserPreference userPreference;
	private String province;//省份
	private String city;//城市
	private String detailLocation;//详细地址

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);

		// 获取启动的次数
		sharePreferenceUtil = new SharePreferenceUtil(this, SharePreferenceUtil.USE_COUNT);
		int count = sharePreferenceUtil.getUseCount();
		userPreference = BaseApplication.getInstance().getUserPreference();

		/************初始化友盟服务**************/
		MobclickAgent.updateOnlineConfig(this);

		//获取定位
		initLocation();
		//开启百度推送服务
		PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,
				Constants.BaiduPushConfig.API_KEY);
		// 基于地理位置推送，可以打开支持地理位置的推送的开关
		PushManager.enableLbs(getApplicationContext());

		if (count == 0) {// 如果是第一次登陆，则启动向导页面
			// 第一次运行拷贝数据库文件
			new initDataBase().execute();
			//				SchoolDbService schoolDbService = SchoolDbService.getInstance(this);
			//				schoolDbService.schoolDao.loadAll();
			sharePreferenceUtil.setUseCount(++count);// 次数加1
			startActivity(new Intent(GuideActivity.this, GuidePagerActivity.class));
		} else {// 如果不是第一次使用,则不启动向导页面，显示欢迎页面。
			if (userPreference.getUserLogin()) {//如果是已经登陆过
				if (NetworkUtils.isNetworkAvailable(GuideActivity.this)) {//如果网络可用
					if (userPreference.getLoveRequest()) {//如果请求了添加情侣，则直接跳转到等待页面
						startActivity(new Intent(GuideActivity.this, WaitActivity.class));
					} else {
						setContentView(R.layout.activity_guide);
						findViewById();
						initView();
						ServerUtil.getInstance(GuideActivity.this).initUserData(GuideActivity.this, false);
						ServerUtil.getInstance(GuideActivity.this).getHeadImagePass();
					}
				} else {
					startActivity(new Intent(GuideActivity.this, MainActivity.class));
				}
			} else {//如果用户没有登录过或者已经注销
				startActivity(new Intent(GuideActivity.this, LoginOrRegisterActivity.class));
			}
			sharePreferenceUtil.setUseCount(++count);// 次数加1
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDestroy() {
		stopListener();// 停止监听
		super.onDestroy();
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
	 * 类名称：initDataBase
	 * 类描述：拷贝数据库
	 * 创建人： 张帅
	 * 创建时间：2014年7月8日 下午4:51:58
	 *
	 */
	public class initDataBase extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			new CopyDataBase(GuideActivity.this).copyDataBase();// 拷贝数据库
			return null;
		}
	}
}
