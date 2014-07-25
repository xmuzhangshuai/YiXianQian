package com.yixianqian.baidupush;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;

import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.config.Constants;
import com.yixianqian.db.MessageItemDbService;
import com.yixianqian.entities.MessageItem;
import com.yixianqian.jsonobject.JsonMessage;
import com.yixianqian.jsonobject.JsonUser;
import com.yixianqian.table.UserTable;
import com.yixianqian.ui.MainActivity;
import com.yixianqian.ui.VertifyToChatActivity;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.FastJsonTool;
import com.yixianqian.utils.FriendPreference;
import com.yixianqian.utils.UserPreference;

public class MyPushMessageReceiver extends FrontiaPushMessageReceiver {
	/** TAG to Log */
	public static final String TAG = MyPushMessageReceiver.class.getSimpleName();
	public static final String MESSAGE_TYPE = "messageType";
	public static final int NOTIFY_ID = 0x000;
	public static int mNewNum = 0;// ֪ͨ������Ϣ��Ŀ����ֻ������һ��ȫ�ֱ�����
	public static final String RESPONSE = "response";
	public static ArrayList<EventHandler> ehList = new ArrayList<EventHandler>();
	private UserPreference userPreference;
	private FriendPreference friendPreference;

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

		JsonMessage jsonMessage = FastJsonTool.getObject(message, JsonMessage.class);
		if (jsonMessage != null) {
			parseMessage(jsonMessage);
		}
	}

	/**
	 * װ����Ϣ
	 * @param msg
	 */
	private void parseMessage(JsonMessage msg) {

		//�ж��Ƿ�������
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BaseApplication
				.getInstance());
		boolean messageSound = sharedPreferences.getBoolean("messageSound", true);
		if (messageSound) {
			BaseApplication.getInstance().getMessagePlayer().start();
		}

		int type = msg.getType();
		switch (type) {
		//��ͨ��Ϣ
		case Constants.MessageType.MESSAGE_TYPE_TEXT:
			chatMessage(msg);
			break;
		//ͼƬ
		case Constants.MessageType.MESSAGE_TYPE_IMG:

			break;
		//����
		case Constants.MessageType.MESSAGE_TYPE_SOUND:

			break;
		//�Ķ�����
		case Constants.MessageType.MESSAGE_TYPE_FLIPPER_REQUEEST:

			break;
		//Ҳ�Ķ�
		case Constants.MessageType.MESSAGE_TYPE_FLIPPER_TO:
			flipperTo(msg.getMessageContent());
			break;
		//��������
		case Constants.MessageType.MESSAGE_TYPE_LOVER:

			break;
		//�ļ�
		case Constants.MessageType.MESSAGE_TYPE_FILE:

			break;
		//֪ͨ
		case Constants.MessageType.MESSAGE_TYPE_NOTIFY:

			break;
		default:
			break;
		}

	}

	/**
	 * ����������Ϣ
	 * @param msg
	 */
	private void chatMessage(JsonMessage msg) {
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

	/**
	 * ����Ҳ�Ķ�,�ڰ�����֤ҳ�棬��ĳ����Ҳ�Ķ������ͱ��û�ID������˵��ն�
	 */
	private void flipperTo(String phone) {
		friendPreference = BaseApplication.getInstance().getFriendPreference();
		BaseApplication application = BaseApplication.getInstance();

		getLoverInfo(phone);//��ȡ��Ϣ

		String name = friendPreference.getF_nickname();
		if (friendPreference.getF_realname() != null) {
			if (friendPreference.getF_realname().length() > 0) {
				name = friendPreference.getF_realname();
			}
		}
		//֪ͨ
		NotificationCompat.Builder builder = new NotificationCompat.Builder(application);
		builder.setSmallIcon(R.drawable.ic_launcher).setContentTitle(name).setContentText("������Ȼ�Ķ�").setAutoCancel(true)
				.setTicker("���˶�����Ȼ�Ķ��ˣ�").setDefaults(Notification.DEFAULT_ALL);
		Intent resultIntent = new Intent(application, VertifyToChatActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(application);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(resultPendingIntent);
		application.getNotificationManager().notify(NOTIFY_ID, builder.build());// ֪ͨһ��
	}

	/**
	 * ��ȡ������Ϣ
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

	@SuppressWarnings("deprecation")
	private void showNotify(JsonMessage message) {
		// TODO Auto-generated method stub
		mNewNum++;
		// ����֪ͨ��
		BaseApplication application = BaseApplication.getInstance();

		CharSequence tickerText = application.getFriendPreference().getF_nickname() + ":" + message.getMessageContent();
		long when = System.currentTimeMillis();
		Notification notification = new Notification(R.drawable.ic_launcher, tickerText, when);

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
