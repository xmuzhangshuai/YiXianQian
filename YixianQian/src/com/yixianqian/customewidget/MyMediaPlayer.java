package com.yixianqian.customewidget;

import java.io.IOException;

import android.media.MediaPlayer;

public class MyMediaPlayer {
	MediaPlayer mPlayer;

	public MyMediaPlayer() {
		mPlayer = new MediaPlayer();
	}

	/**
	 * ����·��
	 * @param path
	 */
	public void setAudioPath(String path) {
		try {
			mPlayer.setDataSource(path);
		} catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ��ʼ����
	 */
	public void beginPlay() {
		try {
			mPlayer.prepare();
			mPlayer.start();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ��ͣ����
	 */
	public void pause() {
		mPlayer.pause();
	}

	/**
	 * ֹͣ����
	 */
	public void stop() {
		mPlayer.stop();
	}
}
