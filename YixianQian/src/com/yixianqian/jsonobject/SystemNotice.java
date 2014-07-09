package com.yixianqian.jsonobject;

import java.sql.Date;

/**
 * 
* @Description: TODO(SystemNotice.java类) 
* @author lks   
* @date 2014年7月9日 上午11:29:49 
* @version V1.0
 */
public class SystemNotice {
	
	private int sn_id;
	private String sn_url;
	private String sn_text;
	private Date sn_time;
	private int sn_state;
	private String sn_imageurl;
	public int getSn_id() {
		return sn_id;
	}
	public void setSn_id(int sn_id) {
		this.sn_id = sn_id;
	}
	public String getSn_url() {
		return sn_url;
	}
	public void setSn_url(String sn_url) {
		this.sn_url = sn_url;
	}
	public String getSn_text() {
		return sn_text;
	}
	public void setSn_text(String sn_text) {
		this.sn_text = sn_text;
	}
	public Date getSn_time() {
		return sn_time;
	}
	public void setSn_time(Date sn_time) {
		this.sn_time = sn_time;
	}
	public int getSn_state() {
		return sn_state;
	}
	public void setSn_state(int sn_state) {
		this.sn_state = sn_state;
	}
	public String getSn_imageurl() {
		return sn_imageurl;
	}
	public void setSn_imageurl(String sn_imageurl) {
		this.sn_imageurl = sn_imageurl;
	}
	public SystemNotice() {
		super();
	}
	
	
}
