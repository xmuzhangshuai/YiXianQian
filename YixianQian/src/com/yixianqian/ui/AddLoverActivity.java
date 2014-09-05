package com.yixianqian.ui;

import org.apache.http.Header;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.customewidget.MyAlertDialog;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.CommonTools;
import com.yixianqian.utils.ToastTool;

/**
 * �����ƣ�AddLoverActivity
 * ����������ҳ��˵��������ҳ�棬�������ֻ��ź�ɨ���ά�����ַ�ʽ
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
	private Button alreadyBtn;
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
		alreadyBtn = (Button) findViewById(R.id.already);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		right_btn_bg.setVisibility(View.GONE);
		topNavText.setText("�������");
		topNavLeftBtn.setOnClickListener(this);
		searchBtn.setOnClickListener(this);
		scanBtn.setOnClickListener(this);
		alreadyBtn.setOnClickListener(this);
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
		case R.id.already:
			Intent intent = new Intent(AddLoverActivity.this, AlreadyInviteActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
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
			if (CommonTools.isMobileNO(scanResult)) {
				Intent intent = new Intent(AddLoverActivity.this, AddLoverInfoActivity.class);
				intent.putExtra(AddLoverInfoActivity.LOVER_PHONE_KEY, scanResult);
				startActivity(intent);
			} else {
				final MyAlertDialog dialog = new MyAlertDialog(AddLoverActivity.this);
				dialog.setShowCancel(false);
				dialog.setTitle("��ʾ");
				dialog.setMessage("�޷�ʶ��...�������һ��򿪸������ĵġ��ҵĶ�ά�롿�����԰ɣ�");
				View.OnClickListener comfirm = new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				};
				dialog.setPositiveButton("ȷ��", comfirm);
				dialog.show();
			}
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
		} else if (!CommonTools.isMobileNO(mPhone)) {
			mPhoneView.setError(getString(R.string.error_phone));
			focusView = mPhoneView;
			cancel = true;
		}

		if (cancel) {
			// �����������ʾ����
			focusView.requestFocus();
		} else {
			// û�д���
			RequestParams params = new RequestParams();
			params.put(UserTable.U_TEL, mPhone);
			TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
				Dialog dialog;

				@Override
				public void onStart() {
					// TODO Auto-generated method stub
					super.onStart();
					dialog = showProgressDialog("���ڲ��ң����Ժ�...");
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, String response) {
					// TODO Auto-generated method stub
					if (!TextUtils.isEmpty(response)) {
						if (response.equals("1")) {
							ToastTool.showLong(AddLoverActivity.this, "û�в��ҵ����ˣ�");
						} else if (response.equals("2")) {
							ToastTool.showLong(AddLoverActivity.this, "�������ں�ta��Ȼ�Ķ��У�");
						} else if (response.equals("3")) {
							ToastTool.showLong(AddLoverActivity.this, "�������ں�ta�����У�");
						} else if (response.equals("4")) {
							Intent intent = new Intent(AddLoverActivity.this, AddLoverInfoActivity.class);
							intent.putExtra(AddLoverInfoActivity.LOVER_PHONE_KEY, mPhone);
							startActivity(intent);
						} else {
							ToastTool.showLong(AddLoverActivity.this, "����������");
						}
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onFinish() {
					// TODO Auto-generated method stub
					if (dialog != null) {
						dialog.dismiss();
					}
					super.onFinish();

				}

				@Override
				public void onCancel() {
					// TODO Auto-generated method stub
					if (dialog != null) {
						dialog.dismiss();
					}
					super.onCancel();
				}
			};
			AsyncHttpClientTool.post(AddLoverActivity.this, "getuserstatebytel", params, responseHandler);
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
