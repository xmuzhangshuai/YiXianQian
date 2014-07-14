package com.yixianqian.customewidget;

import java.io.File;
import java.io.IOException;

import android.media.MediaRecorder;
import android.os.Environment;

public class MyRecorder {
	File soundFileDir;//文件目录
	File soundFile;//录音文件
	String soundName;//文件名称
	MediaRecorder mRecorder;

	public MyRecorder(String soundName) {
		this.soundName = soundName;
		init();
	}

	private void init() {
		try {
			soundFileDir = new File(Environment.getExternalStorageDirectory(), "/yixianqian/audio");
			if (!soundFileDir.exists()) {
				soundFileDir.mkdirs();
			}

			soundFile = new File(soundFileDir, soundName + ".amr");
			if (!soundFile.exists()) {
				soundFile.createNewFile();
			}

			mRecorder = new MediaRecorder();
			//设置录音来源
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

			//设置录音的输出格式
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

			//设置声音编码格式
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

			mRecorder.setOutputFile(soundFile.getAbsolutePath());
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 开始录音
	 */
	public void beginRecord() {
		try {
			mRecorder.prepare();
			//开始录音
			mRecorder.start();
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 结束录音
	 */
	public void stopRecord() {
		if (soundFile != null && soundFile.exists()) {
			if (mRecorder != null) {
				mRecorder.stop();
				mRecorder.release();
				mRecorder = null;
			}
		}
	}

	/**
	 * 返回文件路径
	 */
	public String getPath() {
		if (soundFile != null && soundFile.exists()) {
			return soundFile.getAbsolutePath();
		}
		return null;
	}
}
