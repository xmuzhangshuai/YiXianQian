package com.yixianqian.jsonobject;

import java.io.Serializable;
import java.util.Date;

public class JsonLoveBridgeItem implements Serializable {

	private int n_id;
	private int n_userid;
	private String n_name;
	private String n_small_avatar;
	private String n_gender;
	private int n_schoolid;
	private String n_content;
	private String n_image;
	private Date n_time;
	private int n_flipcount;
	private int n_commentcount;

	public JsonLoveBridgeItem() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getN_id() {
		return n_id;
	}

	public String getN_small_avatar() {
		return n_small_avatar;
	}

	public void setN_small_avatar(String n_small_avatar) {
		this.n_small_avatar = n_small_avatar;
	}

	public void setN_id(int n_id) {
		this.n_id = n_id;
	}

	public int getN_userid() {
		return n_userid;
	}

	public void setN_userid(int n_userid) {
		this.n_userid = n_userid;
	}

	public String getN_name() {
		return n_name;
	}

	public void setN_name(String n_name) {
		this.n_name = n_name;
	}

	public String getN_gender() {
		return n_gender;
	}

	public void setN_gender(String n_gender) {
		this.n_gender = n_gender;
	}

	public int getN_schoolid() {
		return n_schoolid;
	}

	public void setN_schoolid(int n_schoolid) {
		this.n_schoolid = n_schoolid;
	}

	public String getN_content() {
		return n_content;
	}

	public void setN_content(String n_content) {
		this.n_content = n_content;
	}

	public String getN_image() {
		return n_image;
	}

	public void setN_image(String n_image) {
		this.n_image = n_image;
	}

	public Date getN_time() {
		return n_time;
	}

	public void setN_time(Date n_time) {
		this.n_time = n_time;
	}

	public int getN_flipcount() {
		return n_flipcount;
	}

	public void setN_flipcount(int n_flipcount) {
		this.n_flipcount = n_flipcount;
	}

	public int getN_commentcount() {
		return n_commentcount;
	}

	public void setN_commentcount(int n_commentcount) {
		this.n_commentcount = n_commentcount;
	}

	public JsonLoveBridgeItem(int n_id, int n_userid, String n_name, String n_gender, int n_schoolid, String n_content,
			String n_image, Date n_time) {
		super();
		this.n_id = n_id;
		this.n_userid = n_userid;
		this.n_name = n_name;
		this.n_gender = n_gender;
		this.n_schoolid = n_schoolid;
		this.n_content = n_content;
		this.n_image = n_image;
		this.n_time = n_time;
	}

	public JsonLoveBridgeItem(int n_userid, String n_name, String n_gender, int n_schoolid, String n_content,
			String n_image) {
		super();
		this.n_userid = n_userid;
		this.n_name = n_name;
		this.n_gender = n_gender;
		this.n_schoolid = n_schoolid;
		this.n_content = n_content;
		this.n_image = n_image;
	}

}
