package com.yixianqian.ui;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.Header;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.chat.EMContactManager;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.yixianqian.R;
import com.yixianqian.baidupush.SendMsgAsyncTask;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.base.BaseV4Fragment;
import com.yixianqian.config.Constants;
import com.yixianqian.config.Constants.Config;
import com.yixianqian.config.Constants.FlipperStatus;
import com.yixianqian.config.Constants.FlipperType;
import com.yixianqian.config.Constants.MessageType;
import com.yixianqian.customewidget.MyAlertDialog;
import com.yixianqian.db.FlipperDbService;
import com.yixianqian.entities.Flipper;
import com.yixianqian.jsonobject.JsonLoveBridgeItem;
import com.yixianqian.jsonobject.JsonMessage;
import com.yixianqian.jsonobject.JsonUser;
import com.yixianqian.table.FlipperRequestTable;
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
 * �����ƣ�LoveBridgeMyFragment
 * ��������ȵ�ţ��ҵķ���
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��9��11�� ����4:55:29
 *
 */
public class LoveBridgeMyFragment extends BaseV4Fragment {
	private View rootView;// ��View
	private PullToRefreshListView loveBridgeListView;

	protected boolean pauseOnScroll = false;
	protected boolean pauseOnFling = true;
	private UserPreference userPreference;
	private LinkedList<JsonLoveBridgeItem> loveBridgeItemList;
	private int pageNow = 0;//����ҳ��
	private LoveBridgeAdapter mAdapter;
	private ProgressDialog progressDialog;
	private JsonUser jsonUser;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		userPreference = BaseApplication.getInstance().getUserPreference();
		loveBridgeItemList = new LinkedList<JsonLoveBridgeItem>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_love_bridge_my, container, false);

		findViewById();// ��ʼ��views
		initView();

		//��ȡ����
		getDataTask(pageNow);

		loveBridgeListView.setMode(Mode.BOTH);
		mAdapter = new LoveBridgeAdapter();
		loveBridgeListView.setAdapter(mAdapter);
		return rootView;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		loveBridgeListView.setOnScrollListener(new PauseOnScrollListener(imageLoader, pauseOnScroll, pauseOnFling));
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		loveBridgeListView = (PullToRefreshListView) rootView.findViewById(R.id.lovebridge_list);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		//������������ˢ���¼�
		loveBridgeListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

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

	/**
	 * �����ȡ����
	 */
	private void getDataTask(int p) {
		final int page = p;
		RequestParams params = new RequestParams();
		params.put("page", pageNow);

		//����ǵ���
		params.put(UserTable.U_SCHOOLID, userPreference.getU_schoolid());
		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					LogTool.i("LoveBridgeSchoolFragment", "����" + loveBridgeItemList.size());
					List<JsonLoveBridgeItem> temp = FastJsonTool.getObjectList(response, JsonLoveBridgeItem.class);
					if (temp != null) {
						//������״λ�ȡ����
						if (page == 0) {
							if (temp.size() < Config.PAGE_NUM) {
								pageNow = -1;
							}
							loveBridgeItemList = new LinkedList<JsonLoveBridgeItem>();
							loveBridgeItemList.addAll(temp);
						}
						//����ǻ�ȡ����
						else if (page > 0) {
							if (temp.size() < Config.PAGE_NUM) {
								pageNow = -1;
								ToastTool.showShort(getActivity(), "û�и����ˣ�");
							}
							loveBridgeItemList.addAll(temp);
						}
						mAdapter.notifyDataSetChanged();
					}
				}
				loveBridgeListView.onRefreshComplete();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				LogTool.e("LoveBridgeSchoolFragment", "��ȡ�б�ʧ��");
				loveBridgeListView.onRefreshComplete();
			}
		};
		AsyncHttpClientTool.post(getActivity(), "getlovebridgelist", params, responseHandler);
	}

	/**
	 * 
	 * �����ƣ�LoveBridgeAdapter
	 * ��������ȵ�Ź㳡������
	 * �����ˣ� ��˧
	 * ����ʱ�䣺2014��9��12�� ����3:25:07
	 *
	 */
	class LoveBridgeAdapter extends BaseAdapter {
		private class ViewHolder {
			public ImageView headImageView;
			public TextView nameTextView;
			public ImageView genderImageView;
			public TextView timeTextView;
			public TextView contentTextView;
			public ImageView contentImageView;
			public CheckBox flipperBtn;
			public TextView flipperCountTextView;
			public ImageView commentBtn;
			public TextView commentCountTextView;
			public ImageView moreBtn;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return loveBridgeItemList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return loveBridgeItemList.get(position);
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
			final JsonLoveBridgeItem loveBridgeItem = loveBridgeItemList.get(position);
			if (loveBridgeItem == null) {
				return null;
			}

			final ViewHolder holder;
			if (convertView == null) {
				view = LayoutInflater.from(getActivity()).inflate(R.layout.love_bridge_list_item, null);
				holder = new ViewHolder();
				holder.headImageView = (ImageView) view.findViewById(R.id.head_image);
				holder.nameTextView = (TextView) view.findViewById(R.id.name);
				holder.genderImageView = (ImageView) view.findViewById(R.id.gender);
				holder.timeTextView = (TextView) view.findViewById(R.id.time);
				holder.contentTextView = (TextView) view.findViewById(R.id.content);
				holder.contentImageView = (ImageView) view.findViewById(R.id.item_image);
				holder.flipperBtn = (CheckBox) view.findViewById(R.id.flipper);
				holder.flipperCountTextView = (TextView) view.findViewById(R.id.flipper_count);
				holder.commentBtn = (ImageView) view.findViewById(R.id.comment_btn);
				holder.commentCountTextView = (TextView) view.findViewById(R.id.comment_count);
				holder.moreBtn = (ImageView) view.findViewById(R.id.more);
				view.setTag(holder); // ��View���һ����������� 
			} else {
				holder = (ViewHolder) view.getTag(); // ������ȡ����  
			}

			//����ͷ��
			if (!TextUtils.isEmpty(loveBridgeItem.getN_small_avatar())) {
				imageLoader.displayImage(AsyncHttpClientImageSound.getAbsoluteUrl(loveBridgeItem.getN_small_avatar()),
						holder.headImageView, ImageLoaderTool.getHeadImageOptions(10));

				if (userPreference.getU_id() != loveBridgeItem.getN_userid()) {
					//���ͷ���������ҳ��
					holder.headImageView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(getActivity(), PersonDetailActivity.class);
							intent.putExtra(PersonDetailActivity.PERSON_TYPE, Constants.PersonDetailType.SINGLE);
							intent.putExtra(UserTable.U_ID, loveBridgeItem.getN_userid());
							startActivity(intent);
							getActivity().overridePendingTransition(R.anim.zoomin2, R.anim.zoomout);
						}
					});
				}
			}

			//������Ƭ
			if (!TextUtils.isEmpty(loveBridgeItem.getN_image())) {
				imageLoader.displayImage(AsyncHttpClientImageSound.getAbsoluteUrl(loveBridgeItem.getN_image()),
						holder.contentImageView, ImageLoaderTool.getImageOptions());
				holder.contentImageView.setVisibility(View.VISIBLE);
				holder.contentImageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(getActivity(), ImageShowerActivity.class);
						intent.putExtra(ImageShowerActivity.SHOW_BIG_IMAGE,
								AsyncHttpClientImageSound.getAbsoluteUrl(loveBridgeItem.getN_image()));
						startActivity(intent);
						getActivity().overridePendingTransition(R.anim.zoomin2, R.anim.zoomout);
					}
				});
			} else {
				holder.contentImageView.setVisibility(View.GONE);
			}

			//��������
			holder.contentTextView.setText(loveBridgeItem.getN_content());

			//��������
			holder.nameTextView.setText(loveBridgeItem.getN_name());

			//�����Ա�
			if (loveBridgeItem.getN_gender().equals(Constants.Gender.MALE)) {
				holder.genderImageView.setImageResource(R.drawable.male);
			} else {
				holder.genderImageView.setImageResource(R.drawable.female);
			}

			//��������
			holder.timeTextView.setText(DateTimeTools.DateToString(loveBridgeItem.getN_time()));

			//�����Ķ�����
			holder.flipperCountTextView.setText("" + loveBridgeItem.getN_flipcount());

			//�������۴���
			holder.commentCountTextView.setText("" + loveBridgeItem.getN_commentcount());

			//����
			holder.commentBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					startActivity(new Intent(getActivity(), LoveBridgeDetailActivity.class).putExtra(
							LoveBridgeDetailActivity.LOVE_BRIDGE_ITEM, loveBridgeItem));
					getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				}
			});

			holder.flipperBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub

					if (isChecked) {
						flipper(loveBridgeItem.getN_id());
						sendLoveReuest(loveBridgeItem.getN_userid());
					}
				}
			});

			if (userPreference.getU_id() == loveBridgeItem.getN_userid() || holder.flipperBtn.isChecked()) {
				holder.flipperBtn.setEnabled(false);
			} else {
				//�Ķ�
				holder.flipperBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						// TODO Auto-generated method stub
						if (userPreference.getU_id() != loveBridgeItem.getN_userid()) {
							if (isChecked) {
								flipper(loveBridgeItem.getN_id());
								sendLoveReuest(loveBridgeItem.getN_userid());
								holder.flipperCountTextView.setText("" + (loveBridgeItem.getN_flipcount() + 1));
								holder.flipperBtn.setEnabled(false);
							}
						} else {
							ToastTool.showShort(getActivity(), "���ܶ��Լ��Ķ�Ŷ~");
						}
					}
				});
			}
			return view;
		}
	}

	/**
	 * ����Ķ���ť
	 */
	private void flipper(int itemId) {
		RequestParams params = new RequestParams();
		params.put(LoveBridgeItemTable.N_ID, itemId);

		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {
			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					LogTool.i("LoveBridgeSchool", "�Ķ��ɹ���");

				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				LogTool.e("LoveBridgeSchool", "����������");
			}
		};
		AsyncHttpClientTool.post(getActivity(), "raiselovebridgeitem", params, responseHandler);
	}

	/**
	 * �첽���Ͱ�����֤
	 * @param userID
	 */
	private void sendLoveReuest(final int filpperId) {
		if (filpperId > 0) {
			FlipperDbService flipperDbService = FlipperDbService.getInstance(getActivity());
			Flipper flipper = flipperDbService.getFlipperByUserId(filpperId);
			if (flipper != null && flipper.getStatus().equals(FlipperStatus.BEINVITEED)) {//����������
				LogTool.i("DayRecommendAcitvity", "�Ѿ����������");
				String name = flipper.getNickname();
				if (!TextUtils.isEmpty(flipper.getRealname())) {
					name = flipper.getRealname();
				}
				final MyAlertDialog myAlertDialog = new MyAlertDialog(getActivity());
				myAlertDialog.setShowCancel(false);
				myAlertDialog.setTitle("��ʾ");
				myAlertDialog.setMessage("���Ѿ��� " + name + " ������������ڰ�����֤ҳ��������Ҳ������Ȼ�Ķ�������Ϊ�Ķ���ϵ");
				View.OnClickListener comfirm = new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						myAlertDialog.dismiss();
					}
				};
				myAlertDialog.setPositiveButton("ȷ��", comfirm);
				myAlertDialog.show();
			} else {
				LogTool.i("dayRecommend", "���Ͱ�����֤");
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
						progressDialog.setMessage("���ڷ����Ķ�����...");
						progressDialog.setCanceledOnTouchOutside(false);
						progressDialog.show();
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
						// TODO Auto-generated method stub
						LogTool.e("DayRecommendActivity", "����ԭ��" + errorResponse);
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers, String response) {
						// TODO Auto-generated method stub
						ToastTool.showLong(getActivity(), "������֤�ѷ��ͣ��ȴ��Է�ͬ��");
						saveFlipper(filpperId, response);
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
	 * �洢�����ݿ⣬�Ѿ�ͬ��
	 */
	public void saveFlipper(final int flipperId, String response) {
		FlipperDbService flipperDbService = FlipperDbService.getInstance(getActivity());
		Flipper flipper = flipperDbService.getFlipperByUserId(flipperId);
		//������ݿ��д��ڸ��û������������״̬
		if (flipper != null) {
			if (!flipper.getStatus().equals(FlipperStatus.INVITE)) {//����Ѿ����������������
				addContact(flipperId);
			}

			LogTool.i("dayRecommend", "flipper�Ѿ����ڣ�����");
			flipper.setIsRead(true);
			flipper.setTime(new Date());
			flipper.setType(FlipperType.TO);
			flipper.setStatus(Constants.FlipperStatus.INVITE);
			flipperDbService.flipperDao.update(flipper);

			//�����Է�֪ͨ
			JsonMessage jsonMessage = new JsonMessage(userPreference.getName() + "������Ȼ�Ķ�",
					MessageType.MESSAGE_TYPE_FLIPPER_REQUEEST);
			new SendMsgAsyncTask(FastJsonTool.createJsonString(jsonMessage), flipper.getBpushUserID()).send();
		} else {
			//�������˲���δ����״̬������ӻ��ź���
			if (response.equals("1")) {
				addContact(flipperId);
			}
			getUser(flipperId);
		}
	}

	/**
	*  ���contact
	* @param view
	*/
	public void addContact(final int flipperId) {
		LogTool.i("dayRecommend", "������Ӻ���");
		new Thread(new Runnable() {
			public void run() {
				try {
					//��Ӻ���
					EMContactManager.getInstance().addContact("" + flipperId, userPreference.getName() + "������Ȼ�Ķ���");
				} catch (final Exception e) {
				}
			}
		}).start();
	}

	/**
	 * 	�����ȡUser��Ϣ
	 */
	private void getUser(int userId) {
		LogTool.e("DayRecommendActivity", "�����ȡUSER��Ϣ");
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
								jsonUser.getU_weight(), jsonUser.getU_vertify_image_pass(), jsonUser.getU_salary(),
								true, jsonUser.getU_tel(), Constants.FlipperStatus.INVITE, Constants.FlipperType.TO);
						flipperDbService.flipperDao.insert(flipper);

						//�����Է�֪ͨ
						JsonMessage jsonMessage = new JsonMessage(userPreference.getName() + "������Ȼ�Ķ�",
								MessageType.MESSAGE_TYPE_FLIPPER_REQUEEST);
						new SendMsgAsyncTask(FastJsonTool.createJsonString(jsonMessage), flipper.getBpushUserID())
								.send();
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
}
