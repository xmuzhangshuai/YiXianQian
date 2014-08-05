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
				if (type == 1) {
					if (position == 0) {
						shield();
					} else if (position == 1) {
						sendLoveReuest(userId);
					}
				}
				//������Ķ���ϵ
				else if (type == 2) {
					if (position == 0) {
						showDeletDialog();
					} else if (position == 1) {
						becomeLover(friendPreference.getF_id());
					}
				}
				//���������
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
				view.setTag(holder); // ��View���һ����������� 
			} else {
				holder = (ViewHolder) view.getTag(); // ������ȡ����  
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
							new SendNotifyTask(userPreference.getName() + "����������Ķ���ϵ", userPreference.getName(),
									friendPreference.getBpush_UserID()).send();
							friendPreference.clear();
							userPreference.setU_stateid(4);
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
							conversationDbService.conversationDao.delete(conversationDbService
									.getConversationByUser(friendPreference.getF_id()));
							new SendNotifyTask(userPreference.getName() + "������������¹�ϵ", userPreference.getName(),
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
							ToastTool.showShort(getActivity(), "�������ʧ�ܣ�");
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
					ToastTool.showLong(getActivity(), "������֤�ѷ��ͣ�");
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
