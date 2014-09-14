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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.db.ConversationDbService;
import com.yixianqian.entities.Conversation;
import com.yixianqian.jsonobject.JsonUser;
import com.yixianqian.table.LoveRequestTable;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.CommonTools;
import com.yixianqian.utils.FastJsonTool;
import com.yixianqian.utils.FriendPreference;
import com.yixianqian.utils.LogTool;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

/**
 * �����ƣ�AlreadyInviteActivity
 * ���������Ѿ�����������
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��8��4�� ����10:42:35
 *
 */
public class AlreadyInviteActivity extends BaseActivity {
	private ImageView topNavLeftBtn;//��������߰�ť
	private TextView topNavText;//����������
	private View right_btn_bg;
	private EditText mPhoneView;//�ֻ���
	private String mPhone;
	private Button submit;
	private boolean cancel = false;
	private View focusView = null;
	private UserPreference userPreference;
	private FriendPreference friendpreference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_already_invite);
		userPreference = BaseApplication.getInstance().getUserPreference();
		friendpreference = BaseApplication.getInstance().getFriendPreference();

		findViewById();
		initView();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		topNavLeftBtn = (ImageView) findViewById(R.id.nav_left_btn);
		right_btn_bg = (View) findViewById(R.id.right_btn_bg);
		topNavText = (TextView) findViewById(R.id.nav_text);
		mPhoneView = (EditText) findViewById(R.id.phone);
		submit = (Button) findViewById(R.id.submit);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		right_btn_bg.setVisibility(View.GONE);
		topNavText.setText("�ȴ�ƥ��");
		topNavLeftBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			}
		});

		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				attemptPhone();
			}
		});
	}

	private void attemptPhone() {
		// ���ô���
		mPhoneView.setError(null);

		// �洢�û�ֵ
		mPhone = mPhoneView.getText().toString();

		// ����ֻ���
		if (TextUtils.isEmpty(mPhone)) {
			mPhoneView.setError(getString(R.string.error_field_required));
			focusView = mPhoneView;
			cancel = true;
		} else if (!CommonTools.isMobileNO(mPhone)) {
			mPhoneView.setError(getString(R.string.error_phone));
			focusView = mPhoneView;
			cancel = true;
		} else {
			//����Ƿ�����
			RequestParams params = new RequestParams();
			params.put(LoveRequestTable.LR_USERID, userPreference.getU_id());
			params.put(UserTable.U_TEL, mPhone);
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
					if (response.equals("0")) {
						ToastTool.showLong(AlreadyInviteActivity.this, "��û�б����룡");
					} else if (response.equals("-1")) {
						ToastTool.showLong(AlreadyInviteActivity.this, "û�в��ҵ����û���");
					} else {
						JsonUser jsonUser = FastJsonTool.getObject(response, JsonUser.class);
						saveLoverInfo(jsonUser);
						//�����Ի�
						if (jsonUser != null) {
							ConversationDbService conversationDbService = ConversationDbService
									.getInstance(AlreadyInviteActivity.this);
							conversationDbService.conversationDao.deleteAll();
							Conversation conversation = new Conversation(null, (long) jsonUser.getU_id(),
									friendpreference.getName(), jsonUser.getU_small_avatar(), "", 0,
									System.currentTimeMillis());
							conversationDbService.conversationDao.insert(conversation);

							Intent intent = new Intent(AlreadyInviteActivity.this, MainActivity.class);
							startActivity(intent);
							overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
						}
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
					// TODO Auto-generated method stub
					ToastTool.showLong(AlreadyInviteActivity.this, "����������");
				}

				@Override
				public void onFinish() {
					// TODO Auto-generated method stub
					dialog.dismiss();
					super.onFinish();
				}
			};
			AsyncHttpClientTool.post("receiveloverequest", params, responseHandler);
		}

		if (cancel) {
			// �����������ʾ����
			focusView.requestFocus();
		}
	}

	/**
	 * ����������Ϣ
	 */
	void saveLoverInfo(JsonUser jsonUser) {
		if (jsonUser != null) {
			userPreference.setU_stateid(2);
			friendpreference.setType(1);
			friendpreference.setBpush_ChannelID(jsonUser.getU_bpush_channel_id());
			friendpreference.setBpush_UserID(jsonUser.getU_bpush_user_id());
			friendpreference.setF_address(jsonUser.getU_address());
			friendpreference.setF_age(jsonUser.getU_age());
			friendpreference.setF_blood_type(jsonUser.getU_blood_type());
			friendpreference.setF_constell(jsonUser.getU_constell());
			friendpreference.setF_email(jsonUser.getU_email());
			friendpreference.setF_gender(jsonUser.getU_gender());
			friendpreference.setF_height(jsonUser.getU_height());
			friendpreference.setF_id(jsonUser.getU_id());
			friendpreference.setF_introduce(jsonUser.getU_introduce());
			friendpreference.setF_large_avatar(jsonUser.getU_large_avatar());
			friendpreference.setF_nickname(jsonUser.getU_nickname());
			friendpreference.setF_realname(jsonUser.getU_realname());
			friendpreference.setF_salary(jsonUser.getU_salary());
			friendpreference.setF_small_avatar(jsonUser.getU_small_avatar());
			friendpreference.setF_stateid(jsonUser.getU_stateid());
			friendpreference.setF_tel(jsonUser.getU_tel());
			friendpreference.setF_vocationid(jsonUser.getU_vocationid());
			friendpreference.setF_weight(jsonUser.getU_weight());
			friendpreference.setU_cityid(jsonUser.getU_cityid());
			friendpreference.setU_provinceid(jsonUser.getU_provinceid());
			friendpreference.setU_schoolid(jsonUser.getU_schoolid());
			friendpreference.setVertify(jsonUser.getU_vertify_image_pass());
		} else {
			LogTool.e("��ȡ������Ϣʧ�ܣ�JsonUser����Ϊ�գ�");
		}
	}

}
