package com.yixianqian.ui;

import java.util.Date;

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

import com.easemob.chat.EMContactManager;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.baidupush.SendMsgAsyncTask;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.base.BaseApplication;
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
	private TextView schoolTextView;//学校
	private FriendPreference friendpreference;
	private UserPreference userPreference;
	private String loverphone;
	private Button addLoverBtn;
	private Button flipperBtn;
	private JsonUser jsonUser;
	private ImageView genderView;//性别
	private View moreDetaileBtn;//详细资料按钮

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
		schoolTextView = (TextView) findViewById(R.id.school);
		addLoverBtn = (Button) findViewById(R.id.addlover);
		genderView = (ImageView) findViewById(R.id.gender);
		moreDetaileBtn = findViewById(R.id.detail);
		flipperBtn = (Button) findViewById(R.id.flipper);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		right_btn_bg.setVisibility(View.GONE);
		topNavText.setText("添加情侣&心动");
		topNavLeftBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			}
		});

		addLoverBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addLoverRequest();
			}
		});

		moreDetaileBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (jsonUser != null) {
					startActivity(new Intent(AddLoverInfoActivity.this, PersonMoreDetailActivity.class).putExtra(
							"user", jsonUser));
					overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				}
			}
		});

		flipperBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (userPreference.getVertify() == Constants.VertifyState.PASSED) {
					sendLoveReuest(jsonUser.getU_id());
				} else {
					showVertifyDialog();
				}
			}
		});
	}

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
					Intent intent = new Intent(AddLoverInfoActivity.this, ImageShowerActivity.class);
					intent.putExtra(ImageShowerActivity.SHOW_BIG_IMAGE,
							AsyncHttpClientImageSound.getAbsoluteUrl(jsonUser.getU_large_avatar()));
					startActivity(intent);
					AddLoverInfoActivity.this.overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
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

		if (jsonUser.getU_gender().equals(Constants.Gender.MALE)) {
			genderView.setImageResource(R.drawable.male);
		} else {
			genderView.setImageResource(R.drawable.female);
		}

		SchoolDbService schoolDbService = SchoolDbService.getInstance(this);
		schoolTextView.setText(schoolDbService.schoolDao.load((long) jsonUser.getU_schoolid()).getSchoolName());
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	/**
	 * 如果没有通过认证，则进行提示
	 */
	private void showVertifyDialog() {
		final MyAlertDialog myAlertDialog = new MyAlertDialog(AddLoverInfoActivity.this);
		myAlertDialog.setShowTitle(false);
		final int state = userPreference.getVertify();
		if (state == Constants.VertifyState.NOTSUBMIT) {
			myAlertDialog.setMessage("啊哦...这里的每一个人都是学生哦~\n\n您还没有进行学生认证，无法使用该服务");
		} else if (state == Constants.VertifyState.NOTPASSED) {
			myAlertDialog.setMessage("啊哦...这里的每一个人都是学生哦~\n\n您的学生认证未通过，无法使用该服务");
		} else if (state == Constants.VertifyState.VERTIFING) {
			myAlertDialog.setMessage("啊哦...这里的每一个人都是学生哦~\n\n您的认证正在审核中，暂时无法使用该服务");
		}

		View.OnClickListener comfirm = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myAlertDialog.dismiss();
				if (!(state == Constants.VertifyState.VERTIFING)) {
					startActivity(new Intent(AddLoverInfoActivity.this, ApplyVertifyActivity.class));
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
			myAlertDialog.setPositiveButton("去认证", comfirm);
			myAlertDialog.setNegativeButton("暂时不认证", cancle);
		} else if (state == Constants.VertifyState.NOTPASSED) {
			myAlertDialog.setPositiveButton("重新认证", comfirm);
			myAlertDialog.setNegativeButton("暂时不认证", cancle);
		} else if (state == Constants.VertifyState.VERTIFING) {
			myAlertDialog.setShowCancel(false);
			myAlertDialog.setPositiveButton("再等等吧~", comfirm);
		}
		myAlertDialog.show();
	}

	/**
	 * 发起添加情侣请求
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
								finish();
							} else if (response.equals("重复")) {
								ToastTool.showLong(AddLoverInfoActivity.this, "您已经邀请过ta了！");
								finish();
							} else if (response.equals("状态")) {
								ToastTool.showLong(AddLoverInfoActivity.this, "同时是单身的两个人才能成为情侣哦！");
								finish();
							} else {
								//给对方发送消息
								JsonMessage jsonMessage = new JsonMessage(userPreference.getU_tel(),
										Constants.MessageType.MESSAGE_TYPE_LOVE_REQUEST);
								new SendMsgAsyncTask(FastJsonTool.createJsonString(jsonMessage),
										jsonUser.getU_bpush_user_id()).send();
								userPreference.setLoveRequest(true);
								saveLoverInfo();

								//								try {
								//									EMContactManager.getInstance().addContact(""+jsonUser.getU_id(), "加好友");
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
					ToastTool.showLong(AddLoverInfoActivity.this, "添加失败！" + errorResponse);
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
	 * 保存情侣信息
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

	/**
	 * 异步发送爱情验证
	 * @param userID
	 */
	private void sendLoveReuest(final int filpperId) {
		if (filpperId > 0) {
			FlipperDbService flipperDbService = FlipperDbService.getInstance(AddLoverInfoActivity.this);
			Flipper flipper = flipperDbService.getFlipperByUserId(filpperId);
			if (flipper != null && flipper.getStatus().equals(FlipperStatus.BEINVITEED)) {//如果被邀请过
				LogTool.e("PersonDetailActivity", "已经被邀请过了");
				String name = flipper.getNickname();
				if (!TextUtils.isEmpty(flipper.getRealname())) {
					name = flipper.getRealname();
				}
				final MyAlertDialog myAlertDialog = new MyAlertDialog(this);
				myAlertDialog.setShowCancel(false);
				myAlertDialog.setTitle("提示");
				myAlertDialog.setMessage("您已经被 " + name + " 邀请过，可以在爱情验证页面点击“我也对他砰然心动”来成为心动关系");
				View.OnClickListener comfirm = new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						myAlertDialog.dismiss();
					}
				};
				myAlertDialog.setPositiveButton("确定", comfirm);
				myAlertDialog.show();
			} else {
				LogTool.e("PersonDetailActivity", "发送爱情验证");
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
						LogTool.e("DayRecommendActivity", "错误原因" + errorResponse);
						AddLoverInfoActivity.this.finish();
						overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers, String response) {
						// TODO Auto-generated method stub
						ToastTool.showLong(getApplicationContext(), "爱情验证已发送！等待对方同意");
						saveFlipper(filpperId, response);
						AddLoverInfoActivity.this.finish();
						overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						super.onFinish();
					}
				};
				AsyncHttpClientTool.post(AddLoverInfoActivity.this, url, params, responseHandler);
			}
		}
	}

	/**
	 *  添加contact
	 * @param view
	 */
	public void addContact(final int flipperId) {
		LogTool.i("PersonDetailActivity", "环信添加好友");
		new Thread(new Runnable() {
			public void run() {
				try {
					//添加好友
					EMContactManager.getInstance().addContact("" + flipperId, userPreference.getName() + "对您砰然心动！");
				} catch (final Exception e) {
				}
			}
		}).start();
	}

	/**
	 * 存储到数据库，已经同意
	 */
	public void saveFlipper(final int flipperId, String response) {
		FlipperDbService flipperDbService = FlipperDbService.getInstance(AddLoverInfoActivity.this);
		Flipper flipper = flipperDbService.getFlipperByUserId(flipperId);
		//如果数据库中存在该用户的请求，则更新状态
		if (flipper != null) {
			if (!flipper.getStatus().equals(FlipperStatus.INVITE)) {//如果已经请求过，则不再请求
				addContact(flipperId);
			}

			LogTool.i("PersonDetailActivity", "flipper已经存在，更新");
			flipper.setIsRead(true);
			flipper.setTime(new Date());
			flipper.setType(FlipperType.TO);
			flipper.setStatus(Constants.FlipperStatus.INVITE);
			flipperDbService.flipperDao.update(flipper);

			//发给对方通知
			JsonMessage jsonMessage = new JsonMessage(userPreference.getName() + "对您怦然心动",
					MessageType.MESSAGE_TYPE_FLIPPER_REQUEEST);
			new SendMsgAsyncTask(FastJsonTool.createJsonString(jsonMessage), flipper.getBpushUserID()).send();
		} else {
			//如果网络端不是未推送状态，则添加环信好友
			if (response.equals("1")) {
				addContact(flipperId);
			}
			getUser(flipperId);
		}
	}

	/**
	 * 	网络获取User信息
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
						FlipperDbService flipperDbService = FlipperDbService.getInstance(AddLoverInfoActivity.this);
						Flipper flipper = new Flipper(null, jsonUser.getU_id(), jsonUser.getU_bpush_user_id(),
								jsonUser.getU_bpush_channel_id(), jsonUser.getU_nickname(), jsonUser.getU_realname(),
								jsonUser.getU_gender(), jsonUser.getU_email(), jsonUser.getU_large_avatar(),
								jsonUser.getU_small_avatar(), jsonUser.getU_blood_type(), jsonUser.getU_constell(),
								jsonUser.getU_introduce(), jsonUser.getU_birthday(), new Date(), jsonUser.getU_age(),
								jsonUser.getU_vocationid(), jsonUser.getU_stateid(), jsonUser.getU_provinceid(),
								jsonUser.getU_cityid(), jsonUser.getU_schoolid(), jsonUser.getU_height(),
								jsonUser.getU_weight(), jsonUser.getU_vertify_image_pass(), jsonUser.getU_salary(), true,
								jsonUser.getU_tel(), Constants.FlipperStatus.INVITE, Constants.FlipperType.TO);
						flipperDbService.flipperDao.insert(flipper);

						//发给对方通知
						JsonMessage jsonMessage = new JsonMessage(userPreference.getName() + "对您怦然心动",
								MessageType.MESSAGE_TYPE_FLIPPER_REQUEEST);
						new SendMsgAsyncTask(FastJsonTool.createJsonString(jsonMessage), flipper.getBpushUserID())
								.send();
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				ToastTool.showLong(AddLoverInfoActivity.this, "服务器错误");
			}
		};
		AsyncHttpClientTool.post(AddLoverInfoActivity.this, "getuserbyid", params, responseHandler);
	}
}
