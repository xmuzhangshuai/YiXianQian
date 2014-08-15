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
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	public SharedPreferences locationPreferences;// ��¼�û�λ��
	SharedPreferences.Editor locationEditor;
	private SharePreferenceUtil sharePreferenceUtil;
	private UserPreference userPreference;
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
		userPreference = BaseApplication.getInstance().getUserPreference();

		/************��ʼ�����˷���**************/
		MobclickAgent.updateOnlineConfig(this);

		//��ȡ��λ
		initLocation();
		//�����ٶ����ͷ���
		PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,
				Constants.BaiduPushConfig.API_KEY);
		// ���ڵ���λ�����ͣ����Դ�֧�ֵ���λ�õ����͵Ŀ���
		PushManager.enableLbs(getApplicationContext());

		if (count == 0) {// ����ǵ�һ�ε�½����������ҳ��
			// ��һ�����п������ݿ��ļ�
			new initDataBase().execute();
			//				SchoolDbService schoolDbService = SchoolDbService.getInstance(this);
			//				schoolDbService.schoolDao.loadAll();
			sharePreferenceUtil.setUseCount(++count);// ������1
			startActivity(new Intent(GuideActivity.this, GuidePagerActivity.class));
		} else {// ������ǵ�һ��ʹ��,��������ҳ�棬��ʾ��ӭҳ�档
			if (userPreference.getUserLogin()) {//������Ѿ���½��
				if (NetworkUtils.isNetworkAvailable(GuideActivity.this)) {//����������
					if (userPreference.getLoveRequest()) {//���������������£���ֱ����ת���ȴ�ҳ��
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
			} else {//����û�û�е�¼�������Ѿ�ע��
				startActivity(new Intent(GuideActivity.this, LoginOrRegisterActivity.class));
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
	}
}
