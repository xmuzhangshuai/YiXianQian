package com.yixianqian.entities;

import java.util.List;
import com.yixianqian.dao.DaoSession;
import de.greenrobot.dao.DaoException;

import com.yixianqian.dao.CometentDao;
import com.yixianqian.dao.SchoolDao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table COMETENT.
 */
public class Cometent {

    private Long id;
    private String cometentName;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient CometentDao myDao;

    private List<School> schoolList;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Cometent() {
    }

    public Cometent(Long id) {
        this.id = id;
    }

    public Cometent(Long id, String cometentName) {
        this.id = id;
        this.cometentName = cometentName;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCometentDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCometentName() {
        return cometentName;
    }

    public void setCometentName(String cometentName) {
        this.cometentName = cometentName;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<School> getSchoolList() {
        if (schoolList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            SchoolDao targetDao = daoSession.getSchoolDao();
            List<School> schoolListNew = targetDao._queryCometent_SchoolList(id);
            synchronized (this) {
                if(schoolList == null) {
                    schoolList = schoolListNew;
                }
            }
        }
        return schoolList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetSchoolList() {
        schoolList = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}