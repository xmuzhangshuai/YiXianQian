package com.yixianqian.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.baidu.android.pushservice.PushManager;
import com.easemob.chat.ConnectionListener;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMNotifier;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.base.BaseFragmentActivity;
import com.yixianqian.config.Constants;
import com.yixianqian.customewidget.MyAlertDialog;
import com.yixianqian.db.FlipperDbService;
import com.yixianqian.entities.Flipper;
import com.yixianqian.jsonobject.JsonFlipperRequest;
import com.yixianqian.table.FlipperRequestTable;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.FastJsonTool;
import com.yixianqian.utils.LogTool;
import com.yixianqian.utils.UserPreference;

/**
 * 类名称：MainActivity
 * 类描述：主页面，包括两个Fragment，一个是聊天的Fragment，一个是个人中心的Fragment
 * 创建人： 张帅
 * 创建时间：2014年7月16日 下午3:25:58
 *
 */
public class MainActivity extends BaseFragmentActivity {
	private View[] mTabs;
	private UserPreference userPreference;
	//消息接收广播
	private NewMessageBroadcastReceiver msgReceiver;
	// 账号在别处登录
	private boolean isConflict = false;
	private android.app.AlertDialog.Builder conflictBuilder;
	private boolean isConflictDialogShow;
	private HomeFragment homeFragment;
	private PersonalFragment personalFragment;
	private int index;
	// 当前fragment的index
	private int currentTabIndex;
	private Fragment[] fragments;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		userPreference = BaseApplication.getInstance().getUserPreference();
		homeFragment = new HomeFragment();
		personalFragment = new PersonalFragment();
		fragments = new Fragment[] { homeFragment, personalFragment };

		//设置标签
		List<String> tags = new ArrayList<String>();
		tags.add(userPreference.getU_gender());
		PushManager.setTags(this, tags);

		findViewById();
		initView();

