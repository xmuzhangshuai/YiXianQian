package com.yixianqian.jsonobject;

import java.util.Date;

public class JsonBridgeComment {
	
	private int c_id;
	private int n_id;
	private int n_userid;
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

	public int getN_userid() {
		return n_userid;
	}

	public void setN_userid(int n_userid) {
		this.n_userid = n_userid;
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


	public JsonBridgeComment(int n_id, int n_userid, String c_content,
			String c_read, Date c_time) {
		super();
		this.n_id = n_id;
		this.n_userid = n_userid;
		this.c_content = c_content;
		this.c_read = c_read;
		this.c_time = c_time;
	}

	public JsonBridgeComment() {
		super();
	}
	
	
	
}
