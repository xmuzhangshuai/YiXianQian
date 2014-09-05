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

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.db.ConversationDbService;
import com.yixianqian.entities.Conversation;
import com.yixianqian.table.LoveRequestTable;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.FriendPreference;
import com.yixianqian.utils.LogTool;
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
	private Button revokeBtn;
	private ImageView faceImageView;
	private Button goMianBtn;
	private TextView revokeText;

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
		revokeBtn = (Button) findViewById(R.id.revoke);
		faceImageView = (ImageView) findViewById(R.id.face);
		goMianBtn = (Button) findViewById(R.id.gomain);
		revokeText = (TextView) findViewById(R.id.revoke_text);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		right_btn_bg.setVisibility(View.GONE);
		topNavText.setText("添加情侣");
		textInfo.setText("对方还没有接受你的请求，快去叫醒Ta...");
		faceImageView.setImageResource(R.drawable.face_black);
		topNavLeftBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(WaitActivity.this, MainActivity.class));
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
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
		goMianBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(WaitActivity.this, MainActivity.class));
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				finish();
			}
		});

		revokeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				revoke();
			}
		});

		getLoveRequestState();
	}

	/**
	 * 撤回请求
	 */
	private void revoke() {
		RequestParams params = new RequestParams();
		params.put(LoveRequestTable.LR_USERID, userPreference.getU_id());
		params.put(LoveRequestTable.LR_LOVERID, friendpreference.getF_id());
		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
			Dialog dialog;

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
				dialog = showProgressDialog("正在撤回...");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					if (!TextUtils.isEmpty(response)) {
						if (response.equals("1")) {//撤回成功
							ToastTool.showShort(WaitActivity.this, "撤回成功");
							userPreference.setLoveRequest(false);
							startActivity(new Intent(WaitActivity.this, MainActivity.class));
							overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
							finish();
						} else if (response.equals("-1")) {//服务器错误
							LogTool.e("WaitActivity", "撤回添加情侣请求失败！");
						} else if (response.equals("0")) {//对方已经拒绝
							ToastTool.showShort(WaitActivity.this, "撤回成功");
							LogTool.i("WaitActivity", "撤回添加情侣请求,对方已经拒绝！");
							userPreference.setLoveRequest(false);
							startActivity(new Intent(WaitActivity.this, MainActivity.class));
							overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
							finish();
						} else if (response.equals("2")) {//对方已经同意
							ToastTool.showShort(WaitActivity.this, "对方已经同意，无法撤回");
							userPreference.setLoveRequest(false);
							cerateConversation();
							startActivity(new Intent(WaitActivity.this, MainActivity.class));
							overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
							finish();
						}
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				ToastTool.showLong(WaitActivity.this, "撤回添加情侣请求失败");
				LogTool.e("WaitActivity", "撤回添加情侣请求失败！" + errorResponse);
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				dialog.dismiss();
				super.onFinish();
			}
		};
		AsyncHttpClientTool.post("revokeloverequest", params, responseHandler);
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
							textInfo.setText("恭喜！对方接受了你的请求，快去亲亲Ta...");
							revokeBtn.setVisibility(View.INVISIBLE);
							faceImageView.setImageResource(R.drawable.face_red);
							refreshBtn.setVisibility(View.GONE);
							goMianBtn.setVisibility(View.VISIBLE);
							revokeText.setVisibility(View.INVISIBLE);

							userPreference.setLoveRequest(false);
							cerateConversation();

							//							friendpreference.setType(1);
							//							userPreference.setU_stateid(2);
							//
							//							//创建对话
							//							ConversationDbService conversationDbService = ConversationDbService
							//									.getInstance(WaitActivity.this);
							//							conversationDbService.conversationDao.deleteAll();
							//							Conversation conversation = new Conversation(null,
							//									Long.valueOf(friendpreference.getF_id()), friendpreference.getName(),
							//									friendpreference.getF_small_avatar(), "", 0, System.currentTimeMillis());
							//							conversationDbService.conversationDao.insert(conversation);
							//
							//							//给对方发送一条消息
							//							String msg = "我们已经成为情侣啦~！";
							//							//获取到与聊天人的会话对象。参数username为聊天人的userid或者groupid，后文中的username皆是如此
							//							EMConversation emConversation = EMChatManager.getInstance().getConversation(
							//									"" + friendpreference.getF_id());
							//							//创建一条文本消息
							//							EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
							//							//设置消息body
							//							TextMessageBody txtBody = new TextMessageBody(msg);
							//							message.addBody(txtBody);
							//							//设置接收人
							//							message.setReceipt("" + friendpreference.getF_id());
							//							//把消息加入到此会话对象中
							//							emConversation.addMessage(message);
						} else if (response.equals("2")) {
							userPreference.setLoveRequest(false);
							textInfo.setText("啊哦，对方拒绝了你的请求...");
							revokeBtn.setVisibility(View.INVISIBLE);
							faceImageView.setImageResource(R.drawable.face_unhappy);
							refreshBtn.setVisibility(View.GONE);
							goMianBtn.setVisibility(View.VISIBLE);
							revokeText.setVisibility(View.INVISIBLE);
						} else if (response.equals("3")) {
							textInfo.setText("对方还没有接受你的请求，快去叫醒Ta...");
							faceImageView.setImageResource(R.drawable.face_black);
							refreshBtn.setVisibility(View.VISIBLE);
							revokeBtn.setVisibility(View.VISIBLE);
							revokeText.setVisibility(View.VISIBLE);
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

	/**
	 * 创建会话
	 */
	private void cerateConversation() {
		friendpreference.setType(1);
		userPreference.setU_stateid(2);

		//创建对话
		ConversationDbService conversationDbService = ConversationDbService.getInstance(WaitActivity.this);
		conversationDbService.conversationDao.deleteAll();
		Conversation conversation = new Conversation(null, Long.valueOf(friendpreference.getF_id()),
				friendpreference.getName(), friendpreference.getF_small_avatar(), "", 0, System.currentTimeMillis());
		conversationDbService.conversationDao.insert(conversation);

		//给对方发送一条消息
		String msg = "我们已经成为情侣啦~！";
		//获取到与聊天人的会话对象。参数username为聊天人的userid或者groupid，后文中的username皆是如此
		EMConversation emConversation = EMChatManager.getInstance().getConversation("" + friendpreference.getF_id());
		//创建一条文本消息
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
		//设置消息body
		TextMessageBody txtBody = new TextMessageBody(msg);
		message.addBody(txtBody);
		//设置接收人
		message.setReceipt("" + friendpreference.getF_id());
		//把消息加入到此会话对象中
		emConversation.addMessage(message);
	}
}
