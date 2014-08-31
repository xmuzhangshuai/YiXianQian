package com.yixianqian.baidupush;

import java.util.List;

import org.apache.http.Header;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
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
import com.yixianqian.jsonobject.JsonMessage;
import com.yixianqian.table.UserTable;
import com.yixianqian.ui.MainActivity;
import com.yixianqian.ui.VertifyToChatActivity;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.CommonTools;
import com.yixianqian.utils.FastJsonTool;
import com.yixianqian.utils.LogTool;
import com.yixianqian.utils.NetworkUtils;
import com.yixianqian.utils.PreferenceUtils;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

public class MyPushMessageReceiver extends FrontiaPushMessageReceiver {
	/** TAG to Log */
	public static final String TAG = MyPushMessageReceiver.class.getSimpleName();
	public static final String MESSAGE_TYPE = "messageType";
	public static final int NOTIFY_ID = 0x000;
	public static final String RESPONSE = "response";
	private UserPreference userPreference = BaseApplication.getInstance().getUserPreference();

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
		JsonMessage jsonMessage = FastJsonTool.getObject(message, JsonMessage.class);
		if (jsonMessage != null) {
			parseMessage(jsonMessage, context);
		}
	}

	/**
	 * ת����Ϣ
	 * @param msg
	 */
	private void parseMessage(JsonMessage msg, Context context) {
		//�ж��Ƿ�������
		if (PreferenceUtils.getInstance(BaseApplication.getInstance()).getSettingMsgSound()) {
			BaseApplication.getInstance().getMessagePlayer().start();
		}
		int type = msg.getType();
		switch (type) {
		//�Ķ�����
		case Constants.MessageType.MESSAGE_TYPE_FLIPPER_REQUEEST:
			handleFlipperRequest(context, msg.getMessageContent());
			break;
		//Ҳ�Ķ�
		case Constants.MessageType.MESSAGE_TYPE_FLIPPER_TO:
			flipperTo(msg.getMessageContent());
			break;
		//��������
		case Constants.MessageType.MESSAGE_TYPE_LOVE_REQUEST:
			buildLove(msg.getMessageContent());
			break;
		default:
			break;
		}

	}

	/**
	 * �����Ķ�����
	 */
	private void handleFlipperRequest(Context context, String msgContent) {
		BaseApplication application = BaseApplication.getInstance();
		LogTool.i("MyPushMessageReceiver", "�ٶ����ͽ��յ��Ķ�����" + msgContent);

		//�������û�������У�����ʾ֪ͨ
		if (!CommonTools.isAppRunning(context)) {
			//֪ͨ
			NotificationCompat.Builder builder = new NotificationCompat.Builder(application);
			builder.setSmallIcon(R.drawable.ic_launcher).setContentTitle("��Ȼ�Ķ�").setContentText("���˶�����Ȼ�Ķ�������������˭��~")
					.setAutoCancel(true).setTicker("��Ȼ�Ķ���").setDefaults(Notification.DEFAULT_ALL);
			Intent resultIntent = new Intent(application, MainActivity.class);
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(application);
			stackBuilder.addParentStack(MainActivity.class);
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(resultPendingIntent);
			application.getNotificationManager().notify(NOTIFY_ID, builder.build());// ֪ͨһ��
		}
	}

	/**
	 * ���������������
	 * @param phone
	 */
	private void buildLove(String phone) {
		BaseApplication application = BaseApplication.getInstance();
		LogTool.i("MyPushMessageReceiver", "�ٶ����������������");

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
		LogTool.i("MyPushMessageReceiver", "�ٶ�����Ҳ�Ķ�");
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
		LogTool.i("MyPushMessageReceiver", "��userID: " + userId);
		LogTool.i("MyPushMessageReceiver", "userpreference userID: " + userPreference.getBpush_UserID());

		if (errorCode == 0) {
			userPreference = BaseApplication.getInstance().getUserPreference();
			if (!userPreference.getBpush_UserID().equals(userId)) {
				if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(channelId)) {
					updateBpush(userId, channelId);
					userPreference.setAppID(appid);
					userPreference.setBpush_ChannelID(channelId);
					userPreference.setBpush_UserID(userId);
				}
			}
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
	 * ���°ٶ����Ͳ���
	 */
	private void updateBpush(String userID, String channelID) {
		if (!TextUtils.isEmpty(userPreference.getU_password()) && userPreference.getU_id() != -1) {
			RequestParams params = new RequestParams();
			params.put(UserTable.U_ID, userPreference.getU_id());
			params.put(UserTable.U_PASSWORD, userPreference.getU_password());
			params.put(UserTable.U_BPUSH_USER_ID, userID);
			params.put(UserTable.U_BPUSH_CHANNEL_ID, channelID);
			TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, Header[] headers, String response) {
					// TODO Auto-generated method stub
					if (response.endsWith("-1")) {
						LogTool.e("�޸İٶ����Ͳ���", "����������");
					} else if (response.endsWith("1")) {
						LogTool.d("�޸İٶ����Ͳ���", "�޸ĳɹ�");
					} else if (response.endsWith("2")) {
						LogTool.e("�޸İٶ����Ͳ���", "���벻��ȷ");
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
					// TODO Auto-generated method stub
					LogTool.e("�޸İٶ����Ͳ���", "����������");
				}
			};
			AsyncHttpClientTool.post("updateuserbc", params, responseHandler);
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
