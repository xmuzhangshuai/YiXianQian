package com.yixianqian.ui;

import org.apache.http.Header;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.customewidget.MyAlertDialog;
import com.yixianqian.table.LoveBridgeItemTable;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.ToastTool;

/** 
* ������: DeleteLoveBridgeMenu 
* ����: TODOɾ��ȵ�Ź㳡Item 
* ���ߣ���˧
* ʱ�䣺 2014��9��21�� ����4:54:00 
*  
*/
public class DeleteLoveBridgeMenu extends BaseActivity {
	public static final int RESULT_CODE_DELETE = 1000;
	private int position = -1;
	private int item_id = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delete_love_bridge);
		position = getIntent().getIntExtra("position", -1);
		item_id = getIntent().getIntExtra(LoveBridgeItemTable.N_ID, -1);

		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub

	}

	public void delete(View view) {
		final MyAlertDialog myAlertDialog = new MyAlertDialog(this);
		myAlertDialog.setTitle("��ʾ");
		myAlertDialog.setMessage("�Ƿ�ɾ��ȵ�Ź㳡������¼��");
		View.OnClickListener comfirm = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myAlertDialog.dismiss();
				deleteLoveBridge();
			}
		};
		View.OnClickListener cancle = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myAlertDialog.dismiss();
				DeleteLoveBridgeMenu.this.finish();
			}
		};
		myAlertDialog.setPositiveButton("ȷ��", comfirm);
		myAlertDialog.setNegativeButton("ȡ��", cancle);
		myAlertDialog.show();

	}

	public void cancel(View view) {
		finish();
	}

	/**
	 * ɾ����¼
	 */
	private void deleteLoveBridge() {
		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				setResult(RESULT_CODE_DELETE, new Intent().putExtra("position", position));
				finish();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				ToastTool.showShort(BaseApplication.getInstance(), "ɾ��ʧ��");
			}
		};
		RequestParams params = new RequestParams();
		params.put(LoveBridgeItemTable.N_ID, item_id);
		if (position > -1 && item_id > -1) {
			AsyncHttpClientTool.post("deleteloverrecord", params, responseHandler);
		}
	}
}
