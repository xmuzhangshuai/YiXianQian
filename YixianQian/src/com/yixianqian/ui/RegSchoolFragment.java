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
 * �����ƣ�RegSchoolFragment
 * ��������ѧУѡ��ҳ��
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��7��6�� ����7:56:17
 *
 */
public class RegSchoolFragment extends BaseV4Fragment {
	/*************Views************/
	private View rootView;// ��View
	private TextView topNavigation;//����������
	private View leftImageButton;//��������ఴť
	private View rightImageButton;//�������Ҳఴť
	private Spinner mProvinceView;//ʡ
	private Spinner mCityView;//����
	private Spinner mSchoolView;//ѧУ

	private String mProvince;
	private String mCity;
	private String mSchool;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_reg_school, container, false);
		findViewById();// ��ʼ��views
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
		topNavigation.setText("ѧУ");

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
	 * ���ѧУ
	 */
	private void attemptSchool() {

		boolean cancel = false;

		//����Ƿ�ѡ��ʡ
		if (mProvinceView.getSelectedItem().toString().length() == 0) {
			cancel = true;
			Toast.makeText(getActivity(), "��ѡ��ʡ", 1).show();
		}

		//����Ƿ�ѡ����
		if (mCityView.getSelectedItem().toString().length() == 0) {
			cancel = true;
			Toast.makeText(getActivity(), "��ѡ�����ڳ���", 1).show();
		}

		//����Ƿ�ѡѧУ
		if (mSchoolView.getSelectedItem().toString().length() == 0) {
			cancel = true;
			Toast.makeText(getActivity(), "��ѡ������ѧУ", 1).show();
		}

		if (!cancel) {
			// û�д���
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
