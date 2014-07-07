package com.yixianqian.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yixianqian.R;
import com.yixianqian.base.BaseV4Fragment;
import com.yixianqian.utils.SIMCardInfo;

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
	private EditText mPhoneView;//�ֻ���
	private EditText mPasswordView;//����
	private EditText mConformPassView;//ȷ������

	private String mPhone;
	private String mPassword;
	private String mConformPass;

	/**
	 * �û�ע���첽����
	 */
	private UserRegisterTask mRegisterTask = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_reg_phone, container, false);
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
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		//��ʾ�û��ֻ���
		SIMCardInfo siminfo = new SIMCardInfo(getActivity());
		mPhoneView.setText(siminfo.getNativePhoneNumber());

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
	}

	/**
	 * ��֤����
	 */
	private void attepmtAccount() {
		if (mRegisterTask != null) {
			return;
		}

		// ���ô���
		mPasswordView.setError(null);
		mPhoneView.setError(null);
		mConformPassView.setError(null);

		// �洢�û�ֵ
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
		} else if (mPassword.length() < 4) {
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

		if (cancel) {
			// �����������ʾ����
			focusView.requestFocus();
		} else {
			// û�д�����ע��
			mRegisterTask = new UserRegisterTask();
			mRegisterTask.execute((Void) null);

			RegAuthCodeFragment fragment = new RegAuthCodeFragment();
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out, R.anim.push_right_in,
					R.anim.push_right_out);
			transaction.replace(R.id.fragment_container, fragment);
//			transaction.addToBackStack(null);
			transaction.commit();
			Toast.makeText(getActivity(), "��֤���ѷ���", 1).show();
		}
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
	public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				//				 Simulate network access.
				//								UserService userService = UserService.getInstance(getActivity());
				//				
				//								String url = "RegistServlet";
				//								Map<String, String> map = new HashMap<String, String>();
				//								map.put("mail", mEmail);
				//								map.put("pass", mPassword);
				//								map.put("phone", mPhone);
				//				
				//								// ע��
				//								String jsonString = HttpUtil.postRequest(url, map);
				//								JUser net = FastJsonTool.getObject(jsonString, JUser.class);
				//								User local = userService.NetUserToUser(net);
				//								local.setCurrent(true);
				//								String location = locationPreferences.getString(DefaultKeys.PREF_DETAIL_LOCATION, "������");
				//								local.setArea(location);
				//								userService.saveUser(local);
				//				
				//								userService.updateUserToNet();

			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			//			mRegisterTask = null;
			//			showProgress(false);
			//
			//			if (success) {
			//				Toast.makeText(getActivity(), "��ϲ��ע��ɹ����뾡�����Ƹ�������...", 2000).show();
			//				getActivity().finish();
			//			} else {
			//				Toast.makeText(getActivity(), "ע��ʧ��", 1).show();
			//			}
		}

		@Override
		protected void onCancelled() {
			//			mRegisterTask = null;
			//			showProgress(false);
		}
	}

}
