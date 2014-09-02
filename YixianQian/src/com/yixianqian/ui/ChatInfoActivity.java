package com.yixianqian.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.config.Constants;
import com.yixianqian.customewidget.MyAlertDialog;
import com.yixianqian.utils.AsyncHttpClientImageSound;
import com.yixianqian.utils.FriendPreference;
import com.yixianqian.utils.ImageLoaderTool;
import com.yixianqian.utils.LogTool;
import com.yixianqian.utils.PreferenceUtils;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

/**
 * �����ƣ�ChatInfoActivity
 * ��������������Ϣ
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��8��28�� ����4:00:13
 *
 */
public class ChatInfoActivity extends BaseActivity implements OnClickListener {
	private TextView topNavigation;//����������
	private View leftImageButton;//��������ఴť
	private View rightBtnBg;//�������Ҳఴť
	private ImageView headImageView;
	private TextView nameTextView;//��������
	private TextView provinceTextView;//ʡ��
	private TextView schoolTextView;//ѧУ
	private RelativeLayout rl_switch_notification;// ��������Ϣ֪ͨ����
	private RelativeLayout rl_switch_sound;//������������
	private RelativeLayout rl_switch_vibrate;// �����𶯲���
	private ImageView iv_switch_open_notification;//������Ϣ֪ͨimageView
	private ImageView iv_switch_close_notification;//�ر�����Ϣ֪ͨimageview
	private ImageView iv_switch_open_sound;//��������ʾimageview
	private ImageView iv_switch_close_sound;//�ر�������ʾimageview
	private ImageView iv_switch_open_vibrate;//����Ϣ����ʾ
	private ImageView iv_switch_close_vibrate;// �ر���Ϣ����ʾ
	private ImageView iv_switch_open_speaker;//����������������
	private ImageView iv_switch_close_speaker;//�ر���������������
	private RelativeLayout rl_switch_speaker;//��������������
	private View complain;//Ͷ��
	private View clearChatRedcord;//��������¼

