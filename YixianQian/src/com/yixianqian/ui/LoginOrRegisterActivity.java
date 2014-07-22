package com.yixianqian.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;

/**
 * 类名称：LoginOrRegisterActivity 类描述：注册或登录引导页面 创建人： 张帅 创建时间：2014-7-4 上午9:23:09
 * 
 */
public class LoginOrRegisterActivity extends BaseActivity {
	private Button loginButton;
	private Button registerButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_or_register);
		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		loginButton = (Button) findViewById(R.id.login_btn);
		registerButton = (Button) findViewById(R.id.register_btn);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginOrRegisterActivity.this, LoginActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});

		registerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginOrRegisterActivity.this, HeadImageActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});
	}
}
