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
import com.yixianqian.base.BaseActivity;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.db.ConversationDbService;
import com.yixianqian.db.ProvinceDbService;
import com.yixianqian.db.SchoolDbService;
import com.yixianqian.entities.Conversation;
import com.yixianqian.jsonobject.JsonUser;
import com.yixianqian.table.LoveRequestTable;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.AsyncHttpClientImageSound;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.FastJsonTool;
import com.yixianqian.utils.FriendPreference;
import com.yixianqian.utils.ImageLoaderTool;
import com.yixianqian.utils.LogTool;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

public class VertifyToChatActivity extends BaseActivity {
	public static final String VERTIFY_TYPE = "type";
	public static final String PHONE = "phone";
	private ImageView topNavLeftBtn;//导航条左边按钮
	private TextView topNavText;//导航条文字
	private View right_btn_bg;
	private ImageView headImageView;//头像
	private TextView nameTextView;//姓名
	private TextView provinceTextView;//省份
	private TextView schoolTextView;//学校
	private FriendPreference friendpreference;
	private UserPreference userPreference;
	private Button beginChatBtn;
	private Button refuseBtn;
	private int type = -1;
	private JsonUser jsonUser;
	private String mPhone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_vertify_to_chat);

		friendpreference = BaseApplication.getInstance().getFriendPreference();
		userPreference = BaseApplication.getInstance().getUserPreference();
		String typeString = getIntent().getStringExtra(VERTIFY_TYPE);
		if (!TextUtils.isEmpty(typeString)) {
			type = Integer.parseInt(typeString);
			mPhone = getIntent().getStringExtra(PHONE);
			if (mPhone != null) {
				getLoverInfo(mPhone);
			}
		}

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
		refuseBtn = (Button) findViewById(R.id.refuse);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		right_btn_bg.setVisibility(View.GONE);
		if (type == 0) {
			topNavText.setText("心动请求");
			refuseBtn.setVisibility(View.GONE);
		} else if (type == 1) {
			topNavText.setText("爱情请求");
			refuseBtn.setText("拒    绝");
		}
		beginChatBtn.setText("确    定");

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
				if (type == 1) {
					checkLoverRequest();
				} else if (type == 0 && jsonUser != null) {
					saveLoverInfo(jsonUser);
					vertifyToChat();
				}

			}
		});

		refuseBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				refuse();
				friendpreference.clear();
			}
		});
	}

	/**
	 * 确认聊天
	 */
	private void vertifyToChat() {
		if (type == 0) {
			friendpreference.setType(0);
			userPreference.setU_stateid(3);
		} else if (type == 1) {
			friendpreference.setType(1);
			userPreference.setU_stateid(2);
		}

		ConversationDbService conversationDbService = ConversationDbService.getInstance(VertifyToChatActivity.this);
		conversationDbService.conversationDao.deleteAll();
		Conversation conversation = new Conversation(null, Long.valueOf(friendpreference.getF_id()),
				friendpreference.getName(), friendpreference.getF_small_avatar(), "", 0, System.currentTimeMillis());
		conversationDbService.conversationDao.insert(conversation);

		Intent intent = new Intent(VertifyToChatActivity.this, ChatActivity.class);
		intent.putExtra("userId", "" + friendpreference.getF_id());
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		finish();
	}

	/**
	 * 拒绝
	 */
	private void refuse() {
		if (type == 0) {
		} else if (type == 1) {
			RequestParams params = new RequestParams();
			params.put(LoveRequestTable.LR_USERID, userPreference.getU_id());
			params.put(UserTable.U_TEL, mPhone);
			TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
				Dialog dialog;

				@Override
				public void onStart() {
					// TODO Auto-generated method stub
					super.onStart();
					dialog = showProgressDialog("请稍后...");
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, String response) {
					// TODO Auto-generated method stub
					if (response.equals("1")) {
						startActivity(new Intent(VertifyToChatActivity.this, MainActivity.class));
						overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
					// TODO Auto-generated method stub
					ToastTool.showLong(VertifyToChatActivity.this, "服务器错误");
				}

				@Override
				public void onFinish() {
					// TODO Auto-generated method stub
					dialog.dismiss();
					super.onFinish();
				}
			};
			AsyncHttpClientTool.post("refuseloverequest", params, responseHandler);
		}
	}

	/**
	 * 检查是否被邀请
	 */
	private void checkLoverRequest() {
		//检查是否被邀请
		RequestParams params = new RequestParams();
		params.put(LoveRequestTable.LR_USERID, userPreference.getU_id());
		params.put(UserTable.U_TEL, mPhone);
		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
			Dialog dialog;

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
				dialog = showProgressDialog("请稍后...");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				if (response.equals("0")) {
					ToastTool.showLong(VertifyToChatActivity.this, "您没有被邀请！");
				} else {
					JsonUser jsonUser = FastJsonTool.getObject(response, JsonUser.class);
					saveLoverInfo(jsonUser);
					//创建对话
					if (jsonUser != null) {
						vertifyToChat();
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				ToastTool.showLong(VertifyToChatActivity.this, "服务器错误");
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

	/**
	 * 保存情侣信息
	 */
	void saveLoverInfo(JsonUser jsonUser) {
		if (jsonUser != null) {
			//			userPreference.setU_stateid(2);
			//			friendpreference.setType(1);
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
		} else {
			LogTool.e("获取情侣信息失败，JsonUser解析为空！");
		}
	}

	/**
	 * 获取情侣信息
	 */
	private void getLoverInfo(String phone) {
		if (!TextUtils.isEmpty(phone)) {
			RequestParams params = new RequestParams();
			params.put(UserTable.U_TEL, phone);
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
			AsyncHttpClientTool.post(VertifyToChatActivity.this, url, params, responseHandler);
		}
	}

	/**
	 * 初始化信息
	 */
	private void initLoverView() {
		//设置头像
		if (!TextUtils.isEmpty(jsonUser.getU_small_avatar())) {
			imageLoader.displayImage(AsyncHttpClientImageSound.getAbsoluteUrl(jsonUser.getU_small_avatar()),
					headImageView, ImageLoaderTool.getHeadImageOptions(10));
			//点击显示高清头像
			headImageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(VertifyToChatActivity.this, ImageShowerActivity.class);
					intent.putExtra(ImageShowerActivity.SHOW_BIG_IMAGE,
							AsyncHttpClientImageSound.getAbsoluteUrl(jsonUser.getU_large_avatar()));
					startActivity(intent);
					VertifyToChatActivity.this.overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
				}
			});
		}
		//设置姓名、省份、及学校
		//优先显示真实姓名
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

}
