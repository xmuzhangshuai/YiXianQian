package com.yixianqian.ui;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.Header;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.config.Constants;
import com.yixianqian.config.Constants.Config;
import com.yixianqian.jsonobject.JsonBridgeComment;
import com.yixianqian.jsonobject.JsonLoveBridgeItem;
import com.yixianqian.table.BridgeCommentTable;
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
public class LoveBridgeDetailActivity extends BaseActivity {
	public static final String LOVE_BRIDGE_ITEM = "love_bridge_item";

	private View headView;//详情区域
	public ImageView headImageView;
	public TextView nameTextView;
	public ImageView genderImageView;
	public TextView timeTextView;
	public TextView contentTextView;
	public ImageView contentImageView;
	public CheckBox flipperBtn;
	public TextView flipperCountTextView;
	public TextView commentCountTextView;
	public ImageView moreBtn;
	public EditText commentEditText;
	public Button commentBtn;

	private UserPreference userPreference;
	private PullToRefreshListView loveBridgeListView;
	private JsonLoveBridgeItem loveBridgeItem;
	private int pageNow = 0;//控制页数
	private CommentAdapter mAdapter;
	private LinkedList<JsonBridgeComment> commentList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_love_bridge_detail);
		userPreference = BaseApplication.getInstance().getUserPreference();
		commentList = new LinkedList<JsonBridgeComment>();

		loveBridgeItem = (JsonLoveBridgeItem) getIntent().getSerializableExtra(LOVE_BRIDGE_ITEM);

		findViewById();
		initView();

		//获取数据
		//		getDataTask(pageNow);
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
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
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

			//设置评论次数
			commentCountTextView.setText("" + loveBridgeItem.getN_commentcount());

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

			commentBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					comment(commentEditText.getText().toString().trim());
				}
			});
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
		AsyncHttpClientTool.post(LoveBridgeDetailActivity.this, "getlovebridgelist", params, responseHandler);
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
			return null;
		}
	}
}
