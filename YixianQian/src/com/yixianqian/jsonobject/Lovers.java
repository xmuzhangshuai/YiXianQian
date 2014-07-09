package com.yixianqian.jsonobject;

import java.sql.Date;
/**
 * 
* @Description: TODO(Lovers.java类) 
* @author lks   
* @date 2014年7月6日 上午9:55:32 
* @version V1.0
 */
public class Lovers {
	
	private int l_id;
	private int l_userid;
	private int l_loverid;
	private Date l_start_time;
	private Date l_end_time;
	private String l_state;
	private int l_amount;
	
	
	public int getL_id() {
		return l_id;
	}
	public void setL_id(int l_id) {
		this.l_id = l_id;
	}
	public int getL_userid() {
		return l_userid;
	}
	public void setL_userid(int l_userid) {
		this.l_userid = l_userid;
	}
	public int getL_loverid() {
		return l_loverid;
	}
	public void setL_loverid(int l_loverid) {
		this.l_loverid = l_loverid;
	}
	public Date getL_start_time() {
		return l_start_time;
	}
	public void setL_start_time(Date l_start_time) {
		this.l_start_time = l_start_time;
	}
	public Date getL_end_time() {
		return l_end_time;
	}
	public void setL_end_time(Date l_end_time) {
		this.l_end_time = l_end_time;
	}
	public String getL_state() {
		return l_state;
	}
	public void setL_state(String l_state) {
		this.l_state = l_state;
	}
	public int getL_amount() {
		return l_amount;
	}
	public void setL_amount(int l_amount) {
		this.l_amount = l_amount;
	}
	
	
}
