package com.yixianqian.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yixianqian.R;
import com.yixianqian.base.BaseV4Fragment;

/**
 * 类名称：LoveBridgeMyFragment
 * 类描述：鹊桥，我的发布
 * 创建人： 张帅
 * 创建时间：2014年9月11日 下午4:55:29
 *
 */
public class LoveBridgeMyFragment extends BaseV4Fragment {
	private View rootView;// 根View

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_love_bridge_my, container, false);

		findViewById();// 初始化views
		initView();
		return rootView;
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub

	}

}
