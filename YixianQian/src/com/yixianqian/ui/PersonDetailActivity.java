package com.yixianqian.ui;

import org.apache.http.Header;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.base.BaseFragmentActivity;
import com.yixianqian.customewidget.MyAlertDialog;
import com.yixianqian.db.ConversationDbService;
import com.yixianqian.db.ProvinceDbService;
import com.yixianqian.db.SchoolDbService;
import com.yixianqian.entities.Conversation;
import com.yixianqian.jsonobject.JsonUser;
import com.yixianqian.table.FlipperRequestTable;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.AsyncHttpClientImageSound;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.FastJsonTool;
import com.yixianqian.utils.FriendPreference;
import com.yixianqian.utils.ImageLoaderTool;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

/**
 * �����ƣ�PersonDetailActivity
 * ��������������ϸ����ҳ��
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��8��5�� ����3:45:37
 *
 */
public class PersonDetailActivity extends BaseFragmentActivity implements OnClickListener {
	public static final String PERSON_TYPE = "person_type";
	private ImageView topNavLeftBtn;//��������߰�ť
	private TextView topNavText;//����������
	private View right_btn_bg;
	private ImageView topNavRightBtn;
	private TextView timeCapsule;
	private TextView sendMsg;
	private ImageView headImageView;//ͷ��
	private TextView nameTextView;//����
	private TextView provinceTextView;//ʡ��
	private TextView schoolTextView;//ѧУ
	private TextView genderView;//�Ա�
	private FriendPreference friendPreference;
	private UserPreference userPreference;
	private int userId;
	private int type;//�������ͣ�1Ϊİ���ˣ�2Ϊ�Ķ���ϵ��3Ϊ����
	private JsonUser jsonUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_person_detail);

		friendPreference = BaseApplication.getInstance().getFriendPreference();
		userPreference = BaseApplication.getInstance().getUserPreference();
		userId = getIntent().getIntExtra(UserTable.U_ID, -1);
		type = getIntent().getIntExtra(PERSON_TYPE, -1);
		findViewById();
		initView();

		if (type == 1 && userId > 0) {
			getUser();
		} else if (type == 2 || type == 3) {
			initPersonView();
		}
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		topNavLeftBtn = (ImageView) findViewById(R.id.nav_left_btn);
		right_btn_bg = (View) findViewById(R.id.right_btn_bg);
		topNavText = (TextView) findViewById(R.id.nav_text);
		topNavRightBtn = (ImageView) findViewById(R.id.nav_right_btn);
		timeCapsule = (TextView) findViewById(R.id.time_capsule);
		sendMsg = (TextView) findViewById(R.id.send_msg);
		headImageView = (ImageView) findViewById(R.id.head_image);
		nameTextView = (TextView) findViewById(R.id.name);
		provinceTextView = (TextView) findViewById(R.id.province);
		schoolTextView = (TextView) findViewById(R.id.school);
		genderView = (TextView) findViewById(R.id.gender);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavRightBtn.setImageResource(R.drawable.ic_action_overflow);
		right_btn_bg.setBackgroundResource(R.drawable.sel_topnav_btn_bg);
		topNavText.setText("��������");
		topNavLeftBtn.setOnClickListener(this);
		right_btn_bg.setOnClickListener(this);
		sendMsg.setOnClickListener(this);
		if (type == 1 || type == 2) {
			timeCapsule.setOnClickListener(this);
			timeCapsule.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * ��ʼ��������Ϣ
	 */
	private void initPersonView() {
		if (type == 1) {
			//����ͷ��
			if (!TextUtils.isEmpty(jsonUser.getU_small_avatar())) {
				imageLoader.displayImage(AsyncHttpClientImageSound.getAbsoluteUrl(jsonUser.getU_small_avatar()),
						headImageView, ImageLoaderTool.getHeadImageOptions(10));
				//�����ʾ����ͷ��
				headImageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(PersonDetailActivity.this, ImageShowerActivity.class);
						intent.putExtra(ImageShowerActivity.SHOW_BIG_IMAGE,
								AsyncHttpClientImageSound.getAbsoluteUrl(jsonUser.getU_large_avatar()));
						startActivity(intent);
						PersonDetailActivity.this.overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
					}
				});
			}
			//����������ʡ�ݡ���ѧУ
			//������ʾ��ʵ����

			nameTextView.setText(getUserName(jsonUser));
			ProvinceDbService provinceDbService = ProvinceDbService.getInstance(this);
			SchoolDbService schoolDbService = SchoolDbService.getInstance(this);
			provinceTextView.setText(provinceDbService.getProNameById(jsonUser.getU_provinceid()));
			schoolTextView.setText(schoolDbService.schoolDao.load((long) jsonUser.getU_schoolid()).getSchoolName());
			genderView.setText(jsonUser.getU_gender());

		} else if (type == 2 || type == 3) {
			//����������ʡ�ݡ���ѧУ
			//������ʾ��ʵ����
			nameTextView.setText(friendPreference.getName());
			provinceTextView.setText(friendPreference.getProvinceName());
			schoolTextView.setText(friendPreference.getSchoolName());
			//����ͷ��
			if (!TextUtils.isEmpty(friendPreference.getF_small_avatar())) {
				imageLoader.displayImage(
						AsyncHttpClientImageSound.getAbsoluteUrl(friendPreference.getF_small_avatar()), headImageView,
						ImageLoaderTool.getHeadImageOptions(10));
				//�����ʾ����ͷ��
				headImageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(PersonDetailActivity.this, ImageShowerActivity.class);
						intent.putExtra(ImageShowerActivity.SHOW_BIG_IMAGE,
								AsyncHttpClientImageSound.getAbsoluteUrl(friendPreference.getF_large_avatar()));
						startActivity(intent);
						PersonDetailActivity.this.overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
					}
				});
			}
			genderView.setText(friendPreference.getF_gender());
		}
	}

	/**
	 * 	�����ȡUser��Ϣ
	 */
	private void getUser() {
		RequestParams params = new RequestParams();
		params.put(UserTable.U_ID, userId);

		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {
			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					jsonUser = FastJsonTool.getObject(response, JsonUser.class);
					if (jsonUser != null) {
						initPersonView();
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				ToastTool.showLong(PersonDetailActivity.this, "����������");
			}
		};
		AsyncHttpClientTool.post(PersonDetailActivity.this, "getuserbyid", params, responseHandler);
	}

	/**
	 * �˵���ʾ
	 */
	void showDialog() {

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Fragment fragment = getSupportFragmentManager().findFragmentByTag("persondetail");
		if (fragment != null) {
			ft.remove(fragment);
		}
		ft.addToBackStack(null);

		// Create and show the dialog.
		PersonDetailDialog newFragment = PersonDetailDialog.newInstance();
		Bundle bundle = new Bundle();
		bundle.putInt(PERSON_TYPE, type);
		if (type == 1) {
			bundle.putInt(UserTable.U_ID, jsonUser.getU_id());
		} 
		newFragment.setArguments(bundle);
		newFragment.show(ft, "persondetail");
	}

	/**
	 * ���ȷ�������
	 * @return
	 */
	private String getUserName(JsonUser user) {
		if (user != null) {
			String name = user.getU_nickname();
			if (user.getU_realname() != null) {
				if (user.getU_realname().length() > 0) {
					name = user.getU_realname();
				}
			}
			return name;
		}
		return "";
	}

	/**
	 * ����Ϣ��ʾ��
	 */
	void showSendMsgDialog() {
		String msgString = "";
		if (type == 1) {
			msgString = "���� " + getUserName(jsonUser) + " �������Ķ���ϵ����������Ŷ~��";
			final MyAlertDialog myAlertDialog = new MyAlertDialog(this);
			myAlertDialog.setTitle("��ʾ");
			myAlertDialog.setMessage(msgString);
			View.OnClickListener comfirm = new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					myAlertDialog.dismiss();
					sendLoveReuest(jsonUser.getU_id());
				}
			};
			View.OnClickListener cancle = new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					myAlertDialog.dismiss();
				}
			};
			myAlertDialog.setPositiveButton("�Ķ�", comfirm);
			myAlertDialog.setNegativeButton("����", cancle);
			myAlertDialog.show();
		} else if (type == 2 || type == 3) {
			Conversation conversation = ConversationDbService.getInstance(BaseApplication.getInstance())
					.getConversationByUser(friendPreference.getF_id());
			Intent intent = new Intent(PersonDetailActivity.this, ChatActivity2.class);
			intent.putExtra("conversationID", conversation.getId());
			startActivity(intent);
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		}

	}

	/**
	 * �첽���Ͱ�����֤
	 * @param userID
	 */
	private void sendLoveReuest(int filpperId) {
		if (filpperId > 0) {
			String url = "addflipperrequest";
			RequestParams params = new RequestParams();
			int myUserID = userPreference.getU_id();
			params.put(FlipperRequestTable.FR_USERID, myUserID);
			params.put(FlipperRequestTable.FR_FLIPPERID, filpperId);
			TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, Header[] headers, String response) {
					// TODO Auto-generated method stub
					ToastTool.showLong(PersonDetailActivity.this, "������֤�ѷ��ͣ�");
					PersonDetailActivity.this.startActivity(new Intent(PersonDetailActivity.this, MainActivity.class));
					PersonDetailActivity.this.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
					PersonDetailActivity.this.finish();
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
					// TODO Auto-generated method stub
					ToastTool.showLong(PersonDetailActivity.this, errorResponse);
				}
			};
			AsyncHttpClientTool.post(PersonDetailActivity.this, url, params, responseHandler);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.nav_left_btn:
			finish();
			break;
		case R.id.right_btn_bg:
			showDialog();
			break;
		case R.id.time_capsule:
			Intent intent = new Intent(PersonDetailActivity.this, PersonTimeCapsuleActivity.class);
			intent.putExtra("user", jsonUser);
			intent.putExtra(PERSON_TYPE, type);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.send_msg:
			showSendMsgDialog();
			break;
		default:
			break;
		}
	}

}
