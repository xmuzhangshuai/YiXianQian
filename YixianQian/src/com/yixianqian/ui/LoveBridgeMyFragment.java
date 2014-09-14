package com.yixianqian.ui;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.Header;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.yixianqian.R;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.base.BaseV4Fragment;
import com.yixianqian.config.Constants;
import com.yixianqian.config.Constants.Config;
import com.yixianqian.jsonobject.JsonLoveBridgeItem;
import com.yixianqian.table.LoveBridgeItemTable;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.AsyncHttpClientImageSound;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.DateTimeTools;
import com.yixianqian.utils.FastJsonTool;
import com.yixianqian.utils.ImageLoaderTool;
import com.yixianqian.utils.LogTool;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

/**
 * 类名称：LoveBridgeMyFragment
 * 类描述：鹊桥，我的发布
 * 创建人： 张帅
 * 创建时间：2014年9月11日 下午4:55:29
 *
 */
public class LoveBridgeMyFragment extends BaseV4Fragment {
	private View rootView;// 根View
	private PullToRefreshListView loveBridgeListView;

	protected boolean pauseOnScroll = false;
	protected boolean pauseOnFling = true;
	private UserPreference userPreference;
	private LinkedList<JsonLoveBridgeItem> loveBridgeItemList;
	private int pageNow = 0;//控制页数
	private LoveBridgeAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_love_bridge_my, container, false);

		userPreference = BaseApplication.getInstance().getUserPreference();
		loveBridgeItemList = new LinkedList<JsonLoveBridgeItem>();

		findViewById();// 初始化views
		initView();
		//获取数据
		getDataTask(pageNow);
		loveBridgeListView.setMode(Mode.BOTH);
		mAdapter = new LoveBridgeAdapter();
		loveBridgeListView.setAdapter(mAdapter);
		return rootView;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		loveBridgeListView.setOnScrollListener(new PauseOnScrollListener(imageLoader, pauseOnScroll, pauseOnFling));
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		loveBridgeListView = (PullToRefreshListView) rootView.findViewById(R.id.lovebridge_list);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		//设置上拉下拉刷新事件
		loveBridgeListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

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

	/**
	 * 网络获取数据
	 */
	private void getDataTask(int p) {
		final int page = p;
		RequestParams params = new RequestParams();
		params.put("page", pageNow);

		//如果是单身
		params.put(UserTable.U_SCHOOLID, userPreference.getU_schoolid());
		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					LogTool.i("LoveBridgeSchoolFragment", "长度" + loveBridgeItemList.size());
					LogTool.d("内容" + response);
					List<JsonLoveBridgeItem> temp = FastJsonTool.getObjectList(response, JsonLoveBridgeItem.class);
					if (temp != null) {
						//如果是首次获取数据
						if (page == 0) {
							if (temp.size() < Config.PAGE_NUM) {
								pageNow = -1;
							}
							loveBridgeItemList = new LinkedList<JsonLoveBridgeItem>();
							loveBridgeItemList.addAll(temp);
						}
						//如果是获取更多
						else if (page > 0) {
							if (temp.size() < Config.PAGE_NUM) {
								pageNow = -1;
								ToastTool.showShort(getActivity(), "没有更多了！");
							}
							loveBridgeItemList.addAll(temp);
						}
						mAdapter.notifyDataSetChanged();
					}
				}
				loveBridgeListView.onRefreshComplete();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				LogTool.e("LoveBridgeSchoolFragment", "获取列表失败");
				loveBridgeListView.onRefreshComplete();
			}
		};
		AsyncHttpClientTool.post(getActivity(), "getlovebridgelist", params, responseHandler);
	}

	/**
	 * 
	 * 类名称：LoveBridgeAdapter
	 * 类描述：鹊桥广场适配器
	 * 创建人： 张帅
	 * 创建时间：2014年9月12日 下午3:25:07
	 *
	 */
	class LoveBridgeAdapter extends BaseAdapter {
		private class ViewHolder {
			public ImageView headImageView;
			public TextView nameTextView;
			public ImageView genderImageView;
			public TextView timeTextView;
			public TextView contentTextView;
			public ImageView contentImageView;
			public CheckBox flipperBtn;
			public TextView flipperCountTextView;
			public ImageView commentBtn;
			public TextView commentCountTextView;
			public ImageView moreBtn;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return loveBridgeItemList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return loveBridgeItemList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = convertView;
			final JsonLoveBridgeItem loveBridgeItem = loveBridgeItemList.get(position);
			if (loveBridgeItem == null) {
				return null;
			}

			final ViewHolder holder;
			if (convertView == null) {
				view = LayoutInflater.from(getActivity()).inflate(R.layout.love_bridge_list_item, null);
				holder = new ViewHolder();
				holder.headImageView = (ImageView) view.findViewById(R.id.head_image);
				holder.nameTextView = (TextView) view.findViewById(R.id.name);
				holder.genderImageView = (ImageView) view.findViewById(R.id.gender);
				holder.timeTextView = (TextView) view.findViewById(R.id.time);
				holder.contentTextView = (TextView) view.findViewById(R.id.content);
				holder.contentImageView = (ImageView) view.findViewById(R.id.item_image);
				holder.flipperBtn = (CheckBox) view.findViewById(R.id.flipper);
				holder.flipperCountTextView = (TextView) view.findViewById(R.id.flipper_count);
				holder.commentBtn = (ImageView) view.findViewById(R.id.comment_btn);
				holder.commentCountTextView = (TextView) view.findViewById(R.id.comment_count);
				holder.moreBtn = (ImageView) view.findViewById(R.id.more);
				view.setTag(holder); // 给View添加一个格外的数据 
			} else {
				holder = (ViewHolder) view.getTag(); // 把数据取出来  
			}

			//			if (!TextUtils.isEmpty(loveBridgeItem.)) {
			//				
			//			}

			//设置照片
			if (!TextUtils.isEmpty(loveBridgeItem.getN_image())) {
				imageLoader.displayImage(AsyncHttpClientImageSound.getAbsoluteUrl(loveBridgeItem.getN_image()),
						holder.contentImageView, ImageLoaderTool.getImageOptions());
				holder.contentImageView.setVisibility(View.VISIBLE);
				holder.contentImageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(getActivity(), ImageShowerActivity.class);
						intent.putExtra(ImageShowerActivity.SHOW_BIG_IMAGE,
								AsyncHttpClientImageSound.getAbsoluteUrl(loveBridgeItem.getN_image()));
						startActivity(intent);
						getActivity().overridePendingTransition(R.anim.zoomin2, R.anim.zoomout);
					}
				});
			} else {
				holder.contentImageView.setVisibility(View.GONE);
			}

			//设置内容
			holder.contentTextView.setText(loveBridgeItem.getN_content());

			//设置姓名
			holder.nameTextView.setText(loveBridgeItem.getN_content());

			//设置性别
			if (loveBridgeItem.getN_gender().equals(Constants.Gender.MALE)) {
				holder.genderImageView.setImageResource(R.drawable.male);
			} else {
				holder.genderImageView.setImageResource(R.drawable.female);
			}

			//设置日期
			holder.timeTextView.setText(DateTimeTools.DateToString(loveBridgeItem.getN_time()));

			//设置心动次数
			holder.flipperCountTextView.setText("" + loveBridgeItem.getN_flipcount());

			//设置评论次数
			holder.commentCountTextView.setText("" + loveBridgeItem.getN_commentcount());

			//评论
			holder.commentBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

				}
			});

			//心动
			if (!holder.flipperBtn.isChecked()) {
				holder.flipperBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						// TODO Auto-generated method stub

					}
				});
			}
			return view;
		}
	}
}
