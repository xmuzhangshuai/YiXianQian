package com.yixianqian.ui;

import org.apache.http.Header;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.base.BaseV4Fragment;
import com.yixianqian.table.InviteCodeTable;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.CommonTools;
import com.yixianqian.utils.LogTool;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

/** 
* 类名称: RegInviteCodeFragment 
* 描述: TODO验证邀请码页面 
* 作者：张帅
* 时间： 2014年9月26日 下午4:32:24 
*  
*/
public class RegInviteCodeFragment extends BaseV4Fragment {
	private View rootView;// 根View
	private TextView topNavigation;
	private View leftImageButton;
	private View rightImageButton;
	private Button vertifyButton;
	private EditText codeEditText;
	private String inviteCode;
	private UserPreference userPreference;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_reg_invitecode, container, false);
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
		vertifyButton = (Button) rootView.findViewById(R.id.again_authcode);
		codeEditText = (EditText) rootView.findViewById(R.id.autncode);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavigation.setText("邀请码");
		vertifyButton.setEnabled(false);
		rightImageButton.setEnabled(false);

		leftImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getActivity().finish();
			}
		});

		rightImageButton.setVisibility(View.GONE);

		vertifyButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				attepmtAccount();
			}
		});

		//输入验证码时事件
		codeEditText.addTextChangedListener(new TextWatcher() {

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
				String content = codeEditText.getText().toString();
				codeEditText.setError(null);
				if (content != null) {
					if (content.length() == 6) {
						vertifyButton.setEnabled(true);
					} else {
						vertifyButton.setEnabled(false);
					}
				}
			}
		});
	}

	/**
	 * 验证输入
	 */
	private void attepmtAccount() {
		// 重置错误
		codeEditText.setError(null);

		// 存储用户值
		inviteCode = codeEditText.getText().toString().trim();
		boolean cancel = false;
		View focusView = null;

		//检查昵称
		if (TextUtils.isEmpty(inviteCode)) {
			codeEditText.setError(getString(R.string.error_field_required));
			focusView = codeEditText;
			cancel = true;
		} else if (inviteCode.length() != 6) {
			codeEditText.setError(getString(R.string.error_length));
			focusView = codeEditText;
			cancel = true;
		} else if (!CommonTools.isInviteCode(inviteCode)) {
			codeEditText.setError(getString(R.string.error_invite_code));
			focusView = codeEditText;
			cancel = true;
		}

		if (cancel) {
			// 如果错误，则提示错误
			focusView.requestFocus();
		} else {
			//获取验证码
			vetifyCode(inviteCode);
		}
	}

	/**
	 * 验证邀请码
	 */
	private void vetifyCode(String code) {
		RequestParams params = new RequestParams();
		params.put(InviteCodeTable.IC_CODE, code);
		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
			ProgressDialog dialog = new ProgressDialog(getActivity());

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
				dialog.setMessage("正在验证，请稍后...");
				dialog.setCancelable(false);
				dialog.show();
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				if (response.endsWith("1")) {
					userPreference.setInvitCode(response);
					RegGenderFragment fragment = new RegGenderFragment();
					FragmentTransaction transaction = getFragmentManager().beginTransaction();
					transaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out, R.anim.push_right_in,
							R.anim.push_right_out);
					transaction.replace(R.id.fragment_container, fragment);
					transaction.addToBackStack(null);
					transaction.commit();
				} else if (response.equals("0")) {
					codeEditText.setError("该邀请码无效，请重新输入");
					codeEditText.requestFocus();
				} else if (response.endsWith("-1")) {
					ToastTool.showLong(RegInviteCodeFragment.this.getActivity(), "服务器出现异常，请稍后再试");
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				ToastTool.showShort(RegInviteCodeFragment.this.getActivity(), "服务器错误");
				LogTool.e("验证码", "服务器错误,错误代码" + statusCode + "，  原因" + errorResponse);
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				super.onFinish();
				dialog.dismiss();
			}
		};
		AsyncHttpClientTool.post("verityinvitecode", params, responseHandler);
	}
}
