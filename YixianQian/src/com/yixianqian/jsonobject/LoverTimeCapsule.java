package com.yixianqian.jsonobject;

import java.sql.Date;

/**
 * 
* @Description: TODO(LoverTimeCapsule.java类) 
* @author lks   
* @date 2014年7月9日 上午11:26:47 
* @version V1.0
 */
public class LoverTimeCapsule {
	
	private int ltc_msgid;
	private int ltc_loverid;
	private int ltc_userid;
	private Date ltc_record_time;
	private String ltc_voice;
	private int ltc_voice_length;
	private String ltc_photo;
	private String ltc_location;
	public int getLtc_msgid() {
		return ltc_msgid;
	}
	public void setLtc_msgid(int ltc_msgid) {
		this.ltc_msgid = ltc_msgid;
	}
	public int getLtc_loverid() {
		return ltc_loverid;
	}
	public void setLtc_loverid(int ltc_loverid) {
		this.ltc_loverid = ltc_loverid;
	}
	public int getLtc_userid() {
		return ltc_userid;
	}
	public void setLtc_userid(int ltc_userid) {
		this.ltc_userid = ltc_userid;
	}
	public Date getLtc_record_time() {
		return ltc_record_time;
	}
	public void setLtc_record_time(Date ltc_record_time) {
		this.ltc_record_time = ltc_record_time;
	}
	public String getLtc_voice() {
		return ltc_voice;
	}
	public void setLtc_voice(String ltc_voice) {
		this.ltc_voice = ltc_voice;
	}
	public int getLtc_voice_length() {
		return ltc_voice_length;
	}
	public void setLtc_voice_length(int ltc_voice_length) {
		this.ltc_voice_length = ltc_voice_length;
	}
	public String getLtc_photo() {
		return ltc_photo;
	}
	public void setLtc_photo(String ltc_photo) {
		this.ltc_photo = ltc_photo;
	}
	public String getLtc_location() {
		return ltc_location;
	}
	public void setLtc_location(String ltc_location) {
		this.ltc_location = ltc_location;
	}
	
}
