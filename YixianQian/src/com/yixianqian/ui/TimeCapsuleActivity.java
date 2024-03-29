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
import com.yixianqian.config.Constants.Config;
import com.yixianqian.jsonobject.JsonLoverTimeCapsule;
import com.yixianqian.jsonobject.JsonSingleTimeCapsule;
import com.yixianqian.table.LoverTimeCapsuleTable;
import com.yixianqian.table.SingleTimeCapsuleTable;
import com.yixianqian.utils.AsyncHttpClientImageSound;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.DateTimeTools;
import com.yixianqian.utils.DensityUtil;
import com.yixianqian.utils.FastJsonTool;
import com.yixianqian.utils.FriendPreference;
import com.yixianqian.utils.ImageLoaderTool;
import com.yixianqian.utils.LogTool;
import com.yixianqian.utils.SoundLoader;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

/**
 * 类名称：TimeCapsuleActivity
 * 类描述：时间胶囊页面
 * 创建人： 张帅
 * 创建时间：2014年7月9日 下午9:33:16
 *
 */
public class TimeCapsuleActivity extends AbsListViewBaseActivity {

	private PullToRefreshListView timeCapsuleListview;

	private View headView;//用户头像区域
	private ImageView headImage;//我的头像
	private TextView name;//我的姓名
	private ImageView headImage2;//情侣的头像
	private TextView name2;//情侣的姓名
	private View left_btn_bg;//导航条左边按钮
	private TextView topNavText;//导航条文字
	private UserPreference userPreference;
	private FriendPreference friendPreference;
	private int pageNow = 0;//控制页数
	private LinkedList<JsonSingleTimeCapsule> singleTimeCapsuleList;
	private LinkedList<JsonLoverTimeCapsule> loverTimeCapsuleList;
	private TimeCapsuleAdapter timeCapsuleAdapter;
	private int stateID = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_time_capsule);
		userPreference = BaseApplication.getInstance().getUserPreference();
		friendPreference = BaseApplication.getInstance().getFriendPreference();
		singleTimeCapsuleList = new LinkedList<JsonSingleTimeCapsule>();
		loverTimeCapsuleList = new LinkedList<JsonLoverTimeCapsule>();
		stateID = userPreference.getU_stateid();

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
		headImage2 = (ImageView) headView.findViewById(R.id.female_headimage);
		name = (TextView) headView.findViewById(R.id.male_name);
		name2 = (TextView) headView.findViewById(R.id.female_name);
		left_btn_bg = findViewById(R.id.left_btn_bg);
		topNavText = (TextView) findViewById(R.id.nav_text);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavText.setText("时间胶囊");
		left_btn_bg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			}
		});
		//设置头像
		imageLoader.displayImage(AsyncHttpClientImageSound.getAbsoluteUrl(userPreference.getU_small_avatar()),
				headImage, ImageLoaderTool.getHeadImageOptions(10));
		name.setText(userPreference.getName());

		//如果是情侣
		if (stateID == 2) {
			headImage2.setVisibility(View.VISIBLE);
			name2.setVisibility(View.VISIBLE);
			imageLoader.displayImage(AsyncHttpClientImageSound.getAbsoluteUrl(friendPreference.getF_small_avatar()),
					headImage2, ImageLoaderTool.getHeadImageOptions(10));
			name2.setText(friendPreference.getName());
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SharePanelActivity.REQUEST_CODE_SHAREPANEL) {
			switch (resultCode) {
			case SharePanelActivity.RESULT_CODE_DELETE: // 删除记录
				LogTool.d("时间胶囊", "删除时间胶囊成功");
				int position = data.getIntExtra("position", -1);
				if (stateID == 2 && position > -1) {
					loverTimeCapsuleList.remove(position);
					timeCapsuleAdapter.notifyDataSetChanged();
				} else if ((stateID == 3 || stateID == 4) && position > -1) {
					singleTimeCapsuleList.remove(position);
					timeCapsuleAdapter.notifyDataSetChanged();
				}
				break;

			default:
				break;
			}
		}
	}

	@Override
	public void onBackPressed() {
		imageLoader.clearMemoryCache();
		super.onBackPressed();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	/**
	 * 网络获取数据
	 */
	private void getDataTask(int p) {
		final int page = p;
		RequestParams params = new RequestParams();
		params.put("page", pageNow);

		//如果是单身
		if (stateID == 3 || stateID == 4) {
			params.put(SingleTimeCapsuleTable.STC_USERID, userPreference.getU_id());
			TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {

				@Override
				public void onSuccess(int statusCode, Header[] headers, String response) {
					// TODO Auto-generated method stub
					if (statusCode == 200) {
						List<JsonSingleTimeCapsule> temp = FastJsonTool.getObjectList(response,
								JsonSingleTimeCapsule.class);
						if (temp != null) {
							//如果是首次获取数据
							if (page == 0) {
								if (temp.size() < Config.PAGE_NUM) {
									pageNow = -1;
								}
								singleTimeCapsuleList = new LinkedList<JsonSingleTimeCapsule>();
								singleTimeCapsuleList.addAll(temp);
							}
							//如果是获取更多
							else if (page > 0) {
								if (temp.size() < Config.PAGE_NUM) {
									pageNow = -1;
									ToastTool.showShort(TimeCapsuleActivity.this, "没有更多了！");
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
					LogTool.e("TimeCapsuleActivity", "获取时间胶囊失败！");
					timeCapsuleListview.onRefreshComplete();
				}
			};
			AsyncHttpClientTool.post(TimeCapsuleActivity.this, "getsinglecapsule", params, responseHandler);
		}

		//如果是情侣
		else if (stateID == 2) {
			params.put(LoverTimeCapsuleTable.LTC_USERID, userPreference.getU_id());
			params.put(LoverTimeCapsuleTable.LTC_LOVERID, friendPreference.getF_id());
			TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {

				@Override
				public void onSuccess(int statusCode, Header[] headers, String response) {
					// TODO Auto-generated method stub
					if (statusCode == 200) {
						List<JsonLoverTimeCapsule> temp = FastJsonTool.getObjectList(response,
								JsonLoverTimeCapsule.class);
						if (temp != null) {
							//如果是首次获取数据
							if (page == 0) {
								if (temp.size() < Config.PAGE_NUM) {
									pageNow = -1;
								}
								loverTimeCapsuleList = new LinkedList<JsonLoverTimeCapsule>();
								loverTimeCapsuleList.addAll(temp);
							}
							//如果是获取更多
							else if (page > 0) {
								if (temp.size() < Config.PAGE_NUM) {
									pageNow = -1;
									ToastTool.showShort(TimeCapsuleActivity.this, "没有更多了！");
								}
								loverTimeCapsuleList.addAll(temp);
							}
							timeCapsuleAdapter.notifyDataSetChanged();
						}
					}
					timeCapsuleListview.onRefreshComplete();
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
					// TODO Auto-generated method stub
					ToastTool.showShort(TimeCapsuleActivity.this, "获取时间胶囊失败！");
					timeCapsuleListview.onRefreshComplete();
				}
			};
			AsyncHttpClientTool.post(TimeCapsuleActivity.this, "getlovercapsule", params, responseHandler);
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
			if (stateID == 3 || stateID == 4) {
				return singleTimeCapsuleList.size();
			} else if (stateID == 2) {
				return loverTimeCapsuleList.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			if (stateID == 3 || stateID == 4) {
				return singleTimeCapsuleList.get(position);
			} else if (stateID == 2) {
				return loverTimeCapsuleList.get(position);
			}
			return position;
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
				view = LayoutInflater.from(TimeCapsuleActivity.this).inflate(R.layout.time_capsule_list_item, null);
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

			holder.capsuleImage.setMaxHeight(DensityUtil.dip2px(TimeCapsuleActivity.this,
					(DensityUtil.getScreenWidthforDP(TimeCapsuleActivity.this) - 40)));

			//单身
			if (stateID == 3 || stateID == 4) {
				if (!TextUtils.isEmpty(singleTimeCapsuleList.get(position).getStc_photo())) {
					imageLoader.displayImage(AsyncHttpClientImageSound.getAbsoluteUrl(singleTimeCapsuleList.get(
							position).getStc_photo()), holder.capsuleImage, ImageLoaderTool.getImageOptions());
					holder.capsuleImage.setVisibility(View.VISIBLE);
					holder.capsuleImage.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(TimeCapsuleActivity.this, ImageShowerActivity.class);
							intent.putExtra(ImageShowerActivity.SHOW_BIG_IMAGE, AsyncHttpClientImageSound
									.getAbsoluteUrl(singleTimeCapsuleList.get(position).getStc_photo()));
							startActivity(intent);
							TimeCapsuleActivity.this.overridePendingTransition(R.anim.zoomin2, R.anim.zoomout);
						}
					});
				} else {
					holder.capsuleImage.setVisibility(View.GONE);
				}

				holder.timespan.setText("" + singleTimeCapsuleList.get(position).getStc_voice_length() + "\"");
				holder.time.setText(DateTimeTools
						.DateToString(singleTimeCapsuleList.get(position).getStc_record_time()));

				if (!TextUtils.isEmpty(singleTimeCapsuleList.get(position).getStc_voice())) {
					holder.palyView.setVisibility(View.VISIBLE);
				} else {
					holder.palyView.setVisibility(View.GONE);
				}
			}
			//情侣
			else if (stateID == 2) {
				if (!TextUtils.isEmpty(loverTimeCapsuleList.get(position).getLtc_photo())) {
					imageLoader
							.displayImage(AsyncHttpClientImageSound.getAbsoluteUrl(loverTimeCapsuleList.get(position)
									.getLtc_photo()), holder.capsuleImage, ImageLoaderTool.getImageOptions());
					holder.capsuleImage.setVisibility(View.VISIBLE);
					holder.capsuleImage.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(TimeCapsuleActivity.this, ImageShowerActivity.class);
							intent.putExtra(ImageShowerActivity.SHOW_BIG_IMAGE, AsyncHttpClientImageSound
									.getAbsoluteUrl(loverTimeCapsuleList.get(position).getLtc_photo()));
							startActivity(intent);
							TimeCapsuleActivity.this.overridePendingTransition(R.anim.zoomin2, R.anim.zoomout);
						}
					});

				} else {
					holder.capsuleImage.setVisibility(View.GONE);
				}

				holder.timespan.setText("" + loverTimeCapsuleList.get(position).getLtc_voice_length() + "s");
				holder.time
						.setText(DateTimeTools.DateToString(loverTimeCapsuleList.get(position).getLtc_record_time()));

				if (!TextUtils.isEmpty(loverTimeCapsuleList.get(position).getLtc_voice())) {
					holder.palyView.setVisibility(View.VISIBLE);
				} else {
					holder.palyView.setVisibility(View.GONE);
				}
			}

			//点击播放键
			holder.palyView.setOnClickListener(new OnClickListener() {
				boolean isPlaying = false;

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (isPlaying) {
						holder.paly.setImageResource(R.drawable.paly_white);
						SoundLoader.getInstance(TimeCapsuleActivity.this).stop();
						isPlaying = false;
					} else {
						isPlaying = true;
						if (stateID == 3 || stateID == 4) {
							SoundLoader.getInstance(TimeCapsuleActivity.this).start(
									singleTimeCapsuleList.get(position).getStc_voice(), holder.paly);
						} else if (stateID == 2) {
							SoundLoader.getInstance(TimeCapsuleActivity.this).start(
									loverTimeCapsuleList.get(position).getLtc_voice(), holder.paly);
						}
					}
				}
			});

			//点击更多
			holder.moreBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (stateID == 3 || stateID == 4) {
						if (!TextUtils.isEmpty(singleTimeCapsuleList.get(position).getStc_photo())) {
							showMoreDialog(position, AsyncHttpClientImageSound.getAbsoluteUrl(singleTimeCapsuleList
									.get(position).getStc_photo()), true);
						} else {
							showMoreDialog(position, AsyncHttpClientImageSound.getAbsoluteUrl(singleTimeCapsuleList
									.get(position).getStc_photo()), false);
						}

					} else if (stateID == 2) {
						if (!TextUtils.isEmpty(loverTimeCapsuleList.get(position).getLtc_photo())) {
							showMoreDialog(position, AsyncHttpClientImageSound.getAbsoluteUrl(loverTimeCapsuleList.get(
									position).getLtc_photo()), true);
						} else {
							showMoreDialog(position, AsyncHttpClientImageSound.getAbsoluteUrl(loverTimeCapsuleList.get(
									position).getLtc_photo()), false);
						}

					}

				}
			});
			return view;
		}
	}

	/**
	 * 显示更多
	 */
	private void showMoreDialog(int position, String imageUrl, boolean showShare) {

		Intent intent = new Intent(TimeCapsuleActivity.this, SharePanelActivity.class);
		intent.putExtra("position", position);
		intent.putExtra(SharePanelActivity.IMAGE_URL, imageUrl);
		intent.putExtra("showShare", showShare);
		if (stateID == 2) {
			intent.putExtra(LoverTimeCapsuleTable.LTC_USERID, loverTimeCapsuleList.get(position).getLtc_userid());
			intent.putExtra(LoverTimeCapsuleTable.LTC_LOVERID, loverTimeCapsuleList.get(position).getLtc_loverid());
			intent.putExtra(LoverTimeCapsuleTable.LTC_MSGID, loverTimeCapsuleList.get(position).getLtc_msgid());
		} else if (stateID == 3 || stateID == 4) {
			intent.putExtra(SingleTimeCapsuleTable.STC_USERID, singleTimeCapsuleList.get(position).getStc_userid());
			intent.putExtra(SingleTimeCapsuleTable.STC_MSGID, singleTimeCapsuleList.get(position).getStc_msgid());
		}
		startActivityForResult(intent, SharePanelActivity.REQUEST_CODE_SHAREPANEL);
	}

}
