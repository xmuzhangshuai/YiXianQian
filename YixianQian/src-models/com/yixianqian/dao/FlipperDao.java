package com.yixianqian.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.yixianqian.entities.Flipper;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table FLIPPER.
*/
public class FlipperDao extends AbstractDao<Flipper, Long> {

    public static final String TABLENAME = "FLIPPER";

    /**
     * Properties of entity Flipper.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserID = new Property(1, Integer.class, "userID", false, "USER_ID");
        public final static Property Nickname = new Property(2, String.class, "nickname", false, "NICKNAME");
        public final static Property Realname = new Property(3, String.class, "realname", false, "REALNAME");
        public final static Property Gender = new Property(4, String.class, "gender", false, "GENDER");
        public final static Property Email = new Property(5, String.class, "email", false, "EMAIL");
        public final static Property LargeAvatar = new Property(6, String.class, "largeAvatar", false, "LARGE_AVATAR");
        public final static Property SamllAvatar = new Property(7, String.class, "samllAvatar", false, "SAMLL_AVATAR");
        public final static Property BloodType = new Property(8, String.class, "bloodType", false, "BLOOD_TYPE");
        public final static Property Constell = new Property(9, String.class, "constell", false, "CONSTELL");
        public final static Property Introduce = new Property(10, String.class, "introduce", false, "INTRODUCE");
        public final static Property Birthday = new Property(11, java.util.Date.class, "birthday", false, "BIRTHDAY");
        public final static Property Time = new Property(12, java.util.Date.class, "time", false, "TIME");
        public final static Property Age = new Property(13, Integer.class, "age", false, "AGE");
        public final static Property VocationID = new Property(14, Integer.class, "vocationID", false, "VOCATION_ID");
        public final static Property StateID = new Property(15, Integer.class, "stateID", false, "STATE_ID");
        public final static Property ProvinceID = new Property(16, Integer.class, "provinceID", false, "PROVINCE_ID");
        public final static Property CityID = new Property(17, Integer.class, "cityID", false, "CITY_ID");
        public final static Property SchoolID = new Property(18, Integer.class, "schoolID", false, "SCHOOL_ID");
        public final static Property Height = new Property(19, Integer.class, "height", false, "HEIGHT");
        public final static Property Weight = new Property(20, Integer.class, "weight", false, "WEIGHT");
        public final static Property ImagePass = new Property(21, Integer.class, "imagePass", false, "IMAGE_PASS");
        public final static Property Salary = new Property(22, Double.class, "salary", false, "SALARY");
        public final static Property IsRead = new Property(23, Boolean.class, "isRead", false, "IS_READ");
        public final static Property Tel = new Property(24, String.class, "tel", false, "TEL");
    };


    public FlipperDao(DaoConfig config) {
        super(config);
    }
    
    public FlipperDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'FLIPPER' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'USER_ID' INTEGER," + // 1: userID
                "'NICKNAME' TEXT," + // 2: nickname
                "'REALNAME' TEXT," + // 3: realname
                "'GENDER' TEXT," + // 4: gender
                "'EMAIL' TEXT," + // 5: email
                "'LARGE_AVATAR' TEXT," + // 6: largeAvatar
                "'SAMLL_AVATAR' TEXT," + // 7: samllAvatar
                "'BLOOD_TYPE' TEXT," + // 8: bloodType
                "'CONSTELL' TEXT," + // 9: constell
                "'INTRODUCE' TEXT," + // 10: introduce
                "'BIRTHDAY' INTEGER," + // 11: birthday
                "'TIME' INTEGER," + // 12: time
                "'AGE' INTEGER," + // 13: age
                "'VOCATION_ID' INTEGER," + // 14: vocationID
                "'STATE_ID' INTEGER," + // 15: stateID
                "'PROVINCE_ID' INTEGER," + // 16: provinceID
                "'CITY_ID' INTEGER," + // 17: cityID
                "'SCHOOL_ID' INTEGER," + // 18: schoolID
                "'HEIGHT' INTEGER," + // 19: height
                "'WEIGHT' INTEGER," + // 20: weight
                "'IMAGE_PASS' INTEGER," + // 21: imagePass
                "'SALARY' REAL," + // 22: salary
                "'IS_READ' INTEGER," + // 23: isRead
                "'TEL' TEXT);"); // 24: tel
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'FLIPPER'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Flipper entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Integer userID = entity.getUserID();
        if (userID != null) {
            stmt.bindLong(2, userID);
        }
 
        String nickname = entity.getNickname();
        if (nickname != null) {
            stmt.bindString(3, nickname);
        }
 
        String realname = entity.getRealname();
        if (realname != null) {
            stmt.bindString(4, realname);
        }
 
        String gender = entity.getGender();
        if (gender != null) {
            stmt.bindString(5, gender);
        }
 
        String email = entity.getEmail();
        if (email != null) {
            stmt.bindString(6, email);
        }
 
        String largeAvatar = entity.getLargeAvatar();
        if (largeAvatar != null) {
            stmt.bindString(7, largeAvatar);
        }
 
        String samllAvatar = entity.getSamllAvatar();
        if (samllAvatar != null) {
            stmt.bindString(8, samllAvatar);
        }
 
        String bloodType = entity.getBloodType();
        if (bloodType != null) {
            stmt.bindString(9, bloodType);
        }
 
        String constell = entity.getConstell();
        if (constell != null) {
            stmt.bindString(10, constell);
        }
 
        String introduce = entity.getIntroduce();
        if (introduce != null) {
            stmt.bindString(11, introduce);
        }
 
        java.util.Date birthday = entity.getBirthday();
        if (birthday != null) {
            stmt.bindLong(12, birthday.getTime());
        }
 
        java.util.Date time = entity.getTime();
        if (time != null) {
            stmt.bindLong(13, time.getTime());
        }
 
        Integer age = entity.getAge();
        if (age != null) {
            stmt.bindLong(14, age);
        }
 
        Integer vocationID = entity.getVocationID();
        if (vocationID != null) {
            stmt.bindLong(15, vocationID);
        }
 
        Integer stateID = entity.getStateID();
        if (stateID != null) {
            stmt.bindLong(16, stateID);
        }
 
        Integer provinceID = entity.getProvinceID();
        if (provinceID != null) {
            stmt.bindLong(17, provinceID);
        }
 
        Integer cityID = entity.getCityID();
        if (cityID != null) {
            stmt.bindLong(18, cityID);
        }
 
        Integer schoolID = entity.getSchoolID();
        if (schoolID != null) {
            stmt.bindLong(19, schoolID);
        }
 
        Integer height = entity.getHeight();
        if (height != null) {
            stmt.bindLong(20, height);
        }
 
        Integer weight = entity.getWeight();
        if (weight != null) {
            stmt.bindLong(21, weight);
        }
 
        Integer imagePass = entity.getImagePass();
        if (imagePass != null) {
            stmt.bindLong(22, imagePass);
        }
 
        Double salary = entity.getSalary();
        if (salary != null) {
            stmt.bindDouble(23, salary);
        }
 
        Boolean isRead = entity.getIsRead();
        if (isRead != null) {
            stmt.bindLong(24, isRead ? 1l: 0l);
        }
 
        String tel = entity.getTel();
        if (tel != null) {
            stmt.bindString(25, tel);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Flipper readEntity(Cursor cursor, int offset) {
        Flipper entity = new Flipper( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // userID
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // nickname
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // realname
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // gender
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // email
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // largeAvatar
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // samllAvatar
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // bloodType
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // constell
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // introduce
            cursor.isNull(offset + 11) ? null : new java.util.Date(cursor.getLong(offset + 11)), // birthday
            cursor.isNull(offset + 12) ? null : new java.util.Date(cursor.getLong(offset + 12)), // time
            cursor.isNull(offset + 13) ? null : cursor.getInt(offset + 13), // age
            cursor.isNull(offset + 14) ? null : cursor.getInt(offset + 14), // vocationID
            cursor.isNull(offset + 15) ? null : cursor.getInt(offset + 15), // stateID
            cursor.isNull(offset + 16) ? null : cursor.getInt(offset + 16), // provinceID
            cursor.isNull(offset + 17) ? null : cursor.getInt(offset + 17), // cityID
            cursor.isNull(offset + 18) ? null : cursor.getInt(offset + 18), // schoolID
            cursor.isNull(offset + 19) ? null : cursor.getInt(offset + 19), // height
            cursor.isNull(offset + 20) ? null : cursor.getInt(offset + 20), // weight
            cursor.isNull(offset + 21) ? null : cursor.getInt(offset + 21), // imagePass
            cursor.isNull(offset + 22) ? null : cursor.getDouble(offset + 22), // salary
            cursor.isNull(offset + 23) ? null : cursor.getShort(offset + 23) != 0, // isRead
            cursor.isNull(offset + 24) ? null : cursor.getString(offset + 24) // tel
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Flipper entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserID(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setNickname(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setRealname(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setGender(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setEmail(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setLargeAvatar(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setSamllAvatar(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setBloodType(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setConstell(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setIntroduce(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setBirthday(cursor.isNull(offset + 11) ? null : new java.util.Date(cursor.getLong(offset + 11)));
        entity.setTime(cursor.isNull(offset + 12) ? null : new java.util.Date(cursor.getLong(offset + 12)));
        entity.setAge(cursor.isNull(offset + 13) ? null : cursor.getInt(offset + 13));
        entity.setVocationID(cursor.isNull(offset + 14) ? null : cursor.getInt(offset + 14));
        entity.setStateID(cursor.isNull(offset + 15) ? null : cursor.getInt(offset + 15));
        entity.setProvinceID(cursor.isNull(offset + 16) ? null : cursor.getInt(offset + 16));
        entity.setCityID(cursor.isNull(offset + 17) ? null : cursor.getInt(offset + 17));
        entity.setSchoolID(cursor.isNull(offset + 18) ? null : cursor.getInt(offset + 18));
        entity.setHeight(cursor.isNull(offset + 19) ? null : cursor.getInt(offset + 19));
        entity.setWeight(cursor.isNull(offset + 20) ? null : cursor.getInt(offset + 20));
        entity.setImagePass(cursor.isNull(offset + 21) ? null : cursor.getInt(offset + 21));
        entity.setSalary(cursor.isNull(offset + 22) ? null : cursor.getDouble(offset + 22));
        entity.setIsRead(cursor.isNull(offset + 23) ? null : cursor.getShort(offset + 23) != 0);
        entity.setTel(cursor.isNull(offset + 24) ? null : cursor.getString(offset + 24));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Flipper entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Flipper entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
