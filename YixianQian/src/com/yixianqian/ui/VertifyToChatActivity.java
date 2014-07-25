package com.yixianqian.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.db.ConversationDbService;
import com.yixianqian.entities.Conversation;
import com.yixianqian.utils.AsyncHttpClientImageSound;
import com.yixianqian.utils.FriendPreference;
import com.yixianqian.utils.ImageLoaderTool;
import com.yixianqian.utils.UserPreference;

public class VertifyToChatActivity extends BaseActivity {
	private ImageView topNavLeftBtn;//��������߰�ť
	private TextView topNavText;//����������
	private View right_btn_bg;
	private ImageView headImageView;//ͷ��
	private TextView nameTextView;//����
	private TextView provinceTextView;//ʡ��
	private TextView schoolTextView;//ѧУ
	private FriendPreference friendpreference;
	private UserPreference userPreference;
	private Button beginChatBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_vertify_to_chat);

		friendpreference = BaseApplication.getInstance().getFriendPreference();
		userPreference = BaseApplication.getInstance().getUserPreference();

		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		topNavLeftBtn = (ImageView) findViewById(R.id.nav_left_btn);
		right_btn_bg = (View) findViewById(R.id.right_btn_bg);
		topNavText = (TextView) findViewById(R.id.nav_text);
		headImageView = (ImageView) findViewById(R.id.head_image);
		nameTextView = (TextView) findViewById(R.id.name);
		provinceTextView = (TextView) findViewById(R.id.province);
		schoolTextView = (TextView) findViewById(R.id.school);
		beginChatBtn = (Button) findViewById(R.id.addlover);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		right_btn_bg.setVisibility(View.GONE);
		topNavText.setText("�������");
		beginChatBtn.setText("��ʼ����");
		topNavLeftBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		beginChatBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				votifyToChat();
			}
		});

		//����ͷ��
		if (!TextUtils.isEmpty(friendpreference.getF_small_avatar())) {
			imageLoader.displayImage(AsyncHttpClientImageSound.getAbsoluteUrl(friendpreference.getF_small_avatar()),
					headImageView, ImageLoaderTool.getHeadImageOptions(10));
			//�����ʾ����ͷ��
			headImageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(VertifyToChatActivity.this, ImageShowerActivity.class);
					intent.putExtra(ImageShowerActivity.SHOW_BIG_IMAGE,
							AsyncHttpClientImageSound.getAbsoluteUrl(friendpreference.getF_large_avatar()));
					startActivity(intent);
					VertifyToChatActivity.this.overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
				}
			});
		}
		//����������ʡ�ݡ���ѧУ
		//������ʾ��ʵ����
		String name = friendpreference.getF_nickname();
		if (friendpreference.getF_realname() != null) {
			if (friendpreference.getF_realname().length() > 0) {
				name = friendpreference.getF_realname();
			}
		}
		nameTextView.setText(name);
		provinceTextView.setText(friendpreference.getProvinceName());
		schoolTextView.setText(friendpreference.getSchoolName());
	}

	/**
	 * ȷ������
	 */
	private void votifyToChat() {
		friendpreference.setType(0);
		ConversationDbService conversationDbService = ConversationDbService.getInstance(VertifyToChatActivity.this);
		Conversation conversation = new Conversation(null, Long.valueOf(friendpreference.getF_id()),
				friendpreference.getName(), friendpreference.getF_small_avatar(), "�þò���", 6, System.currentTimeMillis());
		conversationDbService.conversationDao.insert(conversation);

		Intent intent = new Intent(VertifyToChatActivity.this, ChatActivity.class);
		intent.putExtra("conversationID", conversationDbService.getIdByConversation(conversation));
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		finish();
	}

}
