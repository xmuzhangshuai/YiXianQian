package com.yixianqian.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.baidupush.SendMsgAsyncTask;
import com.yixianqian.baidupush.SendNotifyTask;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.config.Constants;
import com.yixianqian.config.Constants.FlipperStatus;
import com.yixianqian.config.Constants.FlipperType;
import com.yixianqian.config.Constants.MessageType;
import com.yixianqian.customewidget.MyAlertDialog;
import com.yixianqian.db.ConversationDbService;
import com.yixianqian.db.FlipperDbService;
import com.yixianqian.entities.Flipper;
import com.yixianqian.jsonobject.JsonMessage;
import com.yixianqian.jsonobject.JsonUser;
import com.yixianqian.table.FlipperRequestTable;
import com.yixianqian.table.FlipperTable;
import com.yixianqian.table.LoversTable;
import com.yixianqian.table.ShieldersTable;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.FastJsonTool;
import com.yixianqian.utils.FriendPreference;
import com.yixianqian.utils.LogTool;
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
	private ProgressDialog progressDialog;
	private JsonUser jsonUser;

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

		flipperList.add("解除心动关系");
		flipperList.add("成为情侣");

		loverList.add("解除情侣关系");
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
				if (type == Constants.PersonDetailType.SINGLE) {
					if (position == 0) {
						shield();
					} else if (position == 1) {
						sendLoveReuest(userId);
					}
				}
				//如果是心动关系
				else if (type == Constants.PersonDetailType.FLIPPER) {
					if (position == 0) {
						showDeletDialog();
					} else if (position == 1) {
						becomeLover(friendPreference.getF_id());
					}
				}
				//如果是情侣
				else if (type == Constants.PersonDetailType.LOVER) {
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
			if (type == Constants.PersonDetailType.SINGLE) {
				return strangerList.size();
			} else if (type == Constants.PersonDetailType.FLIPPER) {
				return flipperList.size();
			} else if (type == Constants.PersonDetailType.LOVER) {
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

			if (type == Constants.PersonDetailType.SINGLE) {
				holder.itemName.setText(strangerList.get(position));
				if (position == 1) {
					holder.leftImage.setVisibility(View.VISIBLE);
					holder.leftImage.setImageResource(R.drawable.two_heart);
				} else {
					holder.leftImage.setVisibility(View.GONE);
				}
			} else if (type == Constants.PersonDetailType.FLIPPER) {
				holder.itemName.setText(flipperList.get(position));
				if (position == 1) {
					holder.leftImage.setVisibility(View.VISIBLE);
					holder.leftImage.setImageResource(R.drawable.two_heart);
				} else {
					holder.leftImage.setVisibility(View.GONE);
				}
			} else if (type == Constants.PersonDetailType.LOVER) {
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
							//								new SendNotifyTask(userPreference.getName() + "和您解除了心动关系", userPreference.getName(),
							//										friendPreference.getBpush_UserID()).send();

							//删除会话
							EMChatManager.getInstance().deleteConversation("" + friendPreference.getF_id());
							//删除好友
							try {
								EMContactManager.getInstance().deleteContact("" + friendPreference.getF_id());
							} catch (EaseMobException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							friendPreference.clear();
							userPreference.setU_stateid(4);
							PersonDetailDialog.this.dismiss();
							startActivity(new Intent(getActivity(), MainActivity.class));
							getActivity().finish();
							getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
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
							new SendNotifyTask(userPreference.getName() + "和您解除了情侣关系", userPreference.getName(),
									friendPreference.getBpush_UserID()).send();

							conversationDbService.conversationDao.delete(conversationDbService
									.getConversationByUser(friendPreference.getF_id()));
							//删除会话
							EMChatManager.getInstance().deleteConversation("" + friendPreference.getF_id());
							//删除好友
							try {
								EMContactManager.getInstance().deleteContact("" + friendPreference.getF_id());
							} catch (EaseMobException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							friendPreference.clear();
							userPreference.setU_stateid(4);
							PersonDetailDialog.this.dismiss();
							startActivity(new Intent(getActivity(), MainActivity.class));
							getActivity().finish();
							getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
						}

						@Override
						public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
							// TODO Auto-generated method stub
							ToastTool.showShort(getActivity(), "解除情侣失败！");
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
				getActivity().finish();
				getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.push_right_out);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				LogTool.e("DayRecommendActivity", "错误原因" + errorResponse);
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
	private void sendLoveReuest(final int filpperId) {
		if (filpperId > 0) {
			FlipperDbService flipperDbService = FlipperDbService.getInstance(getActivity());
			Flipper flipper = flipperDbService.getFlipperByUserId(filpperId);
			if (flipper != null && flipper.getStatus().equals(FlipperStatus.BEINVITEED)) {//如果被邀请过
				LogTool.e("PersonDetailActivity", "已经被邀请过了");
				String name = flipper.getNickname();
				if (!TextUtils.isEmpty(flipper.getRealname())) {
					name = flipper.getRealname();
				}
				final MyAlertDialog myAlertDialog = new MyAlertDialog(getActivity());
				myAlertDialog.setShowCancel(false);
				myAlertDialog.setTitle("提示");
				myAlertDialog.setMessage("您已经被 " + name + " 邀请过，可以在爱情验证页面点击“我也对他砰然心动”来成为心动关系");
				View.OnClickListener comfirm = new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						myAlertDialog.dismiss();
					}
				};
				myAlertDialog.setPositiveButton("确定", comfirm);
				myAlertDialog.show();
			} else {
				LogTool.e("PersonDetailActivity", "发送爱情验证");
				String url = "addflipperrequest";
				RequestParams params = new RequestParams();
				int myUserID = userPreference.getU_id();
				params.put(FlipperRequestTable.FR_USERID, myUserID);
				params.put(FlipperRequestTable.FR_FLIPPERID, filpperId);
				TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();
						progressDialog = new ProgressDialog(getActivity());
						progressDialog.setMessage("正在发送请求...");
						progressDialog.setCanceledOnTouchOutside(false);
						progressDialog.show();
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
						// TODO Auto-generated method stub
						LogTool.e("DayRecommendActivity", "错误原因" + errorResponse);
						getActivity().finish();
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers, String response) {
						// TODO Auto-generated method stub
						ToastTool.showLong(getActivity(), "爱情验证已发送！等待对方同意");
						saveFlipper(filpperId, response);
						
						startActivity(new Intent(getActivity(), MainActivity.class));
						getActivity().finish();
						getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						super.onFinish();
						progressDialog.dismiss();
					}
				};
				AsyncHttpClientTool.post(getActivity(), url, params, responseHandler);
			}
		}
	}

	/**
	 *  添加contact
	 * @param view
	 */
	public void addContact(final int flipperId) {
		LogTool.i("personDetailDialog", "环信添加好友");
		new Thread(new Runnable() {
			public void run() {
				try {
					//添加好友
					EMContactManager.getInstance().addContact("" + flipperId, userPreference.getName() + "对您砰然心动！");
				} catch (final Exception e) {
				}
			}
		}).start();
	}

	/**
	 * 存储到数据库，已经同意
	 */
	public void saveFlipper(final int flipperId, String response) {
		FlipperDbService flipperDbService = FlipperDbService.getInstance(getActivity());
		Flipper flipper = flipperDbService.getFlipperByUserId(flipperId);
		//如果数据库中存在该用户的请求，则更新状态
		if (flipper != null) {
			if (!flipper.getStatus().equals(FlipperStatus.INVITE)) {//如果已经请求过，则不再请求
				addContact(flipperId);
			}

			LogTool.i("personDetailDialog", "flipper已经存在，更新");
			flipper.setIsRead(true);
			flipper.setTime(new Date());
			flipper.setType(FlipperType.TO);
			flipper.setStatus(Constants.FlipperStatus.INVITE);
			flipperDbService.flipperDao.update(flipper);

			//发给对方通知
			JsonMessage jsonMessage = new JsonMessage(userPreference.getName() + "对您怦然心动",
					MessageType.MESSAGE_TYPE_FLIPPER_REQUEEST);
			new SendMsgAsyncTask(FastJsonTool.createJsonString(jsonMessage), flipper.getBpushUserID()).send();
		} else {
			//如果网络端不是未推送状态，则添加环信好友
			if (response.equals("1")) {
				addContact(flipperId);
			}
			getUser(flipperId);
		}
	}

	/**
	 * 	网络获取User信息
	 */
	private void getUser(int userId) {
		RequestParams params = new RequestParams();
		params.put(UserTable.U_ID, userId);

		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {
			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					jsonUser = FastJsonTool.getObject(response, JsonUser.class);
					if (jsonUser != null) {
						FlipperDbService flipperDbService = FlipperDbService.getInstance(getActivity());
						Flipper flipper = new Flipper(null, jsonUser.getU_id(), jsonUser.getU_bpush_user_id(),
								jsonUser.getU_bpush_channel_id(), jsonUser.getU_nickname(), jsonUser.getU_realname(),
								jsonUser.getU_gender(), jsonUser.getU_email(), jsonUser.getU_large_avatar(),
								jsonUser.getU_small_avatar(), jsonUser.getU_blood_type(), jsonUser.getU_constell(),
								jsonUser.getU_introduce(), jsonUser.getU_birthday(), new Date(), jsonUser.getU_age(),
								jsonUser.getU_vocationid(), jsonUser.getU_stateid(), jsonUser.getU_provinceid(),
								jsonUser.getU_cityid(), jsonUser.getU_schoolid(), jsonUser.getU_height(),
								jsonUser.getU_weight(), jsonUser.getU_image_pass(), jsonUser.getU_salary(), true,
								jsonUser.getU_tel(), Constants.FlipperStatus.INVITE, Constants.FlipperType.TO);
						flipperDbService.flipperDao.insert(flipper);

						//发给对方通知
						JsonMessage jsonMessage = new JsonMessage(userPreference.getName() + "对您怦然心动",
								MessageType.MESSAGE_TYPE_FLIPPER_REQUEEST);
						new SendMsgAsyncTask(FastJsonTool.createJsonString(jsonMessage), flipper.getBpushUserID())
								.send();
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				ToastTool.showLong(getActivity(), "服务器错误");
			}
		};
		AsyncHttpClientTool.post(getActivity(), "getuserbyid", params, responseHandler);
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
