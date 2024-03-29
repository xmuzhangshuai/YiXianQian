package com.yixianqian.ui;

import org.apache.http.Header;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.CommonTools;
import com.yixianqian.utils.MD5For32;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

/**
 * 类名称：ResetPassActivity
 * 类描述：重置密码页面
 * 创建人： 张帅
 * 创建时间：2014年8月26日 下午4:20:34
 *
 */
public class ResetPassActivity extends BaseActivity implements OnClickListener {
	private TextView topNavigation;//导航栏文字
	private View leftImageButton;//导航栏左侧按钮
	private View rightImageButton;//导航栏右侧按钮
	private EditText mNewPassView;//新密码
	private EditText mConformPassView;//确认密码
	private UserPreference userPreference;

	private String mPhone;
	private String newPass;
	private String confirmPass;
	View focusView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_reset_pass);
		mPhone = getIntent().getStringExtra(UserTable.U_TEL);
		userPreference = BaseApplication.getInstance().getUserPreference();

		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		topNavigation = (TextView) findViewById(R.id.nav_text);
		leftImageButton = (View) findViewById(R.id.left_btn_bg);
		rightImageButton = (View) findViewById(R.id.right_btn_bg);
		mNewPassView = (EditText) findViewById(R.id.newpass);
		mConformPassView = (EditText) findViewById(R.id.conform_password);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavigation.setText("重置密码");
		leftImageButton.setOnClickListener(this);
		rightImageButton.setOnClickListener(this);
	}

	private void attemptPass() {
		// 重置错误
		mNewPassView.setError(null);
		mConformPassView.setError(null);

		// 存储用户值
		newPass = mNewPassView.getText().toString();
		confirmPass = mConformPassView.getText().toString();
		boolean cancel = false;

		// 检查密码
		if (TextUtils.isEmpty(newPass)) {
			mNewPassView.setError(getString(R.string.error_field_required));
			focusView = mNewPassView;
			cancel = true;
		} else if (!CommonTools.isPassValid(newPass)) {
			mNewPassView.setError(getString(R.string.error_pattern_password));
			focusView = mNewPassView;
			cancel = true;
		}

		// 检查重复密码
		else if (TextUtils.isEmpty(confirmPass)) {
			mConformPassView.setError(getString(R.string.error_field_required));
			focusView = mConformPassView;
			cancel = true;
		} else if (!confirmPass.equals(newPass)) {
			mConformPassView.setError(getString(R.string.error_field_conform_pass));
			focusView = mConformPassView;
			cancel = true;
		}

		if (cancel) {
			// 如果错误，则提示错误
			focusView.requestFocus();
		} else {
			// 没有错误，则修改
			RequestParams params = new RequestParams();
			params.put(UserTable.U_NEW_PASSWORD, MD5For32.GetMD5Code(newPass));
			params.put(UserTable.U_TEL, mPhone);
			TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
				Dialog dialog;

				@Override
				public void onStart() {
					// TODO Auto-generated method stub
					super.onStart();
					dialog = showProgressDialog("请稍后...");
				}

				@Override
				public void onFinish() {
					// TODO Auto-generated method stub
					dialog.dismiss();
					super.onFinish();
				}

				@Override
				public void onSuccess(int arg0, Header[] arg1, String arg2) {
					// TODO Auto-generated method stub
					if (arg0 == 200) {
						if (arg2.equals("1")) {
							ToastTool.showShort(ResetPassActivity.this, "重置密码成功！");
							reLogin();
						} else {
							ToastTool.showShort(ResetPassActivity.this, "重置密码失败！");
						}
					}
				}

				@Override
				public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
					// TODO Auto-generated method stub
					ToastTool.showLong(ResetPassActivity.this, "服务器错误");
				}
			};
			AsyncHttpClientTool.post("resetuserpassword", params, responseHandler);
		}
	}

	/**
	 * 修改密码后重新登录
	 */
	private void reLogin() {
		//设置用户不曾登录
		//		BaseApplication.getInstance().logout();
		userPreference.clear();
		Intent intent = new Intent(ResetPassActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.left_btn_bg:
			finish();
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			break;
		case R.id.right_btn_bg:
			attemptPass();
			break;
		default:
			break;
		}
	}

}
