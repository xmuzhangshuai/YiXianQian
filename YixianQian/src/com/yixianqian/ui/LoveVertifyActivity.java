package com.yixianqian.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.baidupush.SendMsgAsyncTask;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.config.Constants;
import com.yixianqian.db.ConversationDbService;
import com.yixianqian.db.FlipperDbService;
import com.yixianqian.entities.Conversation;
import com.yixianqian.entities.Flipper;
import com.yixianqian.jsonobject.JsonMessage;
import com.yixianqian.table.FlipperTable;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.AsyncHttpClientImageSound;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.DateTimeTools;
import com.yixianqian.utils.FastJsonTool;
import com.yixianqian.utils.FriendPreference;
import com.yixianqian.utils.ImageLoaderTool;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

/**
 * 类名称：LoveVertifyActivity
 * 类描述：爱情验证页面
 * 创建人： 张帅
 * 创建时间：2014年7月14日 下午4:08:20
 *
 */
public class LoveVertifyActivity extends BaseActivity {
	/***********VIEWS************/
	private ListView loveVertifyList;
	private FlipperDbService flipperDbService;
	private List<Flipper> flipperList;
	private LoveVertifyAdapter adapter;
	private FriendPreference friendpreference;
	private UserPreference userPreference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_love_vertify);

		flipperDbService = FlipperDbService.getInstance(LoveVertifyActivity.this);
		flipperList = new ArrayList<Flipper>();
		flipperList = flipperDbService.getFlipperList();
		friendpreference = BaseApplication.getInstance().getFriendPreference();
		userPreference = BaseApplication.getInstance().getUserPreference();

		findViewById();
		initView();
		adapter = new LoveVertifyAdapter();
		loveVertifyList.setAdapter(adapter);
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		loveVertifyList = (ListView) findViewById(R.id.love_vertify_list);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * 类名称：LoveVertifyAdapter
	 * 类描述：列表Adapter
	 * 创建人： 张帅
	 * 创建时间：2014年7月14日 下午4:10:00
	 *
	 */
	private class LoveVertifyAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return flipperList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return flipperList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			//标记本心动请求已读
			Flipper flipper = flipperList.get(position);
			if (flipper != null) {
				flipper.setIsRead(true);
				flipperDbService.flipperDao.update(flipper);
			}

			View view = convertView;
			if (convertView == null) {
				view = LayoutInflater.from(LoveVertifyActivity.this).inflate(R.layout.love_vertify_list_item, null);
			}
			ImageView headImageView = (ImageView) view.findViewById(R.id.head_image);
			TextView nameTextView = (TextView) view.findViewById(R.id.name);
			TextView timeTextView = (TextView) view.findViewById(R.id.time);
			View flipperBtn = (View) view.findViewById(R.id.flipped);
			View refuseBtn = (View) view.findViewById(R.id.refuse);

			timeTextView.setText(DateTimeTools.DateToString(flipperList.get(position).getTime()));
			if (!TextUtils.isEmpty(flipperList.get(position).getSamllAvatar())) {
				imageLoader.displayImage(
						AsyncHttpClientImageSound.getAbsoluteUrl(flipperList.get(position).getSamllAvatar()),
						headImageView, ImageLoaderTool.getHeadImageOptions(10));
			}
			//优先显示真名
			String name = flipperList.get(position).getNickname();
			if (!TextUtils.isEmpty(flipperList.get(position).getRealname())) {
				name = flipperList.get(position).getRealname();
			}
			nameTextView.setText(name);

			//点击接受按钮
			flipperBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					addFlipper(flipperList.get(position));
				}
			});

			//点击拒绝按钮
			refuseBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Flipper flipper = flipperList.get(position);
					flipperDbService.flipperDao.delete(flipper);
					flipperList.remove(position);
					adapter.notifyDataSetChanged();
				}
			});
			return view;
		}
	}

	/**
	 * 添加心动关系
	 */
	private void addFlipper(final Flipper flipper) {

		if (flipper != null) {
			RequestParams params = new RequestParams();
			params.put(FlipperTable.F_USERID, userPreference.getU_id());
			params.put(FlipperTable.F_FLIPPERID, flipper.getUserID());
			String url = "buildflipper";
			TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
				Dialog dialog;

				@Override
				public void onStart() {
					// TODO Auto-generated method stub
					super.onStart();
					dialog = showProgressDialog("请稍后...");
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, String response) {
					// TODO Auto-generated method stub
					if (statusCode == 200) {
						if (!TextUtils.isEmpty(response)) {
							if (response.equals("0")) {
								ToastTool.showLong(LoveVertifyActivity.this, "失败！");
							} else {
								friendpreference.clear();
								Map<String, String> map = FastJsonTool.getObject(response, Map.class);
								if (map != null) {
									//通知对方
									JsonMessage jsonMessage = new JsonMessage(userPreference.getU_tel(),
											Constants.MessageType.MESSAGE_TYPE_FLIPPER_TO);
									new SendMsgAsyncTask(FastJsonTool.createJsonString(jsonMessage),
											map.get(UserTable.U_BPUSH_USER_ID)).send();

									userPreference.setU_stateid(3);
									friendpreference.setLoverId(Integer.parseInt(map.get(FlipperTable.F_ID)));
									friendpreference.setType(0);
									friendpreference.setBpush_ChannelID(map.get(UserTable.U_BPUSH_CHANNEL_ID));
									friendpreference.setBpush_UserID(map.get(UserTable.U_BPUSH_USER_ID));
									friendpreference.setF_age(flipper.getAge());
									friendpreference.setF_blood_type(flipper.getBloodType());
									friendpreference.setF_constell(flipper.getConstell());
									friendpreference.setF_email(flipper.getEmail());
									friendpreference.setF_gender(flipper.getGender());
									friendpreference.setF_height(flipper.getHeight());
									friendpreference.setF_id(flipper.getUserID());
									friendpreference.setF_introduce(flipper.getIntroduce());
									friendpreference.setF_large_avatar(flipper.getLargeAvatar());
									friendpreference.setF_nickname(flipper.getNickname());
									friendpreference.setF_realname(flipper.getRealname());
									friendpreference.setF_salary(flipper.getSalary());
									friendpreference.setF_small_avatar(flipper.getSamllAvatar());
									friendpreference.setF_stateid(flipper.getStateID());
									friendpreference.setF_vocationid(flipper.getVocationID());
									friendpreference.setF_weight(flipper.getWeight());
									friendpreference.setU_cityid(flipper.getCityID());
									friendpreference.setU_provinceid(flipper.getProvinceID());
									friendpreference.setU_schoolid(flipper.getSchoolID());

									ConversationDbService conversationDbService = ConversationDbService
											.getInstance(LoveVertifyActivity.this);
									conversationDbService.conversationDao.deleteAll();
									Conversation conversation = new Conversation(null, Long.valueOf(friendpreference
											.getF_id()), friendpreference.getName(),
											friendpreference.getF_small_avatar(), "", 0, System.currentTimeMillis());
									conversationDbService.conversationDao.insert(conversation);

									Intent intent = new Intent(LoveVertifyActivity.this, ChatActivity.class);
									intent.putExtra("conversationID",
											conversationDbService.getIdByConversation(conversation));
									startActivity(intent);
									overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
								} else {
									ToastTool.showLong(LoveVertifyActivity.this, "map为空");
								}
							}
						}
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
					// TODO Auto-generated method stub
					ToastTool.showLong(LoveVertifyActivity.this, "服务器失败！" + errorResponse);
				}

				@Override
				public void onFinish() {
					// TODO Auto-generated method stub
					dialog.dismiss();
					finish();
					super.onFinish();
				}
			};
			AsyncHttpClientTool.post(LoveVertifyActivity.this, url, params, responseHandler);
		}
	}
}
