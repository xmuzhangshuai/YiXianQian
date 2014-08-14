package com.yixianqian.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;

import u.aly.co;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.baidupush.SendNotifyTask;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.base.BaseFragmentActivity;
import com.yixianqian.config.Constants;
import com.yixianqian.customewidget.MyAlertDialog;
import com.yixianqian.db.ConversationDbService;
import com.yixianqian.db.FlipperDbService;
import com.yixianqian.entities.Conversation;
import com.yixianqian.entities.Flipper;
import com.yixianqian.jsonobject.JsonFlipperRequest;
import com.yixianqian.jsonobject.JsonUser;
import com.yixianqian.table.FlipperRequestTable;
import com.yixianqian.table.FlipperTable;
import com.yixianqian.table.LoversTable;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.FastJsonTool;
import com.yixianqian.utils.FriendPreference;
import com.yixianqian.utils.HttpUtil;
import com.yixianqian.utils.LogTool;
import com.yixianqian.utils.ToastTool;
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
	private FriendPreference friendpreference;
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
	private JsonUser jsonUser;
	private MyAlertDialog myAlertDialog;
	private ConversationDbService conversationDbService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		userPreference = BaseApplication.getInstance().getUserPreference();
		friendpreference = BaseApplication.getInstance().getFriendPreference();
		conversationDbService = ConversationDbService.getInstance(MainActivity.this);
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
			Log.e("心动请求，", "以下是被删除：");

			for (String username : usernameList) {
				Log.e("心动请求，", "被" + username + "删除");
				showDeletDialog(Integer.parseInt(username));
			}
		}

		//收到心动请求
		@Override
		public void onContactInvited(final String username, String reason) {
			Log.e("心动请求，", "收到心动请求，来自" + username + ",reason: " + reason);

			// 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不要重复提醒
			Map<String, String> map = new HashMap<String, String>();
			map.put("" + FlipperRequestTable.FR_USERID, "" + userPreference.getU_id());
			map.put("" + FlipperRequestTable.FR_FLIPPERID, username);
			try {
				String response = HttpUtil.postRequest("getjsonflipperrequest", map);
				JsonFlipperRequest fRequest = FastJsonTool.getObject(response, JsonFlipperRequest.class);
				if (fRequest != null) {
					notifyNewFlipper(fRequest);
				} else {
					LogTool.e("心动请求", "JsonFlipperRequest为空");
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			//			new Thread(new Runnable() {
			//
			//				@Override
			//				public void run() {
			//					// TODO Auto-generated method stub
			//					try {
			//						EMChatManager.getInstance().refuseInvitation(username);
			//					} catch (EaseMobException e) {
			//						// TODO Auto-generated catch block
			//						e.printStackTrace();
			//					}
			//				}
			//			}).start();

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
			Log.d("心动请求，", username + "被同意");
			ToastTool.showLong(MainActivity.this, "您对" + username + "的心动请求已经被同意");

			FlipperDbService flipperDbService = FlipperDbService.getInstance(MainActivity.this);
			Flipper flipper = flipperDbService.getFlipperByUserId(Integer.parseInt(username));
			if (flipper != null) {
				flipper.setStatus(Constants.FlipperStatus.BEAGREED);
				flipperDbService.flipperDao.update(flipper);
				saveFlipperRelation(flipper);
			} else {
				saveFlipperRealitionById(Integer.parseInt(username));
			}
		}

		//心动请求被拒绝
		@Override
		public void onContactRefused(String username) {
			Log.d("心动请求，", username + "拒绝");
			ToastTool.showLong(MainActivity.this, "您对" + username + "的心动请求被拒绝");
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
		ToastTool.showShort(getApplicationContext(), flipperRequest.getU_nickname() + "对你怦然心动");
	}

	/**
	 * 保存请求
	 * @param flipper
	 */
	private void saveFlipper(JsonFlipperRequest fRequest) {
		FlipperDbService flipperDbService = FlipperDbService.getInstance(MainActivity.this);
		Flipper flipper = flipperDbService.getFlipperByUserId(fRequest.getU_id());
		//如果数据库中存在该用户的请求，则更新状态
		if (flipper != null) {
			flipper.setIsRead(false);
			flipper.setTime(new Date());
			flipper.setStatus(Constants.FlipperStatus.BEINVITEED);
			flipper.setType(Constants.FlipperType.FROM);
			flipperDbService.flipperDao.update(flipper);
		} else {
			flipper = new Flipper(null, fRequest.getU_id(), fRequest.getU_bpush_user_id(),
					fRequest.getU_bpush_channel_id(), fRequest.getU_nickname(), fRequest.getU_realname(),
					fRequest.getU_gender(), fRequest.getU_email(), fRequest.getU_large_avatar(),
					fRequest.getU_small_avatar(), fRequest.getU_blood_type(), fRequest.getU_constell(),
					fRequest.getU_introduce(), fRequest.getU_birthday(), fRequest.getTime(), fRequest.getU_age(),
					fRequest.getU_vocationid(), fRequest.getU_stateid(), fRequest.getU_provinceid(),
					fRequest.getU_cityid(), fRequest.getU_schoolid(), fRequest.getU_height(), fRequest.getU_weight(),
					fRequest.getU_image_pass(), fRequest.getU_salary(), false, fRequest.getU_tel(),
					Constants.FlipperStatus.BEINVITEED, Constants.FlipperType.FROM);
			flipperDbService.flipperDao.insert(flipper);
		}

	}

	/**
	 * 保存心动者信息
	 */
	private void saveFlipperRelation(Flipper flipper) {
		userPreference.setU_stateid(3);
		friendpreference.setType(0);
		friendpreference.setBpush_ChannelID(flipper.getBpushChannelID());
		friendpreference.setBpush_UserID(flipper.getBpushUserID());
		friendpreference.setF_age(flipper.getAge());
		friendpreference.setF_blood_type(flipper.getBloodType());
		friendpreference.setF_constell(flipper.getConstell());
		friendpreference.setF_email(flipper.getEmail());
		friendpreference.setF_gender(flipper.getGender());
		friendpreference.setF_height(flipper.getHeight());
		friendpreference.setF_id(flipper.getUserID());
		friendpreference.setF_introduce(flipper.getIntroduce());
		friendpreference.setF_large_avatar(flipper.getLargeAvatar());
		friendpreference.setF_nickname(flipper.getNickname());
		friendpreference.setF_realname(flipper.getRealname());
		friendpreference.setF_salary(flipper.getSalary());
		friendpreference.setF_small_avatar(flipper.getSamllAvatar());
		friendpreference.setF_stateid(flipper.getStateID());
		friendpreference.setF_vocationid(flipper.getVocationID());
		friendpreference.setF_weight(flipper.getWeight());
		friendpreference.setU_cityid(flipper.getCityID());
		friendpreference.setU_provinceid(flipper.getProvinceID());
		friendpreference.setU_schoolid(flipper.getSchoolID());
		creatConversation();
	}

	/**
	 * 	网络获取User信息
	 */
	private void saveFlipperRealitionById(int userId) {
		jsonUser = new JsonUser();
		RequestParams params = new RequestParams();
		params.put(UserTable.U_ID, userId);

		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {
			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					jsonUser = FastJsonTool.getObject(response, JsonUser.class);
					if (jsonUser != null) {
						userPreference.setU_stateid(3);
						friendpreference.setType(0);
						friendpreference.setBpush_ChannelID(jsonUser.getU_bpush_channel_id());
						friendpreference.setBpush_UserID(jsonUser.getU_bpush_user_id());
						friendpreference.setF_address(jsonUser.getU_address());
						friendpreference.setF_age(jsonUser.getU_age());
						friendpreference.setF_blood_type(jsonUser.getU_blood_type());
						friendpreference.setF_constell(jsonUser.getU_constell());
						friendpreference.setF_email(jsonUser.getU_email());
						friendpreference.setF_gender(jsonUser.getU_gender());
						friendpreference.setF_height(jsonUser.getU_height());
						friendpreference.setF_id(jsonUser.getU_id());
						friendpreference.setF_introduce(jsonUser.getU_introduce());
						friendpreference.setF_large_avatar(jsonUser.getU_large_avatar());
						friendpreference.setF_nickname(jsonUser.getU_nickname());
						friendpreference.setF_realname(jsonUser.getU_realname());
						friendpreference.setF_salary(jsonUser.getU_salary());
						friendpreference.setF_small_avatar(jsonUser.getU_small_avatar());
						friendpreference.setF_stateid(jsonUser.getU_stateid());
						friendpreference.setF_tel(jsonUser.getU_tel());
						friendpreference.setF_vocationid(jsonUser.getU_vocationid());
						friendpreference.setF_weight(jsonUser.getU_weight());
						friendpreference.setU_cityid(jsonUser.getU_cityid());
						friendpreference.setU_provinceid(jsonUser.getU_provinceid());
						friendpreference.setU_schoolid(jsonUser.getU_schoolid());

						creatConversation();
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				ToastTool.showLong(MainActivity.this, "服务器错误");
			}
		};
		AsyncHttpClientTool.post(MainActivity.this, "getuserbyid", params, responseHandler);
	}

	/**
	 * 创建会话
	 */
	private void creatConversation() {
		conversationDbService.conversationDao.deleteAll();
		Conversation conversation = new Conversation(null, Long.valueOf(friendpreference.getF_id()),
				friendpreference.getName(), friendpreference.getF_small_avatar(), "", 0, System.currentTimeMillis());
		conversationDbService.conversationDao.insert(conversation);

		//给对方发送一条消息
		String msg = "我和你一见钟情，开始聊天吧~！";
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

		if (currentTabIndex == 0) {
			// 当前页面如果为聊天历史页面，刷新此页面
			if (homeFragment != null) {
				homeFragment.refresh();
			}
		}
	}

	//显示删除心动或情侣对话窗口
	private void showDeletDialog(final int userID) {
		//如果是心动关系
		if (userPreference.getU_stateid() == 3) {
			myAlertDialog = new MyAlertDialog(MainActivity.this);
			myAlertDialog.setTitle("提示");
			myAlertDialog.setMessage(friendpreference.getName() + "解除了和您的心动关系");
			View.OnClickListener comfirm = new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					myAlertDialog.dismiss();
					conversationDbService.conversationDao.delete(conversationDbService.getConversationByUser(userID));

					if (currentTabIndex == 0) {
						// 当前页面如果为聊天历史页面，刷新此页面
						if (homeFragment != null) {
							homeFragment.refresh();
						}
					}

					//删除会话
					EMChatManager.getInstance().deleteConversation("" + friendpreference.getF_id());
					friendpreference.clear();
					userPreference.setU_stateid(4);
				}
			};
			myAlertDialog.setPositiveButton("确定", comfirm);
			myAlertDialog.show();
		}
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
