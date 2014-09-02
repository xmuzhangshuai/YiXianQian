package com.yixianqian.ui;

import java.util.LinkedList;

import org.apache.http.Header;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.adapter.HomeListAdapter;
import com.yixianqian.baidupush.SendNotifyTask;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.base.BaseV4Fragment;
import com.yixianqian.customewidget.MyAlertDialog;
import com.yixianqian.db.ConversationDbService;
import com.yixianqian.db.FlipperDbService;
import com.yixianqian.entities.Conversation;
import com.yixianqian.table.FlipperTable;
import com.yixianqian.table.LoversTable;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.FriendPreference;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

/**
 * 类名称：HomeFragment 类描述：主页 创建人： 张帅 创建时间：2014年7月4日 下午5:18:18
 *
 */
public class HomeFragment extends BaseV4Fragment {
	private View rootView;// 根View
	private ImageView topNavLeftBtn;//导航条左边按钮
	private ImageView topNavRightBtn;//导航条右边按钮
	private TextView topNavText;//导航条文字
	private ImageView newMsg;
	private View right_btn_bg;
	private ListView mHomeListView;
	private TextView mEmpty;
	private HomeListAdapter mAdapter;
	private FriendPreference friendPreference;
	private UserPreference userPreference;
	private ConversationDbService conversationDbService;
	private LinkedList<Conversation> conversationList;
	private Vibrator vib;
	private View popBtn;//删除按钮
	private int currentItem = -1;
	private MyAlertDialog myAlertDialog;
	public RelativeLayout errorItem;//无法连接到网络提示
	public TextView errorText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		friendPreference = BaseApplication.getInstance().getFriendPreference();
		userPreference = BaseApplication.getInstance().getUserPreference();
		conversationDbService = ConversationDbService.getInstance(getActivity());
		conversationList = new LinkedList<Conversation>();
		if (userPreference.getU_stateid() == 2 || userPreference.getU_stateid() == 3) {
			conversationList.addAll(conversationDbService.conversationDao.loadAll());
		}
		/**震动服务*/
		vib = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_home, container, false);

		findViewById();// 初始化views
		initView();

		mAdapter = new HomeListAdapter(getActivity(), conversationList);
		mHomeListView.setAdapter(mAdapter);
		return rootView;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//如果有未读心动请求，显示红点
		if (FlipperDbService.getInstance(getActivity()).getFlipperCount() > 0) {
			showNewMsgTip(true);
		} else {
			showNewMsgTip(false);
		}

		refresh();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		topNavLeftBtn = (ImageView) rootView.findViewById(R.id.nav_left_btn);
		topNavRightBtn = (ImageView) rootView.findViewById(R.id.nav_right_btn);
		right_btn_bg = (View) rootView.findViewById(R.id.right_btn_bg);
		mHomeListView = (ListView) rootView.findViewById(R.id.recent_listview);
		mEmpty = (TextView) rootView.findViewById(R.id.empty);
		topNavText = (TextView) rootView.findViewById(R.id.nav_text);
		errorItem = (RelativeLayout) rootView.findViewById(R.id.rl_error_item);
		errorText = (TextView) errorItem.findViewById(R.id.tv_connect_errormsg);
		newMsg = (ImageView) rootView.findViewById(R.id.newmsg);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavText.setText("会话");
		//如果没有对话
		if (conversationList.size() < 1) {
			mEmpty.setVisibility(View.VISIBLE);
		}
		topNavLeftBtn.setImageResource(R.drawable.home);
		topNavRightBtn.setImageResource(R.drawable.ic_action_overflow);
		right_btn_bg.setBackgroundResource(R.drawable.sel_topnav_btn_bg);
		final View popView = getActivity().getLayoutInflater().inflate(R.layout.popup, null);
		popBtn = popView.findViewById(R.id.popup_btn);
		final PopupWindow popup = new PopupWindow(popView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		// 需要设置一下此参数，点击外边可消失 
		popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		//设置点击窗口外边窗口消失 
		popup.setOutsideTouchable(true);
		popup.setFocusable(true);

		right_btn_bg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog();
			}
		});

		mHomeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Intent toChatIntent = new Intent(getActivity(), ChatActivity.class);
				toChatIntent.putExtra("userId", "" + conversationList.get(position).getUserID());
				startActivity(toChatIntent);
				getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});

		/**
		 * 长按事件
		 */
		mHomeListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				vib.vibrate(30);
				int xoff = view.getWidth() / 2 - popView.getWidth();
				popup.showAsDropDown(view, xoff, 0);
				currentItem = position;
				return false;
			}
		});

		/**
		 * 删除按钮
		 */
		popBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popup.dismiss();
				showDeletDialog();
			}
		});

	}

	/**
	 * 刷新对话列表
	 */
	public void refresh() {
		try {
			// 可能会在子线程中调到这方法
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					conversationList.clear();
					conversationList.addAll(conversationDbService.conversationDao.loadAll());
					mAdapter.notifyDataSetChanged();
					if (conversationList.size() < 1) {
						mEmpty.setVisibility(View.VISIBLE);
					} else {
						mEmpty.setVisibility(View.GONE);
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 更新对话列表内容
	 */
	public void refreshConversation() {
		mAdapter = new HomeListAdapter(getActivity(), conversationList);
		mHomeListView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
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
					if (currentItem > -1) {

						RequestParams params = new RequestParams();
						params.put(FlipperTable.F_USERID, userPreference.getU_id());
						params.put(FlipperTable.F_FLIPPERID, friendPreference.getF_id());
						TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {

							@Override
							public void onSuccess(int statusCode, Header[] headers, String response) {
								// TODO Auto-generated method stub
								FlipperDbService flipperDbService = FlipperDbService.getInstance(getActivity());
								flipperDbService.deleteFlipperByUserId(conversationList.get(currentItem).getUserID()
										.intValue());

								conversationDbService.conversationDao.delete(conversationList.get(currentItem));
								conversationList.remove(currentItem);
								mAdapter.notifyDataSetChanged();
								currentItem = -1;
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
								//								EMChatManager.getInstance().deleteConversation("" + friendPreference.getF_id());

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
					if (currentItem > -1) {

						RequestParams params = new RequestParams();
						params.put(LoversTable.L_USERID, userPreference.getU_id());
						params.put(LoversTable.L_LOVERID, friendPreference.getF_id());
						TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {

							@Override
							public void onSuccess(int statusCode, Header[] headers, String response) {
								// TODO Auto-generated method stub
								conversationDbService.conversationDao.delete(conversationList.get(currentItem));
								conversationList.remove(currentItem);
								mAdapter.notifyDataSetChanged();
								currentItem = -1;

								new SendNotifyTask(userPreference.getName() + "和您解除了情侣关系", userPreference.getName(),
										friendPreference.getBpush_UserID()).send();

								//删除好友
								try {
									EMContactManager.getInstance().deleteContact("" + friendPreference.getF_id());
								} catch (EaseMobException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								//删除会话
								//EMChatManager.getInstance().deleteConversation("" + friendPreference.getF_id());
								friendPreference.clear();
								userPreference.setU_stateid(4);
							}

							@Override
							public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
								// TODO Auto-generated method stub
								ToastTool.showShort(getActivity(), "解除情侣失败！");
							}
						};
						AsyncHttpClientTool.post("deletelover", params, responseHandler);
					}
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
	 * 显示或隐藏新消息提示红点
	 */
	public void showNewMsgTip(final boolean show) {
		// 可能会在子线程中调到这方法
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				if (show) {
					newMsg.setVisibility(View.VISIBLE);
				} else {
					newMsg.setVisibility(View.GONE);
				}
			}
		});
	}

	/**
	 * 菜单显示
	 */
	void showDialog() {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		Fragment fragment = getFragmentManager().findFragmentByTag("dialog");
		if (fragment != null) {
			ft.remove(fragment);
		}
		ft.addToBackStack(null);

		// Create and show the dialog.
		HomeDialogFragment newFragment = HomeDialogFragment.newInstance();
		newFragment.show(ft, "dialog");
	}

}
