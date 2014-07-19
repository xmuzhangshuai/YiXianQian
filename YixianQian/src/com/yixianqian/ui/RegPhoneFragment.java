package com.yixianqian.ui;

import java.util.HashMap;
import java.util.Map;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yixianqian.R;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.base.BaseV4Fragment;
import com.yixianqian.config.DefaultKeys;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.HttpUtil;
import com.yixianqian.utils.SIMCardInfo;
import com.yixianqian.utils.UserPreference;

/**
 * 类名称：RegPhoneFragment
 * 类描述：电话和密码填写页面
 * 创建人： 张帅
 * 创建时间：2014年7月6日 下午7:56:56
 *
 */
public class RegPhoneFragment extends BaseV4Fragment {
	/*************Views************/
	private View rootView;// 根View
	private TextView topNavigation;//导航栏文字
	private View leftImageButton;//导航栏左侧按钮
	private View rightImageButton;//导航栏右侧按钮
	private EditText mPhoneView;//手机号
	private EditText mPasswordView;//密码
	private EditText mConformPassView;//确认密码
	private UserPreference userPreference;

	private String mPhone;
	private String mPassword;
	private String mConformPass;
	private boolean phoneAvailable;//手机号是否可用

	private CheckPhoneTask mCheckPhoneTask = null;//检查电话号码

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_reg_phone, container, false);
		userPreference = BaseApplication.getInstance().getUserPreference();

		findViewById();// 初始化views
		initView();
		return rootView;
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		topNavigation = (TextView) getActivity().findViewById(R.id.nav_text);
		leftImageButton = (View) getActivity().findViewById(R.id.left_btn_bg);
		rightImageButton = (View) getActivity().findViewById(R.id.right_btn_bg);
		mPhoneView = (EditText) rootView.findViewById(R.id.phone);
		mPasswordView = (EditText) rootView.findViewById(R.id.password);
		mConformPassView = (EditText) rootView.findViewById(R.id.conform_password);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		//显示用户手机号
		SIMCardInfo siminfo = new SIMCardInfo(getActivity());
		mPhoneView.setText(siminfo.getNativePhoneNumber());
		mPhone = mPhoneView.getText().toString();
		if ((!mPhone.isEmpty()) && mPhone.length() == 11) {
			//检查手机号是否被注册
			mCheckPhoneTask = new CheckPhoneTask();
			mCheckPhoneTask.execute();
		}

		topNavigation.setText("账户");
		leftImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getFragmentManager().popBackStack();
			}
		});

		rightImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				attepmtAccount();
			}
		});

		mPhoneView.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				mPhone = mPhoneView.getText().toString();
				if ((!mPhone.isEmpty()) && mPhone.length() == 11) {
					//检查手机号是否被注册
					mCheckPhoneTask = new CheckPhoneTask();
					mCheckPhoneTask.execute();
				}
			}
		});

	}

	/**
	 * 验证输入
	 */
	private void attepmtAccount() {
		// 重置错误
		mPasswordView.setError(null);
		mPhoneView.setError(null);
		mConformPassView.setError(null);

		// 存储用户值
		mPassword = mPasswordView.getText().toString();
		mPhone = mPhoneView.getText().toString();
		mConformPass = mConformPassView.getText().toString();
		boolean cancel = false;
		View focusView = null;

		// 检查手机号
		if (TextUtils.isEmpty(mPhone)) {
			mPhoneView.setError(getString(R.string.error_field_required));
			focusView = mPhoneView;
			cancel = true;
		} else if (mPhone.length() != 11) {
			mPhoneView.setError(getString(R.string.error_phone));
			focusView = mPhoneView;
			cancel = true;
		}

		// 检查密码
		else if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// 检查重复密码
		else if (TextUtils.isEmpty(mConformPass)) {
			mConformPassView.setError(getString(R.string.error_field_required));
			focusView = mConformPassView;
			cancel = true;
		} else if (!mConformPass.equals(mPassword)) {
			mConformPassView.setError(getString(R.string.error_field_conform_pass));
			focusView = mConformPassView;
			cancel = true;
		}

		else if (!phoneAvailable) {
			mPhoneView.setError("该手机号已被注册！");
			focusView = mPhoneView;
			cancel = true;
		}

		if (cancel) {
			// 如果错误，则提示错误
			focusView.requestFocus();
		} else {
			// 没有错误，则注册
			userPreference.setU_tel(mPhone);
			userPreference.setU_password(mPassword);

			RegAuthCodeFragment fragment = new RegAuthCodeFragment();
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out, R.anim.push_right_in,
					R.anim.push_right_out);
			transaction.replace(R.id.fragment_container, fragment);
			transaction.commit();
			Toast.makeText(getActivity(), "验证码已发送", 1).show();
		}
	}

	/**
	 * 
	 * 类名称：CheckPhoneTask
	 * 类描述：检查手机号是否已经被注册
	 * 创建人： 张帅
	 * 创建时间：2014年7月13日 上午11:25:31
	 *
	 */
	public class CheckPhoneTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			phoneAvailable = false;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				String url = "existtel";
				Map<String, String> map = new HashMap<String, String>();
				map.put(UserTable.U_TEL, mPhoneView.getText().toString());

				String result = HttpUtil.postRequest(url, map);
				if (result.equals(DefaultKeys.TEL_OK)) {
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			mCheckPhoneTask = null;
			phoneAvailable = result;
		}

		@Override
		protected void onCancelled() {
			mCheckPhoneTask = null;
		}

	}
}
