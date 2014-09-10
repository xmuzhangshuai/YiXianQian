package com.yixianqian.ui;

import java.util.Date;

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

import com.easemob.chat.EMContactManager;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.baidupush.SendMsgAsyncTask;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.base.BaseFragmentActivity;
import com.yixianqian.config.Constants;
import com.yixianqian.config.Constants.FlipperStatus;
import com.yixianqian.config.Constants.FlipperType;
import com.yixianqian.config.Constants.MessageType;
import com.yixianqian.customewidget.MyAlertDialog;
import com.yixianqian.db.FlipperDbService;
import com.yixianqian.db.SchoolDbService;
import com.yixianqian.entities.Flipper;
import com.yixianqian.jsonobject.JsonMessage;
import com.yixianqian.jsonobject.JsonUser;
import com.yixianqian.table.FlipperRequestTable;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.AsyncHttpClientImageSound;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.FastJsonTool;
import com.yixianqian.utils.FriendPreference;
import com.yixianqian.utils.ImageLoaderTool;
import com.yixianqian.utils.LogTool;
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
	private View topNavLeftBtn;//��������߰�ť
	private TextView topNavText;//����������
	private View right_btn_bg;
	private ImageView topNavRightBtn;
	private TextView timeCapsule;
	private TextView sendMsg;
	private ImageView headImageView;//ͷ��
	private TextView nameTextView;//����
	private TextView schoolTextView;//ѧУ
	private ImageView genderView;//�Ա�
	private View moreDetaileBtn;//��ϸ���ϰ�ť
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

		if (type == Constants.PersonDetailType.SINGLE && userId > 0) {
			getUser();
		} else if (type == Constants.PersonDetailType.LOVER || type == Constants.PersonDetailType.FLIPPER) {
			initPersonView();
		}
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		topNavLeftBtn = findViewById(R.id.left_btn_bg);
		right_btn_bg = (View) findViewById(R.id.right_btn_bg);
		topNavText = (TextView) findViewById(R.id.nav_text);
		topNavRightBtn = (ImageView) findViewById(R.id.nav_right_btn);
		timeCapsule = (TextView) findViewById(R.id.time_capsule);
		sendMsg = (TextView) findViewById(R.id.send_msg);
		headImageView = (ImageView) findViewById(R.id.head_image);
		nameTextView = (TextView) findViewById(R.id.name);
		schoolTextView = (TextView) findViewById(R.id.school);
		genderView = (ImageView) findViewById(R.id.gender);
		moreDetaileBtn = findViewById(R.id.detail);
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
		moreDetaileBtn.setOnClickListener(this);
		if (type == Constants.PersonDetailType.SINGLE || type == Constants.PersonDetailType.FLIPPER) {
			timeCapsule.setOnClickListener(this);
			timeCapsule.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * ��ʼ��������Ϣ
	 */
	private void initPersonView() {
		if (type == Constants.PersonDetailType.SINGLE) {
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
			SchoolDbService schoolDbService = SchoolDbService.getInstance(this);
			schoolTextView.setText(schoolDbService.schoolDao.load((long) jsonUser.getU_schoolid()).getSchoolName());
			if (jsonUser.getU_gender().equals(Constants.Gender.MALE)) {
				genderView.setImageResource(R.drawable.male);
			} else {
				genderView.setImageResource(R.drawable.female);
			}

		} else if (type == Constants.PersonDetailType.LOVER || type == Constants.PersonDetailType.FLIPPER) {
			//����������ʡ�ݡ���ѧУ
			//������ʾ��ʵ����
			nameTextView.setText(friendPreference.getName());
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
			if (friendPreference.getF_gender().equals(Constants.Gender.MALE)) {
				genderView.setImageResource(R.drawable.male);
			} else {
				genderView.setImageResource(R.drawable.female);
			}
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
	 * ���û��ͨ����֤���������ʾ
	 */
	private void showVertifyDialog() {
		final MyAlertDialog myAlertDialog = new MyAlertDialog(PersonDetailActivity.this);
		myAlertDialog.setShowTitle(false);
		final int state = userPreference.getVertify();
		if (state == Constants.VertifyState.NOTSUBMIT) {
			myAlertDialog.setMessage("��Ŷ...�����ÿһ���˶���ѧ��Ŷ~\n\n����û�н���ѧ����֤���޷�ʹ�ø÷���");
		} else if (state == Constants.VertifyState.NOTPASSED) {
			myAlertDialog.setMessage("��Ŷ...�����ÿһ���˶���ѧ��Ŷ~\n\n����ѧ����֤δͨ�����޷�ʹ�ø÷���");
		} else if (state == Constants.VertifyState.VERTIFING) {
			myAlertDialog.setMessage("��Ŷ...�����ÿһ���˶���ѧ��Ŷ~\n\n������֤��������У���ʱ�޷�ʹ�ø÷���");
		}

		View.OnClickListener comfirm = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myAlertDialog.dismiss();
				if (!(state == Constants.VertifyState.VERTIFING)) {
					startActivity(new Intent(PersonDetailActivity.this, ApplyVertifyActivity.class));
					overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
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
		if (state == Constants.VertifyState.NOTSUBMIT) {
			myAlertDialog.setPositiveButton("ȥ��֤", comfirm);
			myAlertDialog.setNegativeButton("����֤", cancle);
		} else if (state == Constants.VertifyState.NOTPASSED) {
			myAlertDialog.setPositiveButton("������֤", comfirm);
			myAlertDialog.setNegativeButton("����֤", cancle);
		} else if (state == Constants.VertifyState.VERTIFING) {
			myAlertDialog.setShowCancel(false);
			myAlertDialog.setPositiveButton("�ٵȵȰ�~", comfirm);
		}
		myAlertDialog.show();
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
		if (type == Constants.PersonDetailType.SINGLE) {
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
		if (type == Constants.PersonDetailType.SINGLE) {
			msgString = "���� " + getUserName(jsonUser) + " �������Ķ���ϵ����������Ŷ~��";
			final MyAlertDialog myAlertDialog = new MyAlertDialog(this);
			myAlertDialog.setTitle("��ʾ");
			myAlertDialog.setMessage(msgString);
			View.OnClickListener comfirm = new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					myAlertDialog.dismiss();
					if (userPreference.getVertify() == Constants.VertifyState.PASSED) {
						sendLoveReuest(jsonUser.getU_id());
					} else {
						showVertifyDialog();
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
			myAlertDialog.setPositiveButton("�Ķ�", comfirm);
			myAlertDialog.setNegativeButton("����", cancle);
			myAlertDialog.show();
		} else if (type == 2 || type == 3) {
			Intent intent = new Intent(PersonDetailActivity.this, ChatActivity.class);
			intent.putExtra("userId", "" + friendPreference.getF_id());
			startActivity(intent);
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		}
	}

	/**
	 * �첽���Ͱ�����֤
	 * @param userID
	 */
	private void sendLoveReuest(final int filpperId) {
		if (filpperId > 0) {
			FlipperDbService flipperDbService = FlipperDbService.getInstance(PersonDetailActivity.this);
			Flipper flipper = flipperDbService.getFlipperByUserId(filpperId);
			if (flipper != null && flipper.getStatus().equals(FlipperStatus.BEINVITEED)) {//����������
				LogTool.e("PersonDetailActivity", "�Ѿ����������");
				String name = flipper.getNickname();
				if (!TextUtils.isEmpty(flipper.getRealname())) {
					name = flipper.getRealname();
				}
				final MyAlertDialog myAlertDialog = new MyAlertDialog(this);
				myAlertDialog.setShowCancel(false);
				myAlertDialog.setTitle("��ʾ");
				myAlertDialog.setMessage("���Ѿ��� " + name + " ������������ڰ�����֤ҳ��������Ҳ������Ȼ�Ķ�������Ϊ�Ķ���ϵ");
				View.OnClickListener comfirm = new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						myAlertDialog.dismiss();
					}
				};
				myAlertDialog.setPositiveButton("ȷ��", comfirm);
				myAlertDialog.show();
			} else {
				LogTool.e("PersonDetailActivity", "���Ͱ�����֤");
				String url = "addflipperrequest";
				RequestParams params = new RequestParams();
				int myUserID = userPreference.getU_id();
				params.put(FlipperRequestTable.FR_USERID, myUserID);
				params.put(FlipperRequestTable.FR_FLIPPERID, filpperId);
				TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
						// TODO Auto-generated method stub
						LogTool.e("DayRecommendActivity", "����ԭ��" + errorResponse);
						PersonDetailActivity.this.finish();
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers, String response) {
						// TODO Auto-generated method stub
						ToastTool.showLong(getApplicationContext(), "������֤�ѷ��ͣ��ȴ��Է�ͬ��");
						saveFlipper(filpperId, response);
						PersonDetailActivity.this.finish();
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						super.onFinish();
					}
				};
				AsyncHttpClientTool.post(PersonDetailActivity.this, url, params, responseHandler);
			}
		}
	}

	/**
	 *  ���contact
	 * @param view
	 */
	public void addContact(final int flipperId) {
		LogTool.i("PersonDetailActivity", "������Ӻ���");
		new Thread(new Runnable() {
			public void run() {
				try {
					//��Ӻ���
					EMContactManager.getInstance().addContact("" + flipperId, userPreference.getName() + "������Ȼ�Ķ���");
				} catch (final Exception e) {
				}
			}
		}).start();
	}

	/**
	 * �洢�����ݿ⣬�Ѿ�ͬ��
	 */
	public void saveFlipper(final int flipperId, String response) {
		FlipperDbService flipperDbService = FlipperDbService.getInstance(PersonDetailActivity.this);
		Flipper flipper = flipperDbService.getFlipperByUserId(flipperId);
		//������ݿ��д��ڸ��û������������״̬
		if (flipper != null) {
			if (!flipper.getStatus().equals(FlipperStatus.INVITE)) {//����Ѿ����������������
				addContact(flipperId);
			}

			LogTool.i("PersonDetailActivity", "flipper�Ѿ����ڣ�����");
			flipper.setIsRead(true);
			flipper.setTime(new Date());
			flipper.setType(FlipperType.TO);
			flipper.setStatus(Constants.FlipperStatus.INVITE);
			flipperDbService.flipperDao.update(flipper);

			//�����Է�֪ͨ
			JsonMessage jsonMessage = new JsonMessage(userPreference.getName() + "������Ȼ�Ķ�",
					MessageType.MESSAGE_TYPE_FLIPPER_REQUEEST);
			new SendMsgAsyncTask(FastJsonTool.createJsonString(jsonMessage), flipper.getBpushUserID()).send();
		} else {
			//�������˲���δ����״̬������ӻ��ź���
			if (response.equals("1")) {
				addContact(flipperId);
			}
			getUser(flipperId);
		}
	}

	/**
	 * 	�����ȡUser��Ϣ
	 */
	private void getUser(int userId) {
		RequestParams params = new RequestParams();
		params.put(UserTable.U_ID, userId);

		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {
			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					jsonUser = FastJsonTool.getObject(response, JsonUser.class);
					if (jsonUser != null) {
						FlipperDbService flipperDbService = FlipperDbService.getInstance(PersonDetailActivity.this);
						Flipper flipper = new Flipper(null, jsonUser.getU_id(), jsonUser.getU_bpush_user_id(),
								jsonUser.getU_bpush_channel_id(), jsonUser.getU_nickname(), jsonUser.getU_realname(),
								jsonUser.getU_gender(), jsonUser.getU_email(), jsonUser.getU_large_avatar(),
								jsonUser.getU_small_avatar(), jsonUser.getU_blood_type(), jsonUser.getU_constell(),
								jsonUser.getU_introduce(), jsonUser.getU_birthday(), new Date(), jsonUser.getU_age(),
								jsonUser.getU_vocationid(), jsonUser.getU_stateid(), jsonUser.getU_provinceid(),
								jsonUser.getU_cityid(), jsonUser.getU_schoolid(), jsonUser.getU_height(),
								jsonUser.getU_weight(), jsonUser.getU_image_pass(), jsonUser.getU_salary(), true,
								jsonUser.getU_tel(), Constants.FlipperStatus.INVITE, Constants.FlipperType.TO);
						flipperDbService.flipperDao.insert(flipper);

						//�����Է�֪ͨ
						JsonMessage jsonMessage = new JsonMessage(userPreference.getName() + "������Ȼ�Ķ�",
								MessageType.MESSAGE_TYPE_FLIPPER_REQUEEST);
						new SendMsgAsyncTask(FastJsonTool.createJsonString(jsonMessage), flipper.getBpushUserID())
								.send();
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.left_btn_bg:
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
		case R.id.detail:
			if (type == Constants.PersonDetailType.SINGLE) {
				if (jsonUser != null) {
					startActivity(new Intent(PersonDetailActivity.this, PersonMoreDetailActivity.class).putExtra(
							"user", jsonUser));
					overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				}
			} else if (type == Constants.PersonDetailType.LOVER || type == Constants.PersonDetailType.FLIPPER) {
				startActivity(new Intent(PersonDetailActivity.this, PersonMoreDetailActivity.class).putExtra("user",
						friendPreference.getJsonUser()));
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
			break;
		default:
			break;
		}
	}

}
