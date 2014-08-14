package com.yixianqian.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
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

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.db.ConversationDbService;
import com.yixianqian.entities.Conversation;
import com.yixianqian.jsonobject.JsonUser;
import com.yixianqian.server.ServerUtil;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.FastJsonTool;
import com.yixianqian.utils.FriendPreference;
import com.yixianqian.utils.HttpUtil;
import com.yixianqian.utils.MD5For16;
import com.yixianqian.utils.MD5For32;
import com.yixianqian.utils.NetworkUtils;
import com.yixianqian.utils.SIMCardInfo;
import com.yixianqian.utils.ToastTool;
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
	private FriendPreference friendpreference;
	List<JsonUser> jsonUsers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		userPreference = BaseApplication.getInstance().getUserPreference();
		friendpreference = BaseApplication.getInstance().getFriendPreference();
		findViewById();
		initView();

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
		if (TextUtils.isEmpty(password)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (!isPasswordValid(password)) {
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
			mAuthTask = new UserLoginTask(phone, MD5For32.GetMD5Code(password));
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
	 * 登录环信
	 */
	private void loginHuanXin() {
		EMChatManager.getInstance().login(userPreference.getHuanXinUserName(), userPreference.getHuanXinPassword(),
				new EMCallBack() {

					@Override
					public void onSuccess() {
						userPreference.setUserLogin(true);
					}

					@Override
					public void onProgress(int progress, String status) {
					}

					@Override
					public void onError(int code, final String message) {
						userPreference.clear();
					}
				});
		ServerUtil.getInstance(LoginActivity.this).getTodayRecommend(LoginActivity.this, true);
		showProgress(false);
	}

	/**
	 * 登陆
	 * 
	 * @param view
	 */
	public void attempLoginHuanXin(int time) {
		if (!NetworkUtils.isNetworkAvailable(this)) {
			NetworkUtils.networkStateTips(this);
			return;
		}

		// 调用sdk登陆方法登陆聊天服务器
		if (!TextUtils.isEmpty(userPreference.getHuanXinUserName())
				&& !TextUtils.isEmpty(userPreference.getHuanXinPassword())) {
			loginHuanXin();
		} else {
			if (time == 1) {
				userPreference.setHuanXinUserName("" + userPreference.getU_id());
				userPreference.setHuanXinPassword(MD5For16.GetMD5Code(userPreference.getU_password()));
				attempLoginHuanXin(2);
			} else {
				showProgress(false);
				ToastTool.showShort(LoginActivity.this, "登录聊天服务器失败");
			}
		}
	}

	/**
	 * 存储自己的信息
	 */
	private void saveUser(JsonUser user, String password) {
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
		userPreference.setU_password(MD5For32.GetMD5Code(password));
		userPreference.setHuanXinUserName("" + user.getU_id());
		userPreference.setHuanXinPassword(MD5For16.GetMD5Code(password));
	}

	/**
	 * 存储另一半
	 */
	private void saveFriend(JsonUser jsonUser) {
		if (userPreference.getU_stateid() == 2) {
			friendpreference.setType(1);
		} else if (userPreference.getU_stateid() == 3) {
			friendpreference.setType(0);
		} else {
			return;
		}

		friendpreference.setBpush_ChannelID(jsonUser.getU_bpush_channel_id());
		friendpreference.setBpush_UserID(jsonUser.getU_bpush_user_id());
		friendpreference.setF_address(jsonUser.getU_address());
		friendpreference.setF_age(jsonUser.getU_age());
		friendpreference.setF_blood_type(jsonUser.getU_blood_type());
		friendpreference.setF_constell(jsonUser.getU_constell());
		friendpreference.setF_email(jsonUser.getU_email());
		friendpreference.setF_gender(jsonUser.getU_gender());
		friendpreference.setF_height(jsonUser.getU_height());
		friendpreference.setF_id(jsonUser.getU_id());
		friendpreference.setF_introduce(jsonUser.getU_introduce());
		friendpreference.setF_large_avatar(jsonUser.getU_large_avatar());
		friendpreference.setF_nickname(jsonUser.getU_nickname());
		friendpreference.setF_realname(jsonUser.getU_realname());
		friendpreference.setF_salary(jsonUser.getU_salary());
		friendpreference.setF_small_avatar(jsonUser.getU_small_avatar());
		friendpreference.setF_stateid(jsonUser.getU_stateid());
		friendpreference.setF_tel(jsonUser.getU_tel());
		friendpreference.setF_vocationid(jsonUser.getU_vocationid());
		friendpreference.setF_weight(jsonUser.getU_weight());
		friendpreference.setU_cityid(jsonUser.getU_cityid());
		friendpreference.setU_provinceid(jsonUser.getU_provinceid());
		friendpreference.setU_schoolid(jsonUser.getU_schoolid());
	}

	/**
	 * 类名称：UserLoginTask 类描述：异步任务登录 创建人： 张帅 创建时间：2014-7-4 上午9:30:44
	 * 
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Void> {

		private final String mPhone;
		private final String mPassword;

		UserLoginTask(String phone, String password) {
			mPhone = phone;
			mPassword = password;
		}

		@Override
		protected Void doInBackground(Void... params) {
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
			jsonUsers = FastJsonTool.getObjectList(jsonString, JsonUser.class);
			// TODO: register the new account here.
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mAuthTask = null;
			if (jsonUsers != null) {
				if (jsonUsers.size() > 0) {
					if (jsonUsers.size() == 1) {
						saveUser(jsonUsers.get(0), mPassword);
					} else if (jsonUsers.size() > 1) {
						saveUser(jsonUsers.get(0), mPassword);
						saveFriend(jsonUsers.get(1));
						
						//创建对话
						ConversationDbService conversationDbService = ConversationDbService.getInstance(LoginActivity.this);
						Conversation conversation = new Conversation(null, Long.valueOf(friendpreference.getF_id()),
								friendpreference.getName(), friendpreference.getF_small_avatar(), "", 0, System.currentTimeMillis());
						conversationDbService.conversationDao.insert(conversation);
					}
					//登录环信
					attempLoginHuanXin(1);
				}
			} else {
				mPasswordView.setError("用户名或密码错误！");
				mPasswordView.requestFocus();
				showProgress(false);
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}

}
