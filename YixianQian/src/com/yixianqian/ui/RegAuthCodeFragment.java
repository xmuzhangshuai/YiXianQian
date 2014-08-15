package com.yixianqian.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.yixianqian.R;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.base.BaseV4Fragment;
import com.yixianqian.config.DefaultSetting;
import com.yixianqian.table.UserTable;
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
	private EditText authCode;
	private Timer timer;
	private UserPreference userPreference;
	private String huanxinUsername;
	private String huanxinaPassword;

	/**
	 * 用户注册异步任务
	 */
	private UserRegisterTask mRegisterTask = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		userPreference = BaseApplication.getInstance().getUserPreference();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_reg_authcode, container, false);

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
		authCode = (EditText) rootView.findViewById(R.id.autncode);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavigation.setText("填写验证码");
		recLen = DefaultSetting.AUTN_CODE_TIME;
		authCodeButton.setEnabled(false);
		rightImageButton.setEnabled(false);

		leftImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});

		rightImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (vertifyAuthCode()) {

					mRegisterTask = new UserRegisterTask();
					mRegisterTask.execute((Void) null);

				}
			}
		});

		//输入验证码时事件
		authCode.addTextChangedListener(new TextWatcher() {

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
				String content = authCode.getText().toString();
				if (content != null) {
					if (content.length() > 0) {
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
	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				authCodeButton.setText("请在" + recLen + "s内输入验证码");
				if (recLen < 0) {
					timer.cancel();
					authCodeButton.setText("重新获取验证码");
					authCodeButton.setEnabled(true);
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
			handler.sendMessage(message);
		}
	};

	/**
	 * 验证验证码
	 * @return
	 */
	private boolean vertifyAuthCode() {
		return true;
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
	ProgressDialog dialog;

	public class UserRegisterTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog = new ProgressDialog(getActivity());
			dialog.setMessage("请稍后");
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
				map.put(UserTable.U_STATEID, String.valueOf(userPreference.getU_stateid()));
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
				userPreference.setU_id(result);

				// 调用环信sdk注册方法
				huanxinUsername = "" + result;
				huanxinaPassword = MD5For16.GetMD5CodeToLower(userPreference.getU_password());

				//登录到聊天服务器
				EMChatManager.getInstance().login(huanxinUsername, huanxinaPassword, new EMCallBack() {

					@Override
					public void onError(int arg0, final String errorMsg) {
						LogTool.e("环信", "注册后登录聊天服务器失败");
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

	/**
	 * 
	 * 类名称：CreateAccountTask
	 * 类描述：注册环信账号异步任务
	 * 创建人： 张帅
	 * 创建时间：2014年8月11日 下午9:04:00
	 *
	 */
	private class CreateAccountTask extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... args) {
			String userid = args[0];
			String pwd = args[1];

			return userid;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
	}
}
