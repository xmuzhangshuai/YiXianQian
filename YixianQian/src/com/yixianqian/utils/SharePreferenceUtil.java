package com.yixianqian.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.yixianqian.table.UserTable;

/**
 * �����ƣ�SharePreferenceUtil
 * ��������SharedPreferences��һ�������࣬����setParam���ܱ���String, Integer, Boolean, Float, Long���͵Ĳ��� 
 * 		     ͬ������getParam���ܻ�ȡ���������ֻ���������� 
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��7��13�� ����9:00:37
 *
 */
public class SharePreferenceUtil {
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	public static final String USER_SHAREPREFERENCE = "userSharePreference";//�û�SharePreference
	public static final String USE_COUNT = "connt";// ��¼����ʹ�ô���

	public SharePreferenceUtil(Context context, String file) {
		sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
		editor = sp.edit();
	}

	//��¼����ʹ�ô���
	public void setUseCount(int count) {
		editor.putInt("count", count);
		editor.commit();
	}

	public int getUseCount() {
		return sp.getInt("count", 0);
	}

	//�û�ID
	public int getU_id() {
		return sp.getInt(UserTable.U_ID, -1);
	}

	public void setU_id(int u_id) {
		editor.putInt(UserTable.U_ID, u_id);
		editor.commit();
	}

	//�û��ǳ�
	public String getU_nickname() {
		return sp.getString(UserTable.U_NICKNAME, null);
	}

	public void setU_nickname(String u_nickname) {
		editor.putString(UserTable.U_NICKNAME, u_nickname);
		editor.commit();
	}

	//�û���ʵ����
	public String getU_realname() {
		return sp.getString(UserTable.U_REALNAME, null);
	}

	public void setU_realname(String u_realname) {
		editor.putString(UserTable.U_REALNAME, u_realname);
		editor.commit();
	}

	//����
	public String getU_password() {
		return sp.getString(UserTable.U_PASSWORD, null);
	}

	public void setU_password(String u_password) {
		editor.putString(UserTable.U_PASSWORD, u_password);
		editor.commit();
	}

	//�Ա�
	public String getU_gender() {
		return sp.getString(UserTable.U_GENDER, null);
	}

	public void setU_gender(String u_gender) {
		editor.putString(UserTable.U_GENDER, u_gender);
		editor.commit();
	}

	//�ֻ���
	public String getU_tel() {
		return sp.getString(UserTable.U_TEL, null);
	}

	public void setU_tel(String u_tel) {
		editor.putString(UserTable.U_TEL, u_tel);
		editor.commit();
	}

	//����
	public String getU_email() {
		return sp.getString(UserTable.U_EMAIL, null);
	}

	public void setU_email(String u_email) {
		editor.putString(UserTable.U_EMAIL, u_email);
		editor.commit();
	}

	////����
	//	public Date getU_birthday() {
	//		return u_birthday;
	//	}
	//
	//	public void setU_birthday(Date u_birthday) {
	//		editor.put(UserTable.U_BIRTHDAY, u_birthday);
	//		editor.commit();
	//	}

	//����
	public int getU_age() {
		return sp.getInt(UserTable.U_AGE, -1);
	}

	public void setU_age(int u_age) {
		editor.putInt(UserTable.U_AGE, u_age);
		editor.commit();
	}

	//��ͷ��
	public String getU_large_avatar() {
		return sp.getString(UserTable.U_LARGE_AVATAR, null);
	}

	public void setU_large_avatar(String u_large_avatar) {
		editor.putString(UserTable.U_LARGE_AVATAR, u_large_avatar);
		editor.commit();
	}

	//Сͷ��
	public String getU_small_avatar() {
		return sp.getString(UserTable.U_SMALL_AVATAR, null);
	}

	public void setU_small_avatar(String u_small_avatar) {
		editor.putString(UserTable.U_SMALL_AVATAR, u_small_avatar);
		editor.commit();
	}

	//ְҵ����
	public int getU_vocationid() {
		return sp.getInt(UserTable.U_VOCATIONID, -1);
	}

	public void setU_vocationid(int u_vocationid) {
		editor.putInt(UserTable.U_VOCATIONID, u_vocationid);
		editor.commit();
	}

	//״̬
	public int getU_stateid() {
		return sp.getInt(UserTable.U_STATEID, -1);
	}

	public void setU_stateid(int u_stateid) {
		editor.putInt(UserTable.U_STATEID, u_stateid);
		editor.commit();
	}

	//ʡ��
	public int getU_provinceid() {
		return sp.getInt(UserTable.U_PROVINCEID, -1);
	}

	public void setU_provinceid(int u_provinceid) {
		editor.putInt(UserTable.U_PROVINCEID, u_provinceid);
		editor.commit();
	}

	//����
	public int getU_cityid() {
		return sp.getInt(UserTable.U_CITYID, -1);
	}

	public void setU_cityid(int u_cityid) {
		editor.putInt(UserTable.U_CITYID, u_cityid);
		editor.commit();
	}

	//ѧУ
	public int getU_schoolid() {
		return sp.getInt(UserTable.U_SCHOOLID, -1);
	}

	public void setU_schoolid(int u_schoolid) {
		editor.putInt(UserTable.U_SCHOOLID, u_schoolid);
		editor.commit();
	}

	//��ַ
	public String getU_address() {
		return sp.getString(UserTable.U_ADDRESS, null);
	}

	public void setU_address(String u_address) {
		editor.putString(UserTable.U_ADDRESS, u_address);
		editor.commit();
	}

	//����
	public int getU_height() {
		return sp.getInt(UserTable.U_HEIGHT, -1);
	}

	public void setU_height(int u_height) {
		editor.putInt(UserTable.U_HEIGHT, u_height);
		editor.commit();
	}

	//����
	public int getU_weight() {
		return sp.getInt(UserTable.U_WEIGHT, -1);
	}

	public void setU_weight(int u_weight) {
		editor.putInt(UserTable.U_WEIGHT, u_weight);
		editor.commit();
	}

	//Ѫ��
	public String getU_blood_type() {
		return sp.getString(UserTable.U_BLOOD_TYPE, null);
	}

	public void setU_blood_type(String u_blood_type) {
		editor.putString(UserTable.U_BLOOD_TYPE, u_blood_type);
		editor.commit();
	}

	//����
	public String getU_constell() {
		return sp.getString(UserTable.U_CONSTELL, null);
	}

	public void setU_constell(String u_constell) {
		editor.putString(UserTable.U_CONSTELL, u_constell);
		editor.commit();
	}

	//�û����
	public String getU_introduce() {
		return sp.getString(UserTable.U_INTRODUCE, null);
	}

	public void setU_introduce(String u_introduce) {
		editor.putString(UserTable.U_INTRODUCE, u_introduce);
		editor.commit();
	}

	//����
	public double getU_salary() {
		return sp.getInt(UserTable.U_SALARY, -1);
	}

	public void setU_salary(double u_salary) {
		editor.putInt(UserTable.U_SALARY, (int) u_salary);
		editor.commit();
	}

	//	public int getU_amount() {
	//		return u_amount;
	//	}
	//
	//	public void setU_amount(int u_amount) {
	//		editor.putInt(UserTable.U_AGE, u_age);
	//		editor.commit();
	//	}

}