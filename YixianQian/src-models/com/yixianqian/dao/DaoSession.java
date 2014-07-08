package com.yixianqian.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.yixianqian.entities.Province;
import com.yixianqian.entities.City;
import com.yixianqian.entities.Cometent;
import com.yixianqian.entities.School;

import com.yixianqian.dao.ProvinceDao;
import com.yixianqian.dao.CityDao;
import com.yixianqian.dao.CometentDao;
import com.yixianqian.dao.SchoolDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig provinceDaoConfig;
    private final DaoConfig cityDaoConfig;
    private final DaoConfig cometentDaoConfig;
    private final DaoConfig schoolDaoConfig;

    private final ProvinceDao provinceDao;
    private final CityDao cityDao;
    private final CometentDao cometentDao;
    private final SchoolDao schoolDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        provinceDaoConfig = daoConfigMap.get(ProvinceDao.class).clone();
        provinceDaoConfig.initIdentityScope(type);

        cityDaoConfig = daoConfigMap.get(CityDao.class).clone();
        cityDaoConfig.initIdentityScope(type);

        cometentDaoConfig = daoConfigMap.get(CometentDao.class).clone();
        cometentDaoConfig.initIdentityScope(type);

        schoolDaoConfig = daoConfigMap.get(SchoolDao.class).clone();
        schoolDaoConfig.initIdentityScope(type);

        provinceDao = new ProvinceDao(provinceDaoConfig, this);
        cityDao = new CityDao(cityDaoConfig, this);
        cometentDao = new CometentDao(cometentDaoConfig, this);
        schoolDao = new SchoolDao(schoolDaoConfig, this);

        registerDao(Province.class, provinceDao);
        registerDao(City.class, cityDao);
        registerDao(Cometent.class, cometentDao);
        registerDao(School.class, schoolDao);
    }
    
    public void clear() {
        provinceDaoConfig.getIdentityScope().clear();
        cityDaoConfig.getIdentityScope().clear();
        cometentDaoConfig.getIdentityScope().clear();
        schoolDaoConfig.getIdentityScope().clear();
    }

    public ProvinceDao getProvinceDao() {
        return provinceDao;
    }

    public CityDao getCityDao() {
        return cityDao;
    }

    public CometentDao getCometentDao() {
        return cometentDao;
    }

    public SchoolDao getSchoolDao() {
        return schoolDao;
    }

}
