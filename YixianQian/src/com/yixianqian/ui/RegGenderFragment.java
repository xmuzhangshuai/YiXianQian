package com.yixianqian.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.yixianqian.R;
import com.yixianqian.base.BaseV4Fragment;
import com.yixianqian.interfaces.OnCompeletedListener;
import com.yixianqian.interfaces.OnUserInfoChangedListener;

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

//	private RadioGroup userStateGroup;
//	private RadioGroup genderGroup;
	private RadioButton mSingleView;//����
	private RadioButton mLoveView;//����
	private RadioButton mMale;//��
	private RadioButton mFemale;//Ů

	private OnCompeletedListener mOnCompeletedListener;
	private OnUserInfoChangedListener mOnuserInfoChangedListener;

	/**
	 * ʵ��OnCompeletedListener�ӿڣ������Զ�������һҳ
	 */
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try {
			mOnCompeletedListener = (OnCompeletedListener) activity;
			mOnuserInfoChangedListener = (OnUserInfoChangedListener) activity;
		} catch (Exception e) {
			// TODO: handle exception
			throw new ClassCastException(activity.toString() + " ����ʵ��OnCompeletedListener�ӿ�");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_reg_gender, container, false);
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
	protected void findViewById() {
		// TODO Auto-generated method stub
		mSingleView = (RadioButton) rootView.findViewById(R.id.single);
		mLoveView = (RadioButton) rootView.findViewById(R.id.love);
//		userStateGroup = (RadioGroup) rootView.findViewById(R.id.user_state_radiogroup);
//		genderGroup = (RadioGroup) rootView.findViewById(R.id.user_gender_radiogroup);
		mMale = (RadioButton) rootView.findViewById(R.id.male);
		mFemale = (RadioButton) rootView.findViewById(R.id.female);

	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		mSingleView.setOnCheckedChangeListener(this);
		mLoveView.setOnCheckedChangeListener(this);
		mMale.setOnCheckedChangeListener(this);
		mFemale.setOnCheckedChangeListener(this);
	}

	/**
	 * ʵ�ֻص��ӿ�
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		switch (buttonView.getId()) {
		case R.id.single:
			if (isChecked)
				mOnuserInfoChangedListener.onStateChanged(true);
			break;
		case R.id.love:
			if (isChecked)
				mOnuserInfoChangedListener.onStateChanged(false);
			break;
		case R.id.male:
			if (isChecked)
				mOnuserInfoChangedListener.onGenderChanged("��");
			break;
		case R.id.female:
			if (isChecked)
				mOnuserInfoChangedListener.onGenderChanged("Ů");
			break;
		default:
			break;
		}

		if ((mSingleView.isChecked() || mLoveView.isChecked()) && (mMale.isChecked() || mFemale.isChecked())) {
			mOnCompeletedListener.onCompeleted(1);
		}
	}

}