		// 添加显示第一个fragment
		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, homeFragment)
				.add(R.id.fragment_container, personalFragment).hide(personalFragment).show(homeFragment).commit();

		// 注册一个接收消息的BroadcastReceiver
		msgReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
		intentFilter.setPriority(3);
		registerReceiver(msgReceiver, intentFilter);

		// 注册一个ack回执消息的BroadcastReceiver
		IntentFilter ackMessageIntentFilter = new IntentFilter(EMChatManager.getInstance()
				.getAckMessageBroadcastAction());
		ackMessageIntentFilter.setPriority(3);
		registerReceiver(ackMessageReceiver, ackMessageIntentFilter);

		// setContactListener监听联系人的变化等
		EMContactManager.getInstance().setContactListener(new MyContactListener());
		// 注册一个监听连接状态的listener
		EMChatManager.getInstance().addConnectionListener(new MyConnectionListener());
		// 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
		EMChat.getInstance().setAppInited();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		mTabs = new View[2];
		mTabs[0] = (View) findViewById(R.id.homeBtn);
		mTabs[1] = (View) findViewById(R.id.personalBtn);
		// 把第一个tab设为选中状态
		mTabs[0].setSelected(true);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (!isConflict) {
			EMChatManager.getInstance().activityResumed();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (getIntent().getBooleanExtra("conflict", false) && !isConflictDialogShow)
			showConflictDialog();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		final MyAlertDialog myAlertDialog = new MyAlertDialog(this);
		myAlertDialog.setTitle("提示");
		myAlertDialog.setMessage("是否退出一线牵客户端？");
		View.OnClickListener comfirm = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myAlertDialog.dismiss();
				close();
			}
		};
		View.OnClickListener cancle = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myAlertDialog.dismiss();
			}
		};
		myAlertDialog.setPositiveButton("确定", comfirm);
		myAlertDialog.setNegativeButton("取消", cancle);
		myAlertDialog.show();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// 注销广播接收者
		try {
			unregisterReceiver(msgReceiver);
		} catch (Exception e) {
		}
		try {
			unregisterReceiver(ackMessageReceiver);
		} catch (Exception e) {
		}

		if (conflictBuilder != null) {
			conflictBuilder.create().dismiss();
			conflictBuilder = null;
		}
	}

	/**
	 * button点击事件
	 * 
	 * @param view
	 */
	public void onTabClicked(View view) {
		switch (view.getId()) {
		case R.id.homeBtn:
			index = 0;
			break;
		case R.id.personalBtn:
			index = 1;
			break;
		}
		if (currentTabIndex != index) {
			FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
			trx.hide(fragments[currentTabIndex]);
			if (!fragments[index].isAdded()) {
				trx.add(R.id.fragment_container, fragments[index]);
			}
			trx.show(fragments[index]).commit();
		}
		mTabs[currentTabIndex].setSelected(false);
		// 把当前tab设为选中状态
		mTabs[index].setSelected(true);
		currentTabIndex = index;
	}

	/**
	 * 新消息广播接收者
	 * 
	 * 
	 */
	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 消息id
			String msgId = intent.getStringExtra("msgid");
			// 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
			// EMMessage message =
			// EMChatManager.getInstance().getMessage(msgId);

			// 刷新bottom bar消息未读数
			//			updateUnreadLabel();
			if (currentTabIndex == 0) {
				// 当前页面如果为聊天历史页面，刷新此页面
				if (homeFragment != null) {
					homeFragment.refresh();
				}
			}
			// 注销广播，否则在ChatActivity中会收到这个广播
			abortBroadcast();
		}
	}

	/**
	 * 消息回执BroadcastReceiver
	 */
	private BroadcastReceiver ackMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String msgid = intent.getStringExtra("msgid");
			String from = intent.getStringExtra("from");
			EMConversation conversation = EMChatManager.getInstance().getConversation(from);
			if (conversation != null) {
				// 把message设为已读
				EMMessage msg = conversation.getMessage(msgid);
				if (msg != null) {
					msg.isAcked = true;
				}
			}
			abortBroadcast();
		}
	};

	/**
	 * 显示帐号在别处登录dialog
	 */
	private void showConflictDialog() {
		isConflictDialogShow = true;
		BaseApplication.getInstance().logout();

		if (!MainActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (conflictBuilder == null)
					conflictBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
				conflictBuilder.setTitle("下线通知");
				conflictBuilder.setMessage(R.string.connect_conflict);
				conflictBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						conflictBuilder = null;
						finish();
						startActivity(new Intent(MainActivity.this, LoginActivity.class));
					}
				});
				conflictBuilder.setCancelable(false);
				conflictBuilder.create().show();
				isConflict = true;
			} catch (Exception e) {
				Log.e("###", "---------color conflictBuilder error" + e.getMessage());
			}

		}

	}

	/***
	 * 联系人变化listener
	 * 
	 */
	private class MyContactListener implements EMContactListener {

		//添加了心动关系时
		@Override
		public void onContactAdded(List<String> usernameList) {
			// 保存增加的联系人
			//			Map<String, User> localUsers = DemoApplication.getInstance().getContactList();
			//			Map<String, User> toAddUsers = new HashMap<String, User>();
			//			for (String username : usernameList) {
			//				User user = new User();
			//				user.setUsername(username);
			//				String headerName = null;
			//				if (!TextUtils.isEmpty(user.getNick())) {
			//					headerName = user.getNick();
			//				} else {
			//					headerName = user.getUsername();
			//				}
			//				if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
			//					user.setHeader("");
			//				} else if (Character.isDigit(headerName.charAt(0))) {
			//					user.setHeader("#");
			//				} else {
			//					user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(
			//							0, 1).toUpperCase());
			//					char header = user.getHeader().toLowerCase().charAt(0);
			//					if (header < 'a' || header > 'z') {
			//						user.setHeader("#");
			//					}
			//				}
			//				// 暂时有个bug，添加好友时可能会回调added方法两次
			//				if (!localUsers.containsKey(username)) {
			//					userDao.saveContact(user);
			//				}
			//				toAddUsers.put(username, user);
			//			}
			//			localUsers.putAll(toAddUsers);
			//			// 刷新ui
			//			if (currentTabIndex == 0)
			//				contactListFragment.refresh();
			for (String string : usernameList) {
				System.out.println(string + "添加联系人");
			}

		}

		/**
		 * 解除心动关系时
		 */
		@Override
		public void onContactDeleted(List<String> usernameList) {
			// 被删除
			//			Map<String, User> localUsers = DemoApplication.getInstance().getContactList();
			//			for (String username : usernameList) {
			//				localUsers.remove(username);
			//				userDao.deleteContact(username);
			//				inviteMessgeDao.deleteMessage(username);
			//			}
			//			// 刷新ui
			//			if (currentTabIndex == 1)
			//				contactListFragment.refresh();
			//			updateUnreadLabel();
			for (String string : usernameList) {
				System.out.println(string + "删除");
			}
		}

		//收到心动请求
		@Override
		public void onContactInvited(String username, String reason) {
			// 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不要重复提醒
			if (userPreference.getU_stateid() == 4 && !TextUtils.isEmpty(username)) {
				RequestParams params = new RequestParams();
				params.put(FlipperRequestTable.FR_USERID, userPreference.getU_id());
				params.put(FlipperRequestTable.FR_FLIPPERID, Integer.parseInt(username));

				TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {
					@Override
					public void onSuccess(int statusCode, Header[] headers, String response) {
						// TODO Auto-generated method stub
						if (statusCode == 200) {
							JsonFlipperRequest fRequest = FastJsonTool.getObject(response, JsonFlipperRequest.class);
							if (fRequest != null) {
								notifyNewFlipper(fRequest);
							} else {
								LogTool.d("心动请求", "为空");
							}
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
						// TODO Auto-generated method stub
					}
				};
				AsyncHttpClientTool.post(MainActivity.this, "getflipperrequest", params, responseHandler);
			}

			Log.d("心动请求，", username + "心动请求,reason: " + reason);

		}

		//心动请求被同意
		@Override
		public void onContactAgreed(String username) {
			//			List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
			//			for (InviteMessage inviteMessage : msgs) {
			//				if (inviteMessage.getFrom().equals(username)) {
			//					return;
			//				}
			//			}
			//			// 自己封装的javabean
			//			InviteMessage msg = new InviteMessage();
			//			msg.setFrom(username);
			//			msg.setTime(System.currentTimeMillis());
			//			Log.d(TAG, username + "同意了你的好友请求");
			//			msg.setStatus(InviteMesageStatus.BEAGREED);
			//			notifyNewIviteMessage(msg);
			//			if (!TextUtils.isEmpty(username)) {
			//				FlipperDbService flipperDbService = FlipperDbService.getInstance(MainActivity.this);
			//				Flipper flipper = flipperDbService.getFlipperByUserId(Integer.parseInt(username));
			//				flipper.setStatus(Constants.FlipperStatus.BEAGREED);
			//			}
			Log.d("心动请求，", username + "同意");
		}

		//心动请求被拒绝
		@Override
		public void onContactRefused(String username) {
			Log.d("心动请求，", username + "拒绝");
		}

	}

	/**
	 * 保存心动请求并提示新消息
	 * 
	 * @param msg
	 */
	private void notifyNewFlipper(JsonFlipperRequest flipperRequest) {
		saveFlipper(flipperRequest);
		// 提示有新消息
		EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();
	}

	/**
	 * 保存请求
	 * @param flipper
	 */
	private void saveFlipper(JsonFlipperRequest fRequest) {
		FlipperDbService flipperDbService = FlipperDbService.getInstance(MainActivity.this);
		Flipper flipper = new Flipper(null, fRequest.getU_id(), fRequest.getU_nickname(), fRequest.getU_realname(),
				fRequest.getU_gender(), fRequest.getU_email(), fRequest.getU_large_avatar(),
				fRequest.getU_small_avatar(), fRequest.getU_blood_type(), fRequest.getU_constell(),
				fRequest.getU_introduce(), fRequest.getU_birthday(), fRequest.getTime(), fRequest.getU_age(),
				fRequest.getU_vocationid(), fRequest.getU_stateid(), fRequest.getU_provinceid(),
				fRequest.getU_cityid(), fRequest.getU_schoolid(), fRequest.getU_height(), fRequest.getU_weight(),
				fRequest.getU_image_pass(), fRequest.getU_salary(), false, fRequest.getU_tel(),
				Constants.FlipperStatus.BEINVITEED, Constants.FlipperType.FROM);
		flipperDbService.flipperDao.insert(flipper);
	}

	/**
	 * 连接监听listener
	 * 
	 */
	private class MyConnectionListener implements ConnectionListener {

		@Override
		public void onConnected() {
			homeFragment.errorItem.setVisibility(View.GONE);
		}

		@Override
		public void onDisConnected(String errorString) {
			if (errorString != null && errorString.contains("conflict")) {
				// 显示帐号在其他设备登陆dialog
				showConflictDialog();
			} else {
				homeFragment.errorItem.setVisibility(View.VISIBLE);
				homeFragment.errorText.setText("连接不到聊天服务器");
			}
		}

		@Override
		public void onReConnected() {
			homeFragment.errorItem.setVisibility(View.GONE);
		}

		@Override
		public void onReConnecting() {
		}

		@Override
		public void onConnecting(String progress) {
		}

	}
}
