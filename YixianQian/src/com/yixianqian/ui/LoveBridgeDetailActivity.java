package com.yixianqian.ui;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.Header;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.config.Constants;
import com.yixianqian.config.Constants.Config;
import com.yixianqian.jsonobject.JsonBridgeComment;
import com.yixianqian.jsonobject.JsonLoveBridgeItem;
import com.yixianqian.table.BridgeCommentTable;
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
 * 类名称：LoveBridgeDetailActivity
 * 类描述：单条鹊桥记录详情页面
 * 创建人： 张帅
 * 创建时间：2014年9月14日 上午8:47:44
 *
 */
public class LoveBridgeDetailActivity extends BaseActivity implements OnClickListener {
	public static final String LOVE_BRIDGE_ITEM = "love_bridge_item";
	protected boolean pauseOnScroll = false;
	protected boolean pauseOnFling = true;

	private View headView;//详情区域
	private ImageView headImageView;
	private TextView nameTextView;
	private ImageView genderImageView;
	private TextView timeTextView;
	private TextView contentTextView;
	private ImageView contentImageView;
	private CheckBox flipperBtn;
	private TextView flipperCountTextView;
	private TextView commentCountTextView;
	private ImageView moreBtn;
	private EditText commentEditText;
	private Button commentBtn;
	private TextView topNavigation;//导航栏文字
	private View leftImageButton;//导航栏左侧按钮
	private View rightBtnBg;//导航栏右侧按钮
	private ImageView rightBtn;

