package com.yixianqian.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yixianqian.R;
import com.yixianqian.base.BaseV4Fragment;
import com.yixianqian.utils.SIMCardInfo;

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

	private String mPhone;
	private String mPassword;
	private String mConformPass;

	/**
	 * 用户注册异步任务
	 */
	private UserRegisterTask mRegisterTask = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_reg_phone, container, false);
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
	}

	/**
	 * 验证输入
	 */
	private void attepmtAccount() {
		if (mRegisterTask != null) {
			return;
		}

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

		if (cancel) {
			// 如果错误，则提示错误
			focusView.requestFocus();
		} else {
			// 没有错误，则注册
			mRegisterTask = new UserRegisterTask();
			mRegisterTask.execute((Void) null);

			RegAuthCodeFragment fragment = new RegAuthCodeFragment();
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out, R.anim.push_right_in,
					R.anim.push_right_out);
			transaction.replace(R.id.fragment_container, fragment);
//			transaction.addToBackStack(null);
			transaction.commit();
			Toast.makeText(getActivity(), "验证码已发送", 1).show();
		}
	}

	/**   
	*    
	* 项目名称：YiXianQian   
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
				//				 Simulate network access.
				//								UserService userService = UserService.getInstance(getActivity());
				//				
				//								String url = "RegistServlet";
				//								Map<String, String> map = new HashMap<String, String>();
				//								map.put("mail", mEmail);
				//								map.put("pass", mPassword);
				//								map.put("phone", mPhone);
				//				
				//								// 注册
				//								String jsonString = HttpUtil.postRequest(url, map);
				//								JUser net = FastJsonTool.getObject(jsonString, JUser.class);
				//								User local = userService.NetUserToUser(net);
				//								local.setCurrent(true);
				//								String location = locationPreferences.getString(DefaultKeys.PREF_DETAIL_LOCATION, "北京市");
				//								local.setArea(location);
				//								userService.saveUser(local);
				//				
				//								userService.updateUserToNet();

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

}
