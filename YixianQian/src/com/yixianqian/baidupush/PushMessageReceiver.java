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
	public static int mNewNum = 0;// ֪ͨ������Ϣ��Ŀ����ֻ������һ��ȫ�ֱ�����
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
	* ����PushManager.startWork��sdk����push server�������������������첽�ġ�������Ľ��ͨ��onBind���ء�
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
	* PushManager.stopWork() �Ļص�������
	*/
	@Override
	public void onUnbind(Context context, int errorCode, String requestId) {
	}

	/**
	* ����͸����Ϣ�ĺ�����
	*/
	@Override
	public void onMessage(Context context, String message, String customContentString) {
		String messageString = "͸����Ϣ message=" + message + " customContentString=" + customContentString;
		Log.d(TAG, messageString);
		// �Զ������ݻ�ȡ��ʽ��mykey��myvalue��Ӧ͸����Ϣ����ʱ�Զ������������õļ���ֵ
		if (customContentString != null & customContentString != "") {
			//			JsonMessage jsonMessage = FastJsonTool.getObject(customContentString, JsonMessage.class);
			//			if (jsonMessage != null) {
			//				parseMessage(jsonMessage);
			//				System.out.println("��ʶ"+jsonMessage);
			//			}
		}

		System.out.println(message);
		JsonMessage jsonMessage = FastJsonTool.getObject(message, JsonMessage.class);
		if (jsonMessage != null) {
			parseMessage(jsonMessage);
		}
	}

	private void parseMessage(JsonMessage msg) {
		//����ǶԻ���Ϣ
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
		// ����֪ͨ��
		BaseApplication application = BaseApplication.getInstance();

		int icon = R.drawable.f086;
		CharSequence tickerText = application.getFriendPreference().getF_nickname() + ":" + message.getMessageContent();
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);

		notification.flags = Notification.FLAG_NO_CLEAR;
		// ����Ĭ������
		notification.defaults |= Notification.DEFAULT_SOUND;
		// �趨��(���VIBRATEȨ��)
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.contentView = null;

		Intent intent = new Intent(application, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(application, 0, intent, 0);
		notification.setLatestEventInfo(BaseApplication.getInstance(), application.getUserPreference().getU_nickname()
				+ " (" + mNewNum + "������Ϣ)", tickerText, contentIntent);

		application.getNotificationManager().notify(NOTIFY_ID, notification);// ֪ͨһ�²Ż���ЧŶ
	}

	/**
	* ����֪ͨ����ĺ�����ע������֪ͨ���û����ǰ��Ӧ���޷�ͨ���ӿڻ�ȡ֪ͨ�����ݡ�
	*/
	@Override
	public void onNotificationClicked(Context context, String title, String description, String customContentString) {
		String notifyString = "֪ͨ��� title=" + title + " description=" + description + " customContent="
				+ customContentString;
		// �Զ������ݻ�ȡ��ʽ��mykey��myvalue��Ӧ֪ͨ����ʱ�Զ������������õļ���ֵ
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
	* setTags() �Ļص�������
	*/
	@Override
	public void onSetTags(Context context, int errorCode, List<String> sucessTags, List<String> failTags,
			String requestId) {
		//		String responseString = "onSetTags errorCode=" + errorCode + " sucessTags=" + sucessTags + " failTags="
		//				+ failTags + " requestId=" + requestId;
	}

	/**
	* delTags() �Ļص�������
	*/
	@Override
	public void onDelTags(Context context, int errorCode, List<String> sucessTags, List<String> failTags,
			String requestId) {
		//		String responseString = "onDelTags errorCode=" + errorCode + " sucessTags=" + sucessTags + " failTags="
		//				+ failTags + " requestId=" + requestId;
	}

	/**
	* listTags() �Ļص�������
	*/
	@Override
	public void onListTags(Context context, int errorCode, List<String> tags, String requestId) {
		//		String responseString = "onListTags errorCode=" + errorCode + " tags=" + tags;
	}

}
