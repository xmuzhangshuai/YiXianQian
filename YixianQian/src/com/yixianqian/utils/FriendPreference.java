package com.yixianqian.utils;

import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.yixianqian.dao.CityDao.Properties;
import com.yixianqian.db.CityDbService;
import com.yixianqian.db.ProvinceDbService;
import com.yixianqian.db.SchoolDbService;
import com.yixianqian.entities.City;
import com.yixianqian.entities.School;
import com.yixianqian.jsonobject.JsonUser;
import com.yixianqian.table.LoversTable;
import com.yixianqian.table.UserTable;

/**
 * �����ƣ�FriendSharePreference
 * �����������ڴ洢���»����Ķ���ϵ��Ϣ
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��7��18�� ����8:33:34
 *
 */
public class FriendPreference {
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	public static final String FRIEND_SHAREDPREFERENCE = "FriendSharePreference";//���»����Ķ���ϵSharePreference
	private Context context;

	public FriendPreference(Context context) {
		this.context = context;
		sp = context.getSharedPreferences(FRIEND_SHAREDPREFERENCE, Context.MODE_PRIVATE);
		editor = sp.edit();
	}

	public JsonUser getJsonUser() {
		JsonUser jsonUser = new JsonUser(getF_id(), getF_nickname(), getF_realname(), null, getF_gender(), getF_tel(),
				getF_email(), getU_birthday(), getF_age(), getF_large_avatar(), getF_small_avatar(), getF_vocationid(),
				getF_stateid(), getU_provinceid(), getU_cityid(), getU_schoolid(), getF_address(), getF_height(),
				getF_weight(), getF_blood_type(), getF_constell(), getF_introduce(), getF_salary(), 0);
		return jsonUser;
	}

	/**
	 * �������
	 */
	public void clear() {
		editor.clear();
		editor.commit();
	}

	//��¼ͷ���Ƿ����ͨ��
	//-1Ϊδͨ����0Ϊ����ˣ�1Ϊ��ͨ��
	public int getHeadImagePassed() {
		return sp.getInt("passed", 0);
	}

	public void setHeadImagePassed(int passed) {
		editor.putInt("passed", passed);
		editor.commit();
	}

	//��ȡ���ͣ�0Ϊ�Ķ���ϵ��1Ϊ����
	public int getType() {
		return sp.getInt("friend", -1);
	}

	public void setType(int type) {
		editor.putInt("friend", type);
		editor.commit();
	}

	//���¹�ϵID
	public int getLoverId() {
		return sp.getInt(LoversTable.L_ID, -1);
	}

	public void setLoverId(int loverId) {
		editor.putInt(LoversTable.L_ID, loverId);
		editor.commit();
	}

	//���ȷ�������
	public String getName() {
		String name = getF_nickname();
		if (!TextUtils.isEmpty(getF_realname())) {
			name = getF_realname();
		}
		return name;
	}

	//�û�ID
	public int getF_id() {
		return sp.getInt(UserTable.U_ID, -1);
	}

	public void setF_id(int f_id) {
		editor.putInt(UserTable.U_ID, f_id);
		editor.commit();
	}

	//�ٶ�����userID
	public String getBpush_UserID() {
		return sp.getString(UserTable.U_BPUSH_USER_ID, "");
	}

	public void setBpush_UserID(String f_id) {
		editor.putString(UserTable.U_BPUSH_USER_ID, f_id);
		editor.commit();
	}

	//�ٶ�����ChannelID
	public String getBpush_ChannelID() {
		return sp.getString(UserTable.U_BPUSH_CHANNEL_ID, "");
	}

	public void setBpush_ChannelID(String f_id) {
		editor.putString(UserTable.U_BPUSH_CHANNEL_ID, f_id);
		editor.commit();
	}

	//�ٶ�����AppID
	public String getAppID() {
		return sp.getString("appId", "");
	}

	public void setAppID(String appId) {
		editor.putString("appId", appId);
		editor.commit();
	}

	//�û��ǳ�
	public String getF_nickname() {
		return sp.getString(UserTable.U_NICKNAME, null);
	}

	public void setF_nickname(String f_nickname) {
		editor.putString(UserTable.U_NICKNAME, f_nickname);
		editor.commit();
	}

	//�û���ʵ����
	public String getF_realname() {
		return sp.getString(UserTable.U_REALNAME, null);
	}

	public void setF_realname(String u_realname) {
		editor.putString(UserTable.U_REALNAME, u_realname);
		editor.commit();
	}

	//�Ա�
	public String getF_gender() {
		return sp.getString(UserTable.U_GENDER, null);
	}

	public void setF_gender(String u_gender) {
		editor.putString(UserTable.U_GENDER, u_gender);
		editor.commit();
	}

	//�ֻ���
	public String getF_tel() {
		return sp.getString(UserTable.U_TEL, null);
	}

	public void setF_tel(String u_tel) {
		editor.putString(UserTable.U_TEL, u_tel);
		editor.commit();
	}

	//����
	public String getF_email() {
		return sp.getString(UserTable.U_EMAIL, null);
	}

	public void setF_email(String u_email) {
		editor.putString(UserTable.U_EMAIL, u_email);
		editor.commit();
	}

	//����
	public Date getU_birthday() {
		Long time = sp.getLong(UserTable.U_BIRTHDAY, 0);
		if (time != 0) {
			return new Date(time);
		} else {
			return null;
		}
	}

