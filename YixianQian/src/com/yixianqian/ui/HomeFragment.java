package com.yixianqian.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yixianqian.R;
import com.yixianqian.base.BaseV4Fragment;

/**
 * 类名称：HomeFragment 类描述：主页 创建人： 张帅 创建时间：2014年7月4日 下午5:18:18
 *
 */
public class HomeFragment extends BaseV4Fragment {
	private View rootView;// 根View
	private ImageView topNavLeftBtn;//导航条左边按钮
	private ImageView topNavRightBtn;//导航条右边按钮
	private View right_btn_bg;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_home, container, false);
		findViewById();// 初始化views
		initView();
		return rootView;
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		topNavLeftBtn = (ImageView) rootView.findViewById(R.id.nav_left_btn);
		topNavRightBtn = (ImageView) rootView.findViewById(R.id.nav_right_btn);
		right_btn_bg = (View) rootView.findViewById(R.id.right_btn_bg);
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
	}

	/**
	 * 菜单显示
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
