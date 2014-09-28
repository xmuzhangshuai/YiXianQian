package com.yixianqian.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.utils.UserPreference;

/** 
* 类名称: MyHostInviteCodeActivity 
* 描述: TODO(我拥有的邀请码) 
* 作者：张帅
* 时间： 2014年9月28日 上午10:01:24 
*  
*/
public class MyHostInviteCodeActivity extends BaseActivity {
	public static final String INVITY_CODE_FLAGS = "flags";
	public static final int AFTER_REGISTER = 1;
	public static final int MY_HOST_INVITE_CODE = 2;
	private int type = 0;

	private TextView topNavigation;
	private View leftImageButton;
	private View rightImageButton;
	private TextView myInvityCode;
	private TextView info;
	private UserPreference userPreference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_myhost_invite_code);

		type = getIntent().getIntExtra(INVITY_CODE_FLAGS, 0);
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
		myInvityCode = (TextView) findViewById(R.id.invity_code);
		info = (TextView) findViewById(R.id.info);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavigation.setText("邀请特权");

		if (type == AFTER_REGISTER) {
			info.setVisibility(View.VISIBLE);
			leftImageButton.setVisibility(View.GONE);
			rightImageButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(MyHostInviteCodeActivity.this, HeadImageActivity.class);
					startActivity(intent);
					MyHostInviteCodeActivity.this.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
					MyHostInviteCodeActivity.this.finish();
				}
			});
		} else {
			info.setVisibility(View.GONE);
			leftImageButton.setVisibility(View.VISIBLE);
			leftImageButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
				}
			});
			rightImageButton.setVisibility(View.GONE);
		}
		myInvityCode.setText(userPreference.getMyInviteCode());

	}
}
