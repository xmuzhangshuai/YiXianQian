package com.yixianqian.ui;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
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
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.yixianqian.R;
import com.yixianqian.base.AbsListViewBaseActivity;
import com.yixianqian.utils.ImageLoaderTool;
import com.yixianqian.utils.NetworkUtils;

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
	private ImageView topNavLeftBtn;//导航条左边按钮
	private ImageView topNavRightBtn;//导航条右边按钮
	private TextView topNavText;//导航条文字

	// 提醒用户网络状况有异常
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (!NetworkUtils.isNetworkAvailable(TimeCapsuleActivity.this)) {
				NetworkUtils.networkStateTips(TimeCapsuleActivity.this);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_time_capsule);

		findViewById();
		initView();

		timeCapsuleListview.setMode(Mode.BOTH);
		ListView mListView = timeCapsuleListview.getRefreshableView();
		mListView.addHeaderView(headView);
		TimeCapsuleAdapter timeCapsuleAdapter = new TimeCapsuleAdapter();
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
		topNavLeftBtn = (ImageView) findViewById(R.id.nav_left_btn);
		topNavRightBtn = (ImageView) findViewById(R.id.nav_right_btn);
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
		imageLoader.displayImage("http://99touxiang.com/public/upload/nvsheng/18/04-072110_356.jpg", headImage,
				ImageLoaderTool.getHeadImageOptions(10));
		imageLoader.displayImage("http://pic.qqtn.com/file/2013/2013-5/2013051515113135806.png", headImage2,
				ImageLoaderTool.getHeadImageOptions(10));

		//设置上拉下拉刷新事件
		timeCapsuleListview.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				//				pageNow = 0;
				//				new GetDataTask().execute(pageNow);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				//				if (pageNow >= 0)
				//					++pageNow;
				//				new GetDataTask().execute(pageNow);
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// 注册广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		TimeCapsuleActivity.this.registerReceiver(broadcastReceiver, intentFilter);
		timeCapsuleListview.setOnScrollListener(new PauseOnScrollListener(imageLoader, pauseOnScroll, pauseOnFling));
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// 卸载广播
		if (broadcastReceiver != null) {
			TimeCapsuleActivity.this.unregisterReceiver(broadcastReceiver);
		}
	}

	@Override
	public void onBackPressed() {
		AnimateFirstDisplayListener.displayedImages.clear();
		imageLoader.clearMemoryCache();
		super.onBackPressed();
	}

	/** 
	 * 图片加载第一次显示监听器 
	 * @author Administrator 
	 * 
	 */
	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				// 是否第一次显示  
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					// 图片淡入效果  
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
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
		String[] url = new String[] {
				"http://h.hiphotos.baidu.com/image/pic/item/0eb30f2442a7d93327e72977af4bd11373f00163.jpg",
				"http://h.hiphotos.baidu.com/image/pic/item/a8ec8a13632762d0700432bba2ec08fa513dc6f8.jpg",
				"http://a.hiphotos.baidu.com/image/pic/item/d1160924ab18972b642a1bc0e4cd7b899e510aa3.jpg",
				"http://d.hiphotos.baidu.com/image/pic/item/9f510fb30f2442a70ad4db78d343ad4bd0130210.jpg",
				"http://a.hiphotos.baidu.com/image/pic/item/2cf5e0fe9925bc312c821dae5fdf8db1cb13702c.jpg",
				"http://f.hiphotos.baidu.com/image/pic/item/35a85edf8db1cb13a4deb994df54564e93584b86.jpg" };
		private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

		private class ViewHolder {
			public TextView timespan;
			public ImageView capsuleImage;
			public TextView time;
			public ImageView paly;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 6;
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
				view.setTag(holder); // 给View添加一个格外的数据 
			} else {
				holder = (ViewHolder) view.getTag(); // 把数据取出来  
			}

			//设置图片
			imageLoader.displayImage(url[position], holder.capsuleImage, ImageLoaderTool.getImageOptions(),
					animateFirstListener);

			//点击播放键
			//holder.paly.setImageResource(R.drawable.play);
			holder.paly.setOnClickListener(new OnClickListener() {
				int count = 0;

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (count % 2 == 0) {
						holder.paly.setImageResource(R.drawable.play);
					} else {
						holder.paly.setImageResource(R.drawable.suspend);
					}
				}
			});

			return view;
		}

	}

}
