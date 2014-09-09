package com.yixianqian.server;

import java.util.List;
import java.util.Map;

import org.apache.http.Header;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yixianqian.R;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.db.ConversationDbService;
import com.yixianqian.db.TodayRecommendDbService;
import com.yixianqian.entities.Conversation;
import com.yixianqian.entities.TodayRecommend;
import com.yixianqian.jsonobject.JsonTodayRecommend;
import com.yixianqian.jsonobject.JsonUser;
import com.yixianqian.table.UserTable;
import com.yixianqian.ui.DayRecommendActivity;
import com.yixianqian.ui.MainActivity;
import com.yixianqian.utils.AsyncHttpClientImageSound;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.DateTimeTools;
import com.yixianqian.utils.FastJsonTool;
import com.yixianqian.utils.FriendPreference;
import com.yixianqian.utils.ImageLoaderTool;
import com.yixianqian.utils.LogTool;
import com.yixianqian.utils.MD5For16;
import com.yixianqian.utils.SharePreferenceUtil;
import com.yixianqian.utils.UserPreference;

public class ServerUtil {
	private static ServerUtil instance;
	private UserPreference userPreference;
	private FriendPreference friendpreference;
	private SharePreferenceUtil sharePreferenceUtil;
	private List<JsonUser> jsonUsers;

	public ServerUtil() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * �õ�ʵ��
	 * @param context
	 * @return
	 */
	public static ServerUtil getInstance() {
		if (instance == null) {
			instance = new ServerUtil();
			instance.userPreference = BaseApplication.getInstance().getUserPreference();
			instance.friendpreference = BaseApplication.getInstance().getFriendPreference();
			instance.sharePreferenceUtil = new SharePreferenceUtil(BaseApplication.getInstance(),
					SharePreferenceUtil.USE_COUNT);
		}
		return instance;
	}

