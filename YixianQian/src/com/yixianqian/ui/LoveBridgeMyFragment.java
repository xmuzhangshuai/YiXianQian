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
 * 类名称：LoveBridgeMyFragment
 * 类描述：鹊桥，我的发布
 * 创建人： 张帅
 * 创建时间：2014年9月11日 下午4:55:29
 *
 */
public class LoveBridgeMyFragment extends BaseV4Fragment {
	private View rootView;// 根View
	private PullToRefreshListView loveBridgeListView;

	protected boolean pauseOnScroll = false;
	protected boolean pauseOnFling = true;
	private UserPreference userPreference;
	private LinkedList<JsonLoveBridgeItem> loveBridgeItemList;
	private int pageNow = 0;//控制页数
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

		findViewById();// 初始化views
		initView();

		//获取数据
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
		//设置上拉下拉刷新事件
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
	 * 网络获取数据
	 */
	private void getDataTask(int p) {
		final int page = p;
		RequestParams params = new RequestParams();
		params.put("page", pageNow);

		//如果是单身
		params.put(UserTable.U_SCHOOLID, userPreference.getU_schoolid());
		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					LogTool.i("LoveBridgeSchoolFragment", "长度" + loveBridgeItemList.size());
					List<JsonLoveBridgeItem> temp = FastJsonTool.getObjectList(response, JsonLoveBridgeItem.class);
					if (temp != null) {
						//如果是首次获取数据
						if (page == 0) {
							if (temp.size() < Config.PAGE_NUM) {
								pageNow = -1;
							}
							loveBridgeItemList = new LinkedList<JsonLoveBridgeItem>();
							loveBridgeItemList.addAll(temp);
						}
						//如果是获取更多
						else if (page > 0) {
							if (temp.size() < Config.PAGE_NUM) {
								pageNow = -1;
								ToastTool.showShort(getActivity(), "没有更多了！");
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
				LogTool.e("LoveBridgeSchoolFragment", "获取列表失败");
				loveBridgeListView.onRefreshComplete();
			}
		};
		AsyncHttpClientTool.post(getActivity(), "getlovebridgelist", params, responseHandler);
	}

	/**
	 * 
	 * 类名称：LoveBridgeAdapter
	 * 类描述：鹊桥广场适配器
	 * 创建人： 张帅
	 * 创建时间：2014年9月12日 下午3:25:07
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
				view.setTag(holder); // 给View添加一个格外的数据 
			} else {
				holder = (ViewHolder) view.getTag(); // 把数据取出来  
			}

			//设置头像
			if (!TextUtils.isEmpty(loveBridgeItem.getN_small_avatar())) {
				imageLoader.displayImage(AsyncHttpClientImageSound.getAbsoluteUrl(loveBridgeItem.getN_small_avatar()),
						holder.headImageView, ImageLoaderTool.getHeadImageOptions(10));

				if (userPreference.getU_id() != loveBridgeItem.getN_userid()) {
					//点击头像进入详情页面
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

			//设置照片
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

			//设置内容
			holder.contentTextView.setText(loveBridgeItem.getN_content());

			//设置姓名
			holder.nameTextView.setText(loveBridgeItem.getN_name());

			//设置性别
			if (loveBridgeItem.getN_gender().equals(Constants.Gender.MALE)) {
				holder.genderImageView.setImageResource(R.drawable.male);
			} else {
				holder.genderImageView.setImageResource(R.drawable.female);
			}

			//设置日期
			holder.timeTextView.setText(DateTimeTools.DateToString(loveBridgeItem.getN_time()));

			//设置心动次数
			holder.flipperCountTextView.setText("" + loveBridgeItem.getN_flipcount());

			//设置评论次数
			holder.commentCountTextView.setText("" + loveBridgeItem.getN_commentcount());

			//评论
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
				//心动
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
							ToastTool.showShort(getActivity(), "不能对自己心动哦~");
						}
					}
				});
			}
			return view;
		}
	}

	/**
	 * 点击心动按钮
	 */
	private void flipper(int itemId) {
		RequestParams params = new RequestParams();
		params.put(LoveBridgeItemTable.N_ID, itemId);

		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {
			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					LogTool.i("LoveBridgeSchool", "心动成功！");

				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				LogTool.e("LoveBridgeSchool", "服务器错误！");
			}
		};
		AsyncHttpClientTool.post(getActivity(), "raiselovebridgeitem", params, responseHandler);
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
				LogTool.i("DayRecommendAcitvity", "已经被邀请过了");
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
				LogTool.i("dayRecommend", "发送爱情验证");
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
						progressDialog.setMessage("正在发送心动请求...");
						progressDialog.setCanceledOnTouchOutside(false);
						progressDialog.show();
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
						// TODO Auto-generated method stub
						LogTool.e("DayRecommendActivity", "错误原因" + errorResponse);
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers, String response) {
						// TODO Auto-generated method stub
						ToastTool.showLong(getActivity(), "爱情验证已发送！等待对方同意");
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

			LogTool.i("dayRecommend", "flipper已经存在，更新");
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
	*  添加contact
	* @param view
	*/
	public void addContact(final int flipperId) {
		LogTool.i("dayRecommend", "环信添加好友");
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
	 * 	网络获取User信息
	 */
	private void getUser(int userId) {
		LogTool.e("DayRecommendActivity", "网络获取USER信息");
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
}
