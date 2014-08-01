package com.yixianqian.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easemob.chat.EMContactManager;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.baidupush.SendMsgAsyncTask;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.base.BaseFragmentActivity;
import com.yixianqian.config.Constants;
import com.yixianqian.config.Constants.FlipperStatus;
import com.yixianqian.config.Constants.FlipperType;
import com.yixianqian.config.Constants.MessageType;
import com.yixianqian.customewidget.MyAlertDialog;
import com.yixianqian.db.FlipperDbService;
import com.yixianqian.db.TodayRecommendDbService;
import com.yixianqian.entities.Flipper;
import com.yixianqian.entities.TodayRecommend;
import com.yixianqian.jsonobject.JsonMessage;
import com.yixianqian.jsonobject.JsonUser;
import com.yixianqian.table.FlipperRequestTable;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.AsyncHttpClientImageSound;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.DensityUtil;
import com.yixianqian.utils.FastJsonTool;
import com.yixianqian.utils.ImageLoaderTool;
import com.yixianqian.utils.LogTool;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

/**
 * �����ƣ�DayRecommendActivity
 * �������������Ƽ�ҳ��
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��7��4�� ����10:19:44
 *
 */
public class DayRecommendActivity extends BaseFragmentActivity {
	private ViewPager viewPager = null;
	// ��ǰViewPager����
	private int pageIndex = 0;
	private ImageView recommendHelp;
	private ImageView finish_btn;

	//�����Ƽ��û�����
	private List<TodayRecommend> todayRecommendList;
	private UserPreference userPreference;

	// ����Բ��ͼƬ��View
	private ViewGroup imageCircleView = null;
	private ImageView[] imageCircleViews = null;