	/**
	 * �û���¼
	 */
	public void initUserData(final Context context, final boolean isFinished) {
		String mPhone = userPreference.getU_tel();
		final String mPassword = userPreference.getU_password();

		RequestParams params = new RequestParams();
		params.put(UserTable.U_TEL, mPhone);
		params.put(UserTable.U_PASSWORD, mPassword);
		params.put(UserTable.U_BPUSH_USER_ID, userPreference.getBpush_UserID());
		params.put(UserTable.U_BPUSH_CHANNEL_ID, userPreference.getBpush_ChannelID());

		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {
			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				if (statusCode == 200 && !TextUtils.isEmpty(response)) {
					jsonUsers = FastJsonTool.getObjectList(response, JsonUser.class);
					if (jsonUsers != null) {
						if (jsonUsers.size() > 0) {
							LogTool.i("ServerUtil", "��¼�ɹ���");
							if (jsonUsers.size() == 1) {
								int oldState = userPreference.getU_stateid();
								int newState = jsonUsers.get(0).getU_stateid();
								saveUser(jsonUsers.get(0), mPassword);
								//��������»��Ķ���Ϊ����
								if ((oldState == 2 || oldState == 3) && newState == 4) {
									ConversationDbService conversationDbService = ConversationDbService
											.getInstance(context);
									//ɾ���Ự
									Conversation conversation = conversationDbService
											.getConversationByUser(friendpreference.getF_id());
									if (conversation != null) {
										conversationDbService.conversationDao.delete(conversation);
										//ɾ���Ự
										EMChatManager.getInstance().deleteConversation("" + friendpreference.getF_id());
										friendpreference.clear();
										userPreference.setU_stateid(4);
									}
								}
								getTodayRecommend(context, isFinished);
							} else if (jsonUsers.size() > 1) {
								saveUser(jsonUsers.get(0), mPassword);
								saveFriend(jsonUsers.get(1));

								if (jsonUsers.get(1) != null) {
									//�����Ի�
									ConversationDbService conversationDbService = ConversationDbService
											.getInstance(context);

									if (!conversationDbService.isConversationExist(friendpreference.getF_id())) {
										Conversation conversation = new Conversation(null,
												Long.valueOf(friendpreference.getF_id()), friendpreference.getName(),
												friendpreference.getF_small_avatar(), "", 0, System.currentTimeMillis());
										conversationDbService.conversationDao.insert(conversation);
									}
								} else {
									LogTool.e("Login", "��¼��ȡ�����ˣ����ǵڶ���Ϊ��");
								}

								Intent intent = new Intent(context, MainActivity.class);
								context.startActivity(intent);
								((Activity) context).overridePendingTransition(R.anim.push_left_in,
										R.anim.push_left_out);
							}
							//��¼����
							//							attempLoginHuanXin(1);
						} else {
							LogTool.e("ServerUtil", "jsonUsers����Ϊ0");
						}
					} else {
						LogTool.e("ServerUtil", "��¼����Ϊ��");
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
			}
		};
		AsyncHttpClientTool.post("login", params, responseHandler);
	}

	/**
	 * ��¼����
	 */
	//	private void loginHuanXin() {
	//		EMChatManager.getInstance().login(userPreference.getHuanXinUserName(), userPreference.getHuanXinPassword(),
	//				new EMCallBack() {
	//
	//					@Override
	//					public void onSuccess() {
	//						userPreference.setUserLogin(true);
	//						//���»����ǳ�
	//						if (EMChatManager.getInstance().updateCurrentUserNick(userPreference.getName())) {
	//							LogTool.i("LoginActivity", "���»����ǳ�");
	//						} else {
	//							LogTool.e("LoginActivity", "���»����ǳ�ʧ��");
	//						}
	//					}
	//
	//					@Override
	//					public void onProgress(int progress, String status) {
	//					}
	//
	//					@Override
	//					public void onError(int code, final String message) {
	//						LogTool.e("��¼����", "code:" + code + "   message:" + message);
	//					}
	//				});
	//	}

	/**
	 * ��½
	 * 
	 * @param view
	 */
	//	public void attempLoginHuanXin(int time) {
	//		if (!NetworkUtils.isNetworkAvailable(appContext)) {
	//			NetworkUtils.networkStateTips(appContext);
	//			return;
	//		}
	//		// ����sdk��½������½���������
	//		if (!TextUtils.isEmpty(userPreference.getHuanXinUserName())
	//				&& !TextUtils.isEmpty(userPreference.getHuanXinPassword())) {
	//			loginHuanXin();
	//		} else {
	//			if (time == 1) {
	//				userPreference.setHuanXinUserName("" + userPreference.getU_id());
	//				userPreference.setHuanXinPassword(MD5For16.GetMD5CodeToLower(userPreference.getU_password()));
	//				attempLoginHuanXin(2);
	//			}
	//		}
	//	}

	/**
	 * �洢�Լ�����Ϣ
	 */
	private void saveUser(final JsonUser user, final String password) {
		// TODO Auto-generated method stub
		LogTool.i("ServerUtil", "�洢������Ϣ");
		userPreference.setU_birthday(user.getU_birthday());
		userPreference.setU_blood_type(user.getU_blood_type());
		userPreference.setU_cityid(user.getU_cityid());
		userPreference.setU_constell(user.getU_constell());
		userPreference.setU_email(user.getU_email());
		userPreference.setU_gender(user.getU_gender());
		userPreference.setU_age(user.getU_age());
		userPreference.setU_height(user.getU_height());
		userPreference.setU_id(user.getU_id());
		userPreference.setU_introduce(user.getU_introduce());
		userPreference.setU_large_avatar(user.getU_large_avatar());
		userPreference.setU_nickname(user.getU_nickname());
		userPreference.setU_password(user.getU_password());
		userPreference.setU_provinceid(user.getU_provinceid());
		userPreference.setU_realname(user.getU_realname());
		userPreference.setU_salary(user.getU_salary());
		userPreference.setU_schoolid(user.getU_schoolid());
		userPreference.setU_small_avatar(user.getU_small_avatar());
		userPreference.setU_stateid(user.getU_stateid());
		userPreference.setU_tel(user.getU_tel());
		userPreference.setU_vocationid(user.getU_vocationid());
		userPreference.setU_weight(user.getU_weight());
		userPreference.setU_password(password);
		userPreference.setHuanXinUserName("" + user.getU_id());
		userPreference.setHuanXinPassword(MD5For16.GetMD5CodeToLower(password));
		userPreference.setHeadImagePassed(user.getU_image_pass());
		userPreference.setUserLogin(true);
	}

	/**
	 * �洢��һ��
	 */
	private void saveFriend(final JsonUser jsonUser) {
		if (jsonUser != null) {
			LogTool.i("ServerUtil", "�洢��һ����Ϣ");
			if (userPreference.getU_stateid() == 2) {
				friendpreference.setType(1);
			} else if (userPreference.getU_stateid() == 3) {
				friendpreference.setType(0);
			} else {
				return;
			}

			// TODO Auto-generated method stub
			friendpreference.setBpush_ChannelID(jsonUser.getU_bpush_channel_id());
			friendpreference.setBpush_UserID(jsonUser.getU_bpush_user_id());
			friendpreference.setU_birthday(jsonUser.getU_birthday());
			friendpreference.setF_address(jsonUser.getU_address());
			friendpreference.setF_age(jsonUser.getU_age());
			friendpreference.setF_blood_type(jsonUser.getU_blood_type());
			friendpreference.setF_constell(jsonUser.getU_constell());
			friendpreference.setF_email(jsonUser.getU_email());
			friendpreference.setF_gender(jsonUser.getU_gender());
			friendpreference.setF_height(jsonUser.getU_height());
			friendpreference.setF_id(jsonUser.getU_id());
			friendpreference.setF_introduce(jsonUser.getU_introduce());
			friendpreference.setF_large_avatar(jsonUser.getU_large_avatar());
			friendpreference.setF_nickname(jsonUser.getU_nickname());
			friendpreference.setF_realname(jsonUser.getU_realname());
			friendpreference.setF_salary(jsonUser.getU_salary());
			friendpreference.setF_small_avatar(jsonUser.getU_small_avatar());
			friendpreference.setF_stateid(jsonUser.getU_stateid());
			friendpreference.setF_tel(jsonUser.getU_tel());
			friendpreference.setF_vocationid(jsonUser.getU_vocationid());
			friendpreference.setF_weight(jsonUser.getU_weight());
			friendpreference.setU_cityid(jsonUser.getU_cityid());
			friendpreference.setU_provinceid(jsonUser.getU_provinceid());
			friendpreference.setU_schoolid(jsonUser.getU_schoolid());
		}
	}

	/**
	 * ��ʼ���û�����
	 * @param context
	 * @param isFinished
	 */
	//	public void initUserData(final Context context, final boolean isFinished) {
	//		RequestParams params = new RequestParams();
	//		params.put(UserTable.U_ID, userPreference.getU_id());
	//		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {
	//			@Override
	//			public void onSuccess(int statusCode, Header[] headers, String response) {
	//				// TODO Auto-generated method stub
	//				if (statusCode == 200) {
	//					if (!TextUtils.isEmpty(response)) {
	//						if (Integer.parseInt(response) > 0) {
	//							int oldState = userPreference.getU_stateid();
	//							int newState = Integer.parseInt(response);
	//							userPreference.setU_stateid(newState);
	//							//��������»��Ķ���Ϊ����
	//							if ((oldState == 2 || oldState == 3) && newState == 4) {
	//								ConversationDbService conversationDbService = ConversationDbService
	//										.getInstance(context);
	//								//ɾ���Ự
	//								Conversation conversation = conversationDbService
	//										.getConversationByUser(friendpreference.getF_id());
	//								if (conversation != null) {
	//									conversationDbService.conversationDao.delete(conversation);
	//									//ɾ���Ự
	//									EMChatManager.getInstance().deleteConversation("" + friendpreference.getF_id());
	//									friendpreference.clear();
	//									userPreference.setU_stateid(4);
	//								}
	//							}
	//							getTodayRecommend(context, isFinished);
	//						}
	//					}
	//				}
	//			}
	//
	//			@Override
	//			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
	//				// TODO Auto-generated method stub
	//			}
	//		};
	//		AsyncHttpClientTool.post("getuserstate", params, responseHandler);
	//	}

	/**
	 * ��ȡ�����Ƽ�
	 */
	public void getTodayRecommend(final Context context, final boolean isFinished) {
		if (userPreference.getU_stateid() == 4) {
			final TodayRecommendDbService todayRecommendDbService = TodayRecommendDbService.getInstance(context);
			todayRecommendDbService.todayRecommendDao.deleteAll();
			sharePreferenceUtil.setTodayRecommend("");

			//���û���Ƽ���
			if (!sharePreferenceUtil.getTodayRecommend().equals(DateTimeTools.getCurrentDateForString())) {
				RequestParams params = new RequestParams();
				params.put(UserTable.U_ID, userPreference.getU_id());
				TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {
					Intent intent = new Intent(context, DayRecommendActivity.class);

					@Override
					public void onSuccess(int statusCode, Header[] headers, String response) {
						// TODO Auto-generated method stub
						if (statusCode == 200) {
							List<JsonTodayRecommend> todayRecommends = FastJsonTool.getObjectList(response,
									JsonTodayRecommend.class);
							if (todayRecommends != null && todayRecommends.size() > 0) {
								for (JsonTodayRecommend jsonTodayRecommend : todayRecommends) {
									TodayRecommend todayRecommend = new TodayRecommend(null,
											jsonTodayRecommend.getUserID(), jsonTodayRecommend.getUserName(),
											jsonTodayRecommend.getUserAvatar(), jsonTodayRecommend.getUserAge(),
											DateTimeTools.getCurrentDateForString(), jsonTodayRecommend.getSchoolID());
									todayRecommendDbService.todayRecommendDao.insert(todayRecommend);
								}
								context.startActivity(intent);
								((Activity) context).overridePendingTransition(R.anim.push_left_in,
										R.anim.push_left_out);
							} else {
								intent = new Intent(context, MainActivity.class);
								context.startActivity(intent);
								((Activity) context).overridePendingTransition(R.anim.push_left_in,
										R.anim.push_left_out);
								if (isFinished) {
									((Activity) context).finish();
								}
							}
							sharePreferenceUtil.setTodayRecommend(DateTimeTools.getCurrentDateForString());
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
						// TODO Auto-generated method stub
						intent = new Intent(context, MainActivity.class);
						context.startActivity(intent);
						((Activity) context).overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
						if (isFinished) {
							((Activity) context).finish();
						}
					}
				};
				AsyncHttpClientTool.post(context, "userpush", params, responseHandler);
			} else {
				Intent intent = new Intent(context, MainActivity.class);
				context.startActivity(intent);
				((Activity) context).overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				if (isFinished) {
					((Activity) context).finish();
				}
			}
		} else {
			Intent intent = new Intent(context, MainActivity.class);
			context.startActivity(intent);
			((Activity) context).overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			if (isFinished) {
				((Activity) context).finish();
			}
		}
	}

	/**
	 * ��ȡͷ���ַ
	 * @param context
	 */
	public void getHeadImage(final ImageView imageView, final TextView textView) {
		LogTool.i("ServerUtil" + "��ȡͷ���ַ");

		RequestParams params = new RequestParams();
		params.put(UserTable.U_ID, userPreference.getU_id());

		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {
			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					Map<String, String> map = FastJsonTool.getObject(response, Map.class);
					if (map != null) {
						userPreference.setU_large_avatar(map.get(UserTable.U_LARGE_AVATAR));
						userPreference.setU_small_avatar(map.get(UserTable.U_SMALL_AVATAR));
						if (!TextUtils.isEmpty(map.get(UserTable.U_IMAGE_PASS))) {
							if (map.get(UserTable.U_IMAGE_PASS).equals("1")) {
								userPreference.setHeadImagePassed(1);
							} else if (map.get(UserTable.U_IMAGE_PASS).equals("0")) {
								userPreference.setHeadImagePassed(0);
							} else if (map.get(UserTable.U_IMAGE_PASS).equals("-1")) {
								userPreference.setHeadImagePassed(-1);
							}
						}
						//��ʾͷ��
						disPlayHeadImage(imageView, textView);
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
			}
		};
		AsyncHttpClientTool.post("getuseravatardetail", params, responseHandler);
	}

	/**
	 * ��ʾͷ������������ͨ��
	 */
	public void disPlayHeadImage(final ImageView imageView, final TextView textView) {
		int state = userPreference.getHeadImagePassed();
		//��ʾͷ��
		ImageLoader.getInstance().displayImage(
				AsyncHttpClientImageSound.getAbsoluteUrl(userPreference.getU_small_avatar()), imageView,
				ImageLoaderTool.getHeadImageOptions(10));
		//���Ϊ�����
		if (state == 0) {
			textView.setVisibility(View.VISIBLE);
			textView.setText("�����");
			textView.setTextColor(Color.BLACK);
		} else if (state == -1) {
			textView.setVisibility(View.VISIBLE);
			textView.setText("δͨ��");
			textView.setTextColor(Color.RED);
		}
	}

	/**
	 * ��ȡͷ���Ƿ�ͨ��
	 * @param context
	 */
	//	public void getHeadImagePass() {
	//		if (userPreference.getHeadImageChanged()) {
	//			RequestParams params = new RequestParams();
	//			params.put(UserTable.U_ID, userPreference.getU_id());
	//
	//			TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {
	//				@Override
	//				public void onSuccess(int statusCode, Header[] headers, String response) {
	//					// TODO Auto-generated method stub
	//					if (statusCode == 200) {
	//						if (response.equals("1")) {//ͨ��
	//							userPreference.setHeadImagePassed(1);
	//							userPreference.setHeadImageChanged(false);
	//						} else if (response.equals("0")) {//�����
	//							userPreference.setHeadImagePassed(0);
	//						} else if (response.equals("-1")) {//δͨ��
	//							userPreference.setHeadImagePassed(-1);
	//							userPreference.setHeadImageChanged(false);
	//						}
	//					}
	//				}
	//
	//				@Override
	//				public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
	//					// TODO Auto-generated method stub
	//				}
	//			};
	//			AsyncHttpClientTool.post("getuserimagepass", params, responseHandler);
	//		}
	//	}

}
