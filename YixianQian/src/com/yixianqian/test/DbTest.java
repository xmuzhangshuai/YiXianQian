package com.yixianqian.test;

import java.util.List;

import com.yixianqian.db.ProvinceDbService;
import com.yixianqian.db.TodayRecommendDbService;
import com.yixianqian.entities.Province;
import com.yixianqian.entities.TodayRecommend;
import com.yixianqian.utils.MD5For16;

import junit.framework.TestResult;
import android.test.AndroidTestCase;

public class DbTest extends AndroidTestCase {
	public TestResult run() {
		//		ProvinceDbService provinceDbService = new ProvinceDbService();
		//		List<Province> provinces = provinceDbService.provinceDao.loadAll();
//		TodayRecommendDbService todayRecommendDbService = TodayRecommendDbService.getInstance(getContext());
//		todayRecommendDbService.insert();
		System.out.println(MD5For16.GetMD5Code("zhangshuai"));
		System.out.println(MD5For16.MD5("zhangshuai"));
		return null;
	}
}
