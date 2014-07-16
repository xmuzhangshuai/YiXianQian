package com.yixianqian.baidupush;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.baidu.frontia.api.FrontiaPushMessageReceiver;

public class PushMessageReceiver extends FrontiaPushMessageReceiver {
	/** TAG to Log */
	public static final String TAG = PushMessageReceiver.class.getSimpleName();

	/**
	* ����PushManager.startWork��sdk����push server�������������������첽�ġ�������Ľ��ͨ��onBind���ء�
	*/
	@Override
	public void onBind(Context context, int errorCode, String appid, String userId, String channelId, String requestId) {
		String responseString = "onBind errorCode=" + errorCode + " appid=" + appid + " userId=" + userId
				+ " channelId=" + channelId + " requestId=" + requestId;
		Log.d(TAG, responseString);
	}

	/**
	* PushManager.stopWork() �Ļص�������
	*/
	@Override
	public void onUnbind(Context context, int errorCode, String requestId) {
//		String responseString = "onUnbind errorCode=" + errorCode + " requestId = " + requestId;
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
			JSONObject customJson = null;
			try {
				customJson = new JSONObject(customContentString);
				String myvalue = null;
				if (customJson.isNull("mykey")) {
					myvalue = customJson.getString("mykey");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	* ����֪ͨ����ĺ�����ע������֪ͨ���û����ǰ��Ӧ���޷�ͨ���ӿڻ�ȡ֪ͨ�����ݡ�
	*/
	@Override
	public void onNotificationClicked(Context context, String title, String description, String customContentString) {
		String notifyString = "֪ͨ��� title=" + title + " description=" + description + " customContent="
				+ customContentString;
		Log.d(TAG, notifyString);
		// �Զ������ݻ�ȡ��ʽ��mykey��myvalue��Ӧ֪ͨ����ʱ�Զ������������õļ���ֵ
		if (customContentString != null & customContentString != "") {
			JSONObject customJson = null;
			try {
				customJson = new JSONObject(customContentString);
				String myvalue = null;
				if (customJson.isNull("mykey")) {
					myvalue = customJson.getString("mykey");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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

	private void parseMessage(String msg) {
	}
}
