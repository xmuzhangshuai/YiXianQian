package com.yixianqian.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.yixianqian.base.BaseApplication;
import com.yixianqian.dao.DaoSession;
import com.yixianqian.dao.FlipperDao;
import com.yixianqian.dao.FlipperDao.Properties;
import com.yixianqian.entities.Flipper;

public class FlipperDbService {
	private static final String TAG = FlipperDbService.class.getSimpleName();
	private static FlipperDbService instance;
	private static Context appContext;
	private DaoSession mDaoSession;
	public FlipperDao flipperDao;

	public FlipperDbService() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 得到实例
	 * @param context
	 * @return
	 */
	public static FlipperDbService getInstance(Context context) {
		if (instance == null) {
			instance = new FlipperDbService();
			if (appContext == null) {
				appContext = context.getApplicationContext();
			}
			instance.mDaoSession = BaseApplication.getDaoSession(context);
			instance.flipperDao = instance.mDaoSession.getFlipperDao();
		}
		return instance;
	}

	/**
	 * 获取数量
	 * @return
	 */
	public int getFlipperCount() {
		return flipperDao.queryBuilder().where(Properties.IsRead.eq(false)).list().size();
	}

	/**
	 * 获取列表
	 */
	public List<Flipper> getFlipperList() {
		List<Flipper> flippers = new ArrayList<Flipper>();
		flippers = flipperDao.queryBuilder().orderDesc(Properties.Time).list();
		return flippers;
	}
}
