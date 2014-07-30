package com.yixianqian.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

public class AmrAudioPlayer {
	private static final String TAG = "AmrAudioPlayer";

	private static AmrAudioPlayer playerInstance = null;

	private long alreadyReadByteCount = 0;

	private MediaPlayer audioPlayer;
	private Handler handler = new Handler();

	private final String cacheFileName = "audioCacheFile";
	private File cacheFile;
	private int cacheFileCount = 0;

	// ������¼�Ƿ��Ѿ���cacheFile�и������ݵ���һ��cache��
	private boolean hasMovedTheCacheFlag;

	private boolean isPlaying;
	private Activity activity;

	private boolean isChaingCacheToAnother;

	private AmrAudioPlayer() {
	}

	public static AmrAudioPlayer getAmrAudioPlayerInstance() {
		if (playerInstance == null) {
			synchronized (AmrAudioPlayer.class) {
				if (playerInstance == null) {
					playerInstance = new AmrAudioPlayer();
				}
			}
		}
		return playerInstance;
	}

	public void initAmrAudioPlayer(Activity activity) {
		this.activity = activity;
		deleteExistCacheFile();
		initCacheFile();
	}

	private void deleteExistCacheFile() {
		File cacheDir = activity.getCacheDir();
		File[] needDeleteCacheFiles = cacheDir.listFiles();
		for (int index = 0; index < needDeleteCacheFiles.length; ++index) {
			File cache = needDeleteCacheFiles[index];
			if (cache.isFile()) {
				if (cache.getName().contains(cacheFileName.trim())) {
					Log.e(TAG, "delete cache file: " + cache.getName());
					cache.delete();
				}
			}
		}
		needDeleteCacheFiles = null;
	}

	private void initCacheFile() {
		cacheFile = null;
		cacheFile = new File(activity.getCacheDir(), cacheFileName);
	}

	public void start() {
		isPlaying = true;
		isChaingCacheToAnother = false;
		setHasMovedTheCacheToAnotherCache(false);
		new Thread(new NetAudioPlayerThread()).start();
	}

	public void stop() {
		isPlaying = false;
		isChaingCacheToAnother = false;
		setHasMovedTheCacheToAnotherCache(false);
		releaseAudioPlayer();
		deleteExistCacheFile();
		cacheFile = null;
		handler = null;
	}

	private void releaseAudioPlayer() {
		playerInstance = null;
		if (audioPlayer != null) {
			try {
				if (audioPlayer.isPlaying()) {
					audioPlayer.pause();
				}
				audioPlayer.release();
				audioPlayer = null;
			} catch (Exception e) {
			}
		}
	}

	private boolean hasMovedTheCacheToAnotherCache() {
		return hasMovedTheCacheFlag;
	}

	private void setHasMovedTheCacheToAnotherCache(boolean result) {
		hasMovedTheCacheFlag = result;
	}

	private class NetAudioPlayerThread implements Runnable {
		// �ӽ������ݿ�ʼ���㣬���������INIT_BUFFER_SIZEʱ��ʼ����
		private final int INIT_AUDIO_BUFFER = 2 * 1024;
		// ʣ1���ʱ�򲥷��µĻ��������
		private final int CHANGE_CACHE_TIME = 1000;

		public void run() {
			try {
				Socket socket = createSocketConnectToServer();
				receiveNetAudioThenPlay(socket);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage() + "�ӷ���˽�����Ƶʧ�ܡ�����");
			}
		}

		private Socket createSocketConnectToServer() throws Exception {
			String hostName = "";
			InetAddress ipAddress = InetAddress.getByName(hostName);
			int port = 3306;
			Socket socket = new Socket(ipAddress, port);
			return socket;
		}