	private UserPreference userPreference;
	private PullToRefreshListView loveBridgeListView;
	private JsonLoveBridgeItem loveBridgeItem;
	private int pageNow = 0;//控制页数
	private CommentAdapter mAdapter;
	private LinkedList<JsonBridgeComment> commentList;
	private InputMethodManager inputMethodManager;
	private int commentCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_love_bridge_detail);
		userPreference = BaseApplication.getInstance().getUserPreference();
		commentList = new LinkedList<JsonBridgeComment>();
		inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

		loveBridgeItem = (JsonLoveBridgeItem) getIntent().getSerializableExtra(LOVE_BRIDGE_ITEM);

		findViewById();
		initView();

		//获取数据
		getDataTask(pageNow);
		loveBridgeListView.setMode(Mode.BOTH);
		ListView mListView = loveBridgeListView.getRefreshableView();
		mListView.addHeaderView(headView);
		mAdapter = new CommentAdapter();
		loveBridgeListView.setAdapter(mAdapter);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
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
		loveBridgeListView = (PullToRefreshListView) findViewById(R.id.lovebridge_list);
		headView = getLayoutInflater().inflate(R.layout.love_bridge_list_item, null);
		headImageView = (ImageView) headView.findViewById(R.id.head_image);
		nameTextView = (TextView) headView.findViewById(R.id.name);
		genderImageView = (ImageView) headView.findViewById(R.id.gender);
		timeTextView = (TextView) headView.findViewById(R.id.time);
		contentTextView = (TextView) headView.findViewById(R.id.content);
		contentImageView = (ImageView) headView.findViewById(R.id.item_image);
		flipperBtn = (CheckBox) headView.findViewById(R.id.flipper);
		flipperCountTextView = (TextView) headView.findViewById(R.id.flipper_count);
		commentCountTextView = (TextView) headView.findViewById(R.id.comment_count);
		moreBtn = (ImageView) headView.findViewById(R.id.more);
		commentEditText = (EditText) findViewById(R.id.msg_et);
		commentBtn = (Button) findViewById(R.id.send_btn);
		topNavigation = (TextView) findViewById(R.id.nav_text);
		leftImageButton = (View) findViewById(R.id.left_btn_bg);
		rightBtnBg = (View) findViewById(R.id.right_btn_bg);
		rightBtn = (ImageView) findViewById(R.id.nav_right_btn);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		rightBtn.setImageResource(R.drawable.refresh);
		leftImageButton.setOnClickListener(this);
		rightBtnBg.setOnClickListener(this);
		topNavigation.setText("评论");
		flipperBtn.setChecked(true);
		flipperBtn.setEnabled(false);

		//设置上拉下拉刷新事件
		loveBridgeListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				String label = DateUtils.formatDateTime(LoveBridgeDetailActivity.this.getApplicationContext(),
						System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				pageNow = 0;
				getDataTask(pageNow);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				String label = DateUtils.formatDateTime(LoveBridgeDetailActivity.this.getApplicationContext(),
						System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				if (pageNow >= 0)
					++pageNow;
				getDataTask(pageNow);
			}
		});

		if (loveBridgeItem != null) {
			//设置头像
			if (!TextUtils.isEmpty(loveBridgeItem.getN_small_avatar())) {
				imageLoader.displayImage(AsyncHttpClientImageSound.getAbsoluteUrl(loveBridgeItem.getN_small_avatar()),
						headImageView, ImageLoaderTool.getHeadImageOptions(10));

				if (userPreference.getU_id() != loveBridgeItem.getN_userid()) {
					//点击头像进入详情页面
					headImageView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(LoveBridgeDetailActivity.this, PersonDetailActivity.class);
							intent.putExtra(PersonDetailActivity.PERSON_TYPE, Constants.PersonDetailType.SINGLE);
							intent.putExtra(UserTable.U_ID, loveBridgeItem.getN_userid());
							startActivity(intent);
							LoveBridgeDetailActivity.this.overridePendingTransition(R.anim.zoomin2, R.anim.zoomout);
						}
					});
				}
			}

			//设置照片
			if (!TextUtils.isEmpty(loveBridgeItem.getN_image())) {
				imageLoader.displayImage(AsyncHttpClientImageSound.getAbsoluteUrl(loveBridgeItem.getN_image()),
						contentImageView, ImageLoaderTool.getImageOptions());
				contentImageView.setVisibility(View.VISIBLE);
				contentImageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(LoveBridgeDetailActivity.this, ImageShowerActivity.class);
						intent.putExtra(ImageShowerActivity.SHOW_BIG_IMAGE,
								AsyncHttpClientImageSound.getAbsoluteUrl(loveBridgeItem.getN_image()));
						startActivity(intent);
						LoveBridgeDetailActivity.this.overridePendingTransition(R.anim.zoomin2, R.anim.zoomout);
					}
				});
			} else {
				contentImageView.setVisibility(View.GONE);
			}

			moreBtn.setVisibility(View.GONE);

			//设置内容
			contentTextView.setText(loveBridgeItem.getN_content());

			//设置姓名
			nameTextView.setText(loveBridgeItem.getN_name());

			//设置性别
			if (loveBridgeItem.getN_gender().equals(Constants.Gender.MALE)) {
				genderImageView.setImageResource(R.drawable.male);
			} else {
				genderImageView.setImageResource(R.drawable.female);
			}

			//设置日期
			timeTextView.setText(DateTimeTools.DateToString(loveBridgeItem.getN_time()));

			//设置心动次数
			flipperCountTextView.setText("" + loveBridgeItem.getN_flipcount());

			commentCount = loveBridgeItem.getN_commentcount();
			//设置评论次数
			commentCountTextView.setText("" + commentCount);

			commentEditText.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					// TODO Auto-generated method stub
				}

				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					if (s.length() > 0) {
						commentBtn.setEnabled(true);
					} else {
						commentBtn.setEnabled(false);
					}
				}
			});
			commentBtn.setOnClickListener(this);
		}
	}

	/**
	 * 更新数据
	 */
	private void refresh() {
		loveBridgeListView.setRefreshing();
		pageNow = 0;
		getDataTask(pageNow);
	}

	/**
	* 隐藏软键盘
	*/
	private void hideKeyboard() {
		inputMethodManager.hideSoftInputFromWindow(commentEditText.getWindowToken(), 0);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.left_btn_bg:
			finish();
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			break;
		case R.id.right_btn_bg:
			refresh();
			break;
		case R.id.send_btn:
			comment(commentEditText.getText().toString().trim());
			break;

		default:
			break;
		}
	}

	/**
	 * 评论
	 */
	private void comment(String content) {
		if (loveBridgeItem != null && !TextUtils.isEmpty(content)) {
			RequestParams params = new RequestParams();

			//如果是单身
			params.put(BridgeCommentTable.N_ID, loveBridgeItem.getN_id());
			params.put(BridgeCommentTable.C_USERID, userPreference.getU_id());
			params.put(BridgeCommentTable.C_CONTENT, content);
			TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {

				@Override
				public void onSuccess(int statusCode, Header[] headers, String response) {
					// TODO Auto-generated method stub
					if (statusCode == 200) {
						commentEditText.setText("");
						//设置评论次数
						commentCountTextView.setText("" + (++commentCount));
						refresh();
						hideKeyboard();
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
					// TODO Auto-generated method stub
					LogTool.e("LoveBridgeDetail", "评论失败");
				}
			};
			AsyncHttpClientTool.post(LoveBridgeDetailActivity.this, "addbridgecomment", params, responseHandler);
		}
	}

	/**
	 * 网络获取数据
	 */
	private void getDataTask(int p) {
		if (loveBridgeItem == null) {
			return;
		}
		final int page = p;
		RequestParams params = new RequestParams();
		params.put("page", pageNow);
		//如果是单身
		params.put(LoveBridgeItemTable.N_ID, loveBridgeItem.getN_id());
		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					LogTool.i("LoveBridgeDetail", "长度" + commentList.size());
					List<JsonBridgeComment> temp = FastJsonTool.getObjectList(response, JsonBridgeComment.class);
					if (temp != null) {
						//如果是首次获取数据
						if (page == 0) {
							if (temp.size() < Config.PAGE_NUM) {
								pageNow = -1;
							}
							commentList = new LinkedList<JsonBridgeComment>();
							commentList.addAll(temp);
						}
						//如果是获取更多
						else if (page > 0) {
							if (temp.size() < Config.PAGE_NUM) {
								pageNow = -1;
								ToastTool.showShort(LoveBridgeDetailActivity.this, "没有更多了！");
							}
							commentList.addAll(temp);
						}
						mAdapter.notifyDataSetChanged();
					}
				}
				loveBridgeListView.onRefreshComplete();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				LogTool.e("LoveBridgeDetail", "获取列表失败");
				loveBridgeListView.onRefreshComplete();
			}
		};
		AsyncHttpClientTool.post(LoveBridgeDetailActivity.this, "getbridgecommentlist", params, responseHandler);
	}

	/**
	 * 
	 * 类名称：CommentAdapter
	 * 类描述：适配器
	 * 创建人： 张帅
	 * 创建时间：2014年9月14日 上午10:46:10
	 *
	 */
	class CommentAdapter extends BaseAdapter {
		private class ViewHolder {
			public ImageView headImageView;
			public TextView nameTextView;
			public ImageView genderImageView;
			public TextView timeTextView;
			public TextView contentTextView;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return commentList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return commentList.get(position);
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
			final JsonBridgeComment bridgeComment = commentList.get(position);
			if (loveBridgeItem == null) {
				return null;
			}

			final ViewHolder holder;
			if (convertView == null) {
				view = LayoutInflater.from(LoveBridgeDetailActivity.this).inflate(R.layout.comment_list_item, null);
				holder = new ViewHolder();
				holder.headImageView = (ImageView) view.findViewById(R.id.head_image);
				holder.nameTextView = (TextView) view.findViewById(R.id.name);
				holder.genderImageView = (ImageView) view.findViewById(R.id.gender);
				holder.timeTextView = (TextView) view.findViewById(R.id.time);
				holder.contentTextView = (TextView) view.findViewById(R.id.content);
				view.setTag(holder); // 给View添加一个格外的数据 
			} else {
				holder = (ViewHolder) view.getTag(); // 把数据取出来  
			}

			//设置头像
			if (!TextUtils.isEmpty(loveBridgeItem.getN_small_avatar())) {
				imageLoader.displayImage(AsyncHttpClientImageSound.getAbsoluteUrl(bridgeComment.getC_small_avatar()),
						holder.headImageView, ImageLoaderTool.getHeadImageOptions(10));

				if (userPreference.getU_id() != bridgeComment.getC_userid()) {
					//点击头像进入详情页面
					holder.headImageView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(LoveBridgeDetailActivity.this, PersonDetailActivity.class);
							intent.putExtra(PersonDetailActivity.PERSON_TYPE, Constants.PersonDetailType.SINGLE);
							intent.putExtra(UserTable.U_ID, bridgeComment.getC_userid());
							startActivity(intent);
							LoveBridgeDetailActivity.this.overridePendingTransition(R.anim.zoomin2, R.anim.zoomout);
						}
					});
				}
			}

			//设置内容
			holder.contentTextView.setText(bridgeComment.getC_content());

			//设置姓名
			holder.nameTextView.setText(bridgeComment.getC_nickname());

			//设置性别
			if (bridgeComment.getC_gender().equals(Constants.Gender.MALE)) {
				holder.genderImageView.setImageResource(R.drawable.male);
			} else {
				holder.genderImageView.setImageResource(R.drawable.female);
			}

			//设置日期
			holder.timeTextView.setText(DateTimeTools.DateToString(bridgeComment.getC_time()));

			return view;
		}
	}

}
