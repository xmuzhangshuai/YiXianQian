package com.yixianqian.db;

import android.content.Context;

import com.yixianqian.base.BaseApplication;
import com.yixianqian.dao.DaoSession;
import com.yixianqian.dao.UserStateDao;

public class UserStateDbService {
	private static final String TAG = UserStateDbService.class.getSimpleName();
	private static UserStateDbService instance;
	private static Context appContext;
	private DaoSession mDaoSession;
	public UserStateDao userStateDao;

	public UserStateDbService() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * µÃµ½ÊµÀý
	 * @param context
	 * @return
	 */
	public static UserStateDbService getInstance(Context context) {
		if (instance == null) {
			instance = new UserStateDbService();
			if (appContext == null) {
				appContext = context.getApplicationContext();
			}
			instance.mDaoSession = BaseApplication.getDaoSession(context);
			instance.userStateDao = instance.mDaoSession.getUserStateDao();
		}
		return instance;
	}

}