		private void receiveNetAudioThenPlay(Socket socket) throws Exception {
			InputStream inputStream = socket.getInputStream();
			FileOutputStream outputStream = new FileOutputStream(cacheFile);

			final int BUFFER_SIZE = 100 * 1024;// 100kb buffer size
			byte[] buffer = new byte[BUFFER_SIZE];

			// �ռ���10*350b��֮��ſ�ʼ��������
			int testTime = 10;
			try {
				alreadyReadByteCount = 0;
				while (isPlaying) {
					int numOfRead = inputStream.read(buffer);
					if (numOfRead <= 0) {
						break;
					}
					alreadyReadByteCount += numOfRead;
					outputStream.write(buffer, 0, numOfRead);
					outputStream.flush();
					try {
						if (testTime++ >= 10) {
							Log.e(TAG, "cacheFile=" + cacheFile.length());
							testWhetherToChangeCache();
							testTime = 0;
						}
					} catch (Exception e) {
						// TODO: handle exception
					}

					// ��������˽�����������cache����ִ�д˲���
					if (hasMovedTheCacheToAnotherCache() && !isChaingCacheToAnother) {
						if (outputStream != null) {
							outputStream.close();
							outputStream = null;
						}
						// ��������������cacheɾ����Ȼ����0��ʼ�洢
						// initCacheFile();
						outputStream = new FileOutputStream(cacheFile);
						setHasMovedTheCacheToAnotherCache(false);
						alreadyReadByteCount = 0;
					}

				}
			} catch (Exception e) {
				errorOperator();
				e.printStackTrace();
				Log.e(TAG, "socket disconnect...:" + e.getMessage());
				throw new Exception("socket disconnect....");
			} finally {
				buffer = null;
				if (socket != null) {
					socket.close();
				}
				if (inputStream != null) {
					inputStream.close();
					inputStream = null;
				}
				if (outputStream != null) {
					outputStream.close();
					outputStream = null;
				}
				stop();
			}
		}

		private void testWhetherToChangeCache() throws Exception {
			if (audioPlayer == null) {
				firstTimeStartPlayer();
			} else {
				changeAnotherCacheWhenEndOfCurrentCache();
			}
		}

		private void firstTimeStartPlayer() throws Exception {
			// �������Ѿ�����INIT_AUDIO_BUFFER��ʼ����
			if (alreadyReadByteCount >= INIT_AUDIO_BUFFER) {
				Runnable r = new Runnable() {
					public void run() {
						try {
							File firstCacheFile = createFirstCacheFile();
							// �����Ѿ���cache�и������ݣ�Ȼ���ɾ�����cache
							setHasMovedTheCacheToAnotherCache(true);
							audioPlayer = createAudioPlayer(firstCacheFile);
							audioPlayer.start();
						} catch (Exception e) {
							Log.e(TAG, e.getMessage() + " :in firstTimeStartPlayer() fun");
						} finally {
						}
					}
				};
				handler.post(r);
			}
		}

		private File createFirstCacheFile() throws Exception {
			String firstCacheFileName = cacheFileName + (cacheFileCount++);
			File firstCacheFile = new File(activity.getCacheDir(), firstCacheFileName);
			// Ϊʲô��ֱ�Ӳ���cacheFile����Ҫ����cacheFile��һ���µ�cache��Ȼ�󲥷Ŵ��µ�cache��
			// ��Ϊ�˷�ֹǱ�ڵĶ�/д���󣬿�����д��cacheFile��ʱ��
			// MediaPlayer����ͼ�����ݣ� �������Է�ֹ�����ķ�����
			moveFile(cacheFile, firstCacheFile);
			return firstCacheFile;

		}

		private void moveFile(File oldFile, File newFile) throws IOException {
			if (!oldFile.exists()) {
				throw new IOException("oldFile is not exists. in moveFile() fun");
			}
			if (oldFile.length() <= 0) {
				throw new IOException("oldFile size = 0. in moveFile() fun");
			}
			BufferedInputStream reader = new BufferedInputStream(new FileInputStream(oldFile));
			BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(newFile, false));

			final byte[] AMR_HEAD = new byte[] { 0x23, 0x21, 0x41, 0x4D, 0x52, 0x0A };
			writer.write(AMR_HEAD, 0, AMR_HEAD.length);
			writer.flush();

