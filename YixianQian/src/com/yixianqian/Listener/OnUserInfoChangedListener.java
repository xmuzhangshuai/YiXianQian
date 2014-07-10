package com.yixianqian.Listener;

public interface OnUserInfoChangedListener {
	public void onGenderChanged(String gender);

	public void onStateChanged(boolean single);

	public void onProvinceChanged(String province);

	public void onCityChanged(String city);

	public void onSchoolChanged(String school);

	public void onPhoneChanged(String phone);

	public void onPassChanged(String password);
}
