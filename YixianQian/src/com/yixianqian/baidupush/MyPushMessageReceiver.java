package com.yixianqian.baidupush;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.config.Constants;
import com.yixianqian.db.ConversationDbService;
import com.yixianqian.db.MessageItemDbService;
import com.yixianqian.entities.Conversation;
import com.yixianqian.entities.MessageItem;
import com.yixianqian.jsonobject.JsonMessage;
import com.yixianqian.jsonobject.JsonUser;
import com.yixianqian.table.UserTable;
import com.yixianqian.ui.ChatActivity;
import com.yixianqian.ui.MainActivity;
import com.yixianqian.ui.VertifyToChatActivity;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.FastJsonTool;
import com.yixianqian.utils.FriendPreference;
import com.yixianqian.utils.NetworkUtils;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

public class MyPushMessageReceiver extends FrontiaPushMessageReceiver {
	/** TAG to Log */
	public static final String TAG = MyPushMessageReceiver.class.getSimpleName();
	public static final String MESSAGE_TYPE = "messageType";
	public static final int NOTIFY_ID = 0x000;
	public static int mNewNum = 0;// 通知栏新消息条目，我只是用了一个全局变量，
	public static final String RESPONSE = "response";
	public static ArrayList<EventHandler> ehList = new ArrayList<EventHandler>();
	private UserPreference userPreference;
	private FriendPreference friendPreference;
	private ConversationDbService conversationDbService;

	public static abstract interface EventHandler {
		public abstract void onMessage(JsonMessage jsonMessage);

		public abstract void onNotify(String title, String content);
	}

	/**
	* 调用PushManager.startWork后，sdk将对push server发起绑定请求，这个过程是异步的。绑定请求的结果通过onBind返回。
	*/
	@Override
	public void onBind(Context context, int errorCode, String appid, String userId, String channelId, String requestId) {
		paraseContent(context, errorCode, appid, userId, channelId);
	}

	/**
	* PushManager.stopWork() 的回调函数。
	*/
	@Override
	public void onUnbind(Context context, int errorCode, String requestId) {

	}

	/**
	* 接收透传消息的函数。
	*/
	@Override
	public void onMessage(Context context, String message, String customContentString) {
		JsonMessage jsonMessage = FastJsonTool.getObject(message, JsonMessage.class);
		if (jsonMessage != null) {
			parseMessage(jsonMessage);
		}
	}

	/**
	 * 装换消息
	 * @param msg
	 */
	private void parseMessage(JsonMessage msg) {
		//判断是否开启声音
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BaseApplication
				.getInstance());
		boolean messageSound = sharedPreferences.getBoolean("messageSound", true);
		if (messageSound) {
			BaseApplication.getInstance().getMessagePlayer().start();
		}

