package com.yixianqian.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;

/**
 * 类名称：LoveVertifyActivity
 * 类描述：爱情验证页面
 * 创建人： 张帅
 * 创建时间：2014年7月14日 下午4:08:20
 *
 */
public class LoveVertifyActivity extends BaseActivity {
	/***********VIEWS************/
	private ListView loveVertifyList;
	private int vertifyCount = 10;//爱情验证数量

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_love_vertify);

		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		loveVertifyList = (ListView) findViewById(R.id.love_vertify_list);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		loveVertifyList.setAdapter(new LoveVertifyAdapter());
	}

	/**
	 * 
	 * 类名称：LoveVertifyAdapter
	 * 类描述：列表Adapter
	 * 创建人： 张帅
	 * 创建时间：2014年7月14日 下午4:10:00
	 *
	 */
	private class LoveVertifyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return vertifyCount;
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
			View view = convertView;
			if (convertView == null) {
				view = LayoutInflater.from(LoveVertifyActivity.this).inflate(R.layout.love_vertify_list_item, null);
			}
			return view;
		}
	}
}
