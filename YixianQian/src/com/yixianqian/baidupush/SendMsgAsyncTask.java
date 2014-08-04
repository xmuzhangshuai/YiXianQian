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
			send();//重发
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

	// 发送
	public void send() {
		if (NetworkUtils.isNetworkAvailable(BaseApplication.getInstance())) {//如果网络可用
			mTask = new MyAsyncTask();
			mTask.execute();
		} else {
			NetworkUtils.networkStateTips(BaseApplication.getInstance());
		}
	}

	// 停止
	public void stop() {
		if (mTask != null)
			mTask.cancel(true);
	}

	/**
	 * 
	 * 类名称：MyAsyncTask
	 * 类描述：发送消息异步任务
	 * 创建人： 张帅
	 * 创建时间：2014年7月24日 下午8:34:43
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
			if (result.contains(BaiduPush.SEND_MSG_ERROR)) {// 如果消息发送失败，则100ms后重发
				mHandler.postDelayed(reSend, 100);
			} else {
				if (mListener != null)
					mListener.sendScuess();
			}
		}
	}
}
