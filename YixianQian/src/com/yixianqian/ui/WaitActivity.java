package com.yixianqian.ui;

import org.apache.http.Header;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.db.ConversationDbService;
import com.yixianqian.entities.Conversation;
import com.yixianqian.table.LoveRequestTable;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.FriendPreference;
import com.yixianqian.utils.LogTool;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

/**
 * �����ƣ�WaitActivity
 * ���������ȴ��������ҳ��
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��8��4�� ����10:25:08
 *
 */
public class WaitActivity extends BaseActivity {
	private ImageView topNavLeftBtn;//��������߰�ť
	private TextView topNavText;//����������
	private View right_btn_bg;
	private Button refreshBtn;
	private TextView textInfo;
	private FriendPreference friendpreference;
	private UserPreference userPreference;
	private Button revokeBtn;
	private ImageView faceImageView;
	private Button goMianBtn;
	private TextView revokeText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_wait);

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
		refreshBtn = (Button) findViewById(R.id.refresh);
		textInfo = (TextView) findViewById(R.id.info);
		revokeBtn = (Button) findViewById(R.id.revoke);
		faceImageView = (ImageView) findViewById(R.id.face);
		goMianBtn = (Button) findViewById(R.id.gomain);
		revokeText = (TextView) findViewById(R.id.revoke_text);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		right_btn_bg.setVisibility(View.GONE);
		topNavText.setText("�������");
		textInfo.setText("�Է���û�н���������󣬿�ȥ����Ta...");
		faceImageView.setImageResource(R.drawable.face_black);
		topNavLeftBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(WaitActivity.this, MainActivity.class));
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
				finish();
			}
		});
		refreshBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getLoveRequestState();
			}
		});
		goMianBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(WaitActivity.this, MainActivity.class));
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				finish();
			}
		});

		revokeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				revoke();
			}
		});

		getLoveRequestState();
	}

	/**
	 * ��������
	 */
	private void revoke() {
		RequestParams params = new RequestParams();
		params.put(LoveRequestTable.LR_USERID, userPreference.getU_id());
		params.put(LoveRequestTable.LR_LOVERID, friendpreference.getF_id());
		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
			Dialog dialog;

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
				dialog = showProgressDialog("���ڳ���...");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					if (!TextUtils.isEmpty(response)) {
						if (response.equals("1")) {//���سɹ�
							ToastTool.showShort(WaitActivity.this, "���سɹ�");
							userPreference.setLoveRequest(false);
							startActivity(new Intent(WaitActivity.this, MainActivity.class));
							overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
							finish();
						} else if (response.equals("-1")) {//����������
							LogTool.e("WaitActivity", "���������������ʧ�ܣ�");
						} else if (response.equals("0")) {//�Է��Ѿ��ܾ�
							ToastTool.showShort(WaitActivity.this, "���سɹ�");
							LogTool.i("WaitActivity", "���������������,�Է��Ѿ��ܾ���");
							userPreference.setLoveRequest(false);
							startActivity(new Intent(WaitActivity.this, MainActivity.class));
							overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
							finish();
						} else if (response.equals("2")) {//�Է��Ѿ�ͬ��
							ToastTool.showShort(WaitActivity.this, "�Է��Ѿ�ͬ�⣬�޷�����");
							userPreference.setLoveRequest(false);
							cerateConversation();
							startActivity(new Intent(WaitActivity.this, MainActivity.class));
							overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
							finish();
						}
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				ToastTool.showLong(WaitActivity.this, "���������������ʧ��");
				LogTool.e("WaitActivity", "���������������ʧ�ܣ�" + errorResponse);
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				dialog.dismiss();
				super.onFinish();
			}
		};
		AsyncHttpClientTool.post("revokeloverequest", params, responseHandler);
	}

	/**
	 * ��ȡ��������״̬
	 */
	private void getLoveRequestState() {
		RequestParams params = new RequestParams();
		params.put(LoveRequestTable.LR_USERID, userPreference.getU_id());
		params.put(LoveRequestTable.LR_LOVERID, friendpreference.getF_id());
		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
			Dialog dialog;

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
				dialog = showProgressDialog("���Ժ�...");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					if (!TextUtils.isEmpty(response)) {
						if (response.equals("1")) {
							textInfo.setText("��ϲ���Է�������������󣬿�ȥ����Ta...");
							revokeBtn.setVisibility(View.INVISIBLE);
							faceImageView.setImageResource(R.drawable.face_red);
							refreshBtn.setVisibility(View.GONE);
							goMianBtn.setVisibility(View.VISIBLE);
							revokeText.setVisibility(View.INVISIBLE);

							userPreference.setLoveRequest(false);
							cerateConversation();

							//							friendpreference.setType(1);
							//							userPreference.setU_stateid(2);
							//
							//							//�����Ի�
							//							ConversationDbService conversationDbService = ConversationDbService
							//									.getInstance(WaitActivity.this);
							//							conversationDbService.conversationDao.deleteAll();
							//							Conversation conversation = new Conversation(null,
							//									Long.valueOf(friendpreference.getF_id()), friendpreference.getName(),
							//									friendpreference.getF_small_avatar(), "", 0, System.currentTimeMillis());
							//							conversationDbService.conversationDao.insert(conversation);
							//
							//							//���Է�����һ����Ϣ
							//							String msg = "�����Ѿ���Ϊ������~��";
							//							//��ȡ���������˵ĻỰ���󡣲���usernameΪ�����˵�userid����groupid�������е�username�������
							//							EMConversation emConversation = EMChatManager.getInstance().getConversation(
							//									"" + friendpreference.getF_id());
							//							//����һ���ı���Ϣ
							//							EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
							//							//������Ϣbody
							//							TextMessageBody txtBody = new TextMessageBody(msg);
							//							message.addBody(txtBody);
							//							//���ý�����
							//							message.setReceipt("" + friendpreference.getF_id());
							//							//����Ϣ���뵽�˻Ự������
							//							emConversation.addMessage(message);
						} else if (response.equals("2")) {
							userPreference.setLoveRequest(false);
							textInfo.setText("��Ŷ���Է��ܾ����������...");
							revokeBtn.setVisibility(View.INVISIBLE);
							faceImageView.setImageResource(R.drawable.face_unhappy);
							refreshBtn.setVisibility(View.GONE);
							goMianBtn.setVisibility(View.VISIBLE);
							revokeText.setVisibility(View.INVISIBLE);
						} else if (response.equals("3")) {
							textInfo.setText("�Է���û�н���������󣬿�ȥ����Ta...");
							faceImageView.setImageResource(R.drawable.face_black);
							refreshBtn.setVisibility(View.VISIBLE);
							revokeBtn.setVisibility(View.VISIBLE);
							revokeText.setVisibility(View.VISIBLE);
						}
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				ToastTool.showLong(WaitActivity.this, "����ʧ��");
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				dialog.dismiss();
				super.onFinish();
			}
		};
		AsyncHttpClientTool.post("getloverequeststate", params, responseHandler);
	}

	/**
	 * �����Ự
	 */
	private void cerateConversation() {
		friendpreference.setType(1);
		userPreference.setU_stateid(2);

		//�����Ի�
		ConversationDbService conversationDbService = ConversationDbService.getInstance(WaitActivity.this);
		conversationDbService.conversationDao.deleteAll();
		Conversation conversation = new Conversation(null, Long.valueOf(friendpreference.getF_id()),
				friendpreference.getName(), friendpreference.getF_small_avatar(), "", 0, System.currentTimeMillis());
		conversationDbService.conversationDao.insert(conversation);

		//���Է�����һ����Ϣ
		String msg = "�����Ѿ���Ϊ������~��";
		//��ȡ���������˵ĻỰ���󡣲���usernameΪ�����˵�userid����groupid�������е�username�������
		EMConversation emConversation = EMChatManager.getInstance().getConversation("" + friendpreference.getF_id());
		//����һ���ı���Ϣ
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
		//������Ϣbody
		TextMessageBody txtBody = new TextMessageBody(msg);
		message.addBody(txtBody);
		//���ý�����
		message.setReceipt("" + friendpreference.getF_id());
		//����Ϣ���뵽�˻Ự������
		emConversation.addMessage(message);
	}
}
