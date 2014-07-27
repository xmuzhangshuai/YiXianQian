package com.yixianqian.ui;

import org.apache.http.Header;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

/**
 * �����ƣ�ModifyPassActivity
 * ���������޸�����
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��7��27�� ����3:54:33
 *
 */
public class ModifyPassActivity extends BaseActivity implements OnClickListener {
	private TextView topNavigation;//����������
	private View leftImageButton;//��������ఴť
	private View rightImageButton;//�������Ҳఴť
	private EditText mOldPassView;//������
	private EditText mNewPassView;//������
	private EditText mConformPassView;//ȷ������
	private UserPreference userPreference;

	private String oldPass;
	private String newPass;
	private String confirmPass;
	View focusView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_modify_pass);
		userPreference = BaseApplication.getInstance().getUserPreference();

		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		topNavigation = (TextView) findViewById(R.id.nav_text);
		leftImageButton = (View) findViewById(R.id.left_btn_bg);
		rightImageButton = (View) findViewById(R.id.right_btn_bg);
		mOldPassView = (EditText) findViewById(R.id.oldpass);
		mNewPassView = (EditText) findViewById(R.id.newpass);
		mConformPassView = (EditText) findViewById(R.id.conform_password);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavigation.setText("�޸�����");
		leftImageButton.setOnClickListener(this);
		rightImageButton.setOnClickListener(this);
	}

	private void attemptModifyPass() {
		// ���ô���
		mOldPassView.setError(null);
		mNewPassView.setError(null);
		mConformPassView.setError(null);

		// �洢�û�ֵ
		oldPass = mOldPassView.getText().toString();
		newPass = mNewPassView.getText().toString();
		confirmPass = mConformPassView.getText().toString();
		boolean cancel = false;

		//��������
		if (TextUtils.isEmpty(oldPass)) {
			mOldPassView.setError(getString(R.string.error_field_required));
			focusView = mOldPassView;
			cancel = true;
		} else if (oldPass.length() < 6) {
			mOldPassView.setError(getString(R.string.error_invalid_password));
			focusView = mOldPassView;
			cancel = true;
		}

		// �������
		if (TextUtils.isEmpty(newPass)) {
			mNewPassView.setError(getString(R.string.error_field_required));
			focusView = mNewPassView;
			cancel = true;
		} else if (newPass.length() < 6) {
			mNewPassView.setError(getString(R.string.error_invalid_password));
			focusView = mNewPassView;
			cancel = true;
		}

		// ����ظ�����
		else if (TextUtils.isEmpty(confirmPass)) {
			mConformPassView.setError(getString(R.string.error_field_required));
			focusView = mConformPassView;
			cancel = true;
		} else if (!confirmPass.equals(newPass)) {
			mConformPassView.setError(getString(R.string.error_field_conform_pass));
			focusView = mConformPassView;
			cancel = true;
		}

		if (cancel) {
			// �����������ʾ����
			focusView.requestFocus();
		} else {
			// û�д������޸�
			RequestParams params = new RequestParams();
			params.put(UserTable.U_PASSWORD, oldPass);
			params.put(UserTable.U_NEW_PASSWORD, newPass);
			params.put(UserTable.U_ID, userPreference.getU_id());
			TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
				Dialog dialog;

				@Override
				public void onStart() {
					// TODO Auto-generated method stub
					super.onStart();
					dialog = showProgressDialog("���Ժ�...");
				}

				@Override
				public void onFinish() {
					// TODO Auto-generated method stub
					dialog.dismiss();
					super.onFinish();
				}

				@Override
				public void onSuccess(int arg0, Header[] arg1, String arg2) {
					// TODO Auto-generated method stub
					if (arg0 == 200) {
						if (arg2.equals("1")) {
							ToastTool.showShort(ModifyPassActivity.this, "�޸ĳɹ���");
							userPreference.setU_password(newPass);
							finish();
						} else if (arg2.equals("-1")) {
							mOldPassView.setError("�����벻��ȷ");
							focusView = mOldPassView;
							focusView.requestFocus();
						}
					}
				}

				@Override
				public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
					// TODO Auto-generated method stub
					ToastTool.showLong(ModifyPassActivity.this, "����������");
				}
			};
			AsyncHttpClientTool.post("updateuserpwd", params, responseHandler);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.left_btn_bg:
			finish();
			break;
		case R.id.right_btn_bg:
			attemptModifyPass();
			break;
		default:
			break;
		}
	}

}
