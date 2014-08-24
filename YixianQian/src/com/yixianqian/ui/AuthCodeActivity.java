package com.yixianqian.ui;

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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.config.Constants.Config;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.LogTool;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

/**
 * �����ƣ�AuthCodeActivity
 * ���������޸ĵ绰ʱ��д��֤��
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��7��27�� ����3:45:31
 *
 */
public class AuthCodeActivity extends BaseActivity {
	/*************Views************/
	private TextView topNavigation;
	private View leftImageButton;
	private View rightImageButton;
	private int recLen;
	private Button authCodeButton;
	private EditText authCodeView;
	private Timer timer;
	ProgressDialog dialog;
	private BroadcastReceiver smsReceiver;
	private IntentFilter filter2;
	private Handler handler;
	private String strContent;
	private String patternCoder = "(?<!\\d)\\d{6}(?!\\d)";
	private UserPreference userPreference;
	private String mPhone;
	private String authcode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_auth_code);

		mPhone = getIntent().getStringExtra(UserTable.U_TEL);
		authcode = getIntent().getStringExtra(RegAuthCodeFragment.AUTHCODE);
		userPreference = BaseApplication.getInstance().getUserPreference();

		findViewById();
		initView();

		timer = new Timer();
		timer.schedule(task, 1000, 1000);

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
		registerReceiver(smsReceiver, filter2);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(smsReceiver);
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		topNavigation = (TextView) findViewById(R.id.nav_text);
		leftImageButton = (View) findViewById(R.id.left_btn_bg);
		rightImageButton = (View) findViewById(R.id.right_btn_bg);
		authCodeButton = (Button) findViewById(R.id.again_authcode);
		authCodeView = (EditText) findViewById(R.id.autncode);
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
				finish();
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

		rightImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (vertifyAuthCode(authCodeView.getText().toString())) {

					RequestParams params = new RequestParams();
					params.put(UserTable.U_TEL, mPhone);
					params.put(UserTable.U_ID, userPreference.getU_id());
					TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
						@Override
						public void onSuccess(int arg0, Header[] arg1, String arg2) {
							// TODO Auto-generated method stub
							if (arg0 == 200) {
								if (arg2.equals("1")) {
									userPreference.setU_tel(mPhone);
									ToastTool.showLong(AuthCodeActivity.this, "�޸��ֻ�����ɹ�");
									finish();
									overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
								} else {
									ToastTool.showLong(AuthCodeActivity.this, "�޸��ֻ�����ʧ��");
								}
							}
						}

						@Override
						public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
							// TODO Auto-generated method stub
							ToastTool.showLong(AuthCodeActivity.this, "����������");
						}
					};
					AsyncHttpClientTool.post("updateusertel", params, responseHandler);
				} else {
					authCodeView.setError("��֤�����");
					authCodeView.requestFocus();
				}
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
		params.put(UserTable.U_TEL, mPhone);
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
					ToastTool.showShort(AuthCodeActivity.this, "��֤���ѷ���");
				} else if (response.endsWith("-1")) {
					ToastTool.showShort(AuthCodeActivity.this, "����������");
				} else if (response.endsWith("1")) {
					ToastTool.showShort(AuthCodeActivity.this, "�ֻ�����Ϊ��");
				} else {
					ToastTool.showShort(AuthCodeActivity.this, "����������");
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				ToastTool.showShort(AuthCodeActivity.this, "����������");
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
}
