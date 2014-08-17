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
 * 类名称：SettingChatFragment
 * 类描述：聊天设置页面
 * 创建人： 张帅
 * 创建时间：2014年8月17日 下午8:38:30
 *
 */
public class SettingChatFragment extends BaseV4Fragment implements OnClickListener {
	private View rootView;// 根View
	private TextView topNavigation;//导航栏文字
	private View leftImageButton;//导航栏左侧按钮
	private View rightBtnBg;//导航栏右侧按钮
	private ImageView iv_switch_open_speaker;//打开扬声器播放语音
	private ImageView iv_switch_close_speaker;//关闭扬声器播放语音
	private RelativeLayout rl_switch_speaker;//设置扬声器布局
	private View clearChatRedcord;//清空聊天记录
	private EMChatOptions chatOptions;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_setting_chat, container, false);

		findViewById();
		initView();
		return rootView;
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		topNavigation = (TextView) rootView.findViewById(R.id.nav_text);
		leftImageButton = (View) rootView.findViewById(R.id.left_btn_bg);
		rightBtnBg = (View) rootView.findViewById(R.id.right_btn_bg);
		iv_switch_open_speaker = (ImageView) rootView.findViewById(R.id.iv_switch_open_speaker);
		iv_switch_close_speaker = (ImageView) rootView.findViewById(R.id.iv_switch_close_speaker);
		clearChatRedcord = rootView.findViewById(R.id.clear_chat_record);
		rl_switch_speaker = (RelativeLayout) rootView.findViewById(R.id.rl_switch_speaker);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavigation.setText("聊天设置");
		rightBtnBg.setVisibility(View.GONE);
		leftImageButton.setOnClickListener(this);
		clearChatRedcord.setOnClickListener(this);
		rl_switch_speaker.setOnClickListener(this);

		chatOptions = EMChatManager.getInstance().getChatOptions();
		if (chatOptions.getUseSpeaker()) {
			iv_switch_open_speaker.setVisibility(View.VISIBLE);
			iv_switch_close_speaker.setVisibility(View.INVISIBLE);
		} else {
			iv_switch_open_speaker.setVisibility(View.INVISIBLE);
			iv_switch_close_speaker.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_switch_speaker:
			if (iv_switch_open_speaker.getVisibility() == View.VISIBLE) {
				iv_switch_open_speaker.setVisibility(View.INVISIBLE);
				iv_switch_close_speaker.setVisibility(View.VISIBLE);
				chatOptions.setUseSpeaker(false);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				PreferenceUtils.getInstance(getActivity()).setSettingMsgSpeaker(false);
			} else {
				iv_switch_open_speaker.setVisibility(View.VISIBLE);
				iv_switch_close_speaker.setVisibility(View.INVISIBLE);
				chatOptions.setUseSpeaker(true);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				PreferenceUtils.getInstance(getActivity()).setSettingMsgVibrate(true);
			}
			break;
		case R.id.clear_chat_record:
			break;
		case R.id.left_btn_bg:
			break;
		default:
			break;
		}
	}

}
