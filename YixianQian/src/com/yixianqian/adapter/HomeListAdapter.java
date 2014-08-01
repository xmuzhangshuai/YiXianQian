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

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yixianqian.R;
import com.yixianqian.db.ConversationDbService;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.home_list_item, null);
		}
		TextView nickTV = (TextView) convertView.findViewById(R.id.recent_list_item_name);
		TextView msgTV = (TextView) convertView.findViewById(R.id.recent_list_item_msg);
		TextView numTV = (TextView) convertView.findViewById(R.id.unreadmsg);
		TextView timeTV = (TextView) convertView.findViewById(R.id.recent_list_item_time);
		ImageView headIV = (ImageView) convertView.findViewById(R.id.icon);

		nickTV.setText(conversationList.get(position).getName());
		msgTV.setText(conversationList.get(position).getLastMessage());
		if (conversationList.get(position).getNewNum() > 0) {
			numTV.setText("" + conversationList.get(position).getNewNum());
			numTV.setVisibility(View.VISIBLE);
		}

		timeTV.setText(DateTimeTools.getChatTime(conversationList.get(position).getTime()));

		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(
				AsyncHttpClientImageSound.getAbsoluteUrl(conversationList.get(position).getSmallAvatar()), headIV,
				ImageLoaderTool.getHeadImageOptions(10));

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

}
