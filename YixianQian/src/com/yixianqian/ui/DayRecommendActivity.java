package com.yixianqian.ui;

import java.util.ArrayList;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yixianqian.R;
import com.yixianqian.base.BaseFragmentActivity;
import com.yixianqian.config.DefaultSetting;
import com.yixianqian.utils.DensityUtil;

/**
 * 类名称：DayRecommendActivity
 * 类描述：
 * 创建人： 张帅
 * 创建时间：2014年7月4日 下午10:19:44
 *
 */
public class DayRecommendActivity extends BaseFragmentActivity {
	private ViewPager viewPager = null;
	// 当前ViewPager索引
	private int pageIndex = 0;
	private ImageView recommendHelp;
	private ImageView next_btn;

	//滑动推荐用户集合
	private ArrayList<View> recommendPageViews = null;

	// 包含圆点图片的View
	private ViewGroup imageCircleView = null;
	private ImageView[] imageCircleViews = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_day_recommend);

		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		viewPager = (ViewPager) findViewById(R.id.user_slide_page);
		recommendHelp = (ImageView) findViewById(R.id.recommend_help);
		imageCircleView = (ViewGroup) findViewById(R.id.dot);
		next_btn = (ImageView) findViewById(R.id.next_btn);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		recommendPageViews = new ArrayList<View>();
		imageCircleViews = new ImageView[DefaultSetting.RECOMMEND_COUNT];

		for (int i = 0; i < DefaultSetting.RECOMMEND_COUNT; i++) {
			recommendPageViews.add(getLayoutInflater().inflate(R.layout.recommend_user_pager, null));
			ImageView dot = new ImageView(DayRecommendActivity.this);
			dot.setLayoutParams(new LayoutParams(DensityUtil.dip2px(DayRecommendActivity.this, 15), DensityUtil.dip2px(
					DayRecommendActivity.this, 15)));
			if (i == 0) {
				dot.setBackgroundResource(R.drawable.dot_selected);
			} else {
				dot.setBackgroundResource(R.drawable.dot);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(new LayoutParams(DensityUtil.dip2px(
						DayRecommendActivity.this, 15), DensityUtil.dip2px(DayRecommendActivity.this, 15)));
				lp.setMargins(20, 0, 0, 0);
				dot.setLayoutParams(lp);
			}
			imageCircleViews[i] = dot;
			imageCircleView.addView(dot);
		}

		viewPager.setAdapter(new SlideAdapter());
		viewPager.setOnPageChangeListener(new PageChangeListener());

		recommendHelp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(DayRecommendActivity.this, RecommendHelpActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.pop_in, R.anim.pop_out);
			}
		});

		next_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				viewPager.setCurrentItem(++pageIndex);
			}
		});
	}

	/**
	 * 类名称：GetRecommendUserTask
	 * 类描述：异步任务获取推荐用户数据
	 * 创建人： 张帅
	 * 创建时间：2014年7月4日 下午10:19:50
	 *
	 */
	class GetRecommendUserTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	// 滑动推荐用户数据适配器
	private class SlideAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			return recommendPageViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub  
			return super.getItemPosition(object);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			// TODO Auto-generated method stub  
			((ViewPager) arg0).removeView(recommendPageViews.get(arg1));
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			// TODO Auto-generated method stub  
			((ViewPager) arg0).addView(recommendPageViews.get(arg1));

			return recommendPageViews.get(arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			// TODO Auto-generated method stub  

		}

		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub  
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
			// TODO Auto-generated method stub  

		}

		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub  

		}
	}

	// 滑动页面更改事件监听器
	private class PageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub  

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub  

		}

		@Override
		public void onPageSelected(int index) {
			pageIndex = index;

			for (int i = 0; i < imageCircleViews.length; i++) {
				imageCircleViews[index].setBackgroundResource(R.drawable.dot_selected);

				if (index != i) {
					imageCircleViews[i].setBackgroundResource(R.drawable.dot);
				}
			}
		}
	}
}