			try {
				byte[] buffer = new byte[1024];
				int numOfRead = 0;
				Log.d(TAG, "POS...newFile.length=" + newFile.length() + "  old=" + oldFile.length());
				while ((numOfRead = reader.read(buffer, 0, buffer.length)) != -1) {
					writer.write(buffer, 0, numOfRead);
					writer.flush();
				}
				Log.d(TAG, "POS..AFTER...newFile.length=" + newFile.length());
			} catch (IOException e) {
				Log.e(TAG, "moveFile error.. in moveFile() fun." + e.getMessage());
				throw new IOException("moveFile error.. in moveFile() fun.");
			} finally {
				if (reader != null) {
					reader.close();
					reader = null;
				}
				if (writer != null) {
					writer.close();
					writer = null;
				}
			}
		}

		private MediaPlayer createAudioPlayer(File audioFile) throws IOException {
			MediaPlayer mPlayer = new MediaPlayer();

			// It appears that for security/permission reasons, it is better to
			// pass
			// a FileDescriptor rather than a direct path to the File.
			// Also I have seen errors such as "PVMFErrNotSupported" and
			// "Prepare failed.: status=0x1" if a file path String is passed to
			// setDataSource(). So unless otherwise noted, we use a
			// FileDescriptor here.
			FileInputStream fis = new FileInputStream(audioFile);
			mPlayer.reset();
			mPlayer.setDataSource(fis.getFD());
			mPlayer.prepare();
			return mPlayer;
		}

		private void changeAnotherCacheWhenEndOfCurrentCache() throws IOException {
			// ��鵱ǰcacheʣ��ʱ��
			long theRestTime = audioPlayer.getDuration() - audioPlayer.getCurrentPosition();
			Log.e(TAG, "theRestTime=" + theRestTime + "  isChaingCacheToAnother=" + isChaingCacheToAnother);
			if (!isChaingCacheToAnother && theRestTime <= CHANGE_CACHE_TIME) {
				isChaingCacheToAnother = true;

				Runnable r = new Runnable() {
					public void run() {
						try {
							File newCacheFile = createNewCache();
							// �����Ѿ���cache�и������ݣ�Ȼ���ɾ�����cache
							setHasMovedTheCacheToAnotherCache(true);
							transferNewCacheToAudioPlayer(newCacheFile);
						} catch (Exception e) {
							Log.e(TAG, e.getMessage() + ":changeAnotherCacheWhenEndOfCurrentCache() fun");
						} finally {
							deleteOldCache();
							isChaingCacheToAnother = false;
						}
					}
				};
				handler.post(r);
			}
		}

		private File createNewCache() throws Exception {
			// �������������ݵ�cache���Ƶ�newCache�н��в���
			String newCacheFileName = cacheFileName + (cacheFileCount++);
			File newCacheFile = new File(activity.getCacheDir(), newCacheFileName);
			Log.e(TAG, "before moveFile............the size=" + cacheFile.length());
			moveFile(cacheFile, newCacheFile);
			return newCacheFile;
		}

		private void transferNewCacheToAudioPlayer(File newCacheFile) throws Exception {
			MediaPlayer oldPlayer = audioPlayer;

			try {
				audioPlayer = createAudioPlayer(newCacheFile);
				audioPlayer.start();
			} catch (Exception e) {
				Log.e(TAG, "filename=" + newCacheFile.getName() + " size=" + newCacheFile.length());
				Log.e(TAG, e.getMessage() + " " + e.getCause() + " error start..in transfanNer..");
			}
			try {
				oldPlayer.pause();
				oldPlayer.reset();
				oldPlayer.release();
			} catch (Exception e) {
				Log.e(TAG, "ERROR release oldPlayer.");
			} finally {
				oldPlayer = null;
			}
		}

		private void deleteOldCache() {
			int oldCacheFileCount = cacheFileCount - 1;
			String oldCacheFileName = cacheFileName + oldCacheFileCount;
			File oldCacheFile = new File(activity.getCacheDir(), oldCacheFileName);
			if (oldCacheFile.exists()) {
				oldCacheFile.delete();
			}
		}

		private void errorOperator() {
		}
	}

}
