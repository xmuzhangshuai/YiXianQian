package com.yixianqian.utils;

import java.io.File;
import java.io.IOException;

import org.apache.http.Header;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.text.TextUtils;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.yixianqian.R;

/**
 * �����ƣ�SoundLoader
 * ����������Ƶ���Ź�����
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��7��29�� ����9:54:18
 *
 */
public class SoundLoader {
	private SharedPreferences soundPreference;
	private SharedPreferences.Editor editor;
	private static SoundLoader soundLoader;
	private MediaPlayer mediaPlayer = null;
	private File cacheFileDir;
	private File soundFile;
	private Context context;

	public SoundLoader(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		soundPreference = context.getSharedPreferences("soundCache", Context.MODE_PRIVATE);
		editor = soundPreference.edit();
		mediaPlayer = new MediaPlayer();
	}

	public static SoundLoader getInstance(Context context) {
		if (soundLoader != null) {
			return soundLoader;
		} else {
			soundLoader = new SoundLoader(context);
			return soundLoader;
		}
	}

	public void start(String path, final ImageView playImageView) {
		playImageView.setImageResource(R.drawable.suspend_white);
		String soundFilePath = isCached(path);
		//���û�л����
		if (soundFilePath == null) {
			loadFile(path);
		} else {
			play(soundFilePath);
		}

		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				mediaPlayer.stop();
				playImageView.setImageResource(R.drawable.paly_white);
			}
		});
	}

	public void stop() {
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}
	}

	private void loadFile(final String url) {
		//û�л���������¼���
		cacheFileDir = FileUtil.createDir("/yixianqian/cache");

		String absulteUrl = AsyncHttpClientImageSound.getAbsoluteUrl(url);
		AsyncHttpClient client = new AsyncHttpClient();

		client.get(context, absulteUrl, new FileAsyncHttpResponseHandler(context) {

			@Override
			public void onSuccess(int statusCode, Header[] headers, File response) {
				// TODO Auto-generated method stub
				soundFile = new File(cacheFileDir, response.getName() + ".amr");
				FileUtil.copy(response.getAbsolutePath(), soundFile.getAbsolutePath());
				cacheToLocal(url, soundFile.getAbsolutePath());
				play(soundFile.getAbsolutePath());
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable e, File response) {
				// TODO Auto-generated method stub
			}
		});

	}

	/**
	 * �ж��Ƿ񻺴��
	 * @param url
	 * @return
	 */
	private String isCached(String url) {
		String dirString = soundPreference.getString(url, "");
		if (!TextUtils.isEmpty(dirString)) {
			//�ж��ļ��Ƿ����
			if (FileUtil.isExist(dirString)) {
				return dirString;
			} else {
				editor.remove(url);
				editor.commit();
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * ���浽����
	 */
	private void cacheToLocal(String key, String path) {
		editor.putString(key, path);
		editor.commit();
	}

	/**
	 * ����
	 * @param path
	 */
	private void play(String path) {
		mediaPlayer.reset();
		try {
			mediaPlayer.setDataSource(path);
			mediaPlayer.prepare();
		} catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mediaPlayer.start();
	}
}
