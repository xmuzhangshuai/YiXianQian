package com.yixianqian.ui;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.base.BaseV4Fragment;
import com.yixianqian.config.DefaultKeys;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.HttpUtil;
import com.yixianqian.utils.LogTool;
import com.yixianqian.utils.MD5For32;
import com.yixianqian.utils.SIMCardInfo;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

/**
 * �����ƣ�RegPhoneFragment
 * ���������绰��������дҳ��
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��7��6�� ����7:56:56
 *
 */
public class RegPhoneFragment extends BaseV4Fragment {
	/*************Views************/
	private View rootView;// ��View
	private TextView topNavigation;//����������
	private View leftImageButton;//��������ఴť
	private View rightImageButton;//�������Ҳఴť
	private EditText mNameView;//�ǳ�
	private EditText mPhoneView;//�ֻ���
	private EditText mPasswordView;//����
	private EditText mConformPassView;//ȷ������
	private UserPreference userPreference;

	private String mName;
	private String mPhone;
	private String mPassword;
	private String mConformPass;
	private boolean phoneAvailable;//�ֻ����Ƿ����

	private CheckPhoneTask mCheckPhoneTask = null;//���绰����

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_reg_phone, container, false);
		userPreference = BaseApplication.getInstance().getUserPreference();

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
		mPhoneView = (EditText) rootView.findViewById(R.id.phone);
		mPasswordView = (EditText) rootView.findViewById(R.id.password);
		mConformPassView = (EditText) rootView.findViewById(R.id.conform_password);
		mNameView = (EditText) rootView.findViewById(R.id.name);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		//��ʾ�û��ֻ���
		SIMCardInfo siminfo = new SIMCardInfo(getActivity());
		mPhoneView.setText(siminfo.getNativePhoneNumber());
		mPhone = mPhoneView.getText().toString();

		if ((!mPhone.isEmpty()) && mPhone.length() == 11) {
			//����ֻ����Ƿ�ע��
			mCheckPhoneTask = new CheckPhoneTask();
			mCheckPhoneTask.execute();
		}

		topNavigation.setText("�˻�");
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
				attepmtAccount();
			}
		});

		mPhoneView.addTextChangedListener(new TextWatcher() {

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
				mPhone = mPhoneView.getText().toString();
				if ((!mPhone.isEmpty()) && mPhone.length() == 11) {
					//����ֻ����Ƿ�ע��
					mCheckPhoneTask = new CheckPhoneTask();
					mCheckPhoneTask.execute();
				}
			}
		});

	}

	/**
	 * ��֤����
	 */
	private void attepmtAccount() {
		// ���ô���
		mPasswordView.setError(null);
		mPhoneView.setError(null);
		mConformPassView.setError(null);
		mNameView.setError(null);

		// �洢�û�ֵ
		mName = mNameView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		mPhone = mPhoneView.getText().toString();
		mConformPass = mConformPassView.getText().toString();
		boolean cancel = false;
		View focusView = null;

		// ����ֻ���
		if (TextUtils.isEmpty(mPhone)) {
			mPhoneView.setError(getString(R.string.error_field_required));
			focusView = mPhoneView;
			cancel = true;
		} else if (mPhone.length() != 11) {
			mPhoneView.setError(getString(R.string.error_phone));
			focusView = mPhoneView;
			cancel = true;
		}

		// �������
		else if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 6) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// ����ظ�����
		else if (TextUtils.isEmpty(mConformPass)) {
			mConformPassView.setError(getString(R.string.error_field_required));
			focusView = mConformPassView;
			cancel = true;
		} else if (!mConformPass.equals(mPassword)) {
			mConformPassView.setError(getString(R.string.error_field_conform_pass));
			focusView = mConformPassView;
			cancel = true;
		}

		//����ǳ�
		if (TextUtils.isEmpty(mName)) {
			mNameView.setError(getString(R.string.error_field_required));
			focusView = mNameView;
			cancel = true;
		}

		else if (!phoneAvailable) {
			mPhoneView.setError("���ֻ����ѱ�ע�ᣡ");
			focusView = mPhoneView;
			cancel = true;
		}

		if (cancel) {
			// �����������ʾ����
			focusView.requestFocus();
		} else {
			//��ȡ��֤��
			getAuthCode();

			// û�д�����洢ֵ
			userPreference.setU_tel(mPhone);
			userPreference.setU_password(MD5For32.GetMD5Code(mPassword));
			userPreference.setU_nickname(mName);

		}
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
					RegAuthCodeFragment fragment = new RegAuthCodeFragment();
					Bundle bundle = new Bundle();
					bundle.putString(RegAuthCodeFragment.AUTHCODE, response);
					fragment.setArguments(bundle);
					FragmentTransaction transaction = getFragmentManager().beginTransaction();
					transaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out, R.anim.push_right_in,
							R.anim.push_right_out);
					transaction.replace(R.id.fragment_container, fragment);
					transaction.commit();
					ToastTool.showShort(RegPhoneFragment.this.getActivity(), "��֤���ѷ���");

				} else if (response.endsWith("-1")) {
					ToastTool.showShort(RegPhoneFragment.this.getActivity(), "����������");
				} else if (response.endsWith("1")) {
					ToastTool.showShort(RegPhoneFragment.this.getActivity(), "�ֻ�����Ϊ��");
				} else {
					ToastTool.showShort(RegPhoneFragment.this.getActivity(), "����������");
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				ToastTool.showShort(RegPhoneFragment.this.getActivity(), "����������");
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
	 * �����ƣ�CheckPhoneTask
	 * ������������ֻ����Ƿ��Ѿ���ע��
	 * �����ˣ� ��˧
	 * ����ʱ�䣺2014��7��13�� ����11:25:31
	 *
	 */
	public class CheckPhoneTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			phoneAvailable = false;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				String url = "existtel";
				Map<String, String> map = new HashMap<String, String>();
				map.put(UserTable.U_TEL, mPhoneView.getText().toString());

				String result = HttpUtil.postRequest(url, map);
				if (result.equals(DefaultKeys.TEL_OK)) {
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			mCheckPhoneTask = null;
			phoneAvailable = result;
		}

		@Override
		protected void onCancelled() {
			mCheckPhoneTask = null;
		}

	}
}
