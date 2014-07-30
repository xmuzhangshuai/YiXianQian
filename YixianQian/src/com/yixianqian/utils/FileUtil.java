package com.yixianqian.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * �ļ��򵥲���������
 * @author 
 *
 */
public class FileUtil {
	/**
	 * �ļ��Ƿ����
	 * @param filePathName
	 * @return
	 */
	public static boolean isExist(String filePathName) {
		File file = new File(filePathName);
		return (!file.isDirectory() && file.exists());
	}

	public static boolean isDirExist(String filePathName) {
		if (!filePathName.endsWith("/"))
			filePathName += "/";
		File file = new File(filePathName);
		return (file.isDirectory() && file.exists());
	}

	/**
	 * ��ȡ·���������ļ�����ĩβ��'/'
	 * @param filePathName
	 * @return
	 */
	public static String getPath(String filePathName) {
		try {
			return filePathName.substring(0, filePathName.lastIndexOf('/') + 1);
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * ��ȡĿ¼������ ע�⣺ֻ�ܻ�ȡ�磺/aaaa/ssss/ �� /aaaa/dsddd
	 * @param filePathName
	 * @return
	 */
	public static String getDirPathName(String filePathName) {
		try {
			if (filePathName.endsWith("/"))
				filePathName = filePathName.substring(0, filePathName.lastIndexOf('/'));
			return filePathName.substring(filePathName.lastIndexOf("/") + 1, filePathName.length());
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * ��ȡ�ļ���������׺
	 * @param filePathName
	 * @return
	 */
	public static String getName(String filePathName) {
		try {
			return filePathName.substring(filePathName.lastIndexOf('/') + 1);
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * ��ȡ�ļ�����������׺
	 * @return
	 */
	public static String getNameNoPostfix(String filePathName) {
		try {
			return filePathName.substring(filePathName.lastIndexOf('/') + 1, filePathName.lastIndexOf('.'));
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * ������
	 * @param filePathName
	 * @param newPathName
	 */
	public static void rename(String filePathName, String newPathName) {
		if (isNullString(filePathName))
			return;
		if (isNullString(newPathName))
			return;

		FileUtil.delete(newPathName); //liuwp 20120830 �����ƶ�Ӧ���ļ������Ѿ����ڣ���ɾ��

		File file = new File(filePathName);
		File newFile = new File(newPathName);
		file.renameTo(newFile);
	}

	/**
	 * ɾ���ļ�
	 */
	public static void delete(String filePathName) {
		if (isNullString(filePathName))
			return;
		File file = new File(filePathName);
		if (file.isFile() && file.exists()) {
			boolean flag = file.delete();
			LogTool.i("file", "filePathName reset:" + filePathName + " flag:" + flag);
		}
	}

	/**
	 * ����Ŀ¼������·���ϵ�Ŀ¼���ᴴ��
	 * @param path
	 */
	public static void createDir(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	/** ���Դ������ļ�
	 * <br> ����ļ��Ѿ����ڲ�����,����true
	 * @param path ·��
	 * @return  �������ʧ��(Exception) ����false������true
	 */
	public static boolean createEmptyFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			try {
				return file.createNewFile();
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * ��ȡ�ļ���С
	 * @param filePathName
	 * @return
	 */
	public static long getSize(String filePathName) {
		if (isNullString(filePathName))
			return 0;
		File file = new File(filePathName);
		if (file.isFile())
			return file.length();
		return 0;
	}

	/**
	 * ��ȡ�ļ����ݵ�byte����
	 * @param filePathName �ļ���
	 * @param readOffset �����￪ʼ��
	 * @param readLength ��ȡ����
	 * @param dataBuf �������ݵĻ�����
	 * @param bufOffset �����ﱣ��
	 * @return
	 */
	public static boolean readData(String filePathName, int readOffset, int readLength, byte[] dataBuf, int bufOffset) {
		try {
			int readedTotalSize = 0;
			int onceReadSize = 0;

			BufferedInputStream in = new BufferedInputStream(new FileInputStream(filePathName));
			in.skip(readOffset);
			while (readedTotalSize < readLength
					&& (onceReadSize = in.read(dataBuf, bufOffset + readedTotalSize, readLength - readedTotalSize)) >= 0) {
				readedTotalSize += onceReadSize;
			}
			in.read(dataBuf, bufOffset, readLength);
			in.close();
			in = null;
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/** ��ĳ����������������ļ�
	 * @param in ������
	 * @param filePathName Ŀ���ļ�
	 * @return 
	 */
	public static boolean writeFile(InputStream in, String filePathName) {
		boolean flag = false;
		OutputStream outStream = null;
		try {
			File destFile = new File(filePathName);
			if (destFile.exists()) {
				destFile.delete();
			} else {
				destFile.createNewFile();
			}
			outStream = new FileOutputStream(filePathName);
			byte[] buffer = new byte[1024];
			int count = 0;
			while (true) {
				int length = in.read(buffer, 0, 1024);
				if (length > 0) {
					outStream.write(buffer, 0, length);
				} else {
					break;
				}
				count += length;
			}
			if (count > 0) {
				flag = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (outStream != null) {
				try {
					outStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return flag;
	}

	/**
	 * �жϵ�ǰ�ַ����Ƿ�Ϊ��
	 * @param str
	 * @return
	 */
	public static boolean isNullString(String str) {
		if (str == null || str.equals(""))
			return true;
		return false;
	}

	/**
	 * �����ļ�
	 * @param fromPathName
	 * @param toPathName
	 * @return
	 */
	public static int copy(String fromPathName, String toPathName) {
		try {
			InputStream from = new FileInputStream(fromPathName);
			return copy(from, toPathName);
		} catch (FileNotFoundException e) {
			return -1;
		}
	}

	/**
	 * �����ļ�
	 * @param from
	 * @param toPathName
	 * @return
	 */
	public static int copy(InputStream from, String toPathName) {
		try {
			FileUtil.delete(toPathName); //liuwp 20120925 ��ɾ��
			OutputStream to = new FileOutputStream(toPathName);
			byte buf[] = new byte[1024];
			int c;
			while ((c = from.read(buf)) > 0) {
				to.write(buf, 0, c);
			}
			from.close();
			to.close();
			return 0;
		} catch (Exception ex) {
			return -1;
		}
	}

	/**
	 * ����zip�ļ�·��ת��Ϊ�ļ�·��
	 *  @param zipFullPath �����.zip
	 * @return
	 */
	public static String zip2FileFullPath(String zipFullPath) {
		int zipIndex = zipFullPath.lastIndexOf(".zip");
		int zipIndexTmp = zipFullPath.lastIndexOf(".ZIP");
		String tmp = "";
		if (zipIndex > -1) {
			tmp = zipFullPath.substring(0, zipIndex);
		} else if (zipIndexTmp > -1) {
			tmp = zipFullPath.substring(0, zipIndexTmp);
		}
		return tmp;
	}

	/**
	 * �ı��ļ�Ȩ��
	 * @param permission
	 * @param filePathName
	 */
	public static void chmod(String permission, String filePathName) {
		try {
			String command = "chmod " + permission + " " + filePathName;
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}