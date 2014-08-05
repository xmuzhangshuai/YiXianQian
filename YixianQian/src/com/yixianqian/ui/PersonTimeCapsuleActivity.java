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
import android.view.Window;
import android.widget.BaseAdapter;
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
import com.yixianqian.base.AbsListViewBaseActivity;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.config.DefaultSetting;
import com.yixianqian.jsonobject.JsonSingleTimeCapsule;
import com.yixianqian.jsonobject.JsonUser;
import com.yixianqian.table.SingleTimeCapsuleTable;
import com.yixianqian.utils.AsyncHttpClientImageSound;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.DateTimeTools;
import com.yixianqian.utils.DensityUtil;
import com.yixianqian.utils.FastJsonTool;
import com.yixianqian.utils.FriendPreference;
import com.yixianqian.utils.ImageLoaderTool;
import com.yixianqian.utils.SoundLoader;
import com.yixianqian.utils.ToastTool;

public class PersonTimeCapsuleActivity extends AbsListViewBaseActivity {
	private PullToRefreshListView timeCapsuleListview;

	private View headView;//用户头像区域
	private ImageView headImage;//我的头像
	private TextView name;//我的姓名
	private ImageView topNavLeftBtn;//导航条左边按钮
	private TextView topNavText;//导航条文字
	private FriendPreference friendPreference;
	private int pageNow = 0;//控制页数
	private LinkedList<JsonSingleTimeCapsule> singleTimeCapsuleList;
	private TimeCapsuleAdapter timeCapsuleAdapter;
	private JsonUser jsonUser;
	private int type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_time_capsule);
		friendPreference = BaseApplication.getInstance().getFriendPreference();
		singleTimeCapsuleList = new LinkedList<JsonSingleTimeCapsule>();
		type = getIntent().getIntExtra(PersonDetailActivity.PERSON_TYPE, -1);
		jsonUser = (JsonUser) getIntent().getSerializableExtra("user");

		findViewById();
		initView();

		//获取数据
		getDataTask(pageNow);
		timeCapsuleListview.setMode(Mode.BOTH);
		ListView mListView = timeCapsuleListview.getRefreshableView();
		mListView.addHeaderView(headView);
		timeCapsuleAdapter = new TimeCapsuleAdapter();
		timeCapsuleListview.setAdapter(timeCapsuleAdapter);
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		timeCapsuleListview = (PullToRefreshListView) findViewById(R.id.timecapsule_list);
		headView = getLayoutInflater().inflate(R.layout.time_capsule_topview, null);
		headImage = (ImageView) headView.findViewById(R.id.male_headiamge);
		name = (TextView) headView.findViewById(R.id.male_name);
		topNavLeftBtn = (ImageView) findViewById(R.id.nav_left_btn);
		topNavText = (TextView) findViewById(R.id.nav_text);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavText.setText("时间胶囊");
		topNavLeftBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		//设置头像
		if (type == 1) {
			imageLoader.displayImage(AsyncHttpClientImageSound.getAbsoluteUrl(jsonUser.getU_small_avatar()), headImage,
					ImageLoaderTool.getHeadImageOptions(10));
			name.setText(getUserName(jsonUser));

		} else if (type == 2) {
			imageLoader.displayImage(AsyncHttpClientImageSound.getAbsoluteUrl(friendPreference.getF_small_avatar()),
					headImage, ImageLoaderTool.getHeadImageOptions(10));
			name.setText(friendPreference.getName());
		}

		//设置上拉下拉刷新事件
		timeCapsuleListview.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				pageNow = 0;
				getDataTask(pageNow);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				if (pageNow >= 0)
					++pageNow;
				getDataTask(pageNow);
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		timeCapsuleListview.setOnScrollListener(new PauseOnScrollListener(imageLoader, pauseOnScroll, pauseOnFling));
	}

	@Override
	public void onBackPressed() {
		imageLoader.clearMemoryCache();
		super.onBackPressed();
	}

	/**
	 * 优先返回真名
	 * @return
	 */
	private String getUserName(JsonUser user) {
		if (user != null) {
			String name = user.getU_nickname();
			if (user.getU_realname() != null) {
				if (user.getU_realname().length() > 0) {
					name = user.getU_realname();
				}
			}
			return name;
		}
		return "";
	}

	/**
	 * 网络获取数据
	 */
	private void getDataTask(int p) {
		final int page = p;
		RequestParams params = new RequestParams();
		params.put("page", pageNow);
		if (type == 1) {
			params.put(SingleTimeCapsuleTable.STC_USERID, jsonUser.getU_id());
		} else if (type == 2) {
			params.put(SingleTimeCapsuleTable.STC_USERID, friendPreference.getF_id());
		}

		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					List<JsonSingleTimeCapsule> temp = FastJsonTool
							.getObjectList(response, JsonSingleTimeCapsule.class);
					if (temp != null) {
						//如果是首次获取数据
						if (page == 0) {
							if (temp.size() < DefaultSetting.PAGE_NUM) {
								pageNow = -1;
							}
							singleTimeCapsuleList = new LinkedList<JsonSingleTimeCapsule>();
							for (JsonSingleTimeCapsule jsonSingleTimeCapsule : temp) {
								System.out.println("声音" + jsonSingleTimeCapsule.getStc_voice());
							}
							singleTimeCapsuleList.addAll(temp);
						}
						//如果是获取更多
						else if (page > 0) {
							if (temp.size() < DefaultSetting.PAGE_NUM) {
								pageNow = -1;
								ToastTool.showShort(PersonTimeCapsuleActivity.this, "没有更多了！");
							}
							singleTimeCapsuleList.addAll(temp);
						}
						timeCapsuleAdapter.notifyDataSetChanged();
					}
				}
				timeCapsuleListview.onRefreshComplete();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				ToastTool.showShort(PersonTimeCapsuleActivity.this, "获取时间胶囊失败！");
				timeCapsuleListview.onRefreshComplete();
			}
		};
		if (type == 1 || type == 2) {
			AsyncHttpClientTool.post(PersonTimeCapsuleActivity.this, "getsinglecapsule", params, responseHandler);
		}
	}

	/**
	 * 类名称：TimeCapsuleAdapter
	 * 类描述：时间胶囊下拉刷新列表适配器
	 * 创建人： 张帅
	 * 创建时间：2014年7月9日 下午9:32:55
	 *
	 */
	class TimeCapsuleAdapter extends BaseAdapter {

		private class ViewHolder {
			public TextView timespan;
			public ImageView capsuleImage;
			public TextView time;
			public ImageView paly;
			public View palyView;
			public ImageView moreBtn;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return singleTimeCapsuleList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return singleTimeCapsuleList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = convertView;

			final ViewHolder holder;
			if (convertView == null) {
				view = LayoutInflater.from(PersonTimeCapsuleActivity.this).inflate(R.layout.time_capsule_list_item,
						null);
				holder = new ViewHolder();
				holder.timespan = (TextView) view.findViewById(R.id.timespan);
				holder.capsuleImage = (ImageView) view.findViewById(R.id.item_image);
				holder.time = (TextView) view.findViewById(R.id.time);
				holder.paly = (ImageView) view.findViewById(R.id.play);
				holder.palyView = view.findViewById(R.id.playview);
				holder.moreBtn = (ImageView) view.findViewById(R.id.more);
				view.setTag(holder); // 给View添加一个格外的数据 
			} else {
				holder = (ViewHolder) view.getTag(); // 把数据取出来  
			}

			holder.capsuleImage.setMaxHeight(DensityUtil.dip2px(PersonTimeCapsuleActivity.this,
					(DensityUtil.getScreenWidthforDP(PersonTimeCapsuleActivity.this) - 40)));

			//单身
			if (!TextUtils.isEmpty(singleTimeCapsuleList.get(position).getStc_photo())) {
				imageLoader.displayImage(
						AsyncHttpClientImageSound.getAbsoluteUrl(singleTimeCapsuleList.get(position).getStc_photo()),
						holder.capsuleImage, ImageLoaderTool.getImageOptions());
				holder.capsuleImage.setVisibility(View.VISIBLE);
				holder.capsuleImage.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(PersonTimeCapsuleActivity.this, ImageShowerActivity.class);
						intent.putExtra(ImageShowerActivity.SHOW_BIG_IMAGE, AsyncHttpClientImageSound
								.getAbsoluteUrl(singleTimeCapsuleList.get(position).getStc_photo()));
						startActivity(intent);
						PersonTimeCapsuleActivity.this.overridePendingTransition(R.anim.zoomin2, R.anim.zoomout);
					}
				});
			}

			if (!TextUtils.isEmpty(singleTimeCapsuleList.get(position).getStc_voice())) {
				holder.timespan.setText("" + singleTimeCapsuleList.get(position).getStc_voice_length() + "\"");
				holder.time.setText(DateTimeTools
						.DateToString(singleTimeCapsuleList.get(position).getStc_record_time()));
				holder.palyView.setVisibility(View.VISIBLE);
			}

			//点击播放键
			holder.palyView.setOnClickListener(new OnClickListener() {
				boolean isPlaying = false;

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (isPlaying) {
						holder.paly.setImageResource(R.drawable.paly_white);
						SoundLoader.getInstance(PersonTimeCapsuleActivity.this).stop();
						isPlaying = false;
					} else {
						isPlaying = true;
						SoundLoader.getInstance(PersonTimeCapsuleActivity.this).start(
								singleTimeCapsuleList.get(position).getStc_voice(), holder.paly);
					}
				}
			});
			//点击更多
			holder.moreBtn.setVisibility(View.GONE);
			return view;
		}
	}

}
