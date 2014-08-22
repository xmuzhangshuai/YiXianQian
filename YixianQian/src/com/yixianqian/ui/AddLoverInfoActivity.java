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

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.baidupush.SendMsgAsyncTask;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.config.Constants;
import com.yixianqian.db.ProvinceDbService;
import com.yixianqian.db.SchoolDbService;
import com.yixianqian.jsonobject.JsonMessage;
import com.yixianqian.jsonobject.JsonUser;
import com.yixianqian.table.LoveRequestTable;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.AsyncHttpClientImageSound;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.FastJsonTool;
import com.yixianqian.utils.FriendPreference;
import com.yixianqian.utils.ImageLoaderTool;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

/**
 * �����ƣ�AddLoverInfoActivity
 * ��������ͨ�������ֻ��Ż���ɨ�������������Ϣ�������������ҳ��
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��7��24�� ����10:50:39
 *
 */
public class AddLoverInfoActivity extends BaseActivity {
	public static final String LOVER_PHONE_KEY = "lover_phone";
	private ImageView topNavLeftBtn;//��������߰�ť
	private TextView topNavText;//����������
	private View right_btn_bg;
	private ImageView headImageView;//ͷ��
	private TextView nameTextView;//����
	private TextView provinceTextView;//ʡ��
	private TextView schoolTextView;//ѧУ
	private FriendPreference friendpreference;
	private UserPreference userPreference;
	private String loverphone;
	private Button addLoverBtn;
	private JsonUser jsonUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_add_lover_info);

		friendpreference = BaseApplication.getInstance().getFriendPreference();
		userPreference = BaseApplication.getInstance().getUserPreference();
		loverphone = getIntent().getStringExtra(LOVER_PHONE_KEY);
		jsonUser = new JsonUser();

		getLoverInfo();
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
		addLoverBtn = (Button) findViewById(R.id.addlover);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		right_btn_bg.setVisibility(View.GONE);
		topNavText.setText("�������");
		topNavLeftBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		addLoverBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addLoverRequest();
			}
		});
	}

	private void initLoverView() {
		//����ͷ��
		if (!TextUtils.isEmpty(jsonUser.getU_small_avatar())) {
			imageLoader.displayImage(AsyncHttpClientImageSound.getAbsoluteUrl(jsonUser.getU_small_avatar()),
					headImageView, ImageLoaderTool.getHeadImageOptions(10));
			//�����ʾ����ͷ��
			headImageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(AddLoverInfoActivity.this, ImageShowerActivity.class);
					intent.putExtra(ImageShowerActivity.SHOW_BIG_IMAGE,
							AsyncHttpClientImageSound.getAbsoluteUrl(jsonUser.getU_large_avatar()));
					startActivity(intent);
					AddLoverInfoActivity.this.overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
				}
			});
		}
		//����������ʡ�ݡ���ѧУ
		//������ʾ��ʵ����
		String name = jsonUser.getU_nickname();
		if (jsonUser.getU_realname() != null) {
			if (jsonUser.getU_realname().length() > 0) {
				name = jsonUser.getU_realname();
			}
		}
		nameTextView.setText(name);
		ProvinceDbService provinceDbService = ProvinceDbService.getInstance(this);
		SchoolDbService schoolDbService = SchoolDbService.getInstance(this);
		provinceTextView.setText(provinceDbService.getProNameById(jsonUser.getU_provinceid()));
		schoolTextView.setText(schoolDbService.schoolDao.load((long) jsonUser.getU_schoolid()).getSchoolName());
	}

	/**
	 * ���������������
	 */
	private void addLoverRequest() {
		if (jsonUser.getU_id() > 0) {
			RequestParams params = new RequestParams();
			params.put(LoveRequestTable.LR_USERID, userPreference.getU_id());
			params.put(LoveRequestTable.LR_LOVERID, jsonUser.getU_id());
			TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
				Dialog dialog;

				@Override
				public void onStart() {
					// TODO Auto-generated method stub
					super.onStart();
					addLoverBtn.setEnabled(false);
					dialog = showProgressDialog("���Ժ�...");
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, String response) {
					// TODO Auto-generated method stub
					if (statusCode == 200) {
						if (!TextUtils.isEmpty(response)) {
							if (response.equals("0")) {
								addLoverBtn.setEnabled(true);
								ToastTool.showLong(AddLoverInfoActivity.this, "���ʧ�ܣ�");
								finish();
							} else if (response.equals("�ظ�")) {
								ToastTool.showLong(AddLoverInfoActivity.this, "���Ѿ������ta�ˣ�");
								finish();
							} else if (response.equals("״̬")) {
								ToastTool.showLong(AddLoverInfoActivity.this, "ͬʱ�ǵ���������˲��ܳ�Ϊ����Ŷ��");
								finish();
							} else {
								//���Է�������Ϣ
								JsonMessage jsonMessage = new JsonMessage(userPreference.getU_tel(),
										Constants.MessageType.MESSAGE_TYPE_LOVE_REQUEST);
								new SendMsgAsyncTask(FastJsonTool.createJsonString(jsonMessage),
										jsonUser.getU_bpush_user_id()).send();
								userPreference.setLoveRequest(true);
								saveLoverInfo();

								//								try {
								//									EMContactManager.getInstance().addContact(""+jsonUser.getU_id(), "�Ӻ���");
								//								} catch (EaseMobException e) {
								//									// TODO Auto-generated catch block
								//									e.printStackTrace();
								//								}

								Intent intent = new Intent(AddLoverInfoActivity.this, WaitActivity.class);
								startActivity(intent);
								overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
								finish();
							}
						}
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
					// TODO Auto-generated method stub
					addLoverBtn.setEnabled(true);
					ToastTool.showLong(AddLoverInfoActivity.this, "���ʧ�ܣ�" + errorResponse);
				}

				@Override
				public void onFinish() {
					// TODO Auto-generated method stub
					dialog.dismiss();
					super.onFinish();
				}
			};
			AsyncHttpClientTool.post(AddLoverInfoActivity.this, "addloverequest", params, responseHandler);
		}
	}

	/**
	 * ��ȡ������Ϣ
	 */
	private void getLoverInfo() {
		if (!TextUtils.isEmpty(loverphone)) {
			RequestParams params = new RequestParams();
			params.put(UserTable.U_TEL, loverphone);
			String url = "getuserbytel";
			TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, Header[] headers, String response) {
					// TODO Auto-generated method stub
					if (statusCode == 200) {
						jsonUser = FastJsonTool.getObject(response, JsonUser.class);
						if (jsonUser != null) {
							initLoverView();
						}
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
					// TODO Auto-generated method stub
				}
			};
			AsyncHttpClientTool.post(AddLoverInfoActivity.this, url, params, responseHandler);
		}
	}

	/**
	 * ����������Ϣ
	 */
	void saveLoverInfo() {
		if (jsonUser != null) {
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
		}
	}
}
