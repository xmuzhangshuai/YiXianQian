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
 * �����ƣ�LoveVertifyActivity
 * ��������������֤ҳ��
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��7��14�� ����4:08:20
 *
 */
public class LoveVertifyActivity extends BaseActivity {
	/***********VIEWS************/
	private ListView loveVertifyList;
	private int vertifyCount = 10;//������֤����

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
	 * �����ƣ�LoveVertifyAdapter
	 * ���������б�Adapter
	 * �����ˣ� ��˧
	 * ����ʱ�䣺2014��7��14�� ����4:10:00
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
