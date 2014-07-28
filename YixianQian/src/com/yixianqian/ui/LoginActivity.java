package com.yixianqian.ui;

import java.util.HashMap;
import java.util.Map;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.jsonobject.JsonUser;
import com.yixianqian.server.ServerUtil;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.FastJsonTool;
import com.yixianqian.utils.HttpUtil;
import com.yixianqian.utils.NetworkUtils;
import com.yixianqian.utils.SIMCardInfo;
import com.yixianqian.utils.UserPreference;

/**
 * 类名称：LoginActivity 
 * 类描述：登录页面 
 * 创建人： 张帅 
 * 创建时间：2014-7-4 上午9:34:33
 * 
 */
public class LoginActivity extends BaseActivity {
	/**
	 * 用户登录异步任务
	 */
	private UserLoginTask mAuthTask = null;

	// UI references.
	private EditText mPhoneView;//手机号
	private EditText mPasswordView;//密码
	private View mProgressView;//缓冲
	private TextView topNavigation;//导航栏文字
	private View leftImageButton;//导航栏左侧按钮
	private View rightImageButton;//导航栏右侧按钮
	private UserPreference userPreference;

	// 提醒用户网络状况有异常
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (!NetworkUtils.isNetworkAvailable(LoginActivity.this)) {
				NetworkUtils.networkStateTips(LoginActivity.this);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		userPreference = BaseApplication.getInstance().getUserPreference();
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
		LoginActivity.this.registerReceiver(broadcastReceiver, intentFilter);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// 卸载广播
		if (broadcastReceiver != null) {
			LoginActivity.this.unregisterReceiver(broadcastReceiver);
		}
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		mPhoneView = (EditText) findViewById(R.id.phone);
		mPasswordView = (EditText) findViewById(R.id.password);
		mProgressView = findViewById(R.id.login_status);
		topNavigation = (TextView) findViewById(R.id.nav_text);
		leftImageButton = (View) findViewById(R.id.left_btn_bg);
		rightImageButton = (View) findViewById(R.id.right_btn_bg);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		SIMCardInfo siminfo = new SIMCardInfo(LoginActivity.this);
		mPhoneView.setText(siminfo.getNativePhoneNumber());

		topNavigation.setText("登录");
		leftImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		rightImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				attemptLogin();
			}
		});
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mPhoneView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		String phone = mPhoneView.getText().toString();
		String password = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password, if the user entered one.
		if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(phone)) {
			mPhoneView.setError(getString(R.string.error_field_required));
			focusView = mPhoneView;
			cancel = true;
		} else if (!isEmailValid(phone)) {
			mPhoneView.setError(getString(R.string.error_invalid_phone));
			focusView = mPhoneView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			showProgress(true);
			mAuthTask = new UserLoginTask(phone, password);
			mAuthTask.execute((Void) null);
		}
	}

	private boolean isEmailValid(String email) {
		// TODO: Replace this with your own logic
		return email.length() == 11;
	}

	private boolean isPasswordValid(String password) {
		// TODO: Replace this with your own logic
		return password.length() > 5;
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (show) {
			//隐藏软键盘   
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(LoginActivity.this
					.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
			mProgressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
		}
	}

	/**
	 * 类名称：UserLoginTask 类描述：异步任务登录 创建人： 张帅 创建时间：2014-7-4 上午9:30:44
	 * 
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, JsonUser> {

		private final String mPhone;
		private final String mPassword;

		UserLoginTask(String phone, String password) {
			mPhone = phone;
			mPassword = password;
		}

		@Override
		protected JsonUser doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			String url = "login";
			Map<String, String> map = new HashMap<String, String>();
			map.put(UserTable.U_TEL, mPhone);
			map.put(UserTable.U_PASSWORD, mPassword);
			map.put(UserTable.U_BPUSH_USER_ID, userPreference.getBpush_UserID());
			map.put(UserTable.U_BPUSH_CHANNEL_ID, userPreference.getBpush_ChannelID());

			String jsonString = null;
			try {
				jsonString = HttpUtil.postRequest(url, map);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JsonUser user = FastJsonTool.getObject(jsonString, JsonUser.class);
			// TODO: register the new account here.
			return user;
		}

		@Override
		protected void onPostExecute(final JsonUser user) {
			mAuthTask = null;
			showProgress(false);
			if (user != null) {
				userPreference.clear();
				userPreference.setU_birthday(user.getU_birthday());
				userPreference.setU_blood_type(user.getU_blood_type());
				userPreference.setU_cityid(user.getU_cityid());
				userPreference.setU_constell(user.getU_constell());
				userPreference.setU_email(user.getU_email());
				userPreference.setU_gender(user.getU_gender());
				userPreference.setU_age(user.getU_age());
				userPreference.setU_height(user.getU_height());
				userPreference.setU_id(user.getU_id());
				userPreference.setU_introduce(user.getU_introduce());
				userPreference.setU_large_avatar(user.getU_large_avatar());
				userPreference.setU_nickname(user.getU_nickname());
				userPreference.setU_password(user.getU_password());
				userPreference.setU_provinceid(user.getU_provinceid());
				userPreference.setU_realname(user.getU_realname());
				userPreference.setU_salary(user.getU_salary());
				userPreference.setU_schoolid(user.getU_schoolid());
				userPreference.setU_small_avatar(user.getU_small_avatar());
				userPreference.setU_stateid(user.getU_stateid());
				userPreference.setU_tel(user.getU_tel());
				userPreference.setU_vocationid(user.getU_vocationid());
				userPreference.setU_weight(user.getU_weight());

				ServerUtil.getInstance(LoginActivity.this).getFlipperAndRecommend(LoginActivity.this, true);
				userPreference.setUserLogin(true);
			} else {
				mPasswordView.setError("用户名或密码错误！");
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}

}
