package com.yixianqian.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.table.LoverTimeCapsuleTable;
import com.yixianqian.table.SingleTimeCapsuleTable;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.LogTool;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

/**
 * �����ƣ�SharePanelActivity
 * ���������������
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��8��25�� ����9:11:41
 *
 */
public class SharePanelActivity extends BaseActivity {
	public static final int REQUEST_CODE_SHAREPANEL = 3;
	public static final int RESULT_CODE_DELETE = 4;
	private int timeCapsulePosition = -1;
	private UserPreference userPreference;
	private int stateID = 0;
	private int msgID = -1;
	private int userID = -1;
	private int loverID = -1;

	GridView sharePanelGridView;
	private int[] shareItemDrawable = { R.drawable.weixin_popover, R.drawable.weinxinpengyou_popover,
			R.drawable.qq_popover, R.drawable.qzone_popover, R.drawable.weibo_popover, R.drawable.qq_weibo_popover,
			R.drawable.mail_popover, };
	private String[] shareItemName = { "΢�ź���", "΢������Ȧ", "QQ����", "QQ�ռ�", "����΢��", "��Ѷ΢��", "�ʼ�", };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_panel);

		userPreference = BaseApplication.getInstance().getUserPreference();
		stateID = userPreference.getU_stateid();
		timeCapsulePosition = getIntent().getIntExtra("position", -1);
		if (stateID == 2) {
			msgID = getIntent().getIntExtra(LoverTimeCapsuleTable.LTC_MSGID, -1);
			userID = getIntent().getIntExtra(LoverTimeCapsuleTable.LTC_USERID, -1);
			loverID = getIntent().getIntExtra(LoverTimeCapsuleTable.LTC_LOVERID, -1);
		} else if (stateID == 3 || stateID == 4) {
			msgID = getIntent().getIntExtra(SingleTimeCapsuleTable.STC_MSGID, -1);
			userID = getIntent().getIntExtra(SingleTimeCapsuleTable.STC_USERID, -1);
		}

		findViewById();
		initView();

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		sharePanelGridView = (GridView) findViewById(R.id.share_panel);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		initSharePanel();
	}

	public void delete(View view) {
		deleteTimeCapsule();
		finish();
	}

	public void cancel(View view) {
		finish();
	}

	/**
	 * ��ʼ���������
	 */
	private void initSharePanel() {
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < shareItemName.length; i++) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			listItem.put("image", shareItemDrawable[i]);
			listItem.put("name", shareItemName[i]);
			listItems.add(listItem);
		}

		SimpleAdapter adapter = new SimpleAdapter(SharePanelActivity.this, listItems, R.layout.share_item,
				new String[] { "image", "name" }, new int[] { R.id.share_item_image, R.id.share_item_name });
		sharePanelGridView.setAdapter(adapter);
		sharePanelGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0://΢�ź���

					break;
				case 1://΢������Ȧ

					break;
				case 2://QQ����

					break;
				case 3://QQ�ռ�

					break;
				case 4://����΢��

					break;
				case 5://��Ѷ΢��

					break;
				case 6://�ʼ�

					break;
				default:
					break;
				}
			}
		});
	}

	/**
	 * ɾ����¼
	 */
	private void deleteTimeCapsule() {
		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				LogTool.d("ʱ�佺��", "ɾ��ʱ�佺�ҳɹ�");
				setResult(RESULT_CODE_DELETE, new Intent().putExtra("position", timeCapsulePosition));
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				ToastTool.showShort(BaseApplication.getInstance(), "ɾ��ʧ��");
			}
		};
		RequestParams params = new RequestParams();
		if (stateID == 2) {
			params.put(LoverTimeCapsuleTable.LTC_MSGID, msgID);
			params.put(LoverTimeCapsuleTable.LTC_USERID, userID);
			params.put(LoverTimeCapsuleTable.LTC_LOVERID, loverID);
			AsyncHttpClientTool.post("deleteloverrecord", params, responseHandler);
		} else if (stateID == 3 || stateID == 4) {
			params.put(SingleTimeCapsuleTable.STC_MSGID, msgID);
			params.put(SingleTimeCapsuleTable.STC_USERID, userID);
			AsyncHttpClientTool.post("deletesinglerecord", params, responseHandler);
		}
	}
}
