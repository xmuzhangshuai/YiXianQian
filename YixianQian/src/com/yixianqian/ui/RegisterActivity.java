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
 * 类名称：RegisterActivity
 * 类描述：注册页面
 * 创建人： 张帅
 * 创建时间：2014年7月6日 下午7:24:39
 *
 */
public class RegisterActivity extends BaseFragmentActivity implements OnCompeletedListener, OnUserInfoChangedListener {
	/**
	 * 用户注册异步任务
	 */
	private UserRegisterTask mRegisterTask = null;

	private ViewPager mViewPager;
	private FixedSpeedScroller mScroller;

	// UI references.
	//	private EditText mPhoneView;//手机号
	//	private EditText mRealNameView;//姓名
	//	private EditText mPasswordView;//密码
	//	private EditText mConformPassView;//确认密码
	//	private Spinner mProvinceView;//省
	//	private Spinner mCityView;//城市
	//	private Spinner mSchoolView;//学校

	private TextView topNavigation;//导航栏文字
	private View leftImageButton;//导航栏左侧按钮
	private View rightImageButton;//导航栏右侧按钮

	// 用户输入信息
	//	private String mPhone;
	//	private String mRealName;
	//	private String mPassword;
	//	private String mConformPass;
	//	private String mProvince;
	//	private String mCity;
	//	private String mSchool;
	//	private boolean mIsSingle;

	// 提醒用户网络状况有异常
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
		// 注册广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		RegisterActivity.this.registerReceiver(broadcastReceiver, intentFilter);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// 卸载广播
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

		// 新建Adapter
		final RegisterAdapter registerAdapter = new RegisterAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(registerAdapter);
		//		controlViewPagerSpeed();
		topNavigation.setText("新建账户");
		//		//显示用户手机号
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
	 * 注册按钮，如果有错误，检查错误
	 */
	//	public void attemptRegist() {
	//		if (mRegisterTask != null) {
	//			return;
	//		}
	//
	//		// 重置错误
	//		mPasswordView.setError(null);
	//		mPhoneView.setError(null);
	//
	//		// 存储用户值
	//		mPassword = mPasswordView.getText().toString();
	//		mPhone = mPhoneView.getText().toString();
	//
	//		boolean cancel = false;
	//		View focusView = null;
	//
	//		// 检查密码
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
	//		// 检查手机号
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
	//			// 如果错误，则提示错误
	//			focusView.requestFocus();
	//		} else {
	//			// 没有错误，则联网登陆
	//			//			showProgress(true);
	//			mRegisterTask = new UserRegisterTask();
	//			mRegisterTask.execute((Void) null);
	//		}
	//	}

	/*********监听返回键，提示用户正在注册*********/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(R.drawable.icon_warning).setTitle("温馨提示").setMessage("您正在注册过程中，此时退出注册信息将不能保存。是否继续退出？")
					.setPositiveButton("是", backListener).setNegativeButton("否", backListener).show();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**监听对话框里面的button点击事件*/
	DialogInterface.OnClickListener backListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
				finish();
				break;
			case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
				break;
			default:
				break;
			}
		}
	};

	/**   
	*    
	* 项目名称：ExamHelper   
	* 类名称：UserRegisterTask   
	* 类描述：   异步任务注册
	* 创建人：张帅  
	* 创建时间：2014-4-3 下午3:34:13   
	* 修改人：张帅   
	* 修改时间：2014-4-3 下午3:34:13   
	* 修改备注：   
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
				//				// 注册
				//				String jsonString = HttpUtil.postRequest(url, map);
				//				JUser net = FastJsonTool.getObject(jsonString, JUser.class);
				//				User local = userService.NetUserToUser(net);
				//				local.setCurrent(true);
				//				String location = locationPreferences.getString(DefaultKeys.PREF_DETAIL_LOCATION, "北京市");
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
			//				Toast.makeText(getActivity(), "恭喜您注册成功！请尽快完善个人资料...", 2000).show();
			//				getActivity().finish();
			//			} else {
			//				Toast.makeText(getActivity(), "注册失败", 1).show();
			//			}
		}

		@Override
		protected void onCancelled() {
			//			mRegisterTask = null;
			//			showProgress(false);
		}
	}

	/**
	 * 如果本页完成
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
	 * 控制滑页速度
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
