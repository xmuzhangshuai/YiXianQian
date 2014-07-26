package com.yixianqian.ui;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;

/**
 * �����ƣ�ModifyDataActivity
 * ���������޸ĸ�������ҳ��
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��7��26�� ����8:47:52
 *
 */
public class ModifyDataActivity extends BaseActivity implements OnClickListener {
	private TextView topNavigation;//����������
	private View leftImageButton;//��������ఴť
	private View rightImageButton;//�������Ҳఴť
	private EditText nameEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_modify_data);

		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		topNavigation = (TextView) findViewById(R.id.nav_text);
		leftImageButton = (View) findViewById(R.id.left_btn_bg);
		rightImageButton = (View) findViewById(R.id.right_btn_bg);
		nameEditText = (EditText) findViewById(R.id.name);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavigation.setText("�༭����");
		leftImageButton.setOnClickListener(this);
		rightImageButton.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.left_btn_bg:
			finish();
			break;

		default:
			break;
		}
	}

}
