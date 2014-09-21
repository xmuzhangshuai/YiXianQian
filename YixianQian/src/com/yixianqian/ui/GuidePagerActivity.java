package com.yixianqian.ui;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.yixianqian.R;
import com.yixianqian.base.BaseFragmentActivity;

/**
 * ������: GuidePagerActivity 
 * ����: TODO(������һ�仰��������������) 
 * ���ߣ���˧ 
 * ʱ��: 2014��9��21��
 * ����10:21:07
 * 
 */
public class GuidePagerActivity extends BaseFragmentActivity {
	// ��ҳ�ؼ�
	private ViewPager mViewPager;
	private Button loginButton;
	private Button registerButton;

	// ��5���ǵײ���ʾ��ǰ״̬��imageView
	private ImageView mPage0;
	private ImageView mPage1;
	private ImageView mPage2;
	private ImageView mPage3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide_pager);
		findViewById();
		initView();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		mViewPager = (ViewPager) findViewById(R.id.whatsnew_viewpager);

		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		mPage0 = (ImageView) findViewById(R.id.page0);
		mPage1 = (ImageView) findViewById(R.id.page1);
		mPage2 = (ImageView) findViewById(R.id.page2);
		mPage3 = (ImageView) findViewById(R.id.page3);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub

		/*
		 * ������ÿһҳҪ��ʾ�Ĳ��֣�����Ӧ����Ҫ���ص����������ʾ������ �Լ���Ҫ��ʾ����ҳ��
		 */
		LayoutInflater mLi = LayoutInflater.from(this);
		View view1 = mLi.inflate(R.layout.guide_paper_one, null);
		View view2 = mLi.inflate(R.layout.guide_paper_two, null);
		View view3 = mLi.inflate(R.layout.guide_paper_three, null);
		View view4 = mLi.inflate(R.layout.guide_paper_four, null);
		/*
		 * ���ｫÿһҳ��ʾ��view��ŵ�ArrayList������ ������ViewPager��������˳�����չʾ
		 */
		final ArrayList<View> views = new ArrayList<View>();
		views.add(view1);
		views.add(view2);
		views.add(view3);
		views.add(view4);
		/*
		 * ÿ��ҳ���Title���ݴ�ŵ�ArrayList������ ������ViewPager�������е���չʾ
		 */
		final ArrayList<String> titles = new ArrayList<String>();
		titles.add("tab1");
		titles.add("tab2");
		titles.add("tab3");
		titles.add("tab4");

		// ���ViewPager������������
		GuidePagerAdapter mPagerAdapter = new GuidePagerAdapter(views, titles);
		mViewPager.setAdapter(mPagerAdapter);
		loginButton = (Button) view4.findViewById(R.id.login_btn);
		registerButton = (Button) view4.findViewById(R.id.register_btn);
		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(GuidePagerActivity.this, LoginActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});

		registerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(GuidePagerActivity.this, RegisterActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		public void onPageSelected(int page) {

			// ��ҳʱ��ǰpage,�ı䵱ǰ״̬԰��ͼƬ
			switch (page) {
			case 0:
				mPage0.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
				mPage1.setImageDrawable(getResources().getDrawable(R.drawable.page));
				break;
			case 1:
				mPage1.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
				mPage0.setImageDrawable(getResources().getDrawable(R.drawable.page));
				mPage2.setImageDrawable(getResources().getDrawable(R.drawable.page));
				break;
			case 2:
				mPage2.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
				mPage1.setImageDrawable(getResources().getDrawable(R.drawable.page));
				mPage3.setImageDrawable(getResources().getDrawable(R.drawable.page));
				break;
			case 3:
				mPage3.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
				mPage2.setImageDrawable(getResources().getDrawable(R.drawable.page));
				break;
			}
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		public void onPageScrollStateChanged(int arg0) {
		}
	}

	public class GuidePagerAdapter extends PagerAdapter {
		private ArrayList<View> views;
		private ArrayList<String> titles;

		public GuidePagerAdapter(ArrayList<View> views, ArrayList<String> titles) {

			this.views = views;
			this.titles = titles;
		}

		@Override
		public int getCount() {
			return this.views.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(views.get(position));
		}

		// ҳ��view
		public Object instantiateItem(View container, int position) {
			((ViewPager) container).addView(views.get(position));
			return views.get(position);
		}
	}
}
