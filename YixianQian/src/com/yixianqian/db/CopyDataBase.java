package com.yixianqian.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class CopyDataBase {
	@SuppressLint("SdCardPath")
	private static String DATABASE_PATH = "/data/data/com.yixianqian/databases/";
	public static SQLiteDatabase database;
	public static String dbName = "yixianqian.db";
	private Context myContext;

	public CopyDataBase(Context paramContext) {
		this.myContext = paramContext;
	}

	public void copyDataBase() {
		// ��� SQLite ���ݿ��ļ��Ƿ����
		if (!new File(DATABASE_PATH + dbName).exists()) {
			// �� SQLite ���ݿ��ļ������ڣ��ټ��һ�� database Ŀ¼�Ƿ����
			File localFile = new File(DATABASE_PATH);
			// �� database Ŀ¼�����ڣ��½���Ŀ¼
			if (!localFile.exists())
				localFile.mkdir();
			try {
				// �õ� assets Ŀ¼������ʵ��׼���õ� SQLite ���ݿ���Ϊ������
				InputStream localInputStream = this.myContext.getAssets().open(dbName);
				// �����
				FileOutputStream localFileOutputStream = new FileOutputStream(DATABASE_PATH + dbName);
				// �ļ�д��
				byte[] arrayOfByte = new byte[1024];
				while (true) {
					int i = localInputStream.read(arrayOfByte);
					if (i <= 0) {
						// �ر���
						localFileOutputStream.flush();
						localFileOutputStream.close();
						localInputStream.close();
						return;
					}
					localFileOutputStream.write(arrayOfByte, 0, i);
				}
			} catch (FileNotFoundException localFileNotFoundException) {
				localFileNotFoundException.printStackTrace();
				return;
			} catch (IOException localIOException) {
				localIOException.printStackTrace();
			}
		}

	}
}
