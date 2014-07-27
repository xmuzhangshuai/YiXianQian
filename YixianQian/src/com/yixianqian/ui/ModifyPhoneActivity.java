package com.yixianqian.ui;

import org.apache.http.Header;

import android.content.Intent;
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
import com.yixianqian.config.DefaultKeys;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.ToastTool;

/**
 * 类名称：ModifyPhoneActivity
 * 类描述：修改电话
 * 创建人： 张帅
 * 创建时间：2014年7月27日 下午3:45:23
 *
 */
public class ModifyPhoneActivity extends BaseActivity implements OnClickListener {
	private TextView topNavigation;//导航栏文字
	private View leftImageButton;//导航栏左侧按钮
	private View rightImageButton;//导航栏右侧按钮
	private EditText mPhoneView;//手机号
	private String mPhone;
	private boolean cancel = false;
	private View focusView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_modify_phone);

		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		topNavigation = (TextView) findViewById(R.id.nav_text);
		leftImageButton = (View) findViewById(R.id.left_btn_bg);
		rightImageButton = (View) findViewById(R.id.right_btn_bg);
		mPhoneView = (EditText) findViewById(R.id.phone);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavigation.setText("修改手机号");
		leftImageButton.setOnClickListener(this);
		rightImageButton.setOnClickListener(this);
	}

	private void attemptPhone() {
		// 重置错误
		mPhoneView.setError(null);

		// 存储用户值
		mPhone = mPhoneView.getText().toString();

		// 检查手机号
		if (TextUtils.isEmpty(mPhone)) {
			mPhoneView.setError(getString(R.string.error_field_required));
			focusView = mPhoneView;
			cancel = true;
		} else if (mPhone.length() != 11) {
			mPhoneView.setError(getString(R.string.error_phone));
			focusView = mPhoneView;
			cancel = true;
		} else {
			//检查手机号是否被注册
			RequestParams params = new RequestParams();
			params.put(UserTable.U_TEL, mPhone);
			TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {

				@Override
				public void onSuccess(int arg0, Header[] arg1, String arg2) {
					// TODO Auto-generated method stub
					if (arg0 == 200) {
						if (!arg2.equals(DefaultKeys.TEL_OK)) {
							mPhoneView.setError("该手机号已被注册！");
							focusView = mPhoneView;
							cancel = true;
							focusView.requestFocus();
						} else {
							ToastTool.showShort(ModifyPhoneActivity.this, "验证码已发送");
							Intent intent = new Intent(ModifyPhoneActivity.this, AuthCodeActivity.class);
							intent.putExtra(UserTable.U_TEL, mPhone);
							startActivity(intent);
							overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
							finish();
						}
					}
				}

				@Override
				public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
					// TODO Auto-generated method stub
					ToastTool.showLong(ModifyPhoneActivity.this, "服务器错误");
				}
			};
			AsyncHttpClientTool.post("existtel", params, responseHandler);
		}

		if (cancel) {
			// 如果错误，则提示错误
			focusView.requestFocus();
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
			attemptPhone();
			break;
		default:
			break;
		}
	}

}
