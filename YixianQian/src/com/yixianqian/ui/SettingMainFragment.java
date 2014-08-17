package com.yixianqian.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yixianqian.R;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.base.BaseV4Fragment;
import com.yixianqian.utils.FriendPreference;
import com.yixianqian.utils.UserPreference;

/**
 * 
 * �����ƣ�SettingMainFragment
 * ��������������ҳ��
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��8��17�� ����4:26:06
 *
 */
public class SettingMainFragment extends BaseV4Fragment implements OnClickListener {
	private View rootView;// ��View
	private TextView topNavigation;//����������
	private View leftImageButton;//��������ఴť
	private View rightBtnBg;//�������Ҳఴť

	private View settingNewMsg;//����Ϣ����
	private View settingChat;//��������
	private View settingClearCache;//��ջ���
	private View settingFeedback;//����
	private View settingCheckUpdate;//������
	private View settingAbout;//����
	private View settingLogout;//�˳�
	private FragmentTransaction transaction;
	private UserPreference userPreference;
	private FriendPreference friendPreference;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		transaction = getFragmentManager().beginTransaction();
		userPreference = BaseApplication.getInstance().getUserPreference();
		friendPreference = BaseApplication.getInstance().getFriendPreference();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_setting_main, container, false);

		findViewById();
		initView();
		return rootView;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		transaction = null;
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		topNavigation = (TextView) rootView.findViewById(R.id.nav_text);
		leftImageButton = (View) rootView.findViewById(R.id.left_btn_bg);
		rightBtnBg = (View) rootView.findViewById(R.id.right_btn_bg);
		settingNewMsg = rootView.findViewById(R.id.setting_newmsg);
		settingChat = rootView.findViewById(R.id.setting_chat);
		settingClearCache = rootView.findViewById(R.id.setting_clear_cache);
		settingFeedback = rootView.findViewById(R.id.setting_feedback);
		settingCheckUpdate = rootView.findViewById(R.id.setting_check_update);
		settingAbout = rootView.findViewById(R.id.setting_about);
		settingLogout = rootView.findViewById(R.id.setting_logout);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavigation.setText("����");
		rightBtnBg.setVisibility(View.GONE);
		leftImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getActivity().finish();
			}
		});

		settingNewMsg.setOnClickListener(this);
		settingChat.setOnClickListener(this);
		settingClearCache.setOnClickListener(this);
		settingFeedback.setOnClickListener(this);
		settingCheckUpdate.setOnClickListener(this);
		settingAbout.setOnClickListener(this);
		settingLogout.setOnClickListener(this);
	}

	/**
	 * �˳���¼
	 */
	private void logout() {
		//�����û�������¼
		Intent intent;
		BaseApplication.getInstance().logout();
		userPreference.clear();
		friendPreference.clear();
		intent = new Intent(getActivity(), LoginOrRegisterActivity.class);
		getActivity().startActivity(intent);
		getActivity().finish();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.setting_newmsg:
			transaction.setCustomAnimations(R.anim.zoomin2, R.anim.zoomout);
			transaction.replace(R.id.container, new SettingNewMsgFragment());
			transaction.addToBackStack("setting");
			transaction.commit();
			break;
		case R.id.setting_chat:
			transaction.setCustomAnimations(R.anim.zoomin2, R.anim.zoomout);
			transaction.replace(R.id.container, new SettingChatFragment());
			transaction.addToBackStack("setting");
			transaction.commit();
			break;
		case R.id.setting_clear_cache:

			break;
		case R.id.setting_feedback:

			break;
		case R.id.setting_check_update:

			break;
		case R.id.setting_about:

			break;
		case R.id.setting_logout:
			logout();
			break;
		default:
			break;
		}
	}

}
