package com.yixianqian.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.yixianqian.R;
import com.yixianqian.base.BaseV4Fragment;

/**
 * �����ƣ�AuthCodeActivity
 * ����������֤��ҳ��
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��7��4�� ����9:28:01
 *
 */
public class RegAuthCodeFragment extends BaseV4Fragment {
	/*************Views************/
	private View rootView;// ��View
	private TextView topNavigation;
	private View leftImageButton;
	private View rightImageButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_reg_authcode, container, false);
		findViewById();// ��ʼ��views
		initView();
		return rootView;
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		//		topNavigation = (TextView) rootView.findViewById(R.id.nav_text);
		//		leftImageButton = (View) rootView.findViewById(R.id.left_btn_bg);
		//		rightImageButton = (View) rootView.findViewById(R.id.right_btn_bg);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		//		topNavigation.setText("��д��֤��");
		//		leftImageButton.setOnClickListener(new OnClickListener() {
		//			@Override
		//			public void onClick(View v) {
		//				// TODO Auto-generated method stub
		//				getActivity().finish();
		//			}
		//		});
		//
		//		rightImageButton.setOnClickListener(new OnClickListener() {
		//			@Override
		//			public void onClick(View v) {
		//				// TODO Auto-generated method stub
		//			}
		//		});
	}

}
