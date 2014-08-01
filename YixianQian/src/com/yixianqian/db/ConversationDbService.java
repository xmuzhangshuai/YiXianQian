package com.yixianqian.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;

import com.yixianqian.base.BaseApplication;
import com.yixianqian.config.DefaultSetting;
import com.yixianqian.dao.ConversationDao;
import com.yixianqian.dao.ConversationDao.Properties;
import com.yixianqian.dao.DaoSession;
import com.yixianqian.dao.MessageItemDao;
import com.yixianqian.entities.Conversation;
import com.yixianqian.entities.MessageItem;

public class ConversationDbService {
	private static final String TAG = ConversationDbService.class.getSimpleName();
	private static ConversationDbService instance;
	private static Context appContext;
	private DaoSession mDaoSession;
	public ConversationDao conversationDao;
	public MessageItemDao messageItemDao;

	public ConversationDbService() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 得到实例
	 * @param context
	 * @return
	 */
	public static ConversationDbService getInstance(Context context) {
		if (instance == null) {
			instance = new ConversationDbService();
			if (appContext == null) {
				appContext = context.getApplicationContext();
			}
			instance.mDaoSession = BaseApplication.getDaoSession(context);
			instance.conversationDao = instance.mDaoSession.getConversationDao();
			instance.messageItemDao = instance.mDaoSession.getMessageItemDao();
		}
		return instance;
	}

	/**
	 * 根据对话ID返回消息列表
	 * @param id
	 * @return
	 */
	public List<MessageItem> getMsgItemListByConId(long id) {
		Conversation conversation = conversationDao.load(id);
		if (conversation != null) {
			return conversation.getMessageItemList();
		}
		return new ArrayList<MessageItem>();
	}

	/**
	 * 
	 * @param conversation
	 * @return
	 */
	public long getIdByConversation(Conversation conversation) {
		if (conversation.getId() != null) {
			return conversation.getId();
		} else {
			Conversation con = conversationDao.queryBuilder().where(Properties.UserID.eq(conversation.getUserID()))
					.unique();
			if (con != null) {
				return con.getId();
			} else {
				return -1;
			}
		}
	}

	/**
	 * 通过UserID返回对话
	 * @param userID
	 * @return
	 */
	public Conversation getConversationByUser(int userID) {
		if (userID > -1) {
			return conversationDao.queryBuilder().where(Properties.UserID.eq(userID)).unique();
		}
		return null;
	}

	/**
	 * 通过user获得对话
	 * @return
	 */
	public Long getConIdByUserId(int userID) {
		Conversation conversation = getConversationByUser(userID);
		if (conversation != null) {
			return conversation.getId();
		}
		return (long) -1;
	}

	/**
	 * 根据页码返回聊天记录
	 * @return
	 */
	public List<MessageItem> loadMessageByPage(long conversationID, int page) {
		Conversation conversation = conversationDao.load(conversationID);
		List<MessageItem> messageItems = new ArrayList<MessageItem>();
		if (conversation != null) {
			messageItems = messageItemDao.queryBuilder()
					.where(com.yixianqian.dao.MessageItemDao.Properties.ConversationID.eq(conversation.getId()))
					.limit(DefaultSetting.LOAD_MESSAGE_COUNT * (page + 1))
					.orderDesc(com.yixianqian.dao.MessageItemDao.Properties.Time).list();
			Collections.reverse(messageItems);// 前后反转一下消息记录
			return messageItems;
		} else {
			return messageItems;
		}
	}
}
