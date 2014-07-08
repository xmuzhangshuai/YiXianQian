package com.yixianqian.db;

import java.util.List;

import android.content.Context;

import com.yixianqian.base.BaseApplication;
import com.yixianqian.dao.DaoSession;
import com.yixianqian.dao.SchoolDao;
import com.yixianqian.dao.SchoolDao.Properties;
import com.yixianqian.entities.City;
import com.yixianqian.entities.School;

public class SchoolDbService {
	private static final String TAG = SchoolDbService.class.getSimpleName();
	private static SchoolDbService instance;
	private static Context appContext;
	private DaoSession mDaoSession;
	public SchoolDao schoolDao;

	public SchoolDbService() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 得到实例
	 * @param context
	 * @return
	 */
	public static SchoolDbService getInstance(Context context) {
		if (instance == null) {
			instance = new SchoolDbService();
			if (appContext == null) {
				appContext = context.getApplicationContext();
			}
			instance.mDaoSession = BaseApplication.getDaoSession(context);
			instance.schoolDao = instance.mDaoSession.getSchoolDao();
		}
		return instance;
	}

	/**
	 * 根据城市返回学校列表
	 * @return
	 */
	public List<School> getSchoolListByCity(City c) {
		return schoolDao.queryBuilder().where(Properties.CityID.eq(c.getCityID())).list();
	}
}
