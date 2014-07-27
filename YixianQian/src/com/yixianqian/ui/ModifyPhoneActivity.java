package com.yixianqian.ui;

import org.apache.http.Header;

import android.content.Intent;
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
import com.yixianqian.config.DefaultKeys;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.ToastTool;

/**
 * �����ƣ�ModifyPhoneActivity
 * ���������޸ĵ绰
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��7��27�� ����3:45:23
 *
 */
public class ModifyPhoneActivity extends BaseActivity implements OnClickListener {
	private TextView topNavigation;//����������
	private View leftImageButton;//��������ఴť
	private View rightImageButton;//�������Ҳఴť
	private EditText mPhoneView;//�ֻ���
	private String mPhone;
	private boolean cancel = false;
	private View focusView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_modify_phone);

		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		topNavigation = (TextView) findViewById(R.id.nav_text);
		leftImageButton = (View) findViewById(R.id.left_btn_bg);
		rightImageButton = (View) findViewById(R.id.right_btn_bg);
		mPhoneView = (EditText) findViewById(R.id.phone);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavigation.setText("�޸��ֻ���");
		leftImageButton.setOnClickListener(this);
		rightImageButton.setOnClickListener(this);
	}

	private void attemptPhone() {
		// ���ô���
		mPhoneView.setError(null);

		// �洢�û�ֵ
		mPhone = mPhoneView.getText().toString();

		// ����ֻ���
		if (TextUtils.isEmpty(mPhone)) {
			mPhoneView.setError(getString(R.string.error_field_required));
			focusView = mPhoneView;
			cancel = true;
		} else if (mPhone.length() != 11) {
			mPhoneView.setError(getString(R.string.error_phone));
			focusView = mPhoneView;
			cancel = true;
		} else {
			//����ֻ����Ƿ�ע��
			RequestParams params = new RequestParams();
			params.put(UserTable.U_TEL, mPhone);
			TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {

				@Override
				public void onSuccess(int arg0, Header[] arg1, String arg2) {
					// TODO Auto-generated method stub
					if (arg0 == 200) {
						if (!arg2.equals(DefaultKeys.TEL_OK)) {
							mPhoneView.setError("���ֻ����ѱ�ע�ᣡ");
							focusView = mPhoneView;
							cancel = true;
							focusView.requestFocus();
						} else {
							ToastTool.showShort(ModifyPhoneActivity.this, "��֤���ѷ���");
							Intent intent = new Intent(ModifyPhoneActivity.this, AuthCodeActivity.class);
							intent.putExtra(UserTable.U_TEL, mPhone);
							startActivity(intent);
							overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
							finish();
						}
					}
				}

				@Override
				public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
					// TODO Auto-generated method stub
					ToastTool.showLong(ModifyPhoneActivity.this, "����������");
				}
			};
			AsyncHttpClientTool.post("existtel", params, responseHandler);
		}

		if (cancel) {
			// �����������ʾ����
			focusView.requestFocus();
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
			attemptPhone();
			break;
		default:
			break;
		}
	}

}
