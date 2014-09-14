package com.yixianqian.ui;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.yixianqian.R;
import com.yixianqian.base.BaseV4Fragment;

/**
 * 类名称：LoveBridgeMsgFragment
 * 类描述：鹊桥，消息
 * 创建人： 张帅
 * 创建时间：2014年9月11日 下午4:55:42
 *
 */
public class LoveBridgeMsgFragment extends BaseV4Fragment {
	private View rootView;// 根View
	private PullToRefreshListView messageListView;

	protected boolean pauseOnScroll = false;
	protected boolean pauseOnFling = true;
	private int pageNow = 0;//控制页数
	private MessageAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_love_bridge_msg, container, false);

		findViewById();// 初始化views
		initView();
		return rootView;
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		messageListView = (PullToRefreshListView) rootView.findViewById(R.id.lovebridge_list);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		//设置上拉下拉刷新事件
		messageListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				String label = DateUtils.formatDateTime(getActivity().getApplicationContext(),
						System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				pageNow = 0;
				getDataTask(pageNow);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				String label = DateUtils.formatDateTime(getActivity().getApplicationContext(),
						System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				if (pageNow >= 0)
					++pageNow;
				getDataTask(pageNow);
			}
		});
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		messageListView.setOnScrollListener(new PauseOnScrollListener(imageLoader, pauseOnScroll, pauseOnFling));
	}

	/**
	 * 网络获取数据
	 */
	private void getDataTask(int p) {
		//		final int page = p;
		//		RequestParams params = new RequestParams();
		//		params.put("page", pageNow);
		//
		//		params.put(UserTable.U_SCHOOLID, userPreference.getU_schoolid());
		//		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {
		//
		//			@Override
		//			public void onSuccess(int statusCode, Header[] headers, String response) {
		//				// TODO Auto-generated method stub
		//				if (statusCode == 200) {
		//					LogTool.i("LoveBridgeSchoolFragment", "长度" + loveBridgeItemList.size());
		//					List<JsonLoveBridgeItem> temp = FastJsonTool.getObjectList(response, JsonLoveBridgeItem.class);
		//					if (temp != null) {
		//						//如果是首次获取数据
		//						if (page == 0) {
		//							if (temp.size() < Config.PAGE_NUM) {
		//								pageNow = -1;
		//							}
		//							loveBridgeItemList = new LinkedList<JsonLoveBridgeItem>();
		//							loveBridgeItemList.addAll(temp);
		//						}
		//						//如果是获取更多
		//						else if (page > 0) {
		//							if (temp.size() < Config.PAGE_NUM) {
		//								pageNow = -1;
		//								ToastTool.showShort(getActivity(), "没有更多了！");
		//							}
		//							loveBridgeItemList.addAll(temp);
		//						}
		//						mAdapter.notifyDataSetChanged();
		//					}
		//				}
		//				loveBridgeListView.onRefreshComplete();
		//			}
		//
		//			@Override
		//			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
		//				// TODO Auto-generated method stub
		//				LogTool.e("LoveBridgeSchoolFragment", "获取列表失败");
		//				loveBridgeListView.onRefreshComplete();
		//			}
		//		};
		//		AsyncHttpClientTool.post(getActivity(), "getlovebridgelist", params, responseHandler);
	}

	/**
	 * 
	 * 类名称：MessageAdapter
	 * 类描述：适配器
	 * 创建人： 张帅
	 * 创建时间：2014年9月14日 下午3:41:15
	 *
	 */
	class MessageAdapter extends BaseAdapter {
		private class ViewHolder {
			public ImageView headImageView;
			public TextView nameTextView;
			public ImageView genderImageView;
			public TextView timeTextView;
			public TextView contentTextView;
			public TextView loveItemTextView;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
//			View view = convertView;
//			final JsonLoveBridgeItem loveBridgeItem = loveBridgeItemList.get(position);
//			if (loveBridgeItem == null) {
//				return null;
//			}
//
//			final ViewHolder holder;
//			if (convertView == null) {
//				view = LayoutInflater.from(getActivity()).inflate(R.layout.love_bridge_list_item, null);
//				holder = new ViewHolder();
//				holder.headImageView = (ImageView) view.findViewById(R.id.head_image);
//				holder.nameTextView = (TextView) view.findViewById(R.id.name);
//				holder.genderImageView = (ImageView) view.findViewById(R.id.gender);
//				holder.timeTextView = (TextView) view.findViewById(R.id.time);
//				holder.contentTextView = (TextView) view.findViewById(R.id.content);
//				holder.loveItemTextView = (TextView) view.findViewById(R.id.love_bridge_item);
//				view.setTag(holder); // 给View添加一个格外的数据 
//			} else {
//				holder = (ViewHolder) view.getTag(); // 把数据取出来  
//			}
//
//			return view;
			return null;
		}
	}

}