	public void setU_birthday(Date u_birthday) {
		editor.putLong(UserTable.U_BIRTHDAY, u_birthday.getTime());
		editor.commit();
	}

	//����
	public int getF_age() {
		return sp.getInt(UserTable.U_AGE, -1);
	}

	public void setF_age(int u_age) {
		editor.putInt(UserTable.U_AGE, u_age);
		editor.commit();
	}

	//��ͷ��
	public String getF_large_avatar() {
		return sp.getString(UserTable.U_LARGE_AVATAR, null);
	}

	public void setF_large_avatar(String u_large_avatar) {
		editor.putString(UserTable.U_LARGE_AVATAR, u_large_avatar);
		editor.commit();
	}

	//Сͷ��
	public String getF_small_avatar() {
		return sp.getString(UserTable.U_SMALL_AVATAR, null);
	}

	public void setF_small_avatar(String u_small_avatar) {
		editor.putString(UserTable.U_SMALL_AVATAR, u_small_avatar);
		editor.commit();
	}

	//ְҵ����
	public int getF_vocationid() {
		return sp.getInt(UserTable.U_VOCATIONID, -1);
	}

	public void setF_vocationid(int u_vocationid) {
		editor.putInt(UserTable.U_VOCATIONID, u_vocationid);
		editor.commit();
	}

	//״̬
	public int getF_stateid() {
		return sp.getInt(UserTable.U_STATEID, -1);
	}

	public void setF_stateid(int u_stateid) {
		editor.putInt(UserTable.U_STATEID, u_stateid);
		editor.commit();
	}

	//ʡ��
	public int getU_provinceid() {
		return sp.getInt(UserTable.U_PROVINCEID, -1);
	}

	public void setU_provinceid(int u_provinceid) {
		ProvinceDbService provinceDbService = ProvinceDbService.getInstance(context);
		setPeovinceName(provinceDbService.getProNameById(u_provinceid));
		editor.putInt(UserTable.U_PROVINCEID, u_provinceid);
		editor.commit();
	}

	public String getProvinceName() {
		return sp.getString("ProvinceName", "");
	}

	public void setPeovinceName(String name) {
		editor.putString("ProvinceName", name);
		editor.commit();
	}

	//����
	public int getU_cityid() {
		return sp.getInt(UserTable.U_CITYID, -1);
	}

	public void setU_cityid(int u_cityid) {
		CityDbService cityDbService = CityDbService.getInstance(context);
		City city = cityDbService.cityDao.queryBuilder().where(Properties.CityID.eq(u_cityid)).unique();
		if (city != null) {
			setCityName(city.getCityName());
		}
		editor.putInt(UserTable.U_CITYID, u_cityid);
		editor.commit();
	}

	public String getCityName() {
		return sp.getString("cityName", "");
	}

	public void setCityName(String name) {
		editor.putString("cityName", name);
		editor.commit();
	}

	//ѧУ
	public int getU_schoolid() {
		return sp.getInt(UserTable.U_SCHOOLID, -1);
	}

	public void setU_schoolid(int u_schoolid) {
		SchoolDbService schoolDbService = SchoolDbService.getInstance(context);
		School school = schoolDbService.schoolDao.load((long) u_schoolid);
		setSchoolName(school.getSchoolName());
		editor.putInt(UserTable.U_SCHOOLID, u_schoolid);
		editor.commit();
	}

	public String getSchoolName() {
		return sp.getString("schoolName", "");
	}

	public void setSchoolName(String name) {
		editor.putString("schoolName", name);
		editor.commit();
	}

	//��ַ
	public String getF_address() {
		return sp.getString(UserTable.U_ADDRESS, null);
	}

	public void setF_address(String u_address) {
		editor.putString(UserTable.U_ADDRESS, u_address);
		editor.commit();
	}

	//���
	public int getF_height() {
		return sp.getInt(UserTable.U_HEIGHT, -1);
	}

	public void setF_height(int u_height) {
		editor.putInt(UserTable.U_HEIGHT, u_height);
		editor.commit();
	}

	//����
	public int getF_weight() {
		return sp.getInt(UserTable.U_WEIGHT, -1);
	}

	public void setF_weight(int u_weight) {
		editor.putInt(UserTable.U_WEIGHT, u_weight);
		editor.commit();
	}

	//Ѫ��
	public String getF_blood_type() {
		return sp.getString(UserTable.U_BLOOD_TYPE, null);
	}

	public void setF_blood_type(String u_blood_type) {
		editor.putString(UserTable.U_BLOOD_TYPE, u_blood_type);
		editor.commit();
	}

	//����
	public String getF_constell() {
		return sp.getString(UserTable.U_CONSTELL, null);
	}

	public void setF_constell(String u_constell) {
		editor.putString(UserTable.U_CONSTELL, u_constell);
		editor.commit();
	}

	//�û����
	public String getF_introduce() {
		return sp.getString(UserTable.U_INTRODUCE, null);
	}

	public void setF_introduce(String u_introduce) {
		editor.putString(UserTable.U_INTRODUCE, u_introduce);
		editor.commit();
	}

	//����
	public double getF_salary() {
		return sp.getInt(UserTable.U_SALARY, -1);
	}

	public void setF_salary(double u_salary) {
		editor.putInt(UserTable.U_SALARY, (int) u_salary);
		editor.commit();
	}
}
