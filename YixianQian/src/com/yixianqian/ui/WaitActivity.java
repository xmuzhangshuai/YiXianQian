package com.yixianqian.ui;

import org.apache.http.Header;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.baidupush.SendMsgAsyncTask;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.config.Constants;
import com.yixianqian.db.ConversationDbService;
import com.yixianqian.db.MessageItemDbService;
import com.yixianqian.entities.Conversation;
import com.yixianqian.entities.MessageItem;
import com.yixianqian.jsonobject.JsonMessage;
import com.yixianqian.table.LoveRequestTable;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.FastJsonTool;
import com.yixianqian.utils.FriendPreference;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

/**
 * 类名称：WaitActivity
 * 类描述：等待添加情侣页面
 * 创建人： 张帅
 * 创建时间：2014年8月4日 上午10:25:08
 *
 */
public class WaitActivity extends BaseActivity {
	private ImageView topNavLeftBtn;//导航条左边按钮
	private TextView topNavText;//导航条文字
	private View right_btn_bg;
	private Button refreshBtn;
	private TextView textInfo;
	private FriendPreference friendpreference;
	private UserPreference userPreference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_wait);

		friendpreference = BaseApplication.getInstance().getFriendPreference();
		userPreference = BaseApplication.getInstance().getUserPreference();
		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		topNavLeftBtn = (ImageView) findViewById(R.id.nav_left_btn);
		right_btn_bg = (View) findViewById(R.id.right_btn_bg);
		topNavText = (TextView) findViewById(R.id.nav_text);
		refreshBtn = (Button) findViewById(R.id.refresh);
		textInfo = (TextView) findViewById(R.id.info);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		right_btn_bg.setVisibility(View.GONE);
		topNavText.setText("等待匹配");
		textInfo.setText("对方还没有同意您的邀请，请尽快通知对方同意");
		topNavLeftBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		refreshBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getLoveRequestState();
			}
		});
	}

	/**
	 * 获取情侣邀请状态
	 */
	private void getLoveRequestState() {
		RequestParams params = new RequestParams();
		params.put(LoveRequestTable.LR_USERID, userPreference.getU_id());
		params.put(LoveRequestTable.LR_LOVERID, friendpreference.getF_id());
		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
			Dialog dialog;

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
				dialog = showProgressDialog("请稍后...");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					if (!TextUtils.isEmpty(response)) {
						if (response.equals("1")) {
							userPreference.setLoveRequest(false);
							friendpreference.setType(1);
							userPreference.setU_stateid(2);

							//创建对话
							ConversationDbService conversationDbService = ConversationDbService
									.getInstance(WaitActivity.this);
							conversationDbService.conversationDao.deleteAll();
							Conversation conversation = new Conversation(null,
									Long.valueOf(friendpreference.getF_id()), friendpreference.getName(),
									friendpreference.getF_small_avatar(), "", 0, System.currentTimeMillis());
							conversationDbService.conversationDao.insert(conversation);

							//给对方发送一条消息
							String msg = "我们已经成为情侣啦~！";
							JsonMessage message = new JsonMessage(msg, Constants.MessageType.MESSAGE_TYPE_TEXT);
							new SendMsgAsyncTask(FastJsonTool.createJsonString(message),
									friendpreference.getBpush_UserID()).send();
							MessageItem item = new MessageItem(null, Constants.MessageType.MESSAGE_TYPE_TEXT, msg,
									System.currentTimeMillis(), true, false, false,
									conversationDbService.getIdByConversation(conversation));
							MessageItemDbService messageItemDbService = MessageItemDbService
									.getInstance(WaitActivity.this);
							messageItemDbService.messageItemDao.insert(item);

							Intent intent = new Intent(WaitActivity.this, ChatActivity2.class);
							intent.putExtra("conversationID", conversationDbService.getIdByConversation(conversation));
							startActivity(intent);
							overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
							finish();
						} else if (response.equals("2")) {
							//							textInfo.setText("对方拒绝了您的邀请！");
							ToastTool.showLong(WaitActivity.this, "对方拒绝了您的邀请！");
							userPreference.setLoveRequest(false);
							Intent intent = new Intent(WaitActivity.this, MainActivity.class);
							startActivity(intent);
							overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
						} else if (response.equals("3")) {
							textInfo.setText("对方还没有同意您的邀请，请尽快通知对方同意");
						}
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				ToastTool.showLong(WaitActivity.this, "请求失败");
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				dialog.dismiss();
				super.onFinish();
			}
		};
		AsyncHttpClientTool.post("getloverequeststate", params, responseHandler);
	}
}
