package com.yixianqian.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.base.BaseV4Fragment;
import com.yixianqian.config.Constants;
import com.yixianqian.config.Constants.Config;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.HttpUtil;
import com.yixianqian.utils.LogTool;
import com.yixianqian.utils.MD5For16;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

/**
 * 类名称：AuthCodeActivity
 * 类描述：验证码页面
 * 创建人： 张帅
 * 创建时间：2014年7月4日 下午9:28:01
 *
 */
public class RegAuthCodeFragment extends BaseV4Fragment {
	/*************Views************/
	private View rootView;// 根View
	private TextView topNavigation;
	private View leftImageButton;
	private View rightImageButton;
	private int recLen;
	private Button authCodeButton;
	private EditText authCodeView;
	private Timer timer;
	private UserPreference userPreference;
	private String huanxinUsername;
	private String huanxinaPassword;
	ProgressDialog dialog;
	private BroadcastReceiver smsReceiver;
	private IntentFilter filter2;
	private Handler handler;
	private String strContent;
	private String patternCoder = "(?<!\\d)\\d{6}(?!\\d)";

	/**
	 * 用户注册异步任务
	 */
	private UserRegisterTask mRegisterTask = null;

	public static final String AUTHCODE = "authcode";

	private String authcode;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		userPreference = BaseApplication.getInstance().getUserPreference();
		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				authCodeView.setText(strContent);
			};
		};

		filter2 = new IntentFilter();
		filter2.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter2.setPriority(Integer.MAX_VALUE);
		smsReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Object[] objs = (Object[]) intent.getExtras().get("pdus");
				for (Object obj : objs) {
					byte[] pdu = (byte[]) obj;
					SmsMessage sms = SmsMessage.createFromPdu(pdu);
					// 短信的内容
					String message = sms.getMessageBody();
					LogTool.d("验证码", "message     " + message);
					// 短息的手机号。。+86开头？
					String from = sms.getOriginatingAddress();
					LogTool.d("验证码", "from     " + from);
					// Time time = new Time();
					// time.set(sms.getTimestampMillis());
					// String time2 = time.format3339(true);
					// Log.d("logo", from + "   " + message + "  " + time2);
					// strContent = from + "   " + message;
					// handler.sendEmptyMessage(1);
					if (!TextUtils.isEmpty(from)) {
						String code = patternCode(message);
						if (!TextUtils.isEmpty(code)) {
							strContent = code;
							handler.sendEmptyMessage(1);
						}
					}
				}
			}
		};
		getActivity().registerReceiver(smsReceiver, filter2);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		getActivity().unregisterReceiver(smsReceiver);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_reg_authcode, container, false);
		authcode = getArguments().getString(AUTHCODE);

		timer = new Timer();
		timer.schedule(task, 1000, 1000);
		findViewById();// 初始化views
		initView();
		return rootView;
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		topNavigation = (TextView) getActivity().findViewById(R.id.nav_text);
		leftImageButton = (View) getActivity().findViewById(R.id.left_btn_bg);
		rightImageButton = (View) getActivity().findViewById(R.id.right_btn_bg);
		authCodeButton = (Button) rootView.findViewById(R.id.again_authcode);
		authCodeView = (EditText) rootView.findViewById(R.id.autncode);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavigation.setText("填写验证码");
		recLen = Config.AUTN_CODE_TIME;
		authCodeButton.setEnabled(false);
		rightImageButton.setEnabled(false);

		leftImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getFragmentManager().popBackStack();
			}
		});

		rightImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (vertifyAuthCode(authCodeView.getText().toString())) {
					mRegisterTask = new UserRegisterTask();
					mRegisterTask.execute();
				} else {
					authCodeView.setError("验证码错误");
					authCodeView.requestFocus();
				}
			}
		});

		authCodeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				recLen = Config.AUTN_CODE_TIME;
				authCodeButton.setEnabled(false);
				getAuthCode();
				timer = new Timer();
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						recLen--;
						Message message = new Message();
						message.what = 1;
						timeHandler.sendMessage(message);
					}
				}, 1000, 1000);
			}
		});

		//输入验证码时事件
		authCodeView.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				String content = authCodeView.getText().toString();
				if (content != null) {
					if (content.length() == 6) {
						rightImageButton.setEnabled(true);
					} else {
						rightImageButton.setEnabled(false);
					}
				}
			}
		});
	}

	/**
	 * 控制计时
	 */
	final Handler timeHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				authCodeButton.setText("请在" + recLen + "s内输入验证码");
				if (recLen < 0) {
					timer.cancel();
					authCodeButton.setText("重新获取验证码");
					authCodeButton.setEnabled(true);
					rightImageButton.setEnabled(false);
				}
			}
		}
	};

	/**
	 * 计时器
	 */
	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			recLen--;
			Message message = new Message();
			message.what = 1;
			timeHandler.sendMessage(message);
		}
	};

	/**
	 * 验证验证码
	 * @return
	 */
	private boolean vertifyAuthCode(String code) {
		if (!TextUtils.isEmpty(authcode)) {
			if (authcode.equals(code)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 匹配短信中间的6个数字（验证码等）
	 * 
	 * @param patternContent
	 * @return
	 */
	private String patternCode(String patternContent) {
		if (TextUtils.isEmpty(patternContent)) {
			return null;
		}
		Pattern p = Pattern.compile(patternCoder);
		Matcher matcher = p.matcher(patternContent);
		if (matcher.find()) {
			return matcher.group();
		}
		return null;
	}

	/**
	 * 获取验证码
	 * @return
	 */
	private void getAuthCode() {
		RequestParams params = new RequestParams();
		params.put(UserTable.U_TEL, userPreference.getU_tel());
		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub

				LogTool.e("验证码", response);
				if (response.length() == 6) {
					authcode = response;
					ToastTool.showShort(RegAuthCodeFragment.this.getActivity(), "验证码已发送");
				} else if (response.endsWith("-1")) {
					ToastTool.showShort(RegAuthCodeFragment.this.getActivity(), "服务器错误");
				} else if (response.endsWith("1")) {
					ToastTool.showShort(RegAuthCodeFragment.this.getActivity(), "手机号码为空");
				} else {
					ToastTool.showShort(RegAuthCodeFragment.this.getActivity(), "服务器错误");
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				ToastTool.showShort(RegAuthCodeFragment.this.getActivity(), "服务器错误");
				LogTool.e("验证码", "服务器错误,错误代码" + statusCode + "，  原因" + errorResponse);
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				super.onFinish();
			}
		};
		AsyncHttpClientTool.post("getmessage", params, responseHandler);
	}

	/**   
	*    
	* 项目名称：YiXianQian   
	* 类名称：UserRegisterTask   
	* 类描述：   异步任务注册
	* 创建人：张帅  
	* 创建时间：2014-4-3 下午3:34:13   
	* 修改人：张帅   
	* 修改时间：2014-4-3 下午3:34:13   
	* 修改备注：   
	* @version    
	*    
	*/

	public class UserRegisterTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog = new ProgressDialog(getActivity());
			dialog.setMessage("正在注册...");
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				String url = "adduser";
				Map<String, String> map = new HashMap<String, String>();
				map.put(UserTable.U_TEL, userPreference.getU_tel());
				map.put(UserTable.U_PASSWORD, userPreference.getU_password());
				map.put(UserTable.U_STATEID, "" + Constants.UserStateType.SINGLE);
				map.put(UserTable.U_GENDER, userPreference.getU_gender());
				map.put(UserTable.U_SCHOOLID, String.valueOf(userPreference.getU_schoolid()));
				map.put(UserTable.U_CITYID, String.valueOf(userPreference.getU_cityid()));
				map.put(UserTable.U_PROVINCEID, String.valueOf(userPreference.getU_provinceid()));
				map.put(UserTable.U_ADDRESS, userPreference.getU_address());
				map.put(UserTable.U_BPUSH_USER_ID, userPreference.getBpush_UserID());
				map.put(UserTable.U_BPUSH_CHANNEL_ID, userPreference.getBpush_ChannelID());
				map.put(UserTable.U_NICKNAME, userPreference.getU_nickname());

				// 注册
				String result = HttpUtil.postRequest(url, map);
				if (result != null) {
					return Integer.parseInt(result.trim());
				}
			} catch (Exception e) {
				e.printStackTrace();
				return -1;
			}
			return -1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			mRegisterTask = null;

			if (result > -1) {
				dialog.setMessage("正在登录...");
				userPreference.setU_id(result);

				// 调用环信sdk注册方法
				huanxinUsername = "" + result;
				huanxinaPassword = MD5For16.GetMD5CodeToLower(userPreference.getU_password());

				//登录到聊天服务器
				EMChatManager.getInstance().login(huanxinUsername, huanxinaPassword, new EMCallBack() {

					@Override
					public void onError(int arg0, final String errorMsg) {
						LogTool.e("环信", "注册后登录聊天服务器失败");
						ToastTool.showLong(getActivity(), "登录失败");
						dialog.dismiss();
					}

					@Override
					public void onProgress(int arg0, String arg1) {
					}

					@Override
					public void onSuccess() {
						userPreference.setHuanXinUserName(huanxinUsername);
						userPreference.setHuanXinPassword(huanxinaPassword);
						userPreference.setUserLogin(true);
						//						userPreference.setU_password("");//清除密码
						dialog.dismiss();

						Intent intent = new Intent(getActivity(), HeadImageActivity.class);
						startActivity(intent);
						getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
						getActivity().finish();
					}
				});

			} else {
				ToastTool.showLong(getActivity(), "注册失败");
				dialog.dismiss();
			}
		}

		@Override
		protected void onCancelled() {
			mRegisterTask = null;
			dialog.dismiss();
		}
	}
}
