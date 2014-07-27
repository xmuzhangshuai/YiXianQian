package com.yixianqian.ui;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
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
import com.yixianqian.config.DefaultSetting;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

/**
 * 类名称：AuthCodeActivity
 * 类描述：修改电话时填写验证码
 * 创建人： 张帅
 * 创建时间：2014年7月27日 下午3:45:31
 *
 */
public class AuthCodeActivity extends BaseActivity {
	private TextView topNavigation;
	private View leftImageButton;
	private View rightImageButton;
	private int recLen;
	private Button authCodeButton;
	private EditText authCode;
	private Timer timer;
	private UserPreference userPreference;
	private String mPhone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_auth_code);

		mPhone = getIntent().getStringExtra(UserTable.U_TEL);
		userPreference = BaseApplication.getInstance().getUserPreference();

		findViewById();
		initView();

		timer = new Timer();
		timer.schedule(task, 1000, 1000);
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		topNavigation = (TextView) findViewById(R.id.nav_text);
		leftImageButton = (View) findViewById(R.id.left_btn_bg);
		rightImageButton = (View) findViewById(R.id.right_btn_bg);
		authCodeButton = (Button) findViewById(R.id.again_authcode);
		authCode = (EditText) findViewById(R.id.autncode);
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
				finish();
			}
		});

		rightImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (vertifyAuthCode()) {

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
									ToastTool.showLong(AuthCodeActivity.this, "修改手机号码成功");
									finish();
								} else {
									ToastTool.showLong(AuthCodeActivity.this, "修改手机号码失败");
								}
							}
						}

						@Override
						public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
							// TODO Auto-generated method stub
							ToastTool.showLong(AuthCodeActivity.this, "服务器错误");
						}
					};
					AsyncHttpClientTool.post("updateusertel", params, responseHandler);
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

}
