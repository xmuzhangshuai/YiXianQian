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
 * 类名称：AddLoverActivity
 * 类描述：主页面菜单添加情侣页面，有输入手机号和扫描二维码两种方式
 * 创建人： 张帅
 * 创建时间：2014年7月24日 下午4:21:53
 *
 */
public class AddLoverActivity extends BaseActivity implements OnClickListener {
	private ImageView topNavLeftBtn;//导航条左边按钮
	private TextView topNavText;//导航条文字
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
		topNavText.setText("添加情侣");
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
				dialog.setTitle("提示");
				dialog.setMessage("无法识别...让你的另一半打开个人中心的【我的二维码】再试试吧！");
				View.OnClickListener comfirm = new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				};
				dialog.setPositiveButton("确定", comfirm);
				dialog.show();
			}
		}
	}

	/**
	 * 搜索
	 */
	private void attemptSearch() {
		// 重置错误
		mPhoneView.setError(null);
		mPhone = mPhoneView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// 检查手机号
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
			// 如果错误，则提示错误
			focusView.requestFocus();
		} else {
			// 没有错误
			RequestParams params = new RequestParams();
			params.put(UserTable.U_TEL, mPhone);
			TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
				Dialog dialog;

				@Override
				public void onStart() {
					// TODO Auto-generated method stub
					super.onStart();
					dialog = showProgressDialog("正在查找，请稍后...");
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, String response) {
					// TODO Auto-generated method stub
					if (!TextUtils.isEmpty(response)) {
						if (response.equals("1")) {
							ToastTool.showLong(AddLoverActivity.this, "没有查找到此人！");
						} else if (response.equals("2")) {
							ToastTool.showLong(AddLoverActivity.this, "此人正在和ta砰然心动中！");
						} else if (response.equals("3")) {
							ToastTool.showLong(AddLoverActivity.this, "此人正在和ta热恋中！");
						} else if (response.equals("4")) {
							Intent intent = new Intent(AddLoverActivity.this, AddLoverInfoActivity.class);
							intent.putExtra(AddLoverInfoActivity.LOVER_PHONE_KEY, mPhone);
							startActivity(intent);
						} else {
							ToastTool.showLong(AddLoverActivity.this, "服务器错误！");
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
	 * 扫码添加
	 */
	private void scan() {
		Intent openCameraIntent = new Intent(AddLoverActivity.this, CaptureActivity.class);
		startActivityForResult(openCameraIntent, 0);
	}

}
