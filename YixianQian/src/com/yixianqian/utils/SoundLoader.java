package com.yixianqian.utils;

import java.io.File;

import org.apache.http.Header;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Environment;
import android.text.TextUtils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

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
	private final String cacheFileName = "audioCacheFile";
	private File cacheFileDir;
	private File soundFile;
	private Context context;
	private boolean isPlaying;

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
			return new SoundLoader(context);
		}
	}

	public void start(String path) {
		isPlaying = true;
		getCacheFile(path);
	}

	private void getCacheFile(String url) {
		cacheFileDir = new File(Environment.getExternalStorageDirectory(), "/yixianqian/cache");
		if (!cacheFileDir.exists()) {
			cacheFileDir.mkdirs();
		}

		String absulteUrl = AsyncHttpClientImageSound.getAbsoluteUrl(url);
		System.out.println(absulteUrl);
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(context, absulteUrl, new FileAsyncHttpResponseHandler(context) {

			@Override
			public void onSuccess(int statusCode, Header[] headers, File response) {
				// TODO Auto-generated method stub
				//				soundFile = new File(cacheFileDir, response.getName());

				System.out.println("�����");
				System.out.println(response.getAbsolutePath());
				System.out.println(response.getName());
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable e, File response) {
				// TODO Auto-generated method stub
				System.out.println("ʧ��!");
				System.out.println(statusCode);
				System.out.println(e);
			}
		});
	}

	/**
	 * �ж��Ƿ񻺴��
	 * @param url
	 * @return
	 */
	private String getCachedPath(String url) {
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
	private void cacheToFile() {
		
	}
}
