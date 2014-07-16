package com.yixianqian.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yixianqian.R;
import com.yixianqian.adapter.HomeListAdapter;
import com.yixianqian.base.BaseV4Fragment;
import com.yixianqian.swipelistview.SwipeListView;
import com.yixianqian.swipelistview.SwipeListViewListener;

/**
 * �����ƣ�HomeFragment ����������ҳ �����ˣ� ��˧ ����ʱ�䣺2014��7��4�� ����5:18:18
 *
 */
public class HomeFragment extends BaseV4Fragment {
	private View rootView;// ��View
	private ImageView topNavLeftBtn;//��������߰�ť
	private ImageView topNavRightBtn;//�������ұ߰�ť
	private View right_btn_bg;
	private SwipeListView mHomeListView;
	private TextView mEmpty;
	private HomeListAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_home, container, false);
		findViewById();// ��ʼ��views
		initView();
		
		mAdapter = new HomeListAdapter(getActivity(), mHomeListView);
		mHomeListView.setAdapter(mAdapter);
		return rootView;
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		topNavLeftBtn = (ImageView) rootView.findViewById(R.id.nav_left_btn);
		topNavRightBtn = (ImageView) rootView.findViewById(R.id.nav_right_btn);
		right_btn_bg = (View) rootView.findViewById(R.id.right_btn_bg);
		mHomeListView = (SwipeListView) rootView.findViewById(R.id.recent_listview);
		mEmpty = (TextView) rootView.findViewById(R.id.empty);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavLeftBtn.setImageResource(R.drawable.home);
		topNavRightBtn.setImageResource(R.drawable.ic_action_overflow);
		right_btn_bg.setBackgroundResource(R.drawable.sel_topnav_btn_bg);

		right_btn_bg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog();
			}
		});

		mHomeListView.setEmptyView(mEmpty);
		mHomeListView.setSwipeListViewListener(new SwipeListViewListener() {
			
			@Override
			public void onStartOpen(int position, int action, boolean right) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartClose(int position, boolean right) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onOpened(int position, boolean toRight) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onMove(int position, float x) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onListChanged() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLastListItem() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFirstListItem() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onDismiss(int[] reverseSortedPositions) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onClosed(int position, boolean fromRight) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onClickFrontView(int position) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onClickBackView(int position) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onChoiceStarted() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onChoiceEnded() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onChoiceChanged(int position, boolean selected) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public int onChangeSwipeMode(int position) {
				// TODO Auto-generated method stub
				return 0;
			}
		});
	}

	/**
	 * �˵���ʾ
	 */
	void showDialog() {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		Fragment fragment = getFragmentManager().findFragmentByTag("dialog");
		if (fragment != null) {
			ft.remove(fragment);
		}
		ft.addToBackStack(null);

		// Create and show the dialog.
		HomeDialogFragment newFragment = HomeDialogFragment.newInstance();
		newFragment.show(ft, "dialog");
	}

}
