package com.yixianqian.baidupush;

import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;

import com.yixianqian.base.BaseApplication;
import com.yixianqian.utils.NetworkUtils;

public class SendMsgAsyncTask {
	private BaiduPush mBaiduPush;
	private String mMessage;
	private Handler mHandler;
	private MyAsyncTask mTask;
	private String mUserId;
//	private String mChannelId;
	private OnSendScuessListener mListener;

	public interface OnSendScuessListener {
		void sendScuess();
	}

	public void setOnSendScuessListener(OnSendScuessListener listener) {
		this.mListener = listener;
	}

	Runnable reSend = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			send();//�ط�
		}
	};

	public SendMsgAsyncTask(String jsonMsg, String useId) {
		// TODO Auto-generated constructor stub
		mBaiduPush = BaseApplication.getInstance().getBaiduPush();
		mMessage = jsonMsg;
		mUserId = useId;
		mHandler = new Handler();
	}
	
//	public SendMsgAsyncTask(String jsonMsg, String useId,String channelId) {
//		// TODO Auto-generated constructor stub
//		mBaiduPush = BaseApplication.getInstance().getBaiduPush();
//		mMessage = jsonMsg;
//		mUserId = useId;
//		mChannelId = channelId;
//		mHandler = new Handler();
//	}

	// ����
	public void send() {
		if (NetworkUtils.isNetworkAvailable(BaseApplication.getInstance())) {//����������
			mTask = new MyAsyncTask();
			mTask.execute();
		} else {
			NetworkUtils.networkStateTips(BaseApplication.getInstance());
		}
	}

	// ֹͣ
	public void stop() {
		if (mTask != null)
			mTask.cancel(true);
	}

	/**
	 * 
	 * �����ƣ�MyAsyncTask
	 * ��������������Ϣ�첽����
	 * �����ˣ� ��˧
	 * ����ʱ�䣺2014��7��24�� ����8:34:43
	 *
	 */
	class MyAsyncTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... message) {
			String result = "";
			if (!TextUtils.isEmpty(mUserId)){
				result = mBaiduPush.PushMessage(mMessage, mUserId);
			}
//			if (TextUtils.isEmpty(mUserId))
//				result = mBaiduPush.PushMessage(mMessage);
//			else
//				result = mBaiduPush.PushMessage(mMessage, mUserId);
////				result = mBaiduPush.PushMessage(mMessage, mUserId,mChannelId);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result.contains(BaiduPush.SEND_MSG_ERROR)) {// �����Ϣ����ʧ�ܣ���100ms���ط�
				mHandler.postDelayed(reSend, 100);
			} else {
				if (mListener != null)
					mListener.sendScuess();
			}
		}
	}
}
