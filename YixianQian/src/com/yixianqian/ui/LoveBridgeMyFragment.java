package com.yixianqian.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yixianqian.R;
import com.yixianqian.base.BaseV4Fragment;

/**
 * �����ƣ�LoveBridgeMyFragment
 * ��������ȵ�ţ��ҵķ���
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��9��11�� ����4:55:29
 *
 */
public class LoveBridgeMyFragment extends BaseV4Fragment {
	private View rootView;// ��View

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_love_bridge_my, container, false);

		findViewById();// ��ʼ��views
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
