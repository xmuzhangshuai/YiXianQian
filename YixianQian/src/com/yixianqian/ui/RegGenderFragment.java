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
 * 类名称：RegGenderFragment
 * 类描述：选择性别页面
 * 创建人： 张帅
 * 创建时间：2014年7月6日 下午7:28:49
 *
 */
public class RegGenderFragment extends BaseV4Fragment implements OnCheckedChangeListener {
	/*************Views************/
	private View rootView;// 根View

//	private RadioGroup userStateGroup;
//	private RadioGroup genderGroup;
	private RadioButton mSingleView;//单身
	private RadioButton mLoveView;//恋爱
	private RadioButton mMale;//男
	private RadioButton mFemale;//女

	private OnCompeletedListener mOnCompeletedListener;
	private OnUserInfoChangedListener mOnuserInfoChangedListener;

	/**
	 * 实现OnCompeletedListener接口，用于自动进入下一页
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
			throw new ClassCastException(activity.toString() + " 必须实现OnCompeletedListener接口");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_reg_gender, container, false);
		findViewById();// 初始化views
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
	 * 实现回调接口
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
				mOnuserInfoChangedListener.onGenderChanged("男");
			break;
		case R.id.female:
			if (isChecked)
				mOnuserInfoChangedListener.onGenderChanged("女");
			break;
		default:
			break;
		}

		if ((mSingleView.isChecked() || mLoveView.isChecked()) && (mMale.isChecked() || mFemale.isChecked())) {
			mOnCompeletedListener.onCompeleted(1);
		}
	}

}
