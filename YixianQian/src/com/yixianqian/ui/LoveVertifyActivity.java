package com.yixianqian.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.db.FlipperDbService;
import com.yixianqian.entities.Flipper;
import com.yixianqian.utils.AsyncHttpClientImageSound;
import com.yixianqian.utils.DateTimeTools;
import com.yixianqian.utils.ImageLoaderTool;

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
	private FlipperDbService flipperDbService;
	private List<Flipper> flipperList;
	private LoveVertifyAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_love_vertify);

		flipperDbService = FlipperDbService.getInstance(LoveVertifyActivity.this);
		flipperList = new ArrayList<Flipper>();
		flipperList = flipperDbService.getFlipperList();

		findViewById();
		initView();
		adapter = new LoveVertifyAdapter();
		loveVertifyList.setAdapter(adapter);
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		loveVertifyList = (ListView) findViewById(R.id.love_vertify_list);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub

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
			return flipperList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return flipperList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			//��Ǳ��Ķ������Ѷ�
			Flipper flipper = flipperList.get(position);
			if (flipper != null) {
				flipper.setIsRead(true);
				flipperDbService.flipperDao.update(flipper);
			}

			View view = convertView;
			if (convertView == null) {
				view = LayoutInflater.from(LoveVertifyActivity.this).inflate(R.layout.love_vertify_list_item, null);
			}
			ImageView headImageView = (ImageView) view.findViewById(R.id.head_image);
			TextView nameTextView = (TextView) view.findViewById(R.id.name);
			TextView timeTextView = (TextView) view.findViewById(R.id.time);
			View flipperBtn = (View) view.findViewById(R.id.flipped);
			View refuseBtn = (View) view.findViewById(R.id.refuse);

			timeTextView.setText(DateTimeTools.DateToString(flipperList.get(position).getTime()));
			if (!TextUtils.isEmpty(flipperList.get(position).getSamllAvatar())) {
				imageLoader.displayImage(
						AsyncHttpClientImageSound.getAbsoluteUrl(flipperList.get(position).getSamllAvatar()),
						headImageView, ImageLoaderTool.getHeadImageOptions(10));
			}
			//������ʾ����
			String name = flipperList.get(position).getNickname();
			if (!TextUtils.isEmpty(flipperList.get(position).getRealname())) {
				name = flipperList.get(position).getRealname();
			}
			nameTextView.setText(name);

			//������ܰ�ť
			flipperBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

				}
			});

			//����ܾ���ť
			refuseBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Flipper flipper = flipperList.get(position);
					flipperDbService.flipperDao.delete(flipper);
					flipperList.remove(position);
					adapter.notifyDataSetChanged();
				}
			});
			return view;
		}
	}
}
