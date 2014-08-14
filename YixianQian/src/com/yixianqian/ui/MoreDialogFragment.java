package com.yixianqian.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import android.R.integer;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.table.LoverTimeCapsuleTable;
import com.yixianqian.table.SingleTimeCapsuleTable;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

public class MoreDialogFragment extends DialogFragment {
	private View rootView;
	private ListView menuitemListView;
	private List<String> menuitemList;
	private UserPreference userPreference;
	private int stateID = 0;
	private int msgID = -1;
	private int userID = -1;
	private int loverID = -1;
	private int timeCapsulePosition = -1;
	private OnChooseMenuListener onChooseMenuListener;

	public interface OnChooseMenuListener {
		public void onDelete(int position);

		public void onShare(int position);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		onChooseMenuListener = (OnChooseMenuListener) activity;
	}

	/**
	 * ´´½¨ÊµÀý
	 * @return
	 */
	public static MoreDialogFragment newInstance() {
		MoreDialogFragment f = new MoreDialogFragment();
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_TITLE, 0);
		menuitemList = new ArrayList<String>();
		menuitemList.add("É¾    ³ý");
		menuitemList.add("·Ö    Ïí");
		userPreference = BaseApplication.getInstance().getUserPreference();
		stateID = userPreference.getU_stateid();
		timeCapsulePosition = getArguments().getInt("position", -1);
		if (stateID == 2) {
			msgID = getArguments().getInt(LoverTimeCapsuleTable.LTC_MSGID, -1);
			userID = getArguments().getInt(LoverTimeCapsuleTable.LTC_USERID, -1);
			loverID = getArguments().getInt(LoverTimeCapsuleTable.LTC_LOVERID, -1);
		} else if (stateID == 3 || stateID == 4) {
			msgID = getArguments().getInt(SingleTimeCapsuleTable.STC_MSGID, -1);
			userID = getArguments().getInt(SingleTimeCapsuleTable.STC_USERID, -1);
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_dialog, container, false);
		menuitemListView = (ListView) rootView.findViewById(R.id.dialog_listview);
		View view = rootView.findViewById(R.id.divider);
		TextView title = (TextView) rootView.findViewById(R.id.title);
		title.setText("²Ù×÷");
		view.setVisibility(View.VISIBLE);
		title.setVisibility(View.VISIBLE);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.dialog_listview_item,
				R.id.item_name, menuitemList);
		menuitemListView.setAdapter(adapter);

		menuitemListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				MoreDialogFragment.this.dismiss();
				switch (position) {
				case 0:
					deleteTimeCapsule();
					break;
				case 1:
					onChooseMenuListener.onShare(timeCapsulePosition);
					break;
				default:
					break;
				}
			}
		});

		return rootView;
	}

	/**
	 * É¾³ý¼ÇÂ¼
	 */
	void deleteTimeCapsule() {
		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				onChooseMenuListener.onDelete(timeCapsulePosition);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				ToastTool.showShort(BaseApplication.getInstance(), "É¾³ýÊ§°Ü");
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
