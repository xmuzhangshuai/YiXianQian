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
 * �����ƣ�AuthCodeActivity
 * ����������֤��ҳ��
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��7��4�� ����9:28:01
 *
 */
public class RegAuthCodeFragment extends BaseV4Fragment {
	/*************Views************/
	private View rootView;// ��View
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
	 * �û�ע���첽����
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
					// ���ŵ�����
					String message = sms.getMessageBody();
					LogTool.d("��֤��", "message     " + message);
					// ��Ϣ���ֻ��š���+86��ͷ��
					String from = sms.getOriginatingAddress();
					LogTool.d("��֤��", "from     " + from);
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
		findViewById();// ��ʼ��views
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
		topNavigation.setText("��д��֤��");
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
					authCodeView.setError("��֤�����");
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

		//������֤��ʱ�¼�
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
	 * ���Ƽ�ʱ
	 */
	final Handler timeHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				authCodeButton.setText("����" + recLen + "s��������֤��");
				if (recLen < 0) {
					timer.cancel();
					authCodeButton.setText("���»�ȡ��֤��");
					authCodeButton.setEnabled(true);
					rightImageButton.setEnabled(false);
				}
			}
		}
	};

	/**
	 * ��ʱ��
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
	 * ��֤��֤��
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
	 * ƥ������м��6�����֣���֤��ȣ�
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
	 * ��ȡ��֤��
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

				LogTool.e("��֤��", response);
				if (response.length() == 6) {
					authcode = response;
					ToastTool.showShort(RegAuthCodeFragment.this.getActivity(), "��֤���ѷ���");
				} else if (response.endsWith("-1")) {
					ToastTool.showShort(RegAuthCodeFragment.this.getActivity(), "����������");
				} else if (response.endsWith("1")) {
					ToastTool.showShort(RegAuthCodeFragment.this.getActivity(), "�ֻ�����Ϊ��");
				} else {
					ToastTool.showShort(RegAuthCodeFragment.this.getActivity(), "����������");
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				ToastTool.showShort(RegAuthCodeFragment.this.getActivity(), "����������");
				LogTool.e("��֤��", "����������,�������" + statusCode + "��  ԭ��" + errorResponse);
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
	* ��Ŀ���ƣ�YiXianQian   
	* �����ƣ�UserRegisterTask   
	* ��������   �첽����ע��
	* �����ˣ���˧  
	* ����ʱ�䣺2014-4-3 ����3:34:13   
	* �޸��ˣ���˧   
	* �޸�ʱ�䣺2014-4-3 ����3:34:13   
	* �޸ı�ע��   
	* @version    
	*    
	*/

	public class UserRegisterTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog = new ProgressDialog(getActivity());
			dialog.setMessage("����ע��...");
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

				// ע��
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
				dialog.setMessage("���ڵ�¼...");
				userPreference.setU_id(result);

				// ���û���sdkע�᷽��
				huanxinUsername = "" + result;
				huanxinaPassword = MD5For16.GetMD5CodeToLower(userPreference.getU_password());

				//��¼�����������
				EMChatManager.getInstance().login(huanxinUsername, huanxinaPassword, new EMCallBack() {

					@Override
					public void onError(int arg0, final String errorMsg) {
						LogTool.e("����", "ע����¼���������ʧ��");
						ToastTool.showLong(getActivity(), "��¼ʧ��");
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
						//						userPreference.setU_password("");//�������
						dialog.dismiss();

						Intent intent = new Intent(getActivity(), HeadImageActivity.class);
						startActivity(intent);
						getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
						getActivity().finish();
					}
				});

			} else {
				ToastTool.showLong(getActivity(), "ע��ʧ��");
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
