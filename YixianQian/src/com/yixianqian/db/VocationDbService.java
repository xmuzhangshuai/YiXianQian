package com.yixianqian.db;

import android.content.Context;

import com.yixianqian.base.BaseApplication;
import com.yixianqian.dao.DaoSession;
import com.yixianqian.dao.VocationDao;

public class VocationDbService {
	private static final String TAG = VocationDbService.class.getSimpleName();
	private static VocationDbService instance;
	private static Context appContext;
	private DaoSession mDaoSession;
	public VocationDao vocationDao;

	public VocationDbService() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * µÃµ½ÊµÀý
	 * @param context
	 * @return
	 */
	public static VocationDbService getInstance(Context context) {
		if (instance == null) {
			instance = new VocationDbService();
			if (appContext == null) {
				appContext = context.getApplicationContext();
			}
			instance.mDaoSession = BaseApplication.getDaoSession(context);
			instance.vocationDao = instance.mDaoSession.getVocationDao();
		}
		return instance;
	}

}
