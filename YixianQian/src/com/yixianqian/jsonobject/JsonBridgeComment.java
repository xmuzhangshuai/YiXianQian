package com.yixianqian.jsonobject;

import java.util.Date;

public class JsonBridgeComment {

	private int c_id;
	private int n_id;
	private int c_userid;
	private String c_small_avatar;
	private String c_nickname;
	private String c_gender;
	private String c_content;
	private String c_read;
	private Date c_time;

	public int getC_id() {
		return c_id;
	}

	public void setC_id(int c_id) {
		this.c_id = c_id;
	}

	public int getN_id() {
		return n_id;
	}

	public void setN_id(int n_id) {
		this.n_id = n_id;
	}

	public int getC_userid() {
		return c_userid;
	}

	public void setC_userid(int c_userid) {
		this.c_userid = c_userid;
	}

	public String getC_content() {
		return c_content;
	}

	public void setC_content(String c_content) {
		this.c_content = c_content;
	}

	public String getC_read() {
		return c_read;
	}

	public void setC_read(String c_read) {
		this.c_read = c_read;
	}

	public Date getC_time() {
		return c_time;
	}

	public void setC_time(Date c_time) {
		this.c_time = c_time;
	}

	public String getC_small_avatar() {
		return c_small_avatar;
	}

	public void setC_small_avatar(String c_small_avatar) {
		this.c_small_avatar = c_small_avatar;
	}

	public String getC_nickname() {
		return c_nickname;
	}

	public void setC_nickname(String c_nickname) {
		this.c_nickname = c_nickname;
	}

	public String getC_gender() {
		return c_gender;
	}

	public void setC_gender(String c_gender) {
		this.c_gender = c_gender;
	}

	public JsonBridgeComment() {
		super();
	}

}
