package com.yixianqian.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.customewidget.MyAlertDialog;
import com.yixianqian.db.ConversationDbService;
import com.yixianqian.db.FlipperDbService;
import com.yixianqian.table.FlipperTable;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.FriendPreference;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

/**
 * 类名称：HomeDialogFragment
 * 类描述：主页面聊天界面
 * 创建人： 张帅
 * 创建时间：2014年7月14日 下午3:54:20
 *
 */
public class HomeDialogFragment extends DialogFragment {
	private View rootView;
	private ListView menuitemListView;
	private List<String> menuitemList;
	private int flipperCount = -1;
	private FlipperDbService flipperDbService;
	private UserPreference userPreference;
	private HomeDialogAdapter mAdapter;
	private MyAlertDialog myAlertDialog;
	private FriendPreference friendPreference;
	private ConversationDbService conversationDbService;

	/**
	 * 创建实例
	 * @return
	 */
	static HomeDialogFragment newInstance() {
		HomeDialogFragment f = new HomeDialogFragment();
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setStyle(DialogFragment.STYLE_NO_TITLE, 0);
		friendPreference = BaseApplication.getInstance().getFriendPreference();
		flipperDbService = FlipperDbService.getInstance(getActivity());
		conversationDbService = ConversationDbService.getInstance(getActivity());
		userPreference = BaseApplication.getInstance().getUserPreference();
		mAdapter = new HomeDialogAdapter();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		flipperCount = flipperDbService.getFlipperCount();
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_dialog, container, false);
		menuitemListView = (ListView) rootView.findViewById(R.id.dialog_listview);
		menuitemList = new ArrayList<String>();
		final int stateID = userPreference.getU_stateid();

		menuitemList.add("添加情侣");
		menuitemList.add("怦然心动");
		if (stateID == 3) {
			menuitemList.add("解除心动关系");
		}

		menuitemListView.setAdapter(mAdapter);
		menuitemListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = null;
				if (position == 1) {
					//如果是单身状态
					if (stateID == 4) {
						intent = new Intent(getActivity(), LoveVertifyActivity.class);
					} else if (stateID == 2) {//如果是情侣
						ToastTool.showLong(getActivity(), "您正在恋爱，不能接受爱情验证哦~~！");
					} else if (stateID == 3) {//如果是心动
						ToastTool.showLong(getActivity(), "您正在和ta砰然心动，不能贪心哦~~！");
					}
				} else if (position == 0) {
					//如果是单身状态
					if (stateID == 4) {
						intent = new Intent(getActivity(), AddLoverActivity.class);
					} else {
						ToastTool.showLong(getActivity(), "只有单身状态才可以添加情侣哦~！");
					}
				} else if (position == 2) {
					showDeleteDialog();
				}
				if (intent != null) {
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
					HomeDialogFragment.this.dismiss();
				}
			}
		});
		return rootView;
	}

	//显示删除心动或情侣对话窗口
	private void showDeleteDialog() {
		//如果是心动关系
		myAlertDialog = new MyAlertDialog(getActivity());
		myAlertDialog.setTitle("提示");
		myAlertDialog.setMessage("是否解除您和\"" + friendPreference.getName() + "\"的心动关系？解除后你们将不能再取得联系。系统会继续为您牵线搭桥。");
		View.OnClickListener comfirm = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myAlertDialog.dismiss();
				RequestParams params = new RequestParams();
				params.put(FlipperTable.F_USERID, userPreference.getU_id());
				params.put(FlipperTable.F_FLIPPERID, friendPreference.getF_id());
				TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {

					@Override
					public void onSuccess(int statusCode, Header[] headers, String response) {
						// TODO Auto-generated method stub
						FlipperDbService flipperDbService = FlipperDbService.getInstance(getActivity());
						flipperDbService.deleteFlipperByUserId(friendPreference.getF_id());
						conversationDbService.deleteConByUserID(friendPreference.getF_id());

						//								new SendNotifyTask(userPreference.getName() + "和您解除了心动关系", userPreference.getName(),
						//										friendPreference.getBpush_UserID()).send();

						//删除好友
						try {
							EMContactManager.getInstance().deleteContact("" + friendPreference.getF_id());
						} catch (EaseMobException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//删除会话
						//	EMChatManager.getInstance().deleteConversation("" + friendPreference.getF_id());

						friendPreference.clear();
						userPreference.setU_stateid(4);

						HomeDialogFragment.this.dismiss();
						MainActivity activity = (MainActivity) getActivity();
						activity.refresh();
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
						// TODO Auto-generated method stub
						ToastTool.showShort(getActivity(), "解除心动关系失败！");
					}
				};
				AsyncHttpClientTool.post("deleteflipper", params, responseHandler);
			}
		};
		View.OnClickListener cancle = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myAlertDialog.dismiss();
			}
		};
		myAlertDialog.setPositiveButton("解除", comfirm);
		myAlertDialog.setNegativeButton("取消", cancle);
		myAlertDialog.show();
	}

	/**
	 * 
	 * 类名称：HomeDialogAdapter
	 * 类描述：适配器
	 * 创建人： 张帅
	 * 创建时间：2014年7月14日 下午3:27:58
	 *
	 */
	private class HomeDialogAdapter extends BaseAdapter {
		private class ViewHolder {
			public TextView itemName;
			public TextView righttext;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return menuitemList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = convertView;
			final ViewHolder holder;

			if (convertView == null) {
				view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_listview_item, null);
				holder = new ViewHolder();
				holder.itemName = (TextView) view.findViewById(R.id.item_name);
				holder.righttext = (TextView) view.findViewById(R.id.righttext);
				view.setTag(holder); // 给View添加一个格外的数据 
			} else {
				holder = (ViewHolder) view.getTag(); // 把数据取出来  
			}
			holder.itemName.setText(menuitemList.get(position));

			//如果是爱情验证
			if (position == 1) {
				if (flipperCount > 0) {
					holder.righttext.setText("" + flipperCount);
					holder.righttext.setVisibility(View.VISIBLE);
				} else {
					holder.righttext.setVisibility(View.GONE);
				}
			} else {
				holder.righttext.setVisibility(View.GONE);
			}
			return view;
		}
	}
}
