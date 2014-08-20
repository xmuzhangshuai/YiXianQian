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
import com.yixianqian.base.BaseApplication;
import com.yixianqian.config.Constants;
import com.yixianqian.customewidget.MyAlertDialog;
import com.yixianqian.db.ConversationDbService;
import com.yixianqian.db.FlipperDbService;
import com.yixianqian.entities.Flipper;
import com.yixianqian.jsonobject.JsonUser;
import com.yixianqian.table.FlipperRequestTable;
import com.yixianqian.table.FlipperTable;
import com.yixianqian.table.LoversTable;
import com.yixianqian.table.ShieldersTable;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.FastJsonTool;
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
	private ProgressDialog progressDialog;
	private JsonUser jsonUser;

	/**
	 * ����ʵ��
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

		strangerList.add("�����û�");
		strangerList.add("��Ȼ�Ķ�");

		flipperList.add("�����ϵ");
		flipperList.add("��Ϊ����");

		loverList.add("�����ϵ");
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

				//�����İ����
				if (type == Constants.PersonDetailType.SINGLE) {
					if (position == 0) {
						shield();
					} else if (position == 1) {
						sendLoveReuest(userId);
					}
				}
				//������Ķ���ϵ
				else if (type == Constants.PersonDetailType.FLIPPER) {
					if (position == 0) {
						showDeletDialog();
					} else if (position == 1) {
						becomeLover(friendPreference.getF_id());
					}
				}
				//���������
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
	 * �����ƣ�HomeDialogAdapter
	 * ��������������
	 * �����ˣ� ��˧
	 * ����ʱ�䣺2014��7��14�� ����3:27:58
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
				view.setTag(holder); // ��View���һ����������� 
			} else {
				holder = (ViewHolder) view.getTag(); // ������ȡ����  
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

	//��ʾɾ���Ķ������¶Ի�����
	void showDeletDialog() {
		//������Ķ���ϵ
		if (userPreference.getU_stateid() == 3) {
			myAlertDialog = new MyAlertDialog(getActivity());
			myAlertDialog.setTitle("��ʾ");
			myAlertDialog.setMessage("�Ƿ�������\"" + friendPreference.getName() + "\"���Ķ���ϵ����������ǽ�������ȡ����ϵ��ϵͳ�����Ϊ��ǣ�ߴ��š�");
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
							//								new SendNotifyTask(userPreference.getName() + "����������Ķ���ϵ", userPreference.getName(),
							//										friendPreference.getBpush_UserID()).send();

							//ɾ���Ự
							EMChatManager.getInstance().deleteConversation("" + friendPreference.getF_id());
							//ɾ������
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
							getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
							getActivity().finish();
						}

						@Override
						public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
							// TODO Auto-generated method stub
							ToastTool.showShort(getActivity(), "����Ķ���ϵʧ�ܣ�");
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
			myAlertDialog.setPositiveButton("ɾ��", comfirm);
			myAlertDialog.setNegativeButton("ȡ��", cancle);
			myAlertDialog.show();
		}
		//���������
		if (userPreference.getU_stateid() == 2) {
			myAlertDialog = new MyAlertDialog(getActivity());
			myAlertDialog.setTitle("��ʾ");
			myAlertDialog.setMessage("�Ƿ������¹�ϵ�����������\"" + friendPreference.getName() + "\"��������ȡ����ϵ��ϵͳ�����Ϊ��ǣ�ߴ��š�");
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
							//							new SendNotifyTask(userPreference.getName() + "������������¹�ϵ", userPreference.getName(),
							//									friendPreference.getBpush_UserID()).send();
							conversationDbService.conversationDao.delete(conversationDbService
									.getConversationByUser(friendPreference.getF_id()));
							//ɾ���Ự
							EMChatManager.getInstance().deleteConversation("" + friendPreference.getF_id());
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
							ToastTool.showShort(getActivity(), "�������ʧ�ܣ�");
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
			myAlertDialog.setPositiveButton("ɾ��", comfirm);
			myAlertDialog.setNegativeButton("ȡ��", cancle);
			myAlertDialog.show();
		}
	}

	/**
	 * �Ķ�����Ϊ����
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
				ToastTool.showLong(getActivity(), "��ϲ������" + friendPreference.getName() + "��Ϊ�����£�");
				userPreference.setU_stateid(2);

				//ɾ���Ķ���ϵ�������ź���
				try {
					EMContactManager.getInstance().deleteContact("" + friendPreference.getF_id());
				} catch (EaseMobException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

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
	 * �첽���Ͱ�����֤
	 * @param userID
	 */
	private void sendLoveReuest(final int filpperId) {
		if (filpperId > 0) {
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
					progressDialog.setMessage("���ڷ�������...");
					progressDialog.setCanceledOnTouchOutside(false);
					progressDialog.show();
				}

				@Override
				public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
					// TODO Auto-generated method stub
					ToastTool.showLong(getActivity(), arg2);
					PersonDetailDialog.this.dismiss();
				}

				@Override
				public void onSuccess(int arg0, Header[] arg1, String arg2) {
					// TODO Auto-generated method stub
					addContact(filpperId);
					PersonDetailDialog.this.dismiss();
				}

				@Override
				public void onFinish() {
					// TODO Auto-generated method stub
					super.onFinish();
				}
			};
			AsyncHttpClientTool.post(getActivity(), url, params, responseHandler);
		}
	}

	/**
	 *  ���contact
	 * @param view
	 */
	public void addContact(final int flipperId) {

		new Thread(new Runnable() {
			public void run() {
				try {
					//��Ӻ���
					EMContactManager.getInstance().addContact("" + flipperId, userPreference.getName() + "������Ȼ�Ķ���");

					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							saveFlipper(flipperId);
							progressDialog.dismiss();
							getActivity().finish();
							ToastTool.showLong(getActivity(), "������֤�ѷ��ͣ��ȴ��Է�ͬ��");
						}
					});
				} catch (final Exception e) {
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							ToastTool.showLong(getActivity(), "������֤����ʧ��:" + e.getMessage());
						}
					});
				}
			}
		}).start();
	}

	/**
	 * �洢�����ݿ⣬�Ѿ�ͬ��
	 */
	public void saveFlipper(final int flipperId) {
		FlipperDbService flipperDbService = FlipperDbService.getInstance(getActivity());
		Flipper flipper = flipperDbService.getFlipperByUserId(flipperId);
		//������ݿ��д��ڸ��û������������״̬
		if (flipper != null) {
			flipper.setIsRead(true);
			flipper.setTime(new Date());
			flipper.setStatus(Constants.FlipperStatus.INVITE);
			flipper.setType(Constants.FlipperType.TO);
			flipperDbService.flipperDao.update(flipper);
		} else {
			getUser(flipperId);
		}
	}

	/**
	 * 	�����ȡUser��Ϣ
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
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				ToastTool.showLong(getActivity(), "����������");
			}
		};
		AsyncHttpClientTool.post(getActivity(), "getuserbyid", params, responseHandler);
	}

	/**
	 * �����û�
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
					ToastTool.showLong(getActivity(), "ϵͳ�������ٸ����Ƽ�����");
					PersonDetailDialog.this.dismiss();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				ToastTool.showLong(getActivity(), "����������");
				PersonDetailDialog.this.dismiss();
			}
		};
		AsyncHttpClientTool.post(getActivity(), "addshielderrecord", params, responseHandler);
	}

	public Dialog showProgressDialog() {
		ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setMessage("���Ժ�����Ŭ������...");
		dialog.setCancelable(false);
		dialog.show();
		return dialog;
	}
}