	private View divider1, divider2, divider3;
	private EMChatOptions chatOptions;
	private FriendPreference friendPreference;
	private UserPreference userPreference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_chat_info);

		friendPreference = BaseApplication.getInstance().getFriendPreference();
		userPreference = BaseApplication.getInstance().getUserPreference();

		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		rl_switch_notification = (RelativeLayout) findViewById(R.id.rl_switch_notification);
		rl_switch_sound = (RelativeLayout) findViewById(R.id.rl_switch_sound);
		rl_switch_vibrate = (RelativeLayout) findViewById(R.id.rl_switch_vibrate);
		iv_switch_open_notification = (ImageView) findViewById(R.id.iv_switch_open_notification);
		iv_switch_close_notification = (ImageView) findViewById(R.id.iv_switch_close_notification);
		iv_switch_open_sound = (ImageView) findViewById(R.id.iv_switch_open_sound);
		iv_switch_close_sound = (ImageView) findViewById(R.id.iv_switch_close_sound);
		iv_switch_open_vibrate = (ImageView) findViewById(R.id.iv_switch_open_vibrate);
		iv_switch_close_vibrate = (ImageView) findViewById(R.id.iv_switch_close_vibrate);
		iv_switch_open_speaker = (ImageView) findViewById(R.id.iv_switch_open_speaker);
		iv_switch_close_speaker = (ImageView) findViewById(R.id.iv_switch_close_speaker);
		clearChatRedcord = findViewById(R.id.clear_chat_record);
		rl_switch_speaker = (RelativeLayout) findViewById(R.id.rl_switch_speaker);
		complain = findViewById(R.id.complain);

		divider1 = findViewById(R.id.divider1);
		divider2 = findViewById(R.id.divider2);
		divider3 = findViewById(R.id.divider3);
		topNavigation = (TextView) findViewById(R.id.nav_text);
		leftImageButton = (View) findViewById(R.id.left_btn_bg);
		rightBtnBg = (View) findViewById(R.id.right_btn_bg);
		nameTextView = (TextView) findViewById(R.id.name);
		provinceTextView = (TextView) findViewById(R.id.province);
		schoolTextView = (TextView) findViewById(R.id.school);
		headImageView = (ImageView) findViewById(R.id.head_image);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavigation.setText("������Ϣ");
		rightBtnBg.setVisibility(View.GONE);
		//����ͷ��
		if (!TextUtils.isEmpty(friendPreference.getF_small_avatar())) {
			imageLoader.displayImage(AsyncHttpClientImageSound.getAbsoluteUrl(friendPreference.getF_small_avatar()),
					headImageView, ImageLoaderTool.getHeadImageOptions(10));

			//�����������
			headImageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(ChatInfoActivity.this, PersonDetailActivity.class);
					if (userPreference.getU_stateid() == 3) {//������Ķ�
						intent.putExtra(PersonDetailActivity.PERSON_TYPE, Constants.PersonDetailType.FLIPPER);
					} else if (userPreference.getU_stateid() == 2) {//���������
						intent.putExtra(PersonDetailActivity.PERSON_TYPE, Constants.PersonDetailType.LOVER);
					}
					ChatInfoActivity.this.startActivity(intent);
					ChatInfoActivity.this.overridePendingTransition(R.anim.zoomin2, R.anim.zoomout);
				}
			});
		}
		nameTextView.setText(friendPreference.getName());
		schoolTextView.setText(friendPreference.getSchoolName());
		provinceTextView.setText(friendPreference.getProvinceName());

		rl_switch_notification.setOnClickListener(this);
		rl_switch_sound.setOnClickListener(this);
		rl_switch_vibrate.setOnClickListener(this);
		leftImageButton.setOnClickListener(this);
		clearChatRedcord.setOnClickListener(this);
		rl_switch_speaker.setOnClickListener(this);
		complain.setOnClickListener(this);

		chatOptions = EMChatManager.getInstance().getChatOptions();
		if (chatOptions.getNotificationEnable()) {
			iv_switch_open_notification.setVisibility(View.VISIBLE);
			iv_switch_close_notification.setVisibility(View.INVISIBLE);
		} else {
			iv_switch_open_notification.setVisibility(View.INVISIBLE);
			iv_switch_close_notification.setVisibility(View.VISIBLE);

			rl_switch_sound.setVisibility(View.GONE);
			rl_switch_vibrate.setVisibility(View.GONE);
			divider1.setVisibility(View.GONE);
			divider2.setVisibility(View.GONE);
			divider3.setVisibility(View.GONE);
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
		if (chatOptions.getUseSpeaker()) {
			iv_switch_open_speaker.setVisibility(View.VISIBLE);
			iv_switch_close_speaker.setVisibility(View.INVISIBLE);
		} else {
			iv_switch_open_speaker.setVisibility(View.INVISIBLE);
			iv_switch_close_speaker.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	/**
	 * ��������¼
	 */
	private void clearChatRecord() {
		final MyAlertDialog myAlertDialog = new MyAlertDialog(ChatInfoActivity.this);
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
						ToastTool.showShort(ChatInfoActivity.this, "��������¼�ɹ���");
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

	/**
	 * �����ѡ��ͼƬ
	 */
	private void choosePhoto() {
		Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, 2);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK)
			return;

		switch (requestCode) {
		case 2://�����ѡ��
			try {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = ChatInfoActivity.this.getContentResolver().query(selectedImage, filePathColumn, null,
						null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();

				startActivity(new Intent(ChatInfoActivity.this, HandleComplainActivity.class).putExtra(
						HandleComplainActivity.COMPLAIN_IMAGE_PAHT, picturePath));
			} catch (Exception e) {
				// TODO: handle exception   
				e.printStackTrace();
			}
			break;
		}
	}

	/**
	 * ����Ͷ��
	 */
	private void handleComplain() {
		final MyAlertDialog myAlertDialog = new MyAlertDialog(ChatInfoActivity.this);
		myAlertDialog.setTitle("��ʾ");
		myAlertDialog.setMessage("�����ṩ�����¼����֤��");
		View.OnClickListener comfirm = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myAlertDialog.dismiss();
				choosePhoto();
			}
		};
		View.OnClickListener cancle = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myAlertDialog.dismiss();
			}
		};
		myAlertDialog.setPositiveButton("ȥ���", comfirm);
		myAlertDialog.setNegativeButton("ȡ��", cancle);
		myAlertDialog.show();
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
				PreferenceUtils.getInstance(ChatInfoActivity.this).setSettingMsgNotification(false);
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
				PreferenceUtils.getInstance(ChatInfoActivity.this).setSettingMsgNotification(true);
			}
			break;
		case R.id.rl_switch_sound:
			if (iv_switch_open_sound.getVisibility() == View.VISIBLE) {
				iv_switch_open_sound.setVisibility(View.INVISIBLE);
				iv_switch_close_sound.setVisibility(View.VISIBLE);
				chatOptions.setNoticeBySound(false);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				PreferenceUtils.getInstance(ChatInfoActivity.this).setSettingMsgSound(false);
			} else {
				iv_switch_open_sound.setVisibility(View.VISIBLE);
				iv_switch_close_sound.setVisibility(View.INVISIBLE);
				chatOptions.setNoticeBySound(true);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				PreferenceUtils.getInstance(ChatInfoActivity.this).setSettingMsgSound(true);
			}
			break;
		case R.id.rl_switch_vibrate:
			if (iv_switch_open_vibrate.getVisibility() == View.VISIBLE) {
				iv_switch_open_vibrate.setVisibility(View.INVISIBLE);
				iv_switch_close_vibrate.setVisibility(View.VISIBLE);
				chatOptions.setNoticedByVibrate(false);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				PreferenceUtils.getInstance(ChatInfoActivity.this).setSettingMsgVibrate(false);
			} else {
				iv_switch_open_vibrate.setVisibility(View.VISIBLE);
				iv_switch_close_vibrate.setVisibility(View.INVISIBLE);
				chatOptions.setNoticedByVibrate(true);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				PreferenceUtils.getInstance(ChatInfoActivity.this).setSettingMsgVibrate(true);
			}
			break;
		case R.id.rl_switch_speaker:
			if (iv_switch_open_speaker.getVisibility() == View.VISIBLE) {
				iv_switch_open_speaker.setVisibility(View.INVISIBLE);
				iv_switch_close_speaker.setVisibility(View.VISIBLE);
				chatOptions.setUseSpeaker(false);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				PreferenceUtils.getInstance(ChatInfoActivity.this).setSettingMsgSpeaker(false);
			} else {
				iv_switch_open_speaker.setVisibility(View.VISIBLE);
				iv_switch_close_speaker.setVisibility(View.INVISIBLE);
				chatOptions.setUseSpeaker(true);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				PreferenceUtils.getInstance(ChatInfoActivity.this).setSettingMsgVibrate(true);
			}
			break;
		case R.id.clear_chat_record:
			clearChatRecord();
			break;
		case R.id.left_btn_bg:
			finish();
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			break;
		case R.id.complain:
			handleComplain();
			break;
		default:
			break;
		}
	}

}
