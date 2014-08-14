package com.yixianqian.jsonobject;

import java.util.Date;

public class JsonFlipperRequest {
	private int u_id;
	private String u_tel;
	private String u_nickname;
	private String u_realname;
	private String u_gender;
	private String u_email;
	private Date u_birthday;
	private int u_age;
	private String u_large_avatar;
	private String u_small_avatar;
	private int u_vocationid;
	private int u_stateid;
	private int u_provinceid;
	private int u_cityid;
	private int u_schoolid;
	private int u_height;
	private int u_weight;
	private String u_blood_type;
	private String u_constell;
	private String u_introduce;
	private double u_salary;
	//头像审核的字段
	private int u_image_pass;
	//请求时间
	private Date time;
	//用于聊天所需要的字段
	private String u_bpush_user_id;
	private String u_bpush_channel_id;

	public JsonFlipperRequest() {
		// TODO Auto-generated constructor stub
	}

	public int getU_id() {
		return u_id;
	}

	public String getU_bpush_user_id() {
		return u_bpush_user_id;
	}

	public void setU_bpush_user_id(String u_bpush_user_id) {
		this.u_bpush_user_id = u_bpush_user_id;
	}

	public String getU_bpush_channel_id() {
		return u_bpush_channel_id;
	}

	public void setU_bpush_channel_id(String u_bpush_channel_id) {
		this.u_bpush_channel_id = u_bpush_channel_id;
	}

	public void setU_id(int u_id) {
		this.u_id = u_id;
	}

	public String getU_nickname() {
		return u_nickname;
	}

	public void setU_nickname(String u_nickname) {
		this.u_nickname = u_nickname;
	}

	public String getU_realname() {
		return u_realname;
	}

	public void setU_realname(String u_realname) {
		this.u_realname = u_realname;
	}

	public String getU_gender() {
		return u_gender;
	}

	public void setU_gender(String u_gender) {
		this.u_gender = u_gender;
	}

	public String getU_email() {
		return u_email;
	}

	public void setU_email(String u_email) {
		this.u_email = u_email;
	}

	public Date getU_birthday() {
		return u_birthday;
	}

	public void setU_birthday(Date u_birthday) {
		this.u_birthday = u_birthday;
	}

	public int getU_age() {
		return u_age;
	}

	public void setU_age(int u_age) {
		this.u_age = u_age;
	}

	public String getU_large_avatar() {
		return u_large_avatar;
	}

	public void setU_large_avatar(String u_large_avatar) {
		this.u_large_avatar = u_large_avatar;
	}

	public String getU_small_avatar() {
		return u_small_avatar;
	}

	public void setU_small_avatar(String u_small_avatar) {
		this.u_small_avatar = u_small_avatar;
	}

	public int getU_vocationid() {
		return u_vocationid;
	}

	public void setU_vocationid(int u_vocationid) {
		this.u_vocationid = u_vocationid;
	}

	public int getU_stateid() {
		return u_stateid;
	}

	public void setU_stateid(int u_stateid) {
		this.u_stateid = u_stateid;
	}

	public int getU_provinceid() {
		return u_provinceid;
	}

	public void setU_provinceid(int u_provinceid) {
		this.u_provinceid = u_provinceid;
	}

	public int getU_cityid() {
		return u_cityid;
	}

	public void setU_cityid(int u_cityid) {
		this.u_cityid = u_cityid;
	}

	public int getU_schoolid() {
		return u_schoolid;
	}

	public void setU_schoolid(int u_schoolid) {
		this.u_schoolid = u_schoolid;
	}

	public int getU_height() {
		return u_height;
	}

	public void setU_height(int u_height) {
		this.u_height = u_height;
	}

	public int getU_weight() {
		return u_weight;
	}

	public void setU_weight(int u_weight) {
		this.u_weight = u_weight;
	}

	public String getU_blood_type() {
		return u_blood_type;
	}

	public void setU_blood_type(String u_blood_type) {
		this.u_blood_type = u_blood_type;
	}

	public String getU_constell() {
		return u_constell;
	}

	public void setU_constell(String u_constell) {
		this.u_constell = u_constell;
	}

	public String getU_introduce() {
		return u_introduce;
	}

	public void setU_introduce(String u_introduce) {
		this.u_introduce = u_introduce;
	}

	public double getU_salary() {
		return u_salary;
	}

	public void setU_salary(double u_salary) {
		this.u_salary = u_salary;
	}

	public int getU_image_pass() {
		return u_image_pass;
	}

	public void setU_image_pass(int u_image_pass) {
		this.u_image_pass = u_image_pass;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getU_tel() {
		return u_tel;
	}

	public void setU_tel(String u_tel) {
		this.u_tel = u_tel;
	}

	public JsonFlipperRequest(int u_id, String u_tel, String u_nickname, String u_realname, String u_gender,
			String u_email, Date u_birthday, int u_age, String u_large_avatar, String u_small_avatar, int u_vocationid,
			int u_stateid, int u_provinceid, int u_cityid, int u_schoolid, int u_height, int u_weight,
			String u_blood_type, String u_constell, String u_introduce, double u_salary, int u_image_pass, Date time,
			String u_bpush_user_id, String u_bpush_channel_id) {
		super();
		this.u_id = u_id;
		this.u_tel = u_tel;
		this.u_nickname = u_nickname;
		this.u_realname = u_realname;
		this.u_gender = u_gender;
		this.u_email = u_email;
		this.u_birthday = u_birthday;
		this.u_age = u_age;
		this.u_large_avatar = u_large_avatar;
		this.u_small_avatar = u_small_avatar;
		this.u_vocationid = u_vocationid;
		this.u_stateid = u_stateid;
		this.u_provinceid = u_provinceid;
		this.u_cityid = u_cityid;
		this.u_schoolid = u_schoolid;
		this.u_height = u_height;
		this.u_weight = u_weight;
		this.u_blood_type = u_blood_type;
		this.u_constell = u_constell;
		this.u_introduce = u_introduce;
		this.u_salary = u_salary;
		this.u_image_pass = u_image_pass;
		this.time = time;
		this.u_bpush_user_id = u_bpush_user_id;
		this.u_bpush_channel_id = u_bpush_channel_id;
	}

//	public JsonFlipperRequest(int u_id, String u_tel, String u_nickname, String u_realname, String u_gender,
//			String u_email, Date u_birthday, int u_age, String u_large_avatar, String u_small_avatar, int u_vocationid,
//			int u_stateid, int u_provinceid, int u_cityid, int u_schoolid, int u_height, int u_weight,
//			String u_blood_type, String u_constell, String u_introduce, double u_salary, int u_image_pass, Date time) {
//		super();
//		this.u_id = u_id;
//		this.u_tel = u_tel;
//		this.u_nickname = u_nickname;
//		this.u_realname = u_realname;
//		this.u_gender = u_gender;
//		this.u_email = u_email;
//		this.u_birthday = u_birthday;
//		this.u_age = u_age;
//		this.u_large_avatar = u_large_avatar;
//		this.u_small_avatar = u_small_avatar;
//		this.u_vocationid = u_vocationid;
//		this.u_stateid = u_stateid;
//		this.u_provinceid = u_provinceid;
//		this.u_cityid = u_cityid;
//		this.u_schoolid = u_schoolid;
//		this.u_height = u_height;
//		this.u_weight = u_weight;
//		this.u_blood_type = u_blood_type;
//		this.u_constell = u_constell;
//		this.u_introduce = u_introduce;
//		this.u_salary = u_salary;
//		this.u_image_pass = u_image_pass;
//		this.time = time;
//	}

}
