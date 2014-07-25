package com.yixianqian.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.yixianqian.base.BaseApplication;
import com.yixianqian.dao.ConversationDao;
import com.yixianqian.dao.ConversationDao.Properties;
import com.yixianqian.dao.DaoSession;
import com.yixianqian.entities.Conversation;
import com.yixianqian.entities.MessageItem;

public class ConversationDbService {
	private static final String TAG = ConversationDbService.class.getSimpleName();
	private static ConversationDbService instance;
	private static Context appContext;
	private DaoSession mDaoSession;
	public ConversationDao conversationDao;

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
}
