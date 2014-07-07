package com.yixianqian.adapter;

import com.yixianqian.ui.RegAuthCodeFragment;
import com.yixianqian.ui.RegGenderFragment;
import com.yixianqian.ui.RegPhoneFragment;
import com.yixianqian.ui.RegSchoolFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class RegisterAdapter extends FragmentPagerAdapter {

	public RegisterAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		switch (position) {
		case 0:
			return new RegGenderFragment();
		case 1:
			return new RegSchoolFragment();
		case 2:
			return new RegPhoneFragment();
		case 3:
			return new RegAuthCodeFragment();
		default:
			return null;
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 4;
	}

}
