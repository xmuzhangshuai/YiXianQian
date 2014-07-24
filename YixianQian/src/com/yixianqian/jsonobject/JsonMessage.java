package com.yixianqian.jsonobject;

/**
 * 类名称：JSONMessage
 * 类描述：推送的一条消息
 * 创建人： 张帅
 * 创建时间：2014年7月17日 下午4:01:14
 *
 */
public class JsonMessage {

	//	private static final long serialVersionUID = -7802818605354691580L;
	//	private int userID;
	//	private String bpushUserID;
	//	private String bphshChannelID;
	private long timeSamp;
	private String messageContent;

	private int type;

	//	private String tag;

	public JsonMessage() {
	}

	public JsonMessage(String messageContent, int type) {
		super();
		this.messageContent = messageContent;
		this.type = type;
		this.timeSamp = System.currentTimeMillis();
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	//	public JsonMessage(long time_samp, String message, String tag) {
	//		super();
	//		UserPreference util = BaseApplication.getInstance().getUserPreference();
	//		this.userID = util.getU_id();
	//		this.bpushUserID = util.getBpush_UserID();
	//		this.bphshChannelID = util.getBpush_ChannelID();
	//		this.timeSamp = time_samp;
	//		this.messageContent = message;
	//		this.tag = tag;
	//	}

}
