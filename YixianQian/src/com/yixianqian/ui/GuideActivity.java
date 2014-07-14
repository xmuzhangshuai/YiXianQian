package com.yixianqian.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.config.DefaultKeys;
import com.yixianqian.db.CopyDataBase;
import com.yixianqian.utils.SharePreferenceUtil;

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
	private ImageView loadingImage;
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	public SharedPreferences locationPreferences;// 记录用户位置
	SharedPreferences.Editor locationEditor;
	private SharePreferenceUtil sharePreferenceUtil;
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

		//获取定位
		initLocation();

		if (count == 0) {// 如果是第一次登陆，则启动向导页面
			startActivity(new Intent(GuideActivity.this, GuidePagerActivity.class));
			// 第一次运行拷贝数据库文件
			new initDataBase().execute();
			sharePreferenceUtil.setUseCount(++count);// 次数加1
			finish();
		} else {// 如果不是第一次使用,则不启动向导页面，显示欢迎页面。
			if (false) {//如果是已经登陆过
				setContentView(R.layout.activity_guide);
				findViewById();
				initView();
				finish();
			} else {//如果用户没有登录过或者已经注销
				startActivity(new Intent(GuideActivity.this, LoginOrRegisterActivity.class));
				finish();
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
	protected void findViewById() {
		// TODO Auto-generated method stub
		loadingImage = (ImageView) findViewById(R.id.loading_item);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		Animation translate = AnimationUtils.loadAnimation(this, R.anim.splash_loading);
		translate.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				// 启动MainActivity，相当于Intent
				openActivity(MainActivity.class);
				overridePendingTransition(R.anim.splash_fade_in, R.anim.splash_fade_out);
				GuideActivity.this.finish();
			}
		});
		loadingImage.setAnimation(translate);
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

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
		}
	}

}
