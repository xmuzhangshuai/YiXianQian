package com.yixianqian.ui;

import org.apache.http.Header;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.base.BaseV4Fragment;
import com.yixianqian.table.InviteCodeTable;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.CommonTools;
import com.yixianqian.utils.LogTool;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

/** 
* ������: RegInviteCodeFragment 
* ����: TODO��֤������ҳ�� 
* ���ߣ���˧
* ʱ�䣺 2014��9��26�� ����4:32:24 
*  
*/
public class RegInviteCodeFragment extends BaseV4Fragment {
	private View rootView;// ��View
	private TextView topNavigation;
	private View leftImageButton;
	private View rightImageButton;
	private Button vertifyButton;
	private EditText codeEditText;
	private String inviteCode;
	private UserPreference userPreference;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_reg_invitecode, container, false);
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
		vertifyButton = (Button) rootView.findViewById(R.id.again_authcode);
		codeEditText = (EditText) rootView.findViewById(R.id.autncode);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavigation.setText("������");
		vertifyButton.setEnabled(false);
		rightImageButton.setEnabled(false);

		leftImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getActivity().finish();
			}
		});

		rightImageButton.setVisibility(View.GONE);

		vertifyButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				attepmtAccount();
			}
		});

		//������֤��ʱ�¼�
		codeEditText.addTextChangedListener(new TextWatcher() {

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
				String content = codeEditText.getText().toString();
				codeEditText.setError(null);
				if (content != null) {
					if (content.length() == 6) {
						vertifyButton.setEnabled(true);
					} else {
						vertifyButton.setEnabled(false);
					}
				}
			}
		});
	}

	/**
	 * ��֤����
	 */
	private void attepmtAccount() {
		// ���ô���
		codeEditText.setError(null);

		// �洢�û�ֵ
		inviteCode = codeEditText.getText().toString().trim();
		boolean cancel = false;
		View focusView = null;

		//����ǳ�
		if (TextUtils.isEmpty(inviteCode)) {
			codeEditText.setError(getString(R.string.error_field_required));
			focusView = codeEditText;
			cancel = true;
		} else if (inviteCode.length() != 6) {
			codeEditText.setError(getString(R.string.error_length));
			focusView = codeEditText;
			cancel = true;
		} else if (!CommonTools.isInviteCode(inviteCode)) {
			codeEditText.setError(getString(R.string.error_invite_code));
			focusView = codeEditText;
			cancel = true;
		}

		if (cancel) {
			// �����������ʾ����
			focusView.requestFocus();
		} else {
			//��ȡ��֤��
			vetifyCode(inviteCode);
		}
	}

	/**
	 * ��֤������
	 */
	private void vetifyCode(String code) {
		RequestParams params = new RequestParams();
		params.put(InviteCodeTable.IC_CODE, code);
		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
			ProgressDialog dialog = new ProgressDialog(getActivity());

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
				dialog.setMessage("������֤�����Ժ�...");
				dialog.setCancelable(false);
				dialog.show();
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				if (response.endsWith("1")) {
					userPreference.setInvitCode(response);
					RegGenderFragment fragment = new RegGenderFragment();
					FragmentTransaction transaction = getFragmentManager().beginTransaction();
					transaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out, R.anim.push_right_in,
							R.anim.push_right_out);
					transaction.replace(R.id.fragment_container, fragment);
					transaction.addToBackStack(null);
					transaction.commit();
				} else if (response.equals("0")) {
					codeEditText.setError("����������Ч������������");
					codeEditText.requestFocus();
				} else if (response.endsWith("-1")) {
					ToastTool.showLong(RegInviteCodeFragment.this.getActivity(), "�����������쳣�����Ժ�����");
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				ToastTool.showShort(RegInviteCodeFragment.this.getActivity(), "����������");
				LogTool.e("��֤��", "����������,�������" + statusCode + "��  ԭ��" + errorResponse);
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				super.onFinish();
				dialog.dismiss();
			}
		};
		AsyncHttpClientTool.post("verityinvitecode", params, responseHandler);
	}
}
