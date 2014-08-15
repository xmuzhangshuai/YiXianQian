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
     * ����PushManager.startWork��sdk����push
     * server�������������������첽�ġ�������Ľ��ͨ��onBind���ء� �������Ҫ�õ������ͣ���Ҫ�������ȡ��channel
     * id��user id�ϴ���Ӧ��server�У��ٵ���server�ӿ���channel id��user id�������ֻ������û����͡�
     * 
     * @param context
     *            BroadcastReceiver��ִ��Context
     * @param errorCode
     *            �󶨽ӿڷ���ֵ��0 - �ɹ�
     * @param appid
     *            Ӧ��id��errorCode��0ʱΪnull
     * @param userId
     *            Ӧ��user id��errorCode��0ʱΪnull
     * @param channelId
     *            Ӧ��channel id��errorCode��0ʱΪnull
     * @param requestId
     *            �����˷��������id����׷������ʱ���ã�
     * @return none
     */
	@Override
	public void onBind(Context context, int errorCode, String appid, String userId, String channelId, String requestId) {
		paraseContent(context, errorCode, appid, userId, channelId);
	}


    /**
     * PushManager.stopWork() �Ļص�������
     * 
     * @param context
     *            ������
     * @param errorCode
     *            �����롣0��ʾ�������ͽ�󶨳ɹ�����0��ʾʧ�ܡ�
     * @param requestId
     *            ������������͵������id
     */
	@Override
	public void onUnbind(Context context, int errorCode, String requestId) {
	}

	/**
     * ����͸����Ϣ�ĺ�����
     * 
     * @param context
     *            ������
     * @param message
     *            ���͵���Ϣ
     * @param customContentString
     *            �Զ�������,Ϊ�ջ���json�ַ���
     */
	@Override
	public void onMessage(Context context, String message, String customContentString) {
		String messageString = "͸����Ϣ message=" + message + " customContentString=" + customContentString;
		LogTool.d(TAG, messageString);

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
		//		//��ͨ��Ϣ
		//		case Constants.MessageType.MESSAGE_TYPE_TEXT:
		//			chatMessage(msg);
		//			break;
		//		//ͼƬ
		//		case Constants.MessageType.MESSAGE_TYPE_IMG:
		//
		//			break;
		//		//����
		//		case Constants.MessageType.MESSAGE_TYPE_SOUND:
		//
		//			break;
		//�Ķ�����
		case Constants.MessageType.MESSAGE_TYPE_FLIPPER_REQUEEST:

			break;
		//Ҳ�Ķ�
		case Constants.MessageType.MESSAGE_TYPE_FLIPPER_TO:
			flipperTo(msg.getMessageContent());
			break;
		//��������
		case Constants.MessageType.MESSAGE_TYPE_LOVER:
			buildLove(msg.getMessageContent());
			break;
		default:
			break;
		}

	}

	/**
	 * ���������������
	 * @param phone
	 */
	private void buildLove(String phone) {
		BaseApplication application = BaseApplication.getInstance();
		friendPreference = application.getFriendPreference();

		//֪ͨ
		NotificationCompat.Builder builder = new NotificationCompat.Builder(application);
		builder.setSmallIcon(R.drawable.ic_launcher).setContentTitle("��������").setContentText("�����������Ϊ����")
				.setAutoCancel(true).setTicker("�������룡").setDefaults(Notification.DEFAULT_ALL);
		Intent resultIntent = new Intent(application, VertifyToChatActivity.class);
		resultIntent.putExtra(VertifyToChatActivity.VERTIFY_TYPE, "1");
		resultIntent.putExtra(VertifyToChatActivity.PHONE, phone);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(application);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(resultPendingIntent);
		application.getNotificationManager().notify(NOTIFY_ID, builder.build());// ֪ͨһ��
	}

	/**
	 * ����Ҳ�Ķ�,�ڰ�����֤ҳ�棬��ĳ����Ҳ�Ķ������ͱ��û�ID������˵��ն�
	 */
	private void flipperTo(String phone) {
		friendPreference = BaseApplication.getInstance().getFriendPreference();
		BaseApplication application = BaseApplication.getInstance();

		//֪ͨ
		NotificationCompat.Builder builder = new NotificationCompat.Builder(application);
		builder.setSmallIcon(R.drawable.ic_launcher).setContentTitle("��Ȼ�Ķ�").setContentText("���˶�����Ȼ�Ķ��ˣ�����������~��")
				.setAutoCancel(true).setTicker("���˶�����Ȼ�Ķ��ˣ�").setDefaults(Notification.DEFAULT_ALL);
		Intent resultIntent = new Intent(application, VertifyToChatActivity.class);
		resultIntent.putExtra(VertifyToChatActivity.VERTIFY_TYPE, "0");
		resultIntent.putExtra(VertifyToChatActivity.PHONE, phone);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(application);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(resultPendingIntent);
		application.getNotificationManager().notify(NOTIFY_ID, builder.build());// ֪ͨһ��
	}

	/**
     * ����֪ͨ����ĺ�����ע������֪ͨ���û����ǰ��Ӧ���޷�ͨ���ӿڻ�ȡ֪ͨ�����ݡ�
     * 
     * @param context
     *            ������
     * @param title
     *            ���͵�֪ͨ�ı���
     * @param description
     *            ���͵�֪ͨ������
     * @param customContentString
     *            �Զ������ݣ�Ϊ�ջ���json�ַ���
     */
	@Override
	public void onNotificationClicked(Context context, String title, String description, String customContentString) {
	}

	/**
	 * �����¼���
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
					ToastTool.showLong(context, "�˺��ѹ��ڣ������µ�¼");
					// ��ת�����µ�¼�Ľ���
					Intent intent = new Intent();
					intent.setAction("com.yixianqian.loginorregister");
					context.startActivity(intent);
				} else {
					ToastTool.showLong(context, "����ʧ�ܣ���������...");
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							PushManager.startWork(context, PushConstants.LOGIN_TYPE_API_KEY,
									Constants.BaiduPushConfig.API_KEY);
						}
					}, 2000);// ��������¿�ʼ��֤
				}
			} else {
				ToastTool.showLong(context, "�����쳣");
			}
		}
	}


    /**
     * setTags() �Ļص�������
     * 
     * @param context
     *            ������
     * @param errorCode
     *            �����롣0��ʾĳЩtag�Ѿ����óɹ�����0��ʾ����tag�����þ�ʧ�ܡ�
     * @param successTags
     *            ���óɹ���tag
     * @param failTags
     *            ����ʧ�ܵ�tag
     * @param requestId
     *            ������������͵������id
     */
	@Override
	public void onSetTags(Context context, int errorCode, List<String> sucessTags, List<String> failTags,
			String requestId) {
	}


    /**
     * delTags() �Ļص�������
     * 
     * @param context
     *            ������
     * @param errorCode
     *            �����롣0��ʾĳЩtag�Ѿ�ɾ���ɹ�����0��ʾ����tag��ɾ��ʧ�ܡ�
     * @param successTags
     *            �ɹ�ɾ����tag
     * @param failTags
     *            ɾ��ʧ�ܵ�tag
     * @param requestId
     *            ������������͵������id
     */
	@Override
	public void onDelTags(Context context, int errorCode, List<String> sucessTags, List<String> failTags,
			String requestId) {
	}

	 /**
     * listTags() �Ļص�������
     * 
     * @param context
     *            ������
     * @param errorCode
     *            �����롣0��ʾ�о�tag�ɹ�����0��ʾʧ�ܡ�
     * @param tags
     *            ��ǰӦ�����õ�����tag��
     * @param requestId
     *            ������������͵������id
     */
	@Override
	public void onListTags(Context context, int errorCode, List<String> tags, String requestId) {
	}

}
