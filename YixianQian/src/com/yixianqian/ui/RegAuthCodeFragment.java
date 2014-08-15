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
	private EditText authCode;
	private Timer timer;
	private UserPreference userPreference;
	private String huanxinUsername;
	private String huanxinaPassword;

	/**
	 * �û�ע���첽����
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
		authCode = (EditText) rootView.findViewById(R.id.autncode);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavigation.setText("��д��֤��");
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

		//������֤��ʱ�¼�
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
	 * ���Ƽ�ʱ
	 */
	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				authCodeButton.setText("����" + recLen + "s��������֤��");
				if (recLen < 0) {
					timer.cancel();
					authCodeButton.setText("���»�ȡ��֤��");
					authCodeButton.setEnabled(true);
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
			handler.sendMessage(message);
		}
	};

	/**
	 * ��֤��֤��
	 * @return
	 */
	private boolean vertifyAuthCode() {
		return true;
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
	ProgressDialog dialog;

	public class UserRegisterTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog = new ProgressDialog(getActivity());
			dialog.setMessage("���Ժ�");
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
				userPreference.setU_id(result);

				// ���û���sdkע�᷽��
				huanxinUsername = "" + result;
				huanxinaPassword = MD5For16.GetMD5CodeToLower(userPreference.getU_password());

				//��¼�����������
				EMChatManager.getInstance().login(huanxinUsername, huanxinaPassword, new EMCallBack() {

					@Override
					public void onError(int arg0, final String errorMsg) {
						LogTool.e("����", "ע����¼���������ʧ��");
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

	/**
	 * 
	 * �����ƣ�CreateAccountTask
	 * ��������ע�ỷ���˺��첽����
	 * �����ˣ� ��˧
	 * ����ʱ�䣺2014��8��11�� ����9:04:00
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
