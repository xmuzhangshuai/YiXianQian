package com.yixianqian.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.yixianqian.R;
import com.yixianqian.base.BaseV4Fragment;

/**
 * 类名称：RegSchoolFragment
 * 类描述：学校选择页面
 * 创建人： 张帅
 * 创建时间：2014年7月6日 下午7:56:17
 *
 */
public class RegSchoolFragment extends BaseV4Fragment {
	/*************Views************/
	private View rootView;// 根View
	private TextView topNavigation;//导航栏文字
	private View leftImageButton;//导航栏左侧按钮
	private View rightImageButton;//导航栏右侧按钮
	private Spinner mProvinceView;//省
	private Spinner mCityView;//城市
	private Spinner mSchoolView;//学校

	private String mProvince;
	private String mCity;
	private String mSchool;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_reg_school, container, false);
		findViewById();// 初始化views
		initView();
		return rootView;
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		topNavigation = (TextView) getActivity().findViewById(R.id.nav_text);
		leftImageButton = (View) getActivity().findViewById(R.id.left_btn_bg);
		rightImageButton = (View) getActivity().findViewById(R.id.right_btn_bg);
		mProvinceView = (Spinner) rootView.findViewById(R.id.province);
		mCityView = (Spinner) rootView.findViewById(R.id.city);
		mSchoolView = (Spinner) rootView.findViewById(R.id.school);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavigation.setText("学校");

		leftImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getFragmentManager().popBackStack();
			}
		});
		rightImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				attemptSchool();
			}
		});
	}

	/**
	 * 检查学校
	 */
	private void attemptSchool() {

		boolean cancel = false;

		//检查是否选择省
		if (mProvinceView.getSelectedItem().toString().length() == 0) {
			cancel = true;
			Toast.makeText(getActivity(), "请选择省", 1).show();
		}

		//检查是否选城市
		if (mCityView.getSelectedItem().toString().length() == 0) {
			cancel = true;
			Toast.makeText(getActivity(), "请选择所在城市", 1).show();
		}

		//检查是否选学校
		if (mSchoolView.getSelectedItem().toString().length() == 0) {
			cancel = true;
			Toast.makeText(getActivity(), "请选择所在学校", 1).show();
		}

		if (!cancel) {
			// 没有错误
			RegPhoneFragment phoneFragment = new RegPhoneFragment();
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out, R.anim.push_right_in,
					R.anim.push_right_out);
			transaction.replace(R.id.fragment_container, phoneFragment);
//			transaction.addToBackStack(null);
			transaction.commit();
		}
	}
}
