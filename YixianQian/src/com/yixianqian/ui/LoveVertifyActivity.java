package com.yixianqian.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.config.Constants;
import com.yixianqian.customewidget.MyAlertDialog;
import com.yixianqian.db.ConversationDbService;
import com.yixianqian.db.FlipperDbService;
import com.yixianqian.entities.Conversation;
import com.yixianqian.entities.Flipper;
import com.yixianqian.table.FlipperTable;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.AsyncHttpClientImageSound;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.DateTimeTools;
import com.yixianqian.utils.FastJsonTool;
import com.yixianqian.utils.FriendPreference;
import com.yixianqian.utils.ImageLoaderTool;
import com.yixianqian.utils.LogTool;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

/**
 * �����ƣ�LoveVertifyActivity
 * ��������������֤ҳ��
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��7��14�� ����4:08:20
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
	private View leftBtn;
	private View deleteBtn;
	private ImageView rightImage;
	private TextView mEmpty;
	private TextView navText;

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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		flipperList = flipperDbService.getFlipperList();
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		loveVertifyList = (ListView) findViewById(R.id.love_vertify_list);
		mEmpty = (TextView) findViewById(R.id.empty);
		leftBtn = findViewById(R.id.left_btn_bg);
		navText = (TextView) findViewById(R.id.nav_text);
		deleteBtn = findViewById(R.id.right_btn_bg);
		rightImage = (ImageView) findViewById(R.id.nav_right_btn);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		navText.setText("�Ķ���ʷ");
		rightImage.setImageResource(R.drawable.delete_white);

		if (flipperList.size() == 0) {
			mEmpty.setVisibility(View.VISIBLE);
		}

		leftBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		deleteBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				deleteHistroy();
			}
		});

		loveVertifyList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoveVertifyActivity.this, PersonDetailActivity.class);
				if (userPreference.getU_stateid() == 3) {
					intent.putExtra(PersonDetailActivity.PERSON_TYPE, Constants.PersonDetailType.FLIPPER);
				} else if (userPreference.getU_stateid() == 2) {
					intent.putExtra(PersonDetailActivity.PERSON_TYPE, Constants.PersonDetailType.LOVER);
				} else {
					intent.putExtra(PersonDetailActivity.PERSON_TYPE, Constants.PersonDetailType.SINGLE);
				}

				intent.putExtra(UserTable.U_ID, flipperList.get(position).getUserID());
				startActivity(intent);
				overridePendingTransition(R.anim.zoomin2, R.anim.zoomout);
			}
		});
	}

	/**
	 * ɾ���Ķ���ʷ
	 */
	private void deleteHistroy() {
		final MyAlertDialog myAlertDialog = new MyAlertDialog(this);
		myAlertDialog.setTitle("��ʾ");
		myAlertDialog.setMessage("����Ķ���ʷ��");
		View.OnClickListener comfirm = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myAlertDialog.dismiss();
				flipperDbService.flipperDao.deleteAll();
				flipperList = flipperDbService.getFlipperList();
				adapter.notifyDataSetChanged();
				if (flipperList.size() == 0) {
					mEmpty.setVisibility(View.VISIBLE);
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
		myAlertDialog.setPositiveButton("ȷ��", comfirm);
		myAlertDialog.setNegativeButton("ȡ��", cancle);
		myAlertDialog.show();
	}

	/**
	 * 
	 * �����ƣ�LoveVertifyAdapter
	 * ���������б�Adapter
	 * �����ˣ� ��˧
	 * ����ʱ�䣺2014��7��14�� ����4:10:00
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

			//��Ǳ��Ķ������Ѷ�
			final Flipper flipper = flipperList.get(position);
			if (flipper != null) {
				flipper.setIsRead(true);
				flipperDbService.flipperDao.update(flipper);
			}

			String status = flipper.getStatus();

			View view = convertView;
			if (convertView == null) {
				view = LayoutInflater.from(LoveVertifyActivity.this).inflate(R.layout.love_vertify_list_item, null);
			}
			ImageView headImageView = (ImageView) view.findViewById(R.id.head_image);
			TextView nameTextView = (TextView) view.findViewById(R.id.name);
			TextView timeTextView = (TextView) view.findViewById(R.id.time);
			View flipperBtn = (View) view.findViewById(R.id.flipped);
			View refuseBtn = (View) view.findViewById(R.id.refuse);
			TextView info = (TextView) view.findViewById(R.id.info);
			TextView info2 = (TextView) view.findViewById(R.id.info2);

			timeTextView.setText(DateTimeTools.DateToString(flipper.getTime()));
			if (!TextUtils.isEmpty(flipper.getSamllAvatar())) {
				imageLoader.displayImage(AsyncHttpClientImageSound.getAbsoluteUrl(flipper.getSamllAvatar()),
						headImageView, ImageLoaderTool.getHeadImageOptions(10));
				headImageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(LoveVertifyActivity.this, ImageShowerActivity.class);
						intent.putExtra(ImageShowerActivity.SHOW_BIG_IMAGE,
								AsyncHttpClientImageSound.getAbsoluteUrl(flipper.getLargeAvatar()));
						startActivity(intent);
						LoveVertifyActivity.this.overridePendingTransition(R.anim.zoomin, R.anim.zoomout);

					}
				});
			}
			//������ʾ����
			String name = flipper.getNickname();
			if (!TextUtils.isEmpty(flipper.getRealname())) {
				name = flipper.getRealname();
			}
			nameTextView.setText(name);

			if (status.equals(Constants.FlipperStatus.REFUSED)) {//���Ҿܾ����Ķ�����
				nameTextView.setTextColor(getResources().getColor(R.color.unenable));
				info.setText("���Ѿ��ܾ���ta");
				info.setTextColor(getResources().getColor(R.color.unenable));
				info2.setVisibility(View.GONE);
				flipperBtn.setVisibility(View.GONE);
				refuseBtn.setVisibility(View.GONE);
			} else if (status.equals(Constants.FlipperStatus.BEINVITEED)) {//δ��������Ķ�����
				nameTextView.setTextColor(Color.BLACK);
				info.setText("������Ȼ�Ķ�");
				info.setTextColor(Color.BLACK);
				info2.setVisibility(View.VISIBLE);
				flipperBtn.setVisibility(View.VISIBLE);
				refuseBtn.setVisibility(View.VISIBLE);
			} else if (status.equals(Constants.FlipperStatus.INVITE)) {//�ҶԱ��˵�����
				nameTextView.setTextColor(Color.BLACK);
				info.setText("�Ҷ�ta��Ȼ�Ķ����ȴ��Է���Ӧ");
				info.setTextColor(getResources().getColor(R.color.unenable));
				info2.setVisibility(View.GONE);
				flipperBtn.setVisibility(View.GONE);
				refuseBtn.setVisibility(View.GONE);
			} else if (status.equals(Constants.FlipperStatus.AGREED)) {//��ͬ���˱��˵�����
				nameTextView.setTextColor(Color.BLACK);
				info.setText("��ͬ����ta������");
				info.setTextColor(getResources().getColor(R.color.unenable));
				info2.setVisibility(View.GONE);
				flipperBtn.setVisibility(View.GONE);
				refuseBtn.setVisibility(View.GONE);
			} else if (status.equals(Constants.FlipperStatus.BEAGREED)) {//����ͬ�����ҵ�����
				nameTextView.setTextColor(Color.BLACK);
				info.setText("ͬ�����ҵ�����");
				info.setTextColor(getResources().getColor(R.color.unenable));
				info2.setVisibility(View.GONE);
				flipperBtn.setVisibility(View.GONE);
				refuseBtn.setVisibility(View.GONE);
			} else if (status.equals(Constants.FlipperStatus.BEREFUSED)) {//���˾ܾ����ҵ�����
				nameTextView.setTextColor(getResources().getColor(R.color.unenable));
				info.setText("����ta���Ķ����󱻾ܾ���");
				info.setTextColor(getResources().getColor(R.color.unenable));
				info2.setVisibility(View.GONE);
				flipperBtn.setVisibility(View.GONE);
				refuseBtn.setVisibility(View.GONE);
			}

			//������ܰ�ť
			flipperBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					addFlipper(flipperList.get(position), position);
				}
			});

			//����ܾ���ť
			refuseBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					refuse(flipperList.get(position), position);
				}
			});
			return view;
		}
	}

	/**
	 * �ܾ��û�
	 * @param context
	 * @param isFinished
	 */
	public void refuse(final Flipper flipper, final int position) {
		if (flipper != null) {
			RequestParams params = new RequestParams();
			params.put(FlipperTable.F_USERID, userPreference.getU_id());
			params.put(FlipperTable.F_FLIPPERID, flipper.getUserID());
			TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {
				@Override
				public void onSuccess(int statusCode, Header[] headers, String response) {
					// TODO Auto-generated method stub
					if (statusCode == 200) {
						if (!TextUtils.isEmpty(response)) {
							flipper.setStatus(Constants.FlipperStatus.REFUSED);
							flipperDbService.flipperDao.update(flipper);
							adapter.notifyDataSetChanged();
							//							flipperDbService.flipperDao.delete(flipper);
							//							flipperList.remove(position);
							//							adapter.notifyDataSetChanged();

							try {
								EMChatManager.getInstance().refuseInvitation("" + flipper.getUserID());
								LogTool.e("LoverVertifyActivity", "�Ҿܾ��� " + flipper.getUserID() + " ������");
							} catch (EaseMobException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
					// TODO Auto-generated method stub

				}
			};
			AsyncHttpClientTool.post("refuseflipperrequest", params, responseHandler);
		}
	}

	/**
	 * ����Ķ���ϵ
	 */
	private void addFlipper(final Flipper flipper, final int position) {

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
					dialog = showProgressDialog("���Ժ�...");
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, String response) {
					// TODO Auto-generated method stub
					if (statusCode == 200) {
						if (!TextUtils.isEmpty(response)) {
							if (response.equals("0")) {
								ToastTool.showLong(LoveVertifyActivity.this, "ʧ�ܣ�");
							} else {
								flipper.setStatus(Constants.FlipperStatus.AGREED);
								flipper.setTime(new Date());
								flipperDbService.flipperDao.update(flipper);
								adapter.notifyDataSetChanged();

								friendpreference.clear();
								Map<String, String> map = FastJsonTool.getObject(response, Map.class);
								if (map != null) {

									//ͬ��username�ĺ�������
									try {
										EMChatManager.getInstance().acceptInvitation("" + flipper.getUserID());
										LogTool.e("LoverVertifyActivity", "��ͬ���� " + flipper.getUserID() + " ������");
									} catch (EaseMobException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

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
									friendpreference.setVertify(flipper.getImagePass());
									
									ConversationDbService conversationDbService = ConversationDbService
											.getInstance(LoveVertifyActivity.this);
									conversationDbService.conversationDao.deleteAll();
									Conversation conversation = new Conversation(null, Long.valueOf(friendpreference
											.getF_id()), friendpreference.getName(),
											friendpreference.getF_small_avatar(), "", 0, System.currentTimeMillis());
									conversationDbService.conversationDao.insert(conversation);

									Intent intent = new Intent(LoveVertifyActivity.this, ChatActivity.class);
									intent.putExtra("userId", "" + flipper.getUserID());
									startActivity(intent);
									overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
								} else {
									ToastTool.showLong(LoveVertifyActivity.this, "�齨�Ķ���ϵʧ��");
									LogTool.e("�Ķ���ϵ", "�齨�Ķ���ϵʧ�ܣ�buildflipper,����Ϊ��");
								}
							}
						}
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
					// TODO Auto-generated method stub
					ToastTool.showLong(LoveVertifyActivity.this, "������ʧ�ܣ�" + errorResponse);
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
