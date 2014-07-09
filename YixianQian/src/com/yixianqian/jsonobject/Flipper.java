package com.yixianqian.jsonobject;

import java.sql.Date;

/**
 * 
* @Description: TODO(Flipper.java类) 
* @author lks   
* @date 2014年7月6日 上午10:23:51 
* @version V1.0
 */
public class Flipper {
	
	private int f_id;
	private int f_userid;
	private int f_filpperid;
	private Date f_start_time;
	private Date f_end_timeDate;
	private String f_state;
	
	public int getF_id() {
		return f_id;
	}
	public void setF_id(int f_id) {
		this.f_id = f_id;
	}
	public int getF_userid() {
		return f_userid;
	}
	public void setF_userid(int f_userid) {
		this.f_userid = f_userid;
	}
	public int getF_filpperid() {
		return f_filpperid;
	}
	public void setF_filpperid(int f_filpperid) {
		this.f_filpperid = f_filpperid;
	}
	public Date getF_start_time() {
		return f_start_time;
	}
	public void setF_start_time(Date f_start_time) {
		this.f_start_time = f_start_time;
	}
	public Date getF_end_timeDate() {
		return f_end_timeDate;
	}
	public void setF_end_timeDate(Date f_end_timeDate) {
		this.f_end_timeDate = f_end_timeDate;
	}
	public String getF_state() {
		return f_state;
	}
	public void setF_state(String f_state) {
		this.f_state = f_state;
	}
	
	
}
