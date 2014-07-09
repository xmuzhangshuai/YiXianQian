package com.yixianqian.jsonobject;

import java.sql.Date;


/**
 * 
* @Description: TODO(Register.java类) 
* @author lks   
* @date 2014年7月6日 上午9:13:56 
* @version V1.0
 */
public class Register {
	
	private int r_id;
	private int r_userid;
	private Date r_time;
	private String r_result;
	private int r_failedid;
	
	
	public int getR_id() {
		return r_id;
	}
	public void setR_id(int r_id) {
		this.r_id = r_id;
	}
	public int getR_userid() {
		return r_userid;
	}
	public void setR_userid(int r_userid) {
		this.r_userid = r_userid;
	}
	public Date getR_time() {
		return r_time;
	}
	public void setR_time(Date r_time) {
		this.r_time = r_time;
	}
	public String getR_result() {
		return r_result;
	}
	public void setR_result(String r_result) {
		this.r_result = r_result;
	}
	public int getR_failedid() {
		return r_failedid;
	}
	public void setR_failedid(int r_failedid) {
		this.r_failedid = r_failedid;
	}
	
	
}
