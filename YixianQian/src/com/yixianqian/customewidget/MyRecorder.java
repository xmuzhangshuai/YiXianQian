package com.yixianqian.customewidget;

import java.io.File;
import java.io.IOException;

import android.media.MediaRecorder;
import android.os.Environment;

public class MyRecorder {
	File soundFileDir;//�ļ�Ŀ¼
	File soundFile;//¼���ļ�
	String soundName;//�ļ�����
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
			//����¼����Դ
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

			//����¼���������ʽ
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

			//�������������ʽ
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

			mRecorder.setOutputFile(soundFile.getAbsolutePath());
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * ��ʼ¼��
	 */
	public void beginRecord() {
		try {
			mRecorder.prepare();
			//��ʼ¼��
			mRecorder.start();
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ����¼��
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
	 * �����ļ�·��
	 */
	public String getPath() {
		if (soundFile != null && soundFile.exists()) {
			return soundFile.getAbsolutePath();
		}
		return null;
	}
}