		int type = msg.getType();
		switch (type) {
		//普通消息
		case Constants.MessageType.MESSAGE_TYPE_TEXT:
			chatMessage(msg);
			break;
		//图片
		case Constants.MessageType.MESSAGE_TYPE_IMG:

			break;
		//声音
		case Constants.MessageType.MESSAGE_TYPE_SOUND:

			break;
		//心动请求
		case Constants.MessageType.MESSAGE_TYPE_FLIPPER_REQUEEST:

			break;
		//也心动
		case Constants.MessageType.MESSAGE_TYPE_FLIPPER_TO:
			flipperTo(msg.getMessageContent());
			break;
		//情侣请求
		case Constants.MessageType.MESSAGE_TYPE_LOVER:
			buildLove(msg.getMessageContent());
			break;
		//文件
		case Constants.MessageType.MESSAGE_TYPE_FILE:

			break;
		//通知
		case Constants.MessageType.MESSAGE_TYPE_NOTIFY:

			break;
		default:
			break;
		}

	}

	/**
	 * 处理聊天消息
	 * @param msg
	 */
	private void chatMessage(JsonMessage msg) {
		if (ehList.size() > 0) {
			for (EventHandler eventHandler : ehList) {
				eventHandler.onMessage(msg);
			}
		} else {
			showNotify(msg);
			Conversation conversation = ConversationDbService.getInstance(BaseApplication.getInstance())
					.getConversationByUser(friendPreference.getF_id());

			if (conversation != null) {
				MessageItem messageItem = new MessageItem(null, Constants.MessageType.MESSAGE_TYPE_TEXT,
						msg.getMessageContent(), System.currentTimeMillis(), true, true, true, conversation.getId());
				MessageItemDbService messageItemDbService = MessageItemDbService.getInstance(BaseApplication
						.getInstance());
				messageItemDbService.messageItemDao.insert(messageItem);
				conversation.setLastMessage(msg.getMessageContent());
				conversation.setNewNum(mNewNum + 1);
				conversation.setTime(msg.getTimeSamp());
				conversationDbService.conversationDao.update(conversation);
			}
		}
	}

	private void showNotify(JsonMessage message) {
		// TODO Auto-generated method stub
		mNewNum++;
		// 更新通知栏
		BaseApplication application = BaseApplication.getInstance();
		userPreference = application.getUserPreference();
		friendPreference = application.getFriendPreference();
		conversationDbService = ConversationDbService.getInstance(application);

		//通知
		NotificationCompat.Builder builder = new NotificationCompat.Builder(application);
		String title = userPreference.getName() + "(" + mNewNum + "条新消息)";
		String text = friendPreference.getName() + ": " + message.getMessageContent();
		builder.setSmallIcon(R.drawable.ic_launcher).setContentTitle(title).setContentText(text).setAutoCancel(true)
				.setTicker("新消息！").setDefaults(Notification.DEFAULT_ALL);
		Intent resultIntent = new Intent(application, ChatActivity.class);
		resultIntent.putExtra("conversationID", conversationDbService.getConIdByUserId(friendPreference.getF_id()));
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(application);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(resultPendingIntent);
		application.getNotificationManager().notify(NOTIFY_ID, builder.build());// 通知一下
	}

	/**
	 * 处理添加情侣请求
	 * @param phone
	 */
	private void buildLove(String phone) {
		BaseApplication application = BaseApplication.getInstance();
		friendPreference = application.getFriendPreference();

		//		getLoverInfo(phone);//获取信息

		//通知
		NotificationCompat.Builder builder = new NotificationCompat.Builder(application);
		builder.setSmallIcon(R.drawable.ic_launcher).setContentTitle("情侣邀请").setContentText("有人想添加您为情侣")
				.setAutoCancel(true).setTicker("情侣邀请！").setDefaults(Notification.DEFAULT_ALL);
		Intent resultIntent = new Intent(application, VertifyToChatActivity.class);
		resultIntent.putExtra(VertifyToChatActivity.VERTIFY_TYPE, "1");
		resultIntent.putExtra(VertifyToChatActivity.PHONE, phone);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(application);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(resultPendingIntent);
		application.getNotificationManager().notify(NOTIFY_ID, builder.build());// 通知一下
	}

	/**
	 * 处理也心动,在爱情验证页面，对某个人也心动，推送本用户ID到这个人的终端
	 */
	private void flipperTo(String phone) {
		friendPreference = BaseApplication.getInstance().getFriendPreference();
		BaseApplication application = BaseApplication.getInstance();

		getLoverInfo(phone);//获取信息

		//通知
		NotificationCompat.Builder builder = new NotificationCompat.Builder(application);
		builder.setSmallIcon(R.drawable.ic_launcher).setContentTitle(friendPreference.getName())
				.setContentText("对您怦然心动").setAutoCancel(true).setTicker("有人对您砰然心动了！")
				.setDefaults(Notification.DEFAULT_ALL);
		Intent resultIntent = new Intent(application, VertifyToChatActivity.class);
		resultIntent.putExtra(VertifyToChatActivity.VERTIFY_TYPE, "0");
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(application);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(resultPendingIntent);
		application.getNotificationManager().notify(NOTIFY_ID, builder.build());// 通知一下
	}

	/**
	 * 获取情侣信息
	 */
	private void getLoverInfo(String phone) {
		if (!TextUtils.isEmpty(phone)) {
			RequestParams params = new RequestParams();
			params.put(UserTable.U_TEL, phone);
			String url = "getuserbytel";
			TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, Header[] headers, String response) {
					// TODO Auto-generated method stub
					if (statusCode == 200) {
						JsonUser lover = FastJsonTool.getObject(response, JsonUser.class);
						if (lover != null) {
							friendPreference.setBpush_ChannelID(lover.getU_bpush_channel_id());
							friendPreference.setBpush_UserID(lover.getU_bpush_user_id());
							friendPreference.setF_address(lover.getU_address());
							friendPreference.setF_age(lover.getU_age());
							friendPreference.setF_blood_type(lover.getU_blood_type());
							friendPreference.setF_constell(lover.getU_constell());
							friendPreference.setF_email(lover.getU_email());
							friendPreference.setF_gender(lover.getU_gender());
							friendPreference.setF_height(lover.getU_height());
							friendPreference.setF_id(lover.getU_id());
							friendPreference.setF_introduce(lover.getU_introduce());
							friendPreference.setF_large_avatar(lover.getU_large_avatar());
							friendPreference.setF_nickname(lover.getU_nickname());
							friendPreference.setF_realname(lover.getU_realname());
							friendPreference.setF_salary(lover.getU_salary());
							friendPreference.setF_small_avatar(lover.getU_small_avatar());
							friendPreference.setF_stateid(lover.getU_stateid());
							friendPreference.setF_tel(lover.getU_tel());
							friendPreference.setF_vocationid(lover.getU_vocationid());
							friendPreference.setF_weight(lover.getU_weight());
							friendPreference.setU_cityid(lover.getU_cityid());
							friendPreference.setU_provinceid(lover.getU_provinceid());
							friendPreference.setU_schoolid(lover.getU_schoolid());
						}
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
					// TODO Auto-generated method stub
				}
			};
			AsyncHttpClientTool.post(url, params, responseHandler);
		}
	}

	/**
	* 接收通知点击的函数。注：推送通知被用户点击前，应用无法通过接口获取通知的内容。
	*/
	@Override
	public void onNotificationClicked(Context context, String title, String description, String customContentString) {
	}

	/**
	 * 处理登录结果
	 * 
	 * @param errorCode
	 * @param content
	 */
	private void paraseContent(final Context context, int errorCode, String appid, String userId, String channelId) {
		// TODO Auto-generated method stub
		if (errorCode == 0) {
			userPreference = BaseApplication.getInstance().getUserPreference();
			userPreference.setAppID(appid);
			userPreference.setBpush_ChannelID(channelId);
			userPreference.setBpush_UserID(userId);
		} else {
			if (NetworkUtils.isNetworkAvailable(context)) {
				if (errorCode == 30607) {
					ToastTool.showLong(context, "账号已过期，请重新登录");
					// 跳转到重新登录的界面
					Intent intent = new Intent();
					intent.setAction("com.yixianqian.loginorregister");
					context.startActivity(intent);
				} else {
					ToastTool.showLong(context, "启动失败，正在重试...");
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							PushManager.startWork(context, PushConstants.LOGIN_TYPE_API_KEY,
									Constants.BaiduPushConfig.API_KEY);
						}
					}, 2000);// 两秒后重新开始验证
				}
			} else {
				ToastTool.showLong(context, "网络异常");
			}
		}
	}

	/**
	* setTags() 的回调函数。
	*/
	@Override
	public void onSetTags(Context context, int errorCode, List<String> sucessTags, List<String> failTags,
			String requestId) {
	}

	/**
	* delTags() 的回调函数。
	*/
	@Override
	public void onDelTags(Context context, int errorCode, List<String> sucessTags, List<String> failTags,
			String requestId) {
	}

	/**
	* listTags() 的回调函数。
	*/
	@Override
	public void onListTags(Context context, int errorCode, List<String> tags, String requestId) {
	}

}
