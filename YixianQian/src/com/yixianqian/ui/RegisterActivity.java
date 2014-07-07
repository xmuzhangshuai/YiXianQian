package com.yixianqian.ui;

import java.lang.reflect.Field;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.Window;
import android.widget.TextView;

import com.yixianqian.R;
import com.yixianqian.adapter.RegisterAdapter;
import com.yixianqian.base.BaseFragmentActivity;
import com.yixianqian.customewidget.FixedSpeedScroller;
import com.yixianqian.interfaces.OnCompeletedListener;
import com.yixianqian.interfaces.OnUserInfoChangedListener;
import com.yixianqian.utils.NetworkUtils;

/**
 * �����ƣ�RegisterActivity
 * ��������ע��ҳ��
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��7��6�� ����7:24:39
 *
 */
public class RegisterActivity extends BaseFragmentActivity implements OnCompeletedListener, OnUserInfoChangedListener {
	/**
	 * �û�ע���첽����
	 */
	private UserRegisterTask mRegisterTask = null;

	private ViewPager mViewPager;
	private FixedSpeedScroller mScroller;

	// UI references.
	//	private EditText mPhoneView;//�ֻ���
	//	private EditText mRealNameView;//����
	//	private EditText mPasswordView;//����
	//	private EditText mConformPassView;//ȷ������
	//	private Spinner mProvinceView;//ʡ
	//	private Spinner mCityView;//����
	//	private Spinner mSchoolView;//ѧУ

	private TextView topNavigation;//����������
	private View leftImageButton;//��������ఴť
	private View rightImageButton;//�������Ҳఴť

	// �û�������Ϣ
	//	private String mPhone;
	//	private String mRealName;
	//	private String mPassword;
	//	private String mConformPass;
	//	private String mProvince;
	//	private String mCity;
	//	private String mSchool;
	//	private boolean mIsSingle;

	// �����û�����״�����쳣
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (!NetworkUtils.isNetworkAvailable(RegisterActivity.this)) {
				NetworkUtils.networkStateTips(RegisterActivity.this);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);

		findViewById();
		initView();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// ע��㲥
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		RegisterActivity.this.registerReceiver(broadcastReceiver, intentFilter);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// ж�ع㲥
		if (broadcastReceiver != null) {
			RegisterActivity.this.unregisterReceiver(broadcastReceiver);
		}
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		mViewPager = ((ViewPager) findViewById(R.id.pager));

		//		mPhoneView = (EditText) findViewById(R.id.phone);
		//		mPasswordView = (EditText) findViewById(R.id.password);
		topNavigation = (TextView) findViewById(R.id.nav_text);
		leftImageButton = (View) findViewById(R.id.left_btn_bg);
		rightImageButton = (View) findViewById(R.id.right_btn_bg);
		//		mPhoneView = (EditText) findViewById(R.id.phone);
		//
		//		mRealNameView = (EditText) findViewById(R.id.name);
		//		mPasswordView = (EditText) findViewById(R.id.password);
		//		mConformPassView = (EditText) findViewById(R.id.conform_password);
		//		mProvinceView = (Spinner) findViewById(R.id.province);
		//		mCityView = (Spinner) findViewById(R.id.city);
		//		mSchoolView = (Spinner) findViewById(R.id.school);

	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub

		// �½�Adapter
		final RegisterAdapter registerAdapter = new RegisterAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(registerAdapter);
		//		controlViewPagerSpeed();
		topNavigation.setText("�½��˻�");
		//		//��ʾ�û��ֻ���
		//		SIMCardInfo siminfo = new SIMCardInfo(RegisterActivity.this);
		//		mPhoneView.setText(siminfo.getNativePhoneNumber());

		leftImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mViewPager.getCurrentItem() > 0) {
					mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
				}
			}
		});

		rightImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mViewPager.getCurrentItem() < 3) {
					mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
				}
			}
		});
	}

	/**
	 * ע�ᰴť������д��󣬼�����
	 */
	//	public void attemptRegist() {
	//		if (mRegisterTask != null) {
	//			return;
	//		}
	//
	//		// ���ô���
	//		mPasswordView.setError(null);
	//		mPhoneView.setError(null);
	//
	//		// �洢�û�ֵ
	//		mPassword = mPasswordView.getText().toString();
	//		mPhone = mPhoneView.getText().toString();
	//
	//		boolean cancel = false;
	//		View focusView = null;
	//
	//		// �������
	//		if (TextUtils.isEmpty(mPassword)) {
	//			mPasswordView.setError(getString(R.string.error_field_required));
	//			focusView = mPasswordView;
	//			cancel = true;
	//		} else if (mPassword.length() < 4) {
	//			mPasswordView.setError(getString(R.string.error_invalid_password));
	//			focusView = mPasswordView;
	//			cancel = true;
	//		}
	//
	//		// ����ֻ���
	//		if (TextUtils.isEmpty(mPhone)) {
	//			mPhoneView.setError(getString(R.string.error_field_required));
	//			focusView = mPhoneView;
	//			cancel = true;
	//		} else if (mPhone.length() != 11) {
	//			mPhoneView.setError(getString(R.string.error_phone));
	//			focusView = mPhoneView;
	//			cancel = true;
	//		}
	//
	//		if (cancel) {
	//			// �����������ʾ����
	//			focusView.requestFocus();
	//		} else {
	//			// û�д�����������½
	//			//			showProgress(true);
	//			mRegisterTask = new UserRegisterTask();
	//			mRegisterTask.execute((Void) null);
	//		}
	//	}

	/*********�������ؼ�����ʾ�û�����ע��*********/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(R.drawable.icon_warning).setTitle("��ܰ��ʾ").setMessage("������ע������У���ʱ�˳�ע����Ϣ�����ܱ��档�Ƿ�����˳���")
					.setPositiveButton("��", backListener).setNegativeButton("��", backListener).show();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**�����Ի��������button����¼�*/
	DialogInterface.OnClickListener backListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case AlertDialog.BUTTON_POSITIVE:// "ȷ��"��ť�˳�����
				finish();
				break;
			case AlertDialog.BUTTON_NEGATIVE:// "ȡ��"�ڶ�����ťȡ���Ի���
				break;
			default:
				break;
			}
		}
	};

	/**   
	*    
	* ��Ŀ���ƣ�ExamHelper   
	* �����ƣ�UserRegisterTask   
	* ��������   �첽����ע��
	* �����ˣ���˧  
	* ����ʱ�䣺2014-4-3 ����3:34:13   
	* �޸��ˣ���˧   
	* �޸�ʱ�䣺2014-4-3 ����3:34:13   
	* �޸ı�ע��   
	* @version    
	*    
	*/
	public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				// Simulate network access.
				//				UserService userService = UserService.getInstance(getActivity());
				//
				//				String url = "RegistServlet";
				//				Map<String, String> map = new HashMap<String, String>();
				//				map.put("mail", mEmail);
				//				map.put("pass", mPassword);
				//				map.put("phone", mPhone);
				//
				//				// ע��
				//				String jsonString = HttpUtil.postRequest(url, map);
				//				JUser net = FastJsonTool.getObject(jsonString, JUser.class);
				//				User local = userService.NetUserToUser(net);
				//				local.setCurrent(true);
				//				String location = locationPreferences.getString(DefaultKeys.PREF_DETAIL_LOCATION, "������");
				//				local.setArea(location);
				//				userService.saveUser(local);
				//
				//				userService.updateUserToNet();

			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			//			mRegisterTask = null;
			//			showProgress(false);
			//
			//			if (success) {
			//				Toast.makeText(getActivity(), "��ϲ��ע��ɹ����뾡�����Ƹ�������...", 2000).show();
			//				getActivity().finish();
			//			} else {
			//				Toast.makeText(getActivity(), "ע��ʧ��", 1).show();
			//			}
		}

		@Override
		protected void onCancelled() {
			//			mRegisterTask = null;
			//			showProgress(false);
		}
	}

	/**
	 * �����ҳ���
	 */
	@Override
	public void onCompeleted(int position) {
		// TODO Auto-generated method stub
		if (position > 0) {
			mViewPager.setCurrentItem(position, true);

		}
	}

	@Override
	public void onGenderChanged(String gender) {
		// TODO Auto-generated method stub
		System.out.println(gender);
	}

	@Override
	public void onStateChanged(boolean single) {
		// TODO Auto-generated method stub
		System.out.println(single);
	}

	@Override
	public void onProvinceChanged(String province) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCityChanged(String city) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSchoolChanged(String school) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPhoneChanged(String phone) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPassChanged(String password) {
		// TODO Auto-generated method stub

	}

	/**
	 * ���ƻ�ҳ�ٶ�
	 */
	//	private void controlViewPagerSpeed() {
	//		try {
	//			Field mField;
	//
	//			mField = ViewPager.class.getDeclaredField("mScroller");
	//			mField.setAccessible(true);
	//
	//			mScroller = new FixedSpeedScroller(RegisterActivity.this, new AccelerateInterpolator());
	//			mScroller.setmDuration(500);
	//			mField.set(mViewPager, mScroller);
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		}
	//	}
}
