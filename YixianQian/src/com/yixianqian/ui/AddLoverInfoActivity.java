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
import com.yixianqian.db.ConversationDbService;
import com.yixianqian.entities.Conversation;
import com.yixianqian.jsonobject.JsonMessage;
import com.yixianqian.jsonobject.JsonUser;
import com.yixianqian.table.LoversTable;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.AsyncHttpClientImageSound;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.FastJsonTool;
import com.yixianqian.utils.FriendPreference;
import com.yixianqian.utils.ImageLoaderTool;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

/**
 * 类名称：AddLoverInfoActivity
 * 类描述：通过输入手机号或者扫描出来的情侣信息，添加情侣资料页面
 * 创建人： 张帅
 * 创建时间：2014年7月24日 上午10:50:39
 *
 */
public class AddLoverInfoActivity extends BaseActivity {
	public static final String LOVER_PHONE_KEY = "lover_phone";
	private ImageView topNavLeftBtn;//导航条左边按钮
	private TextView topNavText;//导航条文字
	private View right_btn_bg;
	private ImageView headImageView;//头像
	private TextView nameTextView;//姓名
	private TextView provinceTextView;//省份
	private TextView schoolTextView;//学校
	private FriendPreference friendpreference;
	private UserPreference userPreference;
	private String loverphone;
	private Button addLoverBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_add_lover_info);

		friendpreference = BaseApplication.getInstance().getFriendPreference();
		userPreference = BaseApplication.getInstance().getUserPreference();
		loverphone = getIntent().getStringExtra(LOVER_PHONE_KEY);

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
		topNavText.setText("添加情侣");
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
				addLover();
			}
		});
	}

	private void initLoverView() {

		//设置头像
		if (!TextUtils.isEmpty(friendpreference.getF_small_avatar())) {
			imageLoader.displayImage(AsyncHttpClientImageSound.getAbsoluteUrl(friendpreference.getF_small_avatar()),
					headImageView, ImageLoaderTool.getHeadImageOptions(10));
			//点击显示高清头像
			headImageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(AddLoverInfoActivity.this, ImageShowerActivity.class);
					intent.putExtra(ImageShowerActivity.SHOW_BIG_IMAGE,
							AsyncHttpClientImageSound.getAbsoluteUrl(friendpreference.getF_large_avatar()));
					startActivity(intent);
					AddLoverInfoActivity.this.overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
				}
			});
		}
		//设置姓名、省份、及学校
		//优先显示真实姓名
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
	 * 添加情侣
	 */
	private void addLover() {

		if (friendpreference.getF_id() > 0) {
			RequestParams params = new RequestParams();
			params.put(LoversTable.L_USERID, userPreference.getU_id());
			params.put(LoversTable.L_LOVERID, friendpreference.getF_id());
			String url = "buildlover";
			TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
				Dialog dialog;

				@Override
				public void onStart() {
					// TODO Auto-generated method stub
					super.onStart();
					addLoverBtn.setEnabled(false);
					dialog = showProgressDialog("请稍后...");
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, String response) {
					// TODO Auto-generated method stub
					if (statusCode == 200) {
						if (!TextUtils.isEmpty(response)) {
							if (response.equals("0")) {
								addLoverBtn.setEnabled(true);
								ToastTool.showLong(AddLoverInfoActivity.this, "添加失败！");
							} else if (response.equals("重复")) {
								ToastTool.showLong(AddLoverInfoActivity.this, "你们已经是情侣啦！");
							} else {
								//给对方发送消息
								JsonMessage jsonMessage = new JsonMessage(userPreference.getU_tel(),
										Constants.MessageType.MESSAGE_TYPE_LOVER);
								new SendMsgAsyncTask(FastJsonTool.createJsonString(jsonMessage),
										friendpreference.getBpush_UserID()).send();
								//创建对话
								ConversationDbService conversationDbService = ConversationDbService
										.getInstance(AddLoverInfoActivity.this);
								conversationDbService.conversationDao.deleteAll();
								Conversation conversation = new Conversation(null, Long.valueOf(friendpreference
										.getF_id()), friendpreference.getName(), friendpreference.getF_small_avatar(),
										"0", 0, System.currentTimeMillis());
								conversationDbService.conversationDao.insert(conversation);

								friendpreference.setLoverId(Integer.parseInt(response));
								friendpreference.setType(1);
								Intent intent = new Intent(AddLoverInfoActivity.this, MainActivity.class);
								startActivity(intent);
								overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
							}
						}
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
					// TODO Auto-generated method stub
					addLoverBtn.setEnabled(true);
					ToastTool.showLong(AddLoverInfoActivity.this, "添加失败！" + errorResponse);
				}

				@Override
				public void onFinish() {
					// TODO Auto-generated method stub
					dialog.dismiss();
					super.onFinish();
				}
			};
			AsyncHttpClientTool.post(AddLoverInfoActivity.this, url, params, responseHandler);
		}
	}

	/**
	 * 获取情侣信息
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
						JsonUser lover = FastJsonTool.getObject(response, JsonUser.class);
						if (lover != null) {
							friendpreference.setBpush_ChannelID(lover.getU_bpush_channel_id());
							friendpreference.setBpush_UserID(lover.getU_bpush_user_id());
							friendpreference.setF_address(lover.getU_address());
							friendpreference.setF_age(lover.getU_age());
							friendpreference.setF_blood_type(lover.getU_blood_type());
							friendpreference.setF_constell(lover.getU_constell());
							friendpreference.setF_email(lover.getU_email());
							friendpreference.setF_gender(lover.getU_gender());
							friendpreference.setF_height(lover.getU_height());
							friendpreference.setF_id(lover.getU_id());
							friendpreference.setF_introduce(lover.getU_introduce());
							friendpreference.setF_large_avatar(lover.getU_large_avatar());
							friendpreference.setF_nickname(lover.getU_nickname());
							friendpreference.setF_realname(lover.getU_realname());
							friendpreference.setF_salary(lover.getU_salary());
							friendpreference.setF_small_avatar(lover.getU_small_avatar());
							friendpreference.setF_stateid(lover.getU_stateid());
							friendpreference.setF_tel(lover.getU_tel());
							friendpreference.setF_vocationid(lover.getU_vocationid());
							friendpreference.setF_weight(lover.getU_weight());
							friendpreference.setU_cityid(lover.getU_cityid());
							friendpreference.setU_provinceid(lover.getU_provinceid());
							friendpreference.setU_schoolid(lover.getU_schoolid());
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

}
