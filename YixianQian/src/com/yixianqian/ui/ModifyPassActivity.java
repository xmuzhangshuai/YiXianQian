package com.yixianqian.ui;

import org.apache.http.Header;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

/**
 * 类名称：ModifyPassActivity
 * 类描述：修改密码
 * 创建人： 张帅
 * 创建时间：2014年7月27日 下午3:54:33
 *
 */
public class ModifyPassActivity extends BaseActivity implements OnClickListener {
	private TextView topNavigation;//导航栏文字
	private View leftImageButton;//导航栏左侧按钮
	private View rightImageButton;//导航栏右侧按钮
	private EditText mOldPassView;//旧密码
	private EditText mNewPassView;//新密码
	private EditText mConformPassView;//确认密码
	private UserPreference userPreference;

	private String oldPass;
	private String newPass;
	private String confirmPass;
	View focusView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_modify_pass);
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
		mOldPassView = (EditText) findViewById(R.id.oldpass);
		mNewPassView = (EditText) findViewById(R.id.newpass);
		mConformPassView = (EditText) findViewById(R.id.conform_password);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavigation.setText("修改密码");
		leftImageButton.setOnClickListener(this);
		rightImageButton.setOnClickListener(this);
	}

	private void attemptModifyPass() {
		// 重置错误
		mOldPassView.setError(null);
		mNewPassView.setError(null);
		mConformPassView.setError(null);

		// 存储用户值
		oldPass = mOldPassView.getText().toString();
		newPass = mNewPassView.getText().toString();
		confirmPass = mConformPassView.getText().toString();
		boolean cancel = false;

		//检查旧密码
		if (TextUtils.isEmpty(oldPass)) {
			mOldPassView.setError(getString(R.string.error_field_required));
			focusView = mOldPassView;
			cancel = true;
		} else if (oldPass.length() < 6) {
			mOldPassView.setError(getString(R.string.error_invalid_password));
			focusView = mOldPassView;
			cancel = true;
		}

		// 检查密码
		if (TextUtils.isEmpty(newPass)) {
			mNewPassView.setError(getString(R.string.error_field_required));
			focusView = mNewPassView;
			cancel = true;
		} else if (newPass.length() < 6) {
			mNewPassView.setError(getString(R.string.error_invalid_password));
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
			params.put(UserTable.U_PASSWORD, oldPass);
			params.put(UserTable.U_NEW_PASSWORD, newPass);
			params.put(UserTable.U_ID, userPreference.getU_id());
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
							ToastTool.showShort(ModifyPassActivity.this, "修改成功！");
							userPreference.setU_password(newPass);
							finish();
						} else if (arg2.equals("-1")) {
							mOldPassView.setError("旧密码不正确");
							focusView = mOldPassView;
							focusView.requestFocus();
						}
					}
				}

				@Override
				public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
					// TODO Auto-generated method stub
					ToastTool.showLong(ModifyPassActivity.this, "服务器错误");
				}
			};
			AsyncHttpClientTool.post("updateuserpwd", params, responseHandler);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.left_btn_bg:
			finish();
			break;
		case R.id.right_btn_bg:
			attemptModifyPass();
			break;
		default:
			break;
		}
	}

}
