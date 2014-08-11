package com.yixianqian.adapter;

import java.util.LinkedList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yixianqian.R;
import com.yixianqian.entities.Conversation;
import com.yixianqian.utils.AsyncHttpClientImageSound;
import com.yixianqian.utils.DateTimeTools;
import com.yixianqian.utils.ImageLoaderTool;

public class HomeListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private LinkedList<Conversation> conversationList;

	//	private ListView mListView;
	//	private Context mContext;
	//	private ConversationDbService conversationDbService;

	public HomeListAdapter(Context context, ListView listview, LinkedList<Conversation> cList) {
		//		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		//		this.mListView = listview;
		this.conversationList = cList;
		//		this.conversationDbService = ConversationDbService.getInstance(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return conversationList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return conversationList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	private static class ViewHolder {
		/** ��˭�������¼ */
		TextView name;
		/** ��Ϣδ���� */
		TextView unreadLabel;
		/** ���һ����Ϣ������ */
		TextView message;
		/** ���һ����Ϣ��ʱ�� */
		TextView time;
		/** �û�ͷ�� */
		ImageView avatar;
		/** ���һ����Ϣ�ķ���״̬ */
		View msgState;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.home_list_item, parent, false);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();

		if (holder != null) {
			holder.name = (TextView) convertView.findViewById(R.id.recent_list_item_name);
			holder.message = (TextView) convertView.findViewById(R.id.recent_list_item_msg);
			holder.unreadLabel = (TextView) convertView.findViewById(R.id.unreadmsg);
			holder.time = (TextView) convertView.findViewById(R.id.recent_list_item_time);
			holder.avatar = (ImageView) convertView.findViewById(R.id.icon);
			holder.msgState = convertView.findViewById(R.id.msg_state);
		}

		// ��ȡ����û�/Ⱥ��ĻỰ
		//		EMConversation conversation = EMChatManager.getInstance().getConversation(conversationList.get(position).getUserID());

		holder.name.setText(conversationList.get(position).getName());
		holder.message.setText(conversationList.get(position).getLastMessage());
		if (conversationList.get(position).getNewNum() > 0) {
			holder.unreadLabel.setText("" + conversationList.get(position).getNewNum());
			holder.unreadLabel.setVisibility(View.VISIBLE);
		}

		holder.time.setText(DateTimeTools.getChatTime(conversationList.get(position).getTime()));

		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(
				AsyncHttpClientImageSound.getAbsoluteUrl(conversationList.get(position).getSmallAvatar()),
				holder.avatar, ImageLoaderTool.getHeadImageOptions(10));

		return convertView;
	}

	public void remove(int position) {
		if (position < conversationList.size()) {
			conversationList.remove(position);
			notifyDataSetChanged();
		}
	}

	public void remove(Conversation item) {
		if (conversationList.contains(item)) {
			conversationList.remove(item);
			notifyDataSetChanged();
		}
	}

	public void addFirst(Conversation item) {
		if (conversationList.contains(item)) {
			conversationList.remove(item);
		}
		conversationList.addFirst(item);
		notifyDataSetChanged();
	}

	/**
	 * ������Ϣ���ݺ���Ϣ���ͻ�ȡ��Ϣ������ʾ
	 * 
	 * @param message
	 * @param context
	 * @return
	 */
	private String getMessageDigest(EMMessage message, Context context) {
		String digest = "";
		switch (message.getType()) {
		case LOCATION: // λ����Ϣ
			if (message.direct == EMMessage.Direct.RECEIVE) {
				//��sdk���ᵽ��ui�У�ʹ�ø��򵥲�����Ļ�ȡstring����
				//				digest = EasyUtils.getAppResourceString(context, "location_recv");
				digest = getString(context, R.string.location_recv);
				digest = String.format(digest, message.getFrom());
				return digest;
			} else {
				//				digest = EasyUtils.getAppResourceString(context, "location_prefix");
				digest = getString(context, R.string.location_prefix);
			}
			break;
		case IMAGE: // ͼƬ��Ϣ
			ImageMessageBody imageBody = (ImageMessageBody) message.getBody();
			digest = getString(context, R.string.picture) + imageBody.getFileName();
			break;
		case VOICE:// ������Ϣ
			digest = getString(context, R.string.voice);
			break;
		case TXT: // �ı���Ϣ
			TextMessageBody txtBody = (TextMessageBody) message.getBody();
			digest = txtBody.getMessage();
			break;
		default:
			System.err.println("error, unknow type");
			return "";
		}

		return digest;
	}

	String getString(Context context, int resId) {
		return context.getResources().getString(resId);
	}
}
