package com.yixianqian.baidupush;

import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.yixianqian.R;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.config.Constants;
import com.yixianqian.db.ConversationDbService;
import com.yixianqian.jsonobject.JsonMessage;
import com.yixianqian.ui.MainActivity;
import com.yixianqian.ui.VertifyToChatActivity;
import com.yixianqian.utils.FastJsonTool;
import com.yixianqian.utils.FriendPreference;
import com.yixianqian.utils.LogTool;
import com.yixianqian.utils.NetworkUtils;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

public class MyPushMessageReceiver extends FrontiaPushMessageReceiver {
	/** TAG to Log */
	public static final String TAG = MyPushMessageReceiver.class.getSimpleName();
	public static final String MESSAGE_TYPE = "messageType";
	public static final int NOTIFY_ID = 0x000;
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
     * 调用PushManager.startWork后，sdk将对push
     * server发起绑定请求，这个过程是异步的。绑定请求的结果通过onBind返回。 如果您需要用单播推送，需要把这里获取的channel
     * id和user id上传到应用server中，再调用server接口用channel id和user id给单个手机或者用户推送。
     * 
     * @param context
     *            BroadcastReceiver的执行Context
     * @param errorCode
     *            绑定接口返回值，0 - 成功
     * @param appid
     *            应用id。errorCode非0时为null
     * @param userId
     *            应用user id。errorCode非0时为null
     * @param channelId
     *            应用channel id。errorCode非0时为null
     * @param requestId
     *            向服务端发起的请求id。在追查问题时有用；
     * @return none
     */
	@Override
	public void onBind(Context context, int errorCode, String appid, String userId, String channelId, String requestId) {
		paraseContent(context, errorCode, appid, userId, channelId);
	}


    /**
     * PushManager.stopWork() 的回调函数。
     * 
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示从云推送解绑定成功；非0表示失败。
     * @param requestId
     *            分配给对云推送的请求的id
     */
	@Override
	public void onUnbind(Context context, int errorCode, String requestId) {
	}

	/**
     * 接收透传消息的函数。
     * 
     * @param context
     *            上下文
     * @param message
     *            推送的消息
     * @param customContentString
     *            自定义内容,为空或者json字符串
     */
	@Override
	public void onMessage(Context context, String message, String customContentString) {
		String messageString = "透传消息 message=" + message + " customContentString=" + customContentString;
		LogTool.d(TAG, messageString);

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
		//		//普通消息
		//		case Constants.MessageType.MESSAGE_TYPE_TEXT:
		//			chatMessage(msg);
		//			break;
		//		//图片
		//		case Constants.MessageType.MESSAGE_TYPE_IMG:
		//
		//			break;
		//		//声音
		//		case Constants.MessageType.MESSAGE_TYPE_SOUND:
		//
		//			break;
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
		default:
			break;
		}

	}

	/**
	 * 处理添加情侣请求
	 * @param phone
	 */
	private void buildLove(String phone) {
		BaseApplication application = BaseApplication.getInstance();
		friendPreference = application.getFriendPreference();

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

		//通知
		NotificationCompat.Builder builder = new NotificationCompat.Builder(application);
		builder.setSmallIcon(R.drawable.ic_launcher).setContentTitle("怦然心动").setContentText("有人对您怦然心动了，快来看看吧~！")
				.setAutoCancel(true).setTicker("有人对您砰然心动了！").setDefaults(Notification.DEFAULT_ALL);
		Intent resultIntent = new Intent(application, VertifyToChatActivity.class);
		resultIntent.putExtra(VertifyToChatActivity.VERTIFY_TYPE, "0");
		resultIntent.putExtra(VertifyToChatActivity.PHONE, phone);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(application);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(resultPendingIntent);
		application.getNotificationManager().notify(NOTIFY_ID, builder.build());// 通知一下
	}

	/**
     * 接收通知点击的函数。注：推送通知被用户点击前，应用无法通过接口获取通知的内容。
     * 
     * @param context
     *            上下文
     * @param title
     *            推送的通知的标题
     * @param description
     *            推送的通知的描述
     * @param customContentString
     *            自定义内容，为空或者json字符串
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
     * 
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示某些tag已经设置成功；非0表示所有tag的设置均失败。
     * @param successTags
     *            设置成功的tag
     * @param failTags
     *            设置失败的tag
     * @param requestId
     *            分配给对云推送的请求的id
     */
	@Override
	public void onSetTags(Context context, int errorCode, List<String> sucessTags, List<String> failTags,
			String requestId) {
	}


    /**
     * delTags() 的回调函数。
     * 
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示某些tag已经删除成功；非0表示所有tag均删除失败。
     * @param successTags
     *            成功删除的tag
     * @param failTags
     *            删除失败的tag
     * @param requestId
     *            分配给对云推送的请求的id
     */
	@Override
	public void onDelTags(Context context, int errorCode, List<String> sucessTags, List<String> failTags,
			String requestId) {
	}

	 /**
     * listTags() 的回调函数。
     * 
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示列举tag成功；非0表示失败。
     * @param tags
     *            当前应用设置的所有tag。
     * @param requestId
     *            分配给对云推送的请求的id
     */
	@Override
	public void onListTags(Context context, int errorCode, List<String> tags, String requestId) {
	}

}
