package com.yixianqian.db;

import java.util.List;

import android.content.Context;

import com.yixianqian.base.BaseApplication;
import com.yixianqian.dao.DaoSession;
import com.yixianqian.dao.TodayRecommendDao;
import com.yixianqian.dao.TodayRecommendDao.Properties;
import com.yixianqian.entities.TodayRecommend;
import com.yixianqian.utils.DateTimeTools;

public class TodayRecommendDbService {

	private static final String TAG = TodayRecommendDbService.class.getSimpleName();
	private static TodayRecommendDbService instance;
	private static Context appContext;
	private DaoSession mDaoSession;
	public TodayRecommendDao todayRecommendDao;

	public TodayRecommendDbService() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 得到实例
	 * @param context
	 * @return
	 */
	public static TodayRecommendDbService getInstance(Context context) {
		if (instance == null) {
			instance = new TodayRecommendDbService();
			if (appContext == null) {
				appContext = context.getApplicationContext();
			}
			instance.mDaoSession = BaseApplication.getDaoSession(context);
			instance.todayRecommendDao = instance.mDaoSession.getTodayRecommendDao();
		}
		return instance;
	}

	/**
	 * 获得今天的推荐列表
	 * @return
	 */
	public List<TodayRecommend> getTodayRecommendList() {
		return todayRecommendDao.queryBuilder().where(Properties.Date.eq(DateTimeTools.getCurrentDateForString()))
				.list();
	}

	public void insert() {
		TodayRecommend recommend;
		recommend = new TodayRecommend(null, 1, "王语嫣",
				"http://c.hiphotos.baidu.com/image/pic/item/0df3d7ca7bcb0a4650b021586963f6246a60afc5.jpg", 21,
				DateTimeTools.getCurrentDateForString(), 267);
		todayRecommendDao.insert(recommend);
		recommend = new TodayRecommend(null, 2, "佳佳",
				"http://c.hiphotos.baidu.com/image/pic/item/8644ebf81a4c510f5b901ec76259252dd52aa593.jpg", 25,
				DateTimeTools.getCurrentDateForString(), 67);
		todayRecommendDao.insert(recommend);
		recommend = new TodayRecommend(null, 3, "是留美",
				"http://d.hiphotos.baidu.com/image/pic/item/1c950a7b02087bf4619dd435f0d3572c10dfcffd.jpg", 20,
				DateTimeTools.getCurrentDateForString(), 167);
		todayRecommendDao.insert(recommend);
		recommend = new TodayRecommend(
				null,
				4,
				"梦梦",
				"http://e.hiphotos.baidu.com/image/w%3D230/sign=aeb595e4dbf9d72a1764171ee428282a/2fdda3cc7cd98d10b510fdea233fb80e7aec9021.jpg",
				19, DateTimeTools.getCurrentDateForString(), 27);
		todayRecommendDao.insert(recommend);
	}
}
