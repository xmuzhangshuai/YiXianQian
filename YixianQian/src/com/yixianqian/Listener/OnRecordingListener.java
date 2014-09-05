package com.yixianqian.Listener;

/**
 * 类名称：OnRecordingListener
 * 类描述：当在个人中心录音时，使得主页按钮不可用
 * 创建人： 张帅
 * 创建时间：2014年9月5日 下午8:58:41
 *
 */
public interface OnRecordingListener {
	public void onRecordingChanged(boolean isRecording);
}
