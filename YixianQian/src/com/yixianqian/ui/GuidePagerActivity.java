package com.yixianqian.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.yixianqian.R;
import com.yixianqian.base.BaseFragmentActivity;

public class GuidePagerActivity extends BaseFragmentActivity {
	private Button beginBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide_pager);
		findViewById();
		initView();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		beginBtn = (Button) findViewById(R.id.begin);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		beginBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(GuidePagerActivity.this, LoginOrRegisterActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});
	}
}
