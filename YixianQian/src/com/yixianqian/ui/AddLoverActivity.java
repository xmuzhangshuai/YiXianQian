package com.yixianqian.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;

/**
 * �����ƣ�AddLoverActivity
 * ���������������ҳ��
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��7��24�� ����4:21:53
 *
 */
public class AddLoverActivity extends BaseActivity implements OnClickListener {
	private ImageView topNavLeftBtn;//��������߰�ť
	private TextView topNavText;//����������
	private View right_btn_bg;
	private View searchBtn;
	private View scanBtn;
	private EditText mPhoneView;
	private String mPhone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_add_lover);

		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		topNavLeftBtn = (ImageView) findViewById(R.id.nav_left_btn);
		right_btn_bg = (View) findViewById(R.id.right_btn_bg);
		topNavText = (TextView) findViewById(R.id.nav_text);
		searchBtn = findViewById(R.id.search);
		scanBtn = findViewById(R.id.scan);
		mPhoneView = (EditText) findViewById(R.id.phone);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		right_btn_bg.setVisibility(View.GONE);
		topNavText.setText("�������");
		topNavLeftBtn.setOnClickListener(this);
		searchBtn.setOnClickListener(this);
		scanBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.nav_left_btn:
			finish();
			break;
		case R.id.search:
			attemptSearch();
			break;
		case R.id.scan:
			scan();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			Bundle bundle = data.getExtras();
			String scanResult = bundle.getString("result");
			Intent intent = new Intent(AddLoverActivity.this, AddLoverInfoActivity.class);
			intent.putExtra(AddLoverInfoActivity.LOVER_PHONE_KEY, scanResult);
			startActivity(intent);
		}
	}

	/**
	 * ����
	 */
	private void attemptSearch() {
		// ���ô���
		mPhoneView.setError(null);
		mPhone = mPhoneView.getText().toString();

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

		if (cancel) {
			// �����������ʾ����
			focusView.requestFocus();
		} else {
			// û�д���
		}
	}

	/**
	 * ɨ�����
	 */
	private void scan() {
		Intent openCameraIntent = new Intent(AddLoverActivity.this, CaptureActivity.class);
		startActivityForResult(openCameraIntent, 0);
	}
}
