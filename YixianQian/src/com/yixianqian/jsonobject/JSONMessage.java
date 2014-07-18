package com.yixianqian.jsonobject;

import java.io.Serializable;

import com.yixianqian.base.BaseApplication;
import com.yixianqian.utils.SharePreferenceUtil;

/**
 * 类名称：JSONMessage
 * 类描述：推送的一条消息
 * 创建人： 张帅
 * 创建时间：2014年7月17日 下午4:01:14
 *
 */
public class JSONMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7802818605354691580L;
	private int userID;
	private String bpushUserID;
	private String bphshChannelID;
	private long timeSamp;
	private String messageContent;
	private String tag;

	public JSONMessage(long time_samp, String message, String tag) {
		super();
		SharePreferenceUtil util = BaseApplication.getInstance().getSpUtil();
		this.userID = util.getU_id();
		this.bpushUserID = util.getBpush_UserID();
		this.bphshChannelID = util.getBpush_ChannelID();
		this.timeSamp = time_samp;
		this.messageContent = message;
		this.tag = tag;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public String getBpushUserID() {
		return bpushUserID;
	}

	public void setBpushUserID(String bpushUserID) {
		this.bpushUserID = bpushUserID;
	}

	public String getBphshChannelID() {
		return bphshChannelID;
	}

	public void setBphshChannelID(String bphshChannelID) {
		this.bphshChannelID = bphshChannelID;
	}

	public long getTimeSamp() {
		return timeSamp;
	}

	public void setTimeSamp(long timeSamp) {
		this.timeSamp = timeSamp;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "JSONMessage [userID=" + userID + ", bpushUserID=" + bpushUserID + ", bphshChannelID=" + bphshChannelID
				+ ", timeSamp=" + timeSamp + ", messageContent=" + messageContent + ", tag=" + tag + "]";
	}

}
