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
import com.yixianqian.db.ProvinceDbService;
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
 * 类名称：PersonDetailActivity
 * 类描述：个人详细资料页面
 * 创建人： 张帅
 * 创建时间：2014年8月5日 下午3:45:37
 *
 */
public class PersonDetailActivity extends BaseFragmentActivity implements OnClickListener {
	public static final String PERSON_TYPE = "person_type";
	private View topNavLeftBtn;//导航条左边按钮
	private TextView topNavText;//导航条文字
	private View right_btn_bg;
	private ImageView topNavRightBtn;
	private TextView timeCapsule;
	private TextView sendMsg;
	private ImageView headImageView;//头像
	private TextView nameTextView;//姓名
	private TextView provinceTextView;//省份
	private TextView schoolTextView;//学校
	private TextView genderView;//性别
	private FriendPreference friendPreference;
	private UserPreference userPreference;
	private int userId;
	private int type;//个人类型，1为陌生人，2为心动关系，3为情侣
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
		provinceTextView = (TextView) findViewById(R.id.province);
		schoolTextView = (TextView) findViewById(R.id.school);
		genderView = (TextView) findViewById(R.id.gender);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavRightBtn.setImageResource(R.drawable.ic_action_overflow);
		right_btn_bg.setBackgroundResource(R.drawable.sel_topnav_btn_bg);
		topNavText.setText("个人资料");
		topNavLeftBtn.setOnClickListener(this);
		right_btn_bg.setOnClickListener(this);
		sendMsg.setOnClickListener(this);
		if (type == Constants.PersonDetailType.SINGLE || type == Constants.PersonDetailType.FLIPPER) {
			timeCapsule.setOnClickListener(this);
			timeCapsule.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 初始化个人信息
	 */
	private void initPersonView() {
		if (type == Constants.PersonDetailType.SINGLE) {
			//设置头像
			if (!TextUtils.isEmpty(jsonUser.getU_small_avatar())) {
				imageLoader.displayImage(AsyncHttpClientImageSound.getAbsoluteUrl(jsonUser.getU_small_avatar()),
						headImageView, ImageLoaderTool.getHeadImageOptions(10));
				//点击显示高清头像
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
			//设置姓名、省份、及学校
			//优先显示真实姓名

			nameTextView.setText(getUserName(jsonUser));
			ProvinceDbService provinceDbService = ProvinceDbService.getInstance(this);
			SchoolDbService schoolDbService = SchoolDbService.getInstance(this);
			provinceTextView.setText(provinceDbService.getProNameById(jsonUser.getU_provinceid()));
			schoolTextView.setText(schoolDbService.schoolDao.load((long) jsonUser.getU_schoolid()).getSchoolName());
			genderView.setText(jsonUser.getU_gender());

		} else if (type == Constants.PersonDetailType.LOVER || type == Constants.PersonDetailType.FLIPPER) {
			//设置姓名、省份、及学校
			//优先显示真实姓名
			nameTextView.setText(friendPreference.getName());
			provinceTextView.setText(friendPreference.getProvinceName());
			schoolTextView.setText(friendPreference.getSchoolName());
			//设置头像
			if (!TextUtils.isEmpty(friendPreference.getF_small_avatar())) {
				imageLoader.displayImage(
						AsyncHttpClientImageSound.getAbsoluteUrl(friendPreference.getF_small_avatar()), headImageView,
						ImageLoaderTool.getHeadImageOptions(10));
				//点击显示高清头像
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
	 * 	网络获取User信息
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
				ToastTool.showLong(PersonDetailActivity.this, "服务器错误");
			}
		};
		AsyncHttpClientTool.post(PersonDetailActivity.this, "getuserbyid", params, responseHandler);
	}

	/**
	 * 菜单显示
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
	 * 优先返回真名
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
	 * 发消息提示框
	 */
	void showSendMsgDialog() {
		String msgString = "";
		if (type == Constants.PersonDetailType.SINGLE) {
			msgString = "您和 " + getUserName(jsonUser) + " 还不是心动关系，不能聊天哦~！";
			final MyAlertDialog myAlertDialog = new MyAlertDialog(this);
			myAlertDialog.setTitle("提示");
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
			myAlertDialog.setPositiveButton("心动", comfirm);
			myAlertDialog.setNegativeButton("算了", cancle);
			myAlertDialog.show();
		} else if (type == 2 || type == 3) {
			Intent intent = new Intent(PersonDetailActivity.this, ChatActivity.class);
			intent.putExtra("userId", "" + friendPreference.getF_id());
			startActivity(intent);
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		}

	}

	/**
	 * 异步发送爱情验证
	 * @param userID
	 */
	private void sendLoveReuest(final int filpperId) {
		if (filpperId > 0) {
			FlipperDbService flipperDbService = FlipperDbService.getInstance(PersonDetailActivity.this);
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
						PersonDetailActivity.this.finish();
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers, String response) {
						// TODO Auto-generated method stub
						ToastTool.showLong(getApplicationContext(), "爱情验证已发送！等待对方同意");
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
		FlipperDbService flipperDbService = FlipperDbService.getInstance(PersonDetailActivity.this);
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
				ToastTool.showLong(PersonDetailActivity.this, "服务器错误");
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
		default:
			break;
		}
	}

}
