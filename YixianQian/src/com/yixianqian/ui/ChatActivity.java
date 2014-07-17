package com.yixianqian.ui;

import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.xlistview.MsgListView;

import android.os.Bundle;
import android.view.Window;

public class ChatActivity extends BaseActivity {
	private MsgListView mMsgListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.chat_main);
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		mMsgListView = (MsgListView) findViewById(R.id.msg_listView);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub

	}

}