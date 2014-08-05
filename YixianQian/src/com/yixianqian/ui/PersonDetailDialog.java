package com.yixianqian.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.baidupush.SendNotifyTask;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.customewidget.MyAlertDialog;
import com.yixianqian.db.ConversationDbService;
import com.yixianqian.table.FlipperRequestTable;
import com.yixianqian.table.FlipperTable;
import com.yixianqian.table.LoversTable;
import com.yixianqian.table.ShieldersTable;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.FriendPreference;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

public class PersonDetailDialog extends DialogFragment {
	private View rootView;
	private ListView menuitemListView;
	private List<String> strangerList;
	private List<String> flipperList;
	private List<String> loverList;
	private int type;
	private int userId;
	private UserPreference userPreference;
	private FriendPreference friendPreference;
	private MyAlertDialog myAlertDialog;
	private ConversationDbService conversationDbService;

	/**
	 * 创建实例
	 * @return
	 */
	static PersonDetailDialog newInstance() {
		PersonDetailDialog f = new PersonDetailDialog();
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_TITLE, 0);

		type = getArguments().getInt(PersonDetailActivity.PERSON_TYPE);
		userId = getArguments().getInt(UserTable.U_ID);
		strangerList = new ArrayList<String>();
		flipperList = new ArrayList<String>();
		loverList = new ArrayList<String>();
		userPreference = BaseApplication.getInstance().getUserPreference();
		friendPreference = BaseApplication.getInstance().getFriendPreference();
		conversationDbService = ConversationDbService.getInstance(getActivity());

		strangerList.add("屏蔽用户");
		strangerList.add("怦然心动");

		flipperList.add("解除关系");
		flipperList.add("成为情侣");

		loverList.add("解除关系");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_dialog, container, false);
		menuitemListView = (ListView) rootView.findViewById(R.id.dialog_listview);

		menuitemListView.setAdapter(new DialogAdapter());
		menuitemListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub

				//如果是陌生人
				if (type == 1) {
					if (position == 0) {
						shield();
					} else if (position == 1) {
						sendLoveReuest(userId);
					}
				}
				//如果是心动关系
				else if (type == 2) {
					if (position == 0) {
						showDeletDialog();
					} else if (position == 1) {
						becomeLover(friendPreference.getF_id());
					}
				}
				//如果是情侣
				else if (type == 3) {
					if (position == 0) {
						showDeletDialog();
					}
				}
			}
		});
		return rootView;
	}

	/**
	 * 
	 * 类名称：HomeDialogAdapter
	 * 类描述：适配器
	 * 创建人： 张帅
	 * 创建时间：2014年7月14日 下午3:27:58
	 *
	 */
	private class DialogAdapter extends BaseAdapter {
		private class ViewHolder {
			public TextView itemName;
			public ImageView leftImage;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (type == 1) {
				return strangerList.size();
			} else if (type == 2) {
				return flipperList.size();
			} else if (type == 3) {
				return loverList.size();
			} else {
				return 0;
			}
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
				holder.leftImage = (ImageView) view.findViewById(R.id.leftimage);
				view.setTag(holder); // 给View添加一个格外的数据 
			} else {
				holder = (ViewHolder) view.getTag(); // 把数据取出来  
			}

			if (type == 1) {
				holder.itemName.setText(strangerList.get(position));
				if (position == 1) {
					holder.leftImage.setVisibility(View.VISIBLE);
					holder.leftImage.setImageResource(R.drawable.two_heart);
				} else {
					holder.leftImage.setVisibility(View.GONE);
				}
			} else if (type == 2) {
				holder.itemName.setText(flipperList.get(position));
				if (position == 1) {
					holder.leftImage.setVisibility(View.VISIBLE);
					holder.leftImage.setImageResource(R.drawable.two_heart);
				} else {
					holder.leftImage.setVisibility(View.GONE);
				}
			} else if (type == 3) {
				holder.itemName.setText(loverList.get(position));
			}

			return view;
		}
	}

	//显示删除心动或情侣对话窗口
	void showDeletDialog() {
		//如果是心动关系
		if (userPreference.getU_stateid() == 3) {
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
							conversationDbService.conversationDao.delete(conversationDbService
									.getConversationByUser(friendPreference.getF_id()));
							new SendNotifyTask(userPreference.getName() + "和您解除了心动关系", userPreference.getName(),
									friendPreference.getBpush_UserID()).send();
							friendPreference.clear();
							userPreference.setU_stateid(4);
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
			myAlertDialog.setPositiveButton("删除", comfirm);
			myAlertDialog.setNegativeButton("取消", cancle);
			myAlertDialog.show();
		}
		//如果是情侣
		if (userPreference.getU_stateid() == 2) {
			myAlertDialog = new MyAlertDialog(getActivity());
			myAlertDialog.setTitle("提示");
			myAlertDialog.setMessage("是否解除情侣关系？解除后您和\"" + friendPreference.getName() + "\"将不能再取得联系。系统会继续为您牵线搭桥。");
			View.OnClickListener comfirm = new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					myAlertDialog.dismiss();

					RequestParams params = new RequestParams();
					params.put(LoversTable.L_USERID, userPreference.getU_id());
					params.put(LoversTable.L_LOVERID, friendPreference.getF_id());
					TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {

						@Override
						public void onSuccess(int statusCode, Header[] headers, String response) {
							// TODO Auto-generated method stub
							conversationDbService.conversationDao.delete(conversationDbService
									.getConversationByUser(friendPreference.getF_id()));
							new SendNotifyTask(userPreference.getName() + "和您解除了情侣关系", userPreference.getName(),
									friendPreference.getBpush_UserID()).send();
							friendPreference.clear();
							userPreference.setU_stateid(4);
							PersonDetailDialog.this.dismiss();
							startActivity(new Intent(getActivity(), MainActivity.class));
							getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
							getActivity().finish();
						}

						@Override
						public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
							// TODO Auto-generated method stub
							ToastTool.showShort(getActivity(), "解除情侣失败！");
							PersonDetailDialog.this.dismiss();
						}
					};
					AsyncHttpClientTool.post("deletelover", params, responseHandler);
				}
			};
			View.OnClickListener cancle = new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					myAlertDialog.dismiss();
				}
			};
			myAlertDialog.setPositiveButton("删除", comfirm);
			myAlertDialog.setNegativeButton("取消", cancle);
			myAlertDialog.show();
		}
	}

	/**
	 * 心动升级为情侣
	 */
	private void becomeLover(int filpperId) {
		RequestParams params = new RequestParams();
		int myUserID = userPreference.getU_id();
		params.put(LoversTable.L_USERID, myUserID);
		params.put(LoversTable.L_LOVERID, filpperId);
		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
			Dialog dialog;

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
				dialog = showProgressDialog();
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				ToastTool.showLong(getActivity(), "恭喜！您和" + friendPreference.getName() + "成为了情侣！");
				userPreference.setU_stateid(2);
				PersonDetailDialog.this.dismiss();
				getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
				getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.push_right_out);
				getActivity().finish();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				ToastTool.showLong(getActivity(), errorResponse);
				PersonDetailDialog.this.dismiss();
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				super.onFinish();
				dialog.dismiss();
			}
		};
		AsyncHttpClientTool.post(getActivity(), "buildlover", params, responseHandler);
	}

	/**
	 * 异步发送爱情验证
	 * @param userID
	 */
	private void sendLoveReuest(int filpperId) {
		if (filpperId > 0) {
			String url = "addflipperrequest";
			RequestParams params = new RequestParams();
			int myUserID = userPreference.getU_id();
			params.put(FlipperRequestTable.FR_USERID, myUserID);
			params.put(FlipperRequestTable.FR_FLIPPERID, filpperId);
			TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, Header[] headers, String response) {
					// TODO Auto-generated method stub
					ToastTool.showLong(getActivity(), "爱情验证已发送！");
					PersonDetailDialog.this.dismiss();
					getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
					getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
					getActivity().finish();
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
					// TODO Auto-generated method stub
					ToastTool.showLong(getActivity(), errorResponse);
					PersonDetailDialog.this.dismiss();
				}
			};
			AsyncHttpClientTool.post(getActivity(), url, params, responseHandler);
		}
	}

	/**
	 * 屏蔽用户
	 */
	private void shield() {
		RequestParams params = new RequestParams();
		params.put(ShieldersTable.S_USERID, userPreference.getU_id());
		params.put(ShieldersTable.S_SHIELDERID, userId);

		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {
			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					ToastTool.showLong(getActivity(), "系统将不会再给您推荐此人");
					PersonDetailDialog.this.dismiss();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				ToastTool.showLong(getActivity(), "服务器错误");
				PersonDetailDialog.this.dismiss();
			}
		};
		AsyncHttpClientTool.post(getActivity(), "addshielderrecord", params, responseHandler);
	}

	public Dialog showProgressDialog() {
		ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setMessage("请稍候，正在努力加载...");
		dialog.setCancelable(false);
		dialog.show();
		return dialog;
	}
}
