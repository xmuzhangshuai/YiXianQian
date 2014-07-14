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
 * �����ƣ�GuideActivity
 * ������������ҳ�棬�״����н����״�����ҳ�棬GuidePagerActivity;�û�û�е�¼������¼/ע��ҳ�棻�û��Ѿ���¼���������ҳ�����ҳ��
 * �ڼ���ɳ���ĳ�ʼ�������� �����ˣ� ��˧ ����ʱ�䣺2014-7-4 ����8:32:52
 * 
 */
/**
 * �����ƣ�GuideActivity
 * ���������״ΰ�װ���е�activity����ӭҳ�档����������佫���ݿ⿽�����ֻ��洢�С�
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��7��8�� ����4:51:53
 *
 */
public class GuideActivity extends BaseActivity {
	private ImageView loadingImage;
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	public SharedPreferences locationPreferences;// ��¼�û�λ��
	SharedPreferences.Editor locationEditor;
	private SharePreferenceUtil sharePreferenceUtil;
	private String province;//ʡ��
	private String city;//����
	private String detailLocation;//��ϸ��ַ

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);

		// ��ȡ�����Ĵ���
		sharePreferenceUtil = new SharePreferenceUtil(this, SharePreferenceUtil.USE_COUNT);
		int count = sharePreferenceUtil.getUseCount();

		//��ȡ��λ
		initLocation();

		if (count == 0) {// ����ǵ�һ�ε�½����������ҳ��
			startActivity(new Intent(GuideActivity.this, GuidePagerActivity.class));
			// ��һ�����п������ݿ��ļ�
			new initDataBase().execute();
			sharePreferenceUtil.setUseCount(++count);// ������1
			finish();
		} else {// ������ǵ�һ��ʹ��,��������ҳ�棬��ʾ��ӭҳ�档
			if (false) {//������Ѿ���½��
				setContentView(R.layout.activity_guide);
				findViewById();
				initView();
				finish();
			} else {//����û�û�е�¼�������Ѿ�ע��
				startActivity(new Intent(GuideActivity.this, LoginOrRegisterActivity.class));
				finish();
			}
			sharePreferenceUtil.setUseCount(++count);// ������1
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
				// ����MainActivity���൱��Intent
				openActivity(MainActivity.class);
				overridePendingTransition(R.anim.splash_fade_in, R.anim.splash_fade_out);
				GuideActivity.this.finish();
			}
		});
		loadingImage.setAnimation(translate);
	}

	@Override
	public void onDestroy() {
		stopListener();// ֹͣ����
		super.onDestroy();
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
	 * �����ƣ�initDataBase
	 * ���������������ݿ�
	 * �����ˣ� ��˧
	 * ����ʱ�䣺2014��7��8�� ����4:51:58
	 *
	 */
	public class initDataBase extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			new CopyDataBase(GuideActivity.this).copyDataBase();// �������ݿ�
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
		}
	}

}
