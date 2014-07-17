package com.yixianqian.jsonobject;

/**
 * 类名称：JsonTodayRecommend
 * 类描述：今日推荐DTO
 * 创建人： 张帅
 * 创建时间：2014年7月16日 上午9:49:25
 *
 */
public class JsonTodayRecommend {
	private Integer userID;
	private String userName;
	private String userAvatar;
	private Integer userAge;
	private Integer schoolID;

	public JsonTodayRecommend(Integer userID, String userName, String userAvatar, Integer userAge, Integer schoolID) {
		super();
		this.userID = userID;
		this.userName = userName;
		this.userAvatar = userAvatar;
		this.userAge = userAge;
		this.schoolID = schoolID;
	}

	public Integer getUserID() {
		return userID;
	}

	public void setUserID(Integer userID) {
		this.userID = userID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserAvatar() {
		return userAvatar;
	}

	public void setUserAvatar(String userAvatar) {
		this.userAvatar = userAvatar;
	}

	public Integer getUserAge() {
		return userAge;
	}

	public void setUserAge(Integer userAge) {
		this.userAge = userAge;
	}

	public Integer getSchoolID() {
		return schoolID;
	}

	public void setSchoolID(Integer schoolID) {
		this.schoolID = schoolID;
	}

}
