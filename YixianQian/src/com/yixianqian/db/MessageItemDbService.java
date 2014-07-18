package com.yixianqian.db;

import android.content.Context;

import com.yixianqian.base.BaseApplication;
import com.yixianqian.dao.DaoSession;
import com.yixianqian.dao.MessageItemDao;

public class MessageItemDbService {
	private static final String TAG = MessageItemDbService.class.getSimpleName();
	private static MessageItemDbService instance;
	private static Context appContext;
	private DaoSession mDaoSession;
	public MessageItemDao messageItemDao;

	public MessageItemDbService() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * µÃµ½ÊµÀý
	 * @param context
	 * @return
	 */
	public static MessageItemDbService getInstance(Context context) {
		if (instance == null) {
			instance = new MessageItemDbService();
			if (appContext == null) {
				appContext = context.getApplicationContext();
			}
			instance.mDaoSession = BaseApplication.getDaoSession(context);
			instance.messageItemDao = instance.mDaoSession.getMessageItemDao();
		}
		return instance;
	}
}
