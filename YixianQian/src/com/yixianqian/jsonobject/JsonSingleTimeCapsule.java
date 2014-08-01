package com.yixianqian.jsonobject;

import java.util.Date;

/**
 * 
* @Description: TODO(SingleTimeCapsule.java类) 
* @author lks   
* @date 2014年7月9日 上午11:32:10 
* @version V1.0
 */
public class JsonSingleTimeCapsule {

	private int stc_msgid;;
	private int stc_userid;
	private Date stc_record_time;
	private String stc_voice;
	private int stc_voice_length;
	private String stc_photo;
	private String stc_location;

	public JsonSingleTimeCapsule() {
	}

	public int getStc_msgid() {
		return stc_msgid;
	}

	public void setStc_msgid(int stc_msgid) {
		this.stc_msgid = stc_msgid;
	}

	public int getStc_userid() {
		return stc_userid;
	}

	public void setStc_userid(int stc_userid) {
		this.stc_userid = stc_userid;
	}

	public Date getStc_record_time() {
		return stc_record_time;
	}

	public void setStc_record_time(Date stc_record_time) {
		this.stc_record_time = stc_record_time;
	}

	public String getStc_voice() {
		return stc_voice;
	}

	public void setStc_voice(String stc_voice) {
		this.stc_voice = stc_voice;
	}

	public int getStc_voice_length() {
		return stc_voice_length;
	}

	public void setStc_voice_length(int stc_voice_length) {
		this.stc_voice_length = stc_voice_length;
	}

	public String getStc_photo() {
		return stc_photo;
	}

	public void setStc_photo(String stc_photo) {
		this.stc_photo = stc_photo;
	}

	public String getStc_location() {
		return stc_location;
	}

	public void setStc_location(String stc_location) {
		this.stc_location = stc_location;
	}

}
