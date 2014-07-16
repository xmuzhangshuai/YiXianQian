package com.yixianqian.adapter;

import java.util.LinkedList;

import com.yixianqian.R;
import com.yixianqian.swipelistview.SwipeListView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class HomeListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	//	private LinkedList<RecentItem> mData;
	private SwipeListView mListView;
	//	private MessageDB mMessageDB;
	//	private RecentDB mRecentDB;
	private Context mContext;

	public HomeListAdapter(Context context, SwipeListView listview) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		this.mListView = listview;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 8;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
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
		Button deleteBtn = (Button) convertView.findViewById(R.id.recent_del_btn);

		deleteBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				notifyDataSetChanged();
				if (mListView != null)
					mListView.closeOpenedItems();
			}
		});
		return convertView;
	}

	//	public HomeListAdapter(Context context, LinkedList<RecentItem> data, SwipeListView listview) {
	//		mContext = context;
	//		this.mInflater = LayoutInflater.from(context);
	//		mData = data;
	//		this.mListView = listview;
	//		mMessageDB = PushApplication.getInstance().getMessageDB();
	//		mRecentDB = PushApplication.getInstance().getRecentDB();
	//	}

	//	public void remove(int position) {
	//		if (position < mData.size()) {
	//			mData.remove(position);
	//			notifyDataSetChanged();
	//		}
	//	}
	//
	//	public void remove(RecentItem item) {
	//		if (mData.contains(item)) {
	//			mData.remove(item);
	//			notifyDataSetChanged();
	//		}
	//	}
	//
	//	public void addFirst(RecentItem item) {
	//		if (mData.contains(item)) {
	//			mData.remove(item);
	//		}
	//		mData.addFirst(item);
	//		L.i("addFirst: " + item);
	//		notifyDataSetChanged();
	//	}
	//
	//	@Override
	//	public int getCount() {
	//		// TODO Auto-generated method stub
	//		return mData.size();
	//	}
	//
	//	@Override
	//	public Object getItem(int position) {
	//		// TODO Auto-generated method stub
	//		return mData.get(position);
	//	}
	//
	//	@Override
	//	public long getItemId(int position) {
	//		// TODO Auto-generated method stub
	//		return position;
	//	}
	//
	//	@Override
	//	public View getView(final int position, View convertView, ViewGroup parent) {
	//		// TODO Auto-generated method stub
	//		final RecentItem item = mData.get(position);
	//		if (convertView == null) {
	//			convertView = mInflater.inflate(R.layout.recent_listview_item, null);
	//		}
	//		TextView nickTV = (TextView) convertView.findViewById(R.id.recent_list_item_name);
	//		TextView msgTV = (TextView) convertView.findViewById(R.id.recent_list_item_msg);
	//		TextView numTV = (TextView) convertView.findViewById(R.id.unreadmsg);
	//		TextView timeTV = (TextView) convertView.findViewById(R.id.recent_list_item_time);
	//		ImageView headIV = (ImageView) convertView.findViewById(R.id.icon);
	//		Button deleteBtn = (Button) convertView.findViewById(R.id.recent_del_btn);
	//		nickTV.setText(item.getName());
	//		msgTV.setText(convertNormalStringToSpannableString(item.getMessage()), BufferType.SPANNABLE);
	//		timeTV.setText(TimeUtil.getChatTime(item.getTime()));
	//		headIV.setImageResource(PushApplication.heads[item.getHeadImg()]);
	//		int num = mMessageDB.getNewCount(item.getUserId());
	//		if (num > 0) {
	//			numTV.setVisibility(View.VISIBLE);
	//			numTV.setText(num + "");
	//		} else {
	//			numTV.setVisibility(View.GONE);
	//		}
	//		deleteBtn.setOnClickListener(new OnClickListener() {
	//
	//			@Override
	//			public void onClick(View v) {
	//				// TODO Auto-generated method stub
	//				mData.remove(position);
	//				mRecentDB.delRecent(item.getUserId());
	//				notifyDataSetChanged();
	//				if (mListView != null)
	//					mListView.closeOpenedItems();
	//			}
	//		});
	//		return convertView;
	//	}

}
