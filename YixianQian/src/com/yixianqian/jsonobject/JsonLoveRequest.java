package com.yixianqian.jsonobject;

import java.util.Date;

public class JsonLoveRequest {

	private int lr_id;
	private int lr_userid;
	private int lr_loverid;
	private Date lr_time;
	private String lr_state;

	public int getLr_id() {
		return lr_id;
	}

	public void setLr_id(int lr_id) {
		this.lr_id = lr_id;
	}

	public int getLr_userid() {
		return lr_userid;
	}

	public void setLr_userid(int lr_userid) {
		this.lr_userid = lr_userid;
	}

	public int getLr_loverid() {
		return lr_loverid;
	}

	public void setLr_loverid(int lr_loverid) {
		this.lr_loverid = lr_loverid;
	}

	public Date getLr_time() {
		return lr_time;
	}

	public void setLr_time(Date lr_time) {
		this.lr_time = lr_time;
	}

	public String getLr_state() {
		return lr_state;
	}

	public void setLr_state(String lr_state) {
		this.lr_state = lr_state;
	}

	public JsonLoveRequest(int lr_id, int lr_userid, int lr_loverid, Date lr_time, String lr_state) {
		super();
		this.lr_id = lr_id;
		this.lr_userid = lr_userid;
		this.lr_loverid = lr_loverid;
		this.lr_time = lr_time;
		this.lr_state = lr_state;
	}

	public JsonLoveRequest() {
		super();
	}

}
