package com.yixianqian.baidupush;

import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.yixianqian.R;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.config.Constants;
import com.yixianqian.db.MessageItemDbService;
import com.yixianqian.entities.MessageItem;
import com.yixianqian.jsonobject.JsonMessage;
import com.yixianqian.ui.MainActivity;
import com.yixianqian.utils.FastJsonTool;
import com.yixianqian.utils.UserPreference;

public class PushMessageReceiver extends FrontiaPushMessageReceiver {
	/** TAG to Log */
	public static final String TAG = PushMessageReceiver.class.getSimpleName();
	public static final int NOTIFY_ID = 0x000;
	public static int mNewNum = 0;// 通知栏新消息条目，我只是用了一个全局变量，
	public static final String RESPONSE = "response";
	public static ArrayList<EventHandler> ehList = new ArrayList<EventHandler>();
	private UserPreference userPreference;

	public static abstract interface EventHandler {
		public abstract void onMessage(JsonMessage jsonMessage);

		public abstract void onBind(String method, int errorCode, String content);

		public abstract void onNotify(String title, String content);

		public abstract void onNetChange(boolean isNetConnected);
	}

	/**
	* 调用PushManager.startWork后，sdk将对push server发起绑定请求，这个过程是异步的。绑定请求的结果通过onBind返回。
	*/
	@Override
	public void onBind(Context context, int errorCode, String appid, String userId, String channelId, String requestId) {
		userPreference = BaseApplication.getInstance().getUserPreference();
		userPreference.setBpush_ChannelID(channelId);
		userPreference.setBpush_UserID(userId);
		userPreference.setAppID(appid);
		System.out.println("channelId   " + channelId);
		System.out.println("userId   " + userId);
		System.out.println("appid   " + appid);
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
		String messageString = "透传消息 message=" + message + " customContentString=" + customContentString;
		Log.d(TAG, messageString);
		// 自定义内容获取方式，mykey和myvalue对应透传消息推送时自定义内容中设置的键和值
		if (customContentString != null & customContentString != "") {
			//			JsonMessage jsonMessage = FastJsonTool.getObject(customContentString, JsonMessage.class);
			//			if (jsonMessage != null) {
			//				parseMessage(jsonMessage);
			//				System.out.println("标识"+jsonMessage);
			//			}
		}

		System.out.println(message);
		JsonMessage jsonMessage = FastJsonTool.getObject(message, JsonMessage.class);
		if (jsonMessage != null) {
			parseMessage(jsonMessage);
		}
	}

	private void parseMessage(JsonMessage msg) {
		//如果是对话消息
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BaseApplication
				.getInstance());
		boolean messageSound = sharedPreferences.getBoolean("messageSound", true);
		if (messageSound) {
			BaseApplication.getInstance().getMessagePlayer().start();
		}
		if (ehList.size() > 0) {
			for (EventHandler eventHandler : ehList) {
				eventHandler.onMessage(msg);
			}
		} else {
			showNotify(msg);
			MessageItem messageItem = new MessageItem(null, Constants.MessageType.MESSAGE_TYPE_TEXT,
					msg.getMessageContent(), System.currentTimeMillis(), true, true, true, 1);
			MessageItemDbService messageItemDbService = MessageItemDbService.getInstance(BaseApplication.getInstance());
			messageItemDbService.messageItemDao.insert(messageItem);
		}
	}

	@SuppressWarnings("deprecation")
	private void showNotify(JsonMessage message) {
		// TODO Auto-generated method stub
		mNewNum++;
		// 更新通知栏
		BaseApplication application = BaseApplication.getInstance();

		int icon = R.drawable.f086;
		CharSequence tickerText = application.getFriendPreference().getF_nickname() + ":" + message.getMessageContent();
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);

		notification.flags = Notification.FLAG_NO_CLEAR;
		// 设置默认声音
		notification.defaults |= Notification.DEFAULT_SOUND;
		// 设定震动(需加VIBRATE权限)
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.contentView = null;

		Intent intent = new Intent(application, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(application, 0, intent, 0);
		notification.setLatestEventInfo(BaseApplication.getInstance(), application.getUserPreference().getU_nickname()
				+ " (" + mNewNum + "条新消息)", tickerText, contentIntent);

		application.getNotificationManager().notify(NOTIFY_ID, notification);// 通知一下才会生效哦
	}

	/**
	* 接收通知点击的函数。注：推送通知被用户点击前，应用无法通过接口获取通知的内容。
	*/
	@Override
	public void onNotificationClicked(Context context, String title, String description, String customContentString) {
		String notifyString = "通知点击 title=" + title + " description=" + description + " customContent="
				+ customContentString;
		// 自定义内容获取方式，mykey和myvalue对应通知推送时自定义内容中设置的键和值
		if (customContentString != null & customContentString != "") {
			//			JSONObject customJson = null;
			//			try {
			//				customJson = new JSONObject(customContentString);
			//				String myvalue = null;
			//				if (customJson.isNull("mykey")) {
			//					myvalue = customJson.getString("mykey");
			//				}
			//			} catch (JSONException e) {
			//				// TODO Auto-generated catch block
			//				e.printStackTrace();
			//			}
		}
	}

	/**
	* setTags() 的回调函数。
	*/
	@Override
	public void onSetTags(Context context, int errorCode, List<String> sucessTags, List<String> failTags,
			String requestId) {
		//		String responseString = "onSetTags errorCode=" + errorCode + " sucessTags=" + sucessTags + " failTags="
		//				+ failTags + " requestId=" + requestId;
	}

	/**
	* delTags() 的回调函数。
	*/
	@Override
	public void onDelTags(Context context, int errorCode, List<String> sucessTags, List<String> failTags,
			String requestId) {
		//		String responseString = "onDelTags errorCode=" + errorCode + " sucessTags=" + sucessTags + " failTags="
		//				+ failTags + " requestId=" + requestId;
	}

	/**
	* listTags() 的回调函数。
	*/
	@Override
	public void onListTags(Context context, int errorCode, List<String> tags, String requestId) {
		//		String responseString = "onListTags errorCode=" + errorCode + " tags=" + tags;
	}

}