	//ϲ��
	private CheckBox like;
	private int currentLike = -1;
	private ProgressDialog progressDialog;
	private JsonUser jsonUser;
	private boolean isLiked = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_day_recommend);
		todayRecommendList = new ArrayList<TodayRecommend>();
		todayRecommendList = TodayRecommendDbService.getInstance(this).getTodayRecommendList();
		userPreference = BaseApplication.getInstance().getUserPreference();

		findViewById();
		initView();

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		close();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		viewPager = (ViewPager) findViewById(R.id.user_slide_page);
		recommendHelp = (ImageView) findViewById(R.id.recommend_help);
		imageCircleView = (ViewGroup) findViewById(R.id.dot);
		finish_btn = (ImageView) findViewById(R.id.finish_btn);
		like = (CheckBox) findViewById(R.id.choose_love);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		imageCircleViews = new ImageView[todayRecommendList.size()];

		//����Բ��
		for (int i = 0; i < todayRecommendList.size(); i++) {
			ImageView dot = new ImageView(DayRecommendActivity.this);
			dot.setLayoutParams(new LayoutParams(DensityUtil.dip2px(DayRecommendActivity.this, 15), DensityUtil.dip2px(
					DayRecommendActivity.this, 15)));
			if (i == 0) {
				dot.setBackgroundResource(R.drawable.dot_selected);
			} else {
				dot.setBackgroundResource(R.drawable.dot);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(new LayoutParams(DensityUtil.dip2px(
						DayRecommendActivity.this, 15), DensityUtil.dip2px(DayRecommendActivity.this, 15)));
				lp.setMargins(20, 0, 0, 0);
				dot.setLayoutParams(lp);
			}
			imageCircleViews[i] = dot;
			imageCircleView.addView(dot);
		}

		viewPager.setAdapter(new SlideAdapter());
		viewPager.setOnPageChangeListener(new PageChangeListener());

		recommendHelp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(DayRecommendActivity.this, RecommendHelpActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.pop_in, R.anim.pop_out);
			}
		});

		finish_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isLiked) {
					TodayRecommend todayRecommend = todayRecommendList.get(currentLike);
					if (todayRecommend != null) {
						sendLoveReuest(todayRecommendList.get(currentLike).getUserID());
					}
				} else {
					skip();
				}
			}
		});

		//ѡ��ϲ������֮���첽������֤������ת����ҳ
		like.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					currentLike = pageIndex;
					isLiked = true;
				} else {
					if (currentLike == pageIndex) {
						isLiked = false;
					}
				}
				refreshFinishBtn();
			}
		});
	}

	private void refreshFinishBtn() {
		if (isLiked) {
			finish_btn.setImageResource(R.drawable.sel_day_confirm_btn);
		} else {
			finish_btn.setImageResource(R.drawable.sel_day_skip_btn);
		}
	}

	/**
	 * ����
	 */
	private void skip() {
		startActivity(new Intent(DayRecommendActivity.this, MainActivity.class));
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		finish();
	}

	/**
	 * �첽���Ͱ�����֤
	 * @param userID
	 */
	private void sendLoveReuest(final int filpperId) {
		if (filpperId > 0) {
			FlipperDbService flipperDbService = FlipperDbService.getInstance(DayRecommendActivity.this);
			Flipper flipper = flipperDbService.getFlipperByUserId(filpperId);
			if (flipper != null && flipper.getStatus().equals(FlipperStatus.BEINVITEED)) {//����������
				LogTool.e("DayRecommendAcitvity", "�Ѿ����������");
				String name = flipper.getNickname();
				if (!TextUtils.isEmpty(flipper.getRealname())) {
					name = flipper.getRealname();
				}
				final MyAlertDialog myAlertDialog = new MyAlertDialog(this);
				myAlertDialog.setShowCancel(false);
				myAlertDialog.setTitle("��ʾ");
				myAlertDialog.setMessage("���Ѿ��� " + name + " ������������ڰ�����֤ҳ��������Ҳ������Ȼ�Ķ�������Ϊ�Ķ���ϵ");
				View.OnClickListener comfirm = new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						myAlertDialog.dismiss();
						Intent intent = new Intent(DayRecommendActivity.this, MainActivity.class);
						startActivity(intent);
						overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
					}
				};
				myAlertDialog.setPositiveButton("ȷ��", comfirm);
				myAlertDialog.show();
			} else {
				LogTool.e("dayRecommend", "���Ͱ�����֤");
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
						progressDialog = new ProgressDialog(DayRecommendActivity.this);
						progressDialog.setMessage("���ڷ�������...");
						progressDialog.setCanceledOnTouchOutside(false);
						progressDialog.show();
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
						// TODO Auto-generated method stub
						LogTool.e("DayRecommendActivity", "����ԭ��" + errorResponse);
						DayRecommendActivity.this.finish();
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers, String response) {
						// TODO Auto-generated method stub
						ToastTool.showLong(getApplicationContext(), "������֤�ѷ��ͣ��ȴ��Է�ͬ��");
						saveFlipper(filpperId, response);

						Intent intent = new Intent(DayRecommendActivity.this, MainActivity.class);
						startActivity(intent);
						overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
						DayRecommendActivity.this.finish();
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						super.onFinish();
						progressDialog.dismiss();
					}
				};
				AsyncHttpClientTool.post(DayRecommendActivity.this, url, params, responseHandler);
			}
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
	 * �洢�����ݿ⣬�Ѿ�ͬ��
	 */
	public void saveFlipper(final int flipperId, String response) {
		FlipperDbService flipperDbService = FlipperDbService.getInstance(DayRecommendActivity.this);
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
						FlipperDbService flipperDbService = FlipperDbService.getInstance(DayRecommendActivity.this);
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
				ToastTool.showLong(DayRecommendActivity.this, "����������");
			}
		};
		AsyncHttpClientTool.post(DayRecommendActivity.this, "getuserbyid", params, responseHandler);
	}

	/**
	 * 
	 * �����ƣ�SlideAdapter
	 * �������������Ƽ��û�����������
	 * �����ˣ� ��˧
	 * ����ʱ�䣺2014��7��14�� ����2:41:29
	 *
	 */
	private class SlideAdapter extends PagerAdapter {
		private class ViewHolder {
			public TextView name;
			public TextView gender;
			public ImageView headimage;
			public TextView age;
			public TextView school;
		}

		@Override
		public int getCount() {
			return todayRecommendList.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub  
			return super.getItemPosition(object);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub  
			container.removeView((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			// TODO Auto-generated method stub  
			final ViewHolder holder;

			View view = getLayoutInflater().inflate(R.layout.recommend_user_pager, null);
			holder = new ViewHolder();
			holder.name = (TextView) view.findViewById(R.id.name);
			holder.age = (TextView) view.findViewById(R.id.age);
			holder.school = (TextView) view.findViewById(R.id.school);
			holder.headimage = (ImageView) view.findViewById(R.id.headimage);
			holder.gender = (TextView) view.findViewById(R.id.gender);

			holder.name.setText(todayRecommendList.get(position).getUserName());
			if (todayRecommendList.get(position).getUserAge() > 0) {
				holder.age.setVisibility(View.VISIBLE);
				holder.age.setText("" + todayRecommendList.get(position).getUserAge() + "��");
			} else {
				holder.age.setVisibility(View.INVISIBLE);
			}

			holder.school.setText(todayRecommendList.get(position).getSchool().getSchoolName());
			holder.gender.setText((userPreference.getU_gender().equals(Constants.Gender.MALE)) ? "Ů" : "��");

			if (!TextUtils.isEmpty(todayRecommendList.get(position).getUserAvatar())) {
				imageLoader.displayImage(
						AsyncHttpClientImageSound.getAbsoluteUrl(todayRecommendList.get(position).getUserAvatar()),
						holder.headimage, ImageLoaderTool.getHeadImageOptions());
			}

			//���ͷ���������ҳ��
			holder.headimage.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(DayRecommendActivity.this, PersonDetailActivity.class);
					intent.putExtra(PersonDetailActivity.PERSON_TYPE, Constants.PersonDetailType.SINGLE);
					intent.putExtra(UserTable.U_ID, todayRecommendList.get(position).getUserID());
					startActivity(intent);
					overridePendingTransition(R.anim.zoomin2, R.anim.zoomout);
				}
			});
			container.addView(view);
			return view;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			// TODO Auto-generated method stub  

		}

		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub  
			return null;
		}

		@Override
		public void startUpdate(ViewGroup container) {
			// TODO Auto-generated method stub  

		}

		@Override
		public void finishUpdate(ViewGroup container) {
			// TODO Auto-generated method stub  

		}
	}

	/**
	 * 
	 * �����ƣ�PageChangeListener
	 * ������������ҳ������¼�������
	 * �����ˣ� ��˧
	 * ����ʱ�䣺2014��7��14�� ����2:41:18
	 *
	 */
	private class PageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int state) {
			// TODO Auto-generated method stub  

		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			// TODO Auto-generated method stub  

		}

		@Override
		public void onPageSelected(int position) {
			pageIndex = position;
			for (int i = 0; i < imageCircleViews.length; i++) {
				imageCircleViews[position].setBackgroundResource(R.drawable.dot_selected);
				if (position != i) {
					imageCircleViews[i].setBackgroundResource(R.drawable.dot);
				}
			}
			like.setChecked((currentLike == pageIndex) ? true : false);
		}
	}
}
