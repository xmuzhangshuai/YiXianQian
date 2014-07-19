package com.yixianqian.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TextView;

import com.yixianqian.R;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.base.BaseV4Fragment;
import com.yixianqian.utils.UserPreference;

/**
 * �����ƣ�RegGenderFragment
 * ��������ѡ���Ա�ҳ��
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��7��6�� ����7:28:49
 *
 */
public class RegGenderFragment extends BaseV4Fragment implements OnCheckedChangeListener {
	/*************Views************/
	private View rootView;// ��View

	private RadioButton mSingleView;//����
	private RadioButton mLoveView;//����
	private RadioButton mMale;//��
	private RadioButton mFemale;//Ů
	private TextView topNavigation;//����������
	private View leftImageButton;//��������ఴť
	private View rightImageButton;//�������Ҳఴť

	private boolean mIsSingle;
	private String gender;
	private UserPreference userPreference;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_reg_gender, container, false);
		userPreference = BaseApplication.getInstance().getUserPreference();
		
		findViewById();// ��ʼ��views
		initView();
		return rootView;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		mSingleView = (RadioButton) rootView.findViewById(R.id.single);
		mLoveView = (RadioButton) rootView.findViewById(R.id.love);
		mMale = (RadioButton) rootView.findViewById(R.id.male);
		mFemale = (RadioButton) rootView.findViewById(R.id.female);

		topNavigation = (TextView) getActivity().findViewById(R.id.nav_text);
		leftImageButton = (View) getActivity().findViewById(R.id.left_btn_bg);
		rightImageButton = (View) getActivity().findViewById(R.id.right_btn_bg);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavigation.setText("״̬");
		rightImageButton.setEnabled(false);
		leftImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getActivity().finish();
			}
		});
		rightImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				next();
			}
		});
		mSingleView.setOnCheckedChangeListener(this);
		mLoveView.setOnCheckedChangeListener(this);
		mMale.setOnCheckedChangeListener(this);
		mFemale.setOnCheckedChangeListener(this);
	}

	/**
	 * ����Ƿ���ѡ
	 */
	private void attemptGender() {

		boolean cancel = false;

		//����Ƿ�ѡ��״̬
		if (!mSingleView.isChecked() && !mLoveView.isChecked()) {
			cancel = true;
			rightImageButton.setEnabled(false);
		} else if (mSingleView.isChecked()) {
			userPreference.setU_stateid(4);
		} else if (mLoveView.isChecked()) {
			userPreference.setU_stateid(2);
		}

		//����Ƿ�ѡ�Ա�
		if (!mMale.isChecked() && !mFemale.isChecked()) {
			cancel = true;
			rightImageButton.setEnabled(false);
		} else if (mMale.isChecked()) {
			userPreference.setU_gender("��");
		} else if (mFemale.isChecked()) {
			userPreference.setU_gender("Ů");
		}

		if (!cancel) {
			// û�д���
			rightImageButton.setEnabled(true);
		}
	}

	/**
	 * ��һ��
	 */
	private void next() {
		RegSchoolFragment regSchoolFragment = new RegSchoolFragment();
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out, R.anim.push_right_in,
				R.anim.push_right_out);
		transaction.replace(R.id.fragment_container, regSchoolFragment);
		//		transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		attemptGender();
	}
}
