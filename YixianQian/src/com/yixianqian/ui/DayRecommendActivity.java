package com.yixianqian.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yixianqian.R;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.base.BaseFragmentActivity;
import com.yixianqian.config.Constants;
import com.yixianqian.db.TodayRecommendDbService;
import com.yixianqian.entities.TodayRecommend;
import com.yixianqian.utils.AsyncHttpClientImageSound;
import com.yixianqian.utils.DensityUtil;
import com.yixianqian.utils.ImageLoaderTool;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

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
	private List<TodayRecommend> todayRecommendList;
	private UserPreference userPreference;

	// 包含圆点图片的View
	private ViewGroup imageCircleView = null;
	private ImageView[] imageCircleViews = null;

	//喜欢
	private CheckBox like;
	private int currentLike;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_day_recommend);
		todayRecommendList = new ArrayList<TodayRecommend>();
		todayRecommendList = TodayRecommendDbService.getInstance(this).getTodayRecommendList();
		userPreference = BaseApplication.getInstance().getUserPreference();

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
		like = (CheckBox) findViewById(R.id.choose_love);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		imageCircleViews = new ImageView[todayRecommendList.size()];

		//设置圆点
		for (int i = 0; i < todayRecommendList.size(); i++) {
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

		like.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					currentLike = pageIndex;
					Intent intent = new Intent(DayRecommendActivity.this,MainActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
					ToastTool.showLong(DayRecommendActivity.this, "爱情验证已发送！");
				}
			}
		});
	}

	/**
	 * 
	 * 类名称：SlideAdapter
	 * 类描述：滑动推荐用户数据适配器
	 * 创建人： 张帅
	 * 创建时间：2014年7月14日 下午2:41:29
	 *
	 */
	private class SlideAdapter extends PagerAdapter {
		private class ViewHolder {
			public TextView name;
			public TextView gender;
			public ImageView headimage;
			public TextView age;
			public TextView school;
		}

		@Override
		public int getCount() {
			return todayRecommendList.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub  
			return super.getItemPosition(object);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub  
			container.removeView((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub  
			final ViewHolder holder;

			View view = getLayoutInflater().inflate(R.layout.recommend_user_pager, null);
			holder = new ViewHolder();
			holder.name = (TextView) view.findViewById(R.id.name);
			holder.age = (TextView) view.findViewById(R.id.age);
			holder.school = (TextView) view.findViewById(R.id.school);
			holder.headimage = (ImageView) view.findViewById(R.id.headimage);
			holder.gender = (TextView) view.findViewById(R.id.gender);

			holder.name.setText(todayRecommendList.get(position).getUserName());
			holder.age.setText("" + todayRecommendList.get(position).getUserAge());
			holder.school.setText(todayRecommendList.get(position).getSchool().getSchoolName());
			if (userPreference.getU_gender().equals(Constants.Gender.MALE)) {
				holder.gender.setText("女");
			} else {
				holder.gender.setText("男");
			}

			if (todayRecommendList.get(position).getUserAvatar() != null) {
				if (todayRecommendList.get(position).getUserAvatar().length() > 0) {
					imageLoader.displayImage(
							AsyncHttpClientImageSound.getAbsoluteUrl(todayRecommendList.get(position).getUserAvatar()),
							holder.headimage, ImageLoaderTool.getHeadImageOptions());
				}
			}

			container.addView(view);
			return view;
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
		public void startUpdate(ViewGroup container) {
			// TODO Auto-generated method stub  

		}

		@Override
		public void finishUpdate(ViewGroup container) {
			// TODO Auto-generated method stub  

		}
	}

	/**
	 * 
	 * 类名称：PageChangeListener
	 * 类描述：滑动页面更改事件监听器
	 * 创建人： 张帅
	 * 创建时间：2014年7月14日 下午2:41:18
	 *
	 */
	private class PageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int state) {
			// TODO Auto-generated method stub  

		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			// TODO Auto-generated method stub  

		}

		@Override
		public void onPageSelected(int position) {
			pageIndex = position;
			for (int i = 0; i < imageCircleViews.length; i++) {
				imageCircleViews[position].setBackgroundResource(R.drawable.dot_selected);
				if (position != i) {
					imageCircleViews[i].setBackgroundResource(R.drawable.dot);
				}
			}
			like.setChecked((currentLike == pageIndex) ? true : false);
		}
	}
}
