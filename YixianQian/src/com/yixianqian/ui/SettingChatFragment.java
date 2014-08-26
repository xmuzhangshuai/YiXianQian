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
import com.yixianqian.base.BaseApplication;
import com.yixianqian.base.BaseV4Fragment;
import com.yixianqian.customewidget.MyAlertDialog;
import com.yixianqian.utils.FriendPreference;
import com.yixianqian.utils.LogTool;
import com.yixianqian.utils.PreferenceUtils;
import com.yixianqian.utils.ToastTool;

/**
 * �����ƣ�SettingChatFragment
 * ����������������ҳ��
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��8��17�� ����8:38:30
 *
 */
public class SettingChatFragment extends BaseV4Fragment implements OnClickListener {
	private View rootView;// ��View
	private TextView topNavigation;//����������
	private View leftImageButton;//��������ఴť
	private View rightBtnBg;//�������Ҳఴť
	private ImageView iv_switch_open_speaker;//����������������
	private ImageView iv_switch_close_speaker;//�ر���������������
	private RelativeLayout rl_switch_speaker;//��������������
	private View clearChatRedcord;//��������¼
	private EMChatOptions chatOptions;
	private FriendPreference friendPreference;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		friendPreference = BaseApplication.getInstance().getFriendPreference();
	}

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
		topNavigation.setText("��������");
		rightBtnBg.setVisibility(View.GONE);
		leftImageButton.setVisibility(View.GONE);
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

	/**
	 * ��������¼
	 */
	private void clearChatRecord() {
		final MyAlertDialog myAlertDialog = new MyAlertDialog(getActivity());
		myAlertDialog.setTitle("��ʾ");
		myAlertDialog.setMessage("�Ƿ���������¼��");
		View.OnClickListener comfirm = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myAlertDialog.dismiss();
				int userId = friendPreference.getF_id();
				if (userId > -1) {
					if (EMChatManager.getInstance().getConversation("" + userId) != null) {
						EMChatManager.getInstance().clearConversation("" + userId);
						ToastTool.showShort(getActivity(), "��������¼�ɹ���");
					} else {
						LogTool.i("settingChatFragment", "�ỰΪnull");
					}
				} else {
					LogTool.i("settingChatFragment", "��һ�벻����");
				}
			}
		};
		View.OnClickListener cancle = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myAlertDialog.dismiss();
			}
		};
		myAlertDialog.setPositiveButton("ȷ��", comfirm);
		myAlertDialog.setNegativeButton("ȡ��", cancle);
		myAlertDialog.show();
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
			clearChatRecord();
			break;
		default:
			break;
		}
	}

}
