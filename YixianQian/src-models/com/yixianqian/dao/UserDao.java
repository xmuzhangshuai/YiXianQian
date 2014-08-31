package com.yixianqian.dao;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.internal.DaoConfig;

import com.yixianqian.entities.City;
import com.yixianqian.entities.Province;
import com.yixianqian.entities.School;

import com.yixianqian.entities.User;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table USER.
*/
public class UserDao extends AbstractDao<User, Void> {

    public static final String TABLENAME = "USER";

    /**
     * Properties of entity User.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property UserID = new Property(0, Long.class, "userID", false, "USER_ID");
        public final static Property BpushUserID = new Property(1, String.class, "bpushUserID", false, "BPUSH_USER_ID");
        public final static Property BpushChannelID = new Property(2, String.class, "bpushChannelID", false, "BPUSH_CHANNEL_ID");
        public final static Property Nickname = new Property(3, String.class, "nickname", false, "NICKNAME");
        public final static Property Realname = new Property(4, String.class, "realname", false, "REALNAME");
        public final static Property Password = new Property(5, String.class, "password", false, "PASSWORD");
        public final static Property Gender = new Property(6, String.class, "gender", false, "GENDER");
        public final static Property Tel = new Property(7, String.class, "tel", false, "TEL");
        public final static Property Email = new Property(8, String.class, "email", false, "EMAIL");
        public final static Property Large_avatar = new Property(9, String.class, "large_avatar", false, "LARGE_AVATAR");
        public final static Property Small_avatar = new Property(10, String.class, "small_avatar", false, "SMALL_AVATAR");
        public final static Property Address = new Property(11, String.class, "address", false, "ADDRESS");
        public final static Property Blood_type = new Property(12, String.class, "blood_type", false, "BLOOD_TYPE");
        public final static Property Constell = new Property(13, String.class, "constell", false, "CONSTELL");
        public final static Property Introduce = new Property(14, String.class, "introduce", false, "INTRODUCE");
        public final static Property Age = new Property(15, Integer.class, "age", false, "AGE");
        public final static Property Vocationid = new Property(16, Integer.class, "vocationid", false, "VOCATIONID");
        public final static Property Stateid = new Property(17, Integer.class, "stateid", false, "STATEID");
        public final static Property Height = new Property(18, Integer.class, "height", false, "HEIGHT");
        public final static Property Weight = new Property(19, Integer.class, "weight", false, "WEIGHT");
        public final static Property Image_pass = new Property(20, Integer.class, "image_pass", false, "IMAGE_PASS");
        public final static Property Salary = new Property(21, Double.class, "salary", false, "SALARY");
        public final static Property Birthday = new Property(22, java.util.Date.class, "birthday", false, "BIRTHDAY");
        public final static Property SchoolID = new Property(23, long.class, "schoolID", false, "SCHOOL_ID");
        public final static Property CityID = new Property(24, long.class, "cityID", false, "CITY_ID");
        public final static Property ProvinceID = new Property(25, long.class, "provinceID", false, "PROVINCE_ID");
    };

    private DaoSession daoSession;


    public UserDao(DaoConfig config) {
        super(config);
    }
    
    public UserDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'USER' (" + //
                "'USER_ID' INTEGER," + // 0: userID
                "'BPUSH_USER_ID' TEXT," + // 1: bpushUserID
                "'BPUSH_CHANNEL_ID' TEXT," + // 2: bpushChannelID
                "'NICKNAME' TEXT," + // 3: nickname
                "'REALNAME' TEXT," + // 4: realname
                "'PASSWORD' TEXT," + // 5: password
                "'GENDER' TEXT," + // 6: gender
                "'TEL' TEXT," + // 7: tel
                "'EMAIL' TEXT," + // 8: email
                "'LARGE_AVATAR' TEXT," + // 9: large_avatar
                "'SMALL_AVATAR' TEXT," + // 10: small_avatar
                "'ADDRESS' TEXT," + // 11: address
                "'BLOOD_TYPE' TEXT," + // 12: blood_type
                "'CONSTELL' TEXT," + // 13: constell
                "'INTRODUCE' TEXT," + // 14: introduce
                "'AGE' INTEGER," + // 15: age
                "'VOCATIONID' INTEGER," + // 16: vocationid
                "'STATEID' INTEGER," + // 17: stateid
                "'HEIGHT' INTEGER," + // 18: height
                "'WEIGHT' INTEGER," + // 19: weight
                "'IMAGE_PASS' INTEGER," + // 20: image_pass
                "'SALARY' REAL," + // 21: salary
                "'BIRTHDAY' INTEGER," + // 22: birthday
                "'SCHOOL_ID' INTEGER NOT NULL ," + // 23: schoolID
                "'CITY_ID' INTEGER NOT NULL ," + // 24: cityID
                "'PROVINCE_ID' INTEGER NOT NULL );"); // 25: provinceID
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'USER'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, User entity) {
        stmt.clearBindings();
 
        Long userID = entity.getUserID();
        if (userID != null) {
            stmt.bindLong(1, userID);
        }
 
        String bpushUserID = entity.getBpushUserID();
        if (bpushUserID != null) {
            stmt.bindString(2, bpushUserID);
        }
 
        String bpushChannelID = entity.getBpushChannelID();
        if (bpushChannelID != null) {
            stmt.bindString(3, bpushChannelID);
        }
 
        String nickname = entity.getNickname();
        if (nickname != null) {
            stmt.bindString(4, nickname);
        }
 
        String realname = entity.getRealname();
        if (realname != null) {
            stmt.bindString(5, realname);
        }
 
        String password = entity.getPassword();
        if (password != null) {
            stmt.bindString(6, password);
        }
 
        String gender = entity.getGender();
        if (gender != null) {
            stmt.bindString(7, gender);
        }
 
        String tel = entity.getTel();
        if (tel != null) {
            stmt.bindString(8, tel);
        }
 
        String email = entity.getEmail();
        if (email != null) {
            stmt.bindString(9, email);
        }
 
        String large_avatar = entity.getLarge_avatar();
        if (large_avatar != null) {
            stmt.bindString(10, large_avatar);
        }
 
        String small_avatar = entity.getSmall_avatar();
        if (small_avatar != null) {
            stmt.bindString(11, small_avatar);
        }
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(12, address);
        }
 
        String blood_type = entity.getBlood_type();
        if (blood_type != null) {
            stmt.bindString(13, blood_type);
        }
 
        String constell = entity.getConstell();
        if (constell != null) {
            stmt.bindString(14, constell);
        }
 
        String introduce = entity.getIntroduce();
        if (introduce != null) {
            stmt.bindString(15, introduce);
        }
 
        Integer age = entity.getAge();
        if (age != null) {
            stmt.bindLong(16, age);
        }
 
        Integer vocationid = entity.getVocationid();
        if (vocationid != null) {
            stmt.bindLong(17, vocationid);
        }
 
        Integer stateid = entity.getStateid();
        if (stateid != null) {
            stmt.bindLong(18, stateid);
        }
 
        Integer height = entity.getHeight();
        if (height != null) {
            stmt.bindLong(19, height);
        }
 
        Integer weight = entity.getWeight();
        if (weight != null) {
            stmt.bindLong(20, weight);
        }
 
        Integer image_pass = entity.getImage_pass();
        if (image_pass != null) {
            stmt.bindLong(21, image_pass);
        }
 
        Double salary = entity.getSalary();
        if (salary != null) {
            stmt.bindDouble(22, salary);
        }
 
        java.util.Date birthday = entity.getBirthday();
        if (birthday != null) {
            stmt.bindLong(23, birthday.getTime());
        }
        stmt.bindLong(24, entity.getSchoolID());
        stmt.bindLong(25, entity.getCityID());
        stmt.bindLong(26, entity.getProvinceID());
    }

    @Override
    protected void attachEntity(User entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    /** @inheritdoc */
    @Override
    public User readEntity(Cursor cursor, int offset) {
        User entity = new User( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // userID
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // bpushUserID
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // bpushChannelID
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // nickname
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // realname
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // password
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // gender
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // tel
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // email
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // large_avatar
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // small_avatar
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // address
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // blood_type
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // constell
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // introduce
            cursor.isNull(offset + 15) ? null : cursor.getInt(offset + 15), // age
            cursor.isNull(offset + 16) ? null : cursor.getInt(offset + 16), // vocationid
            cursor.isNull(offset + 17) ? null : cursor.getInt(offset + 17), // stateid
            cursor.isNull(offset + 18) ? null : cursor.getInt(offset + 18), // height
            cursor.isNull(offset + 19) ? null : cursor.getInt(offset + 19), // weight
            cursor.isNull(offset + 20) ? null : cursor.getInt(offset + 20), // image_pass
            cursor.isNull(offset + 21) ? null : cursor.getDouble(offset + 21), // salary
            cursor.isNull(offset + 22) ? null : new java.util.Date(cursor.getLong(offset + 22)), // birthday
            cursor.getLong(offset + 23), // schoolID
            cursor.getLong(offset + 24), // cityID
            cursor.getLong(offset + 25) // provinceID
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, User entity, int offset) {
        entity.setUserID(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setBpushUserID(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setBpushChannelID(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setNickname(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setRealname(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setPassword(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setGender(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setTel(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setEmail(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setLarge_avatar(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setSmall_avatar(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setAddress(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setBlood_type(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setConstell(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setIntroduce(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setAge(cursor.isNull(offset + 15) ? null : cursor.getInt(offset + 15));
        entity.setVocationid(cursor.isNull(offset + 16) ? null : cursor.getInt(offset + 16));
        entity.setStateid(cursor.isNull(offset + 17) ? null : cursor.getInt(offset + 17));
        entity.setHeight(cursor.isNull(offset + 18) ? null : cursor.getInt(offset + 18));
        entity.setWeight(cursor.isNull(offset + 19) ? null : cursor.getInt(offset + 19));
        entity.setImage_pass(cursor.isNull(offset + 20) ? null : cursor.getInt(offset + 20));
        entity.setSalary(cursor.isNull(offset + 21) ? null : cursor.getDouble(offset + 21));
        entity.setBirthday(cursor.isNull(offset + 22) ? null : new java.util.Date(cursor.getLong(offset + 22)));
        entity.setSchoolID(cursor.getLong(offset + 23));
        entity.setCityID(cursor.getLong(offset + 24));
        entity.setProvinceID(cursor.getLong(offset + 25));
     }
    
    /** @inheritdoc */
    @Override
    protected Void updateKeyAfterInsert(User entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    /** @inheritdoc */
    @Override
    public Void getKey(User entity) {
        return null;
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getSchoolDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T1", daoSession.getCityDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T2", daoSession.getProvinceDao().getAllColumns());
            builder.append(" FROM USER T");
            builder.append(" LEFT JOIN SCHOOL T0 ON T.'SCHOOL_ID'=T0.'_id'");
            builder.append(" LEFT JOIN CITY T1 ON T.'CITY_ID'=T1.'_id'");
            builder.append(" LEFT JOIN PROVINCE T2 ON T.'PROVINCE_ID'=T2.'_id'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected User loadCurrentDeep(Cursor cursor, boolean lock) {
        User entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        School school = loadCurrentOther(daoSession.getSchoolDao(), cursor, offset);
         if(school != null) {
            entity.setSchool(school);
        }
        offset += daoSession.getSchoolDao().getAllColumns().length;

        City city = loadCurrentOther(daoSession.getCityDao(), cursor, offset);
         if(city != null) {
            entity.setCity(city);
        }
        offset += daoSession.getCityDao().getAllColumns().length;

        Province province = loadCurrentOther(daoSession.getProvinceDao(), cursor, offset);
         if(province != null) {
            entity.setProvince(province);
        }

        return entity;    
    }

    public User loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<User> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<User> list = new ArrayList<User>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<User> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<User> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}