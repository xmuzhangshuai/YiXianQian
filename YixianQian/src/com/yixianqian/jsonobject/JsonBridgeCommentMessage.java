package com.yixianqian.jsonobject;

import java.util.Date;

//我的消息实体
public class JsonBridgeCommentMessage {
	
	//我的主体
	private int messageid;
	private String message;
	//评论人的具体内容
	private int userid;
	private String username;
	private String gender;
	private String small_avatar;
	private String commentcontent;
	private Date commenttime;
	public int getMessageid() {
		return messageid;
	}
	public void setMessageid(int messageid) {
		this.messageid = messageid;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getSmall_avatar() {
		return small_avatar;
	}
	public void setSmall_avatar(String small_avatar) {
		this.small_avatar = small_avatar;
	}
	public String getCommentcontent() {
		return commentcontent;
	}
	public void setCommentcontent(String commentcontent) {
		this.commentcontent = commentcontent;
	}
	public Date getCommenttime() {
		return commenttime;
	}
	public void setCommenttime(Date commenttime) {
		this.commenttime = commenttime;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	
	
}
