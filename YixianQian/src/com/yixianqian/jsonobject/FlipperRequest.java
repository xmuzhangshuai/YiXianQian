package com.yixianqian.jsonobject;

import java.sql.Date;

/**
 * 
* @Description: TODO(FlipperRequest.java类) 
* @author lks   
* @date 2014年7月6日 上午10:26:28 
* @version V1.0
 */
public class FlipperRequest {
	
	private int fr_id;
	private int fr_userid;
	private int fr_flipperid;
	private Date fr_state;
	private Date fr_time;
	
	public int getFr_id() {
		return fr_id;
	}
	public void setFr_id(int fr_id) {
		this.fr_id = fr_id;
	}
	public int getFr_userid() {
		return fr_userid;
	}
	public void setFr_userid(int fr_userid) {
		this.fr_userid = fr_userid;
	}
	public int getFr_flipperid() {
		return fr_flipperid;
	}
	public void setFr_flipperid(int fr_flipperid) {
		this.fr_flipperid = fr_flipperid;
	}
	public Date getFr_state() {
		return fr_state;
	}
	public void setFr_state(Date fr_state) {
		this.fr_state = fr_state;
	}
	public Date getFr_time() {
		return fr_time;
	}
	public void setFr_time(Date fr_time) {
		this.fr_time = fr_time;
	}
	
}
