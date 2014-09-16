package com.yixianqian.ui;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.Header;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.yixianqian.R;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.base.BaseV4Fragment;
import com.yixianqian.config.Constants;
import com.yixianqian.config.Constants.Config;
import com.yixianqian.jsonobject.JsonBridgeCommentMessage;
import com.yixianqian.table.LoveBridgeItemTable;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.AsyncHttpClientImageSound;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.DateTimeTools;
import com.yixianqian.utils.FastJsonTool;
import com.yixianqian.utils.ImageLoaderTool;
import com.yixianqian.utils.LogTool;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

/**
 * �����ƣ�LoveBridgeMsgFragment
 * ��������ȵ�ţ���Ϣ
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��9��11�� ����4:55:42
 *
 */
public class LoveBridgeMsgFragment extends BaseV4Fragment {
	private View rootView;// ��View
	private PullToRefreshListView messageListView;

	protected boolean pauseOnScroll = false;
	protected boolean pauseOnFling = true;
	private int pageNow = 0;//����ҳ��
	private MessageAdapter mAdapter;
	private UserPreference userPreference;
	private LinkedList<JsonBridgeCommentMessage> messageList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		userPreference = BaseApplication.getInstance().getUserPreference();
		messageList = new LinkedList<JsonBridgeCommentMessage>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_love_bridge_msg, container, false);

		findViewById();// ��ʼ��views
		initView();

		//��ȡ����
		getDataTask(pageNow);

		messageListView.setMode(Mode.BOTH);
		mAdapter = new MessageAdapter();
		messageListView.setAdapter(mAdapter);
		return rootView;
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		messageListView = (PullToRefreshListView) rootView.findViewById(R.id.lovebridge_list);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		//������������ˢ���¼�
		messageListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				String label = DateUtils.formatDateTime(getActivity().getApplicationContext(),
						System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				pageNow = 0;
				getDataTask(pageNow);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				String label = DateUtils.formatDateTime(getActivity().getApplicationContext(),
						System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				if (pageNow >= 0)
					++pageNow;
				getDataTask(pageNow);
			}
		});
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		messageListView.setOnScrollListener(new PauseOnScrollListener(imageLoader, pauseOnScroll, pauseOnFling));
	}

	/**
	 * �����ȡ����
	 */
	private void getDataTask(int p) {
		final int page = p;
		RequestParams params = new RequestParams();
		params.put("page", pageNow);
		params.put(LoveBridgeItemTable.N_USERID, userPreference.getU_id());
		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					LogTool.i("LoveBridgeMsgFragment", "����" + messageList.size());
					List<JsonBridgeCommentMessage> temp = FastJsonTool.getObjectList(response,
							JsonBridgeCommentMessage.class);
					if (temp != null) {
						//������״λ�ȡ����
						if (page == 0) {
							if (temp.size() < Config.PAGE_NUM) {
								pageNow = -1;
							}
							messageList = new LinkedList<JsonBridgeCommentMessage>();
							messageList.addAll(temp);
						}
						//����ǻ�ȡ����
						else if (page > 0) {
							if (temp.size() < Config.PAGE_NUM) {
								pageNow = -1;
								ToastTool.showShort(getActivity(), "û�и����ˣ�");
							}
							messageList.addAll(temp);
						}
						mAdapter.notifyDataSetChanged();
					}
				}
				messageListView.onRefreshComplete();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				LogTool.e("LoveBridgeMsgFragment", "��ȡ�б�ʧ��");
				messageListView.onRefreshComplete();
			}
		};
		AsyncHttpClientTool.post(getActivity(), "getbridgemessagelist", params, responseHandler);
	}

	/**
	 * ����ID��ȡJsonLoveBridgeItem����
	 */
	private void goToDetail(int loveItemId) {
		RequestParams params = new RequestParams();
		params.put(LoveBridgeItemTable.N_ID, loveItemId);
		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					if (!TextUtils.isEmpty(response)) {
						int loveItemId = Integer.parseInt(response);
						if (loveItemId > 0) {
							startActivity(new Intent(getActivity(), LoveBridgeDetailActivity.class).putExtra(
									LoveBridgeDetailActivity.LOVE_BRIDGE_ITEM, loveItemId));
							getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
						}
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				LogTool.e("LoveBridgeMsgFragment", "��ȡ����ʧ��");
			}
		};
		AsyncHttpClientTool.post(getActivity(), "", params, responseHandler);
	}

	/**
	 * 
	 * �����ƣ�MessageAdapter
	 * ��������������
	 * �����ˣ� ��˧
	 * ����ʱ�䣺2014��9��14�� ����3:41:15
	 *
	 */
	class MessageAdapter extends BaseAdapter {
		private class ViewHolder {
			public ImageView headImageView;
			public TextView nameTextView;
			public ImageView genderImageView;
			public TextView timeTextView;
			public TextView contentTextView;
			public TextView loveItemTextView;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return messageList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return messageList.get(position);
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
			final JsonBridgeCommentMessage message = messageList.get(position);
			if (message == null) {
				return null;
			}

			final ViewHolder holder;
			if (convertView == null) {
				view = LayoutInflater.from(getActivity()).inflate(R.layout.love_bridge_msg_list_item, null);
				holder = new ViewHolder();
				holder.headImageView = (ImageView) view.findViewById(R.id.head_image);
				holder.nameTextView = (TextView) view.findViewById(R.id.name);
				holder.genderImageView = (ImageView) view.findViewById(R.id.gender);
				holder.timeTextView = (TextView) view.findViewById(R.id.time);
				holder.contentTextView = (TextView) view.findViewById(R.id.content);
				holder.loveItemTextView = (TextView) view.findViewById(R.id.love_bridge_item);
				view.setTag(holder); // ��View���һ����������� 
			} else {
				holder = (ViewHolder) view.getTag(); // ������ȡ����  
			}

			//����ͷ��
			if (!TextUtils.isEmpty(message.getSmall_avatar())) {
				imageLoader.displayImage(AsyncHttpClientImageSound.getAbsoluteUrl(message.getSmall_avatar()),
						holder.headImageView, ImageLoaderTool.getHeadImageOptions(10));

				if (userPreference.getU_id() != message.getUserid()) {
					//���ͷ���������ҳ��
					holder.headImageView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(getActivity(), PersonDetailActivity.class);
							intent.putExtra(PersonDetailActivity.PERSON_TYPE, Constants.PersonDetailType.SINGLE);
							intent.putExtra(UserTable.U_ID, message.getUserid());
							startActivity(intent);
							getActivity().overridePendingTransition(R.anim.zoomin2, R.anim.zoomout);
						}
					});
				}
			}

			//��������
			holder.contentTextView.setText(message.getCommentcontent());

			//��������
			holder.nameTextView.setText(message.getUsername());

			//�����Ա�
			if (message.getGender().equals(Constants.Gender.MALE)) {
				holder.genderImageView.setImageResource(R.drawable.male);
			} else {
				holder.genderImageView.setImageResource(R.drawable.female);
			}

			//��������
			holder.timeTextView.setText(DateTimeTools.DateToString(message.getCommenttime()));

			holder.loveItemTextView.setText(message.getMessage());

			holder.loveItemTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					goToDetail(message.getMessageid());
				}
			});

			return view;
		}
	}

}
