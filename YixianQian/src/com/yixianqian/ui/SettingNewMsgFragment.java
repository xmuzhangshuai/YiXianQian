package com.yixianqian.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.yixianqian.R;
import com.yixianqian.base.BaseV4Fragment;
import com.yixianqian.utils.PreferenceUtils;

/**
 * �����ƣ�SettingNewMsgFragment
 * ������������Ϣ��������
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��8��17�� ����5:05:34
 *
 */
public class SettingNewMsgFragment extends BaseV4Fragment implements OnClickListener {
	private View rootView;// ��View
	private RelativeLayout rl_switch_notification;// ��������Ϣ֪ͨ����
	private RelativeLayout rl_switch_sound;//������������
	private RelativeLayout rl_switch_vibrate;// �����𶯲���
	private ImageView iv_switch_open_notification;//������Ϣ֪ͨimageView
	private ImageView iv_switch_close_notification;//�ر�����Ϣ֪ͨimageview
	private ImageView iv_switch_open_sound;//��������ʾimageview
	private ImageView iv_switch_close_sound;//�ر�������ʾimageview
	private ImageView iv_switch_open_vibrate;//����Ϣ����ʾ
	private ImageView iv_switch_close_vibrate;// �ر���Ϣ����ʾ

	private View divider1, divider2, divider3;

	private EMChatOptions chatOptions;
	private TextView topNavigation;//����������
	private View leftImageButton;//��������ఴť
	private View rightBtnBg;//�������Ҳఴť

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_setting_newmsg, container, false);

		findViewById();
		initView();
		return rootView;
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		rl_switch_notification = (RelativeLayout) rootView.findViewById(R.id.rl_switch_notification);
		rl_switch_sound = (RelativeLayout) rootView.findViewById(R.id.rl_switch_sound);
		rl_switch_vibrate = (RelativeLayout) rootView.findViewById(R.id.rl_switch_vibrate);

		iv_switch_open_notification = (ImageView) rootView.findViewById(R.id.iv_switch_open_notification);
		iv_switch_close_notification = (ImageView) rootView.findViewById(R.id.iv_switch_close_notification);
		iv_switch_open_sound = (ImageView) rootView.findViewById(R.id.iv_switch_open_sound);
		iv_switch_close_sound = (ImageView) rootView.findViewById(R.id.iv_switch_close_sound);
		iv_switch_open_vibrate = (ImageView) rootView.findViewById(R.id.iv_switch_open_vibrate);
		iv_switch_close_vibrate = (ImageView) rootView.findViewById(R.id.iv_switch_close_vibrate);

		divider1 = rootView.findViewById(R.id.divider1);
		divider2 = rootView.findViewById(R.id.divider2);
		divider3 = rootView.findViewById(R.id.divider3);
		topNavigation = (TextView) rootView.findViewById(R.id.nav_text);
		leftImageButton = (View) rootView.findViewById(R.id.left_btn_bg);
		rightBtnBg = (View) rootView.findViewById(R.id.right_btn_bg);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavigation.setText("����Ϣ����");
		rightBtnBg.setVisibility(View.GONE);
		leftImageButton.setVisibility(View.GONE);

		rl_switch_notification.setOnClickListener(this);
		rl_switch_sound.setOnClickListener(this);
		rl_switch_vibrate.setOnClickListener(this);

		chatOptions = EMChatManager.getInstance().getChatOptions();
		if (chatOptions.getNotificationEnable()) {
			iv_switch_open_notification.setVisibility(View.VISIBLE);
			iv_switch_close_notification.setVisibility(View.INVISIBLE);
		} else {
			iv_switch_open_notification.setVisibility(View.INVISIBLE);
			iv_switch_close_notification.setVisibility(View.VISIBLE);
		}
		if (chatOptions.getNoticedBySound()) {
			iv_switch_open_sound.setVisibility(View.VISIBLE);
			iv_switch_close_sound.setVisibility(View.INVISIBLE);
		} else {
			iv_switch_open_sound.setVisibility(View.INVISIBLE);
			iv_switch_close_sound.setVisibility(View.VISIBLE);
		}
		if (chatOptions.getNoticedByVibrate()) {
			iv_switch_open_vibrate.setVisibility(View.VISIBLE);
			iv_switch_close_vibrate.setVisibility(View.INVISIBLE);
		} else {
			iv_switch_open_vibrate.setVisibility(View.INVISIBLE);
			iv_switch_close_vibrate.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_switch_notification:
			if (iv_switch_open_notification.getVisibility() == View.VISIBLE) {
				iv_switch_open_notification.setVisibility(View.INVISIBLE);
				iv_switch_close_notification.setVisibility(View.VISIBLE);
				rl_switch_sound.setVisibility(View.GONE);
				rl_switch_vibrate.setVisibility(View.GONE);
				divider1.setVisibility(View.GONE);
				divider2.setVisibility(View.GONE);
				divider3.setVisibility(View.GONE);
				chatOptions.setNotificationEnable(false);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				PreferenceUtils.getInstance(getActivity()).setSettingMsgNotification(false);
			} else {
				iv_switch_open_notification.setVisibility(View.VISIBLE);
				iv_switch_close_notification.setVisibility(View.INVISIBLE);
				rl_switch_sound.setVisibility(View.VISIBLE);
				rl_switch_vibrate.setVisibility(View.VISIBLE);
				divider1.setVisibility(View.VISIBLE);
				divider2.setVisibility(View.VISIBLE);
				divider3.setVisibility(View.VISIBLE);
				chatOptions.setNotificationEnable(true);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				PreferenceUtils.getInstance(getActivity()).setSettingMsgNotification(true);
			}
			break;
		case R.id.rl_switch_sound:
			if (iv_switch_open_sound.getVisibility() == View.VISIBLE) {
				iv_switch_open_sound.setVisibility(View.INVISIBLE);
				iv_switch_close_sound.setVisibility(View.VISIBLE);
				chatOptions.setNoticeBySound(false);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				PreferenceUtils.getInstance(getActivity()).setSettingMsgSound(false);
			} else {
				iv_switch_open_sound.setVisibility(View.VISIBLE);
				iv_switch_close_sound.setVisibility(View.INVISIBLE);
				chatOptions.setNoticeBySound(true);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				PreferenceUtils.getInstance(getActivity()).setSettingMsgSound(true);
			}
			break;
		case R.id.rl_switch_vibrate:
			if (iv_switch_open_vibrate.getVisibility() == View.VISIBLE) {
				iv_switch_open_vibrate.setVisibility(View.INVISIBLE);
				iv_switch_close_vibrate.setVisibility(View.VISIBLE);
				chatOptions.setNoticedByVibrate(false);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				PreferenceUtils.getInstance(getActivity()).setSettingMsgVibrate(false);
			} else {
				iv_switch_open_vibrate.setVisibility(View.VISIBLE);
				iv_switch_close_vibrate.setVisibility(View.INVISIBLE);
				chatOptions.setNoticedByVibrate(true);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				PreferenceUtils.getInstance(getActivity()).setSettingMsgVibrate(true);
			}
			break;
		default:
			break;
		}

	}

}
