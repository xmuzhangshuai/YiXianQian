package com.yixianqian.test;

import java.util.List;

import com.yixianqian.db.ProvinceDbService;
import com.yixianqian.entities.Province;

import junit.framework.TestResult;
import android.test.AndroidTestCase;

public class DbTest extends AndroidTestCase {
	public TestResult run() {
		ProvinceDbService provinceDbService = new ProvinceDbService();
		List<Province> provinces = provinceDbService.provinceDao.loadAll();
		return null;
	}
}
