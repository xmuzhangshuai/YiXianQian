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

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yixianqian.R;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.db.FlipperDbService;
import com.yixianqian.db.TodayRecommendDbService;
import com.yixianqian.entities.Flipper;
import com.yixianqian.entities.TodayRecommend;
import com.yixianqian.jsonobject.JsonFlipperRequest;
import com.yixianqian.jsonobject.JsonTodayRecommend;
import com.yixianqian.table.UserTable;
import com.yixianqian.ui.DayRecommendActivity;
import com.yixianqian.ui.MainActivity;
import com.yixianqian.utils.AsyncHttpClientImageSound;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.DateTimeTools;
import com.yixianqian.utils.FastJsonTool;
import com.yixianqian.utils.ImageLoaderTool;
import com.yixianqian.utils.SharePreferenceUtil;
import com.yixianqian.utils.UserPreference;

public class ServerUtil {
	private static ServerUtil instance;
	private static Context appContext;
	private UserPreference userPreference;
	private SharePreferenceUtil sharePreferenceUtil;

	public ServerUtil() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 得到实例
	 * @param context
	 * @return
	 */
	public static ServerUtil getInstance(Context context) {
		if (instance == null) {
			instance = new ServerUtil();
			if (appContext == null) {
				appContext = context.getApplicationContext();
			}
			instance.userPreference = BaseApplication.getInstance().getUserPreference();
			instance.sharePreferenceUtil = new SharePreferenceUtil(context, SharePreferenceUtil.USE_COUNT);
		}
		return instance;
	}

	/**
	 * 获取心动请求
	 */
	public void getFlipperAndRecommend(final Context context, final boolean isFinished) {
		//如果是单身，请求心动和今日推荐
		if (userPreference.getU_stateid() == 4) {
			final FlipperDbService flipperDbService = FlipperDbService.getInstance(context);
			RequestParams params = new RequestParams();
			params.put(UserTable.U_ID, userPreference.getU_id());

			TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {
				@Override
				public void onSuccess(int statusCode, Header[] headers, String response) {
					// TODO Auto-generated method stub
					if (statusCode == 200) {
						List<JsonFlipperRequest> jsonFlipperRequests = FastJsonTool.getObjectList(response,
								JsonFlipperRequest.class);
						if (jsonFlipperRequests != null && jsonFlipperRequests.size() > 0) {
							for (JsonFlipperRequest fRequest : jsonFlipperRequests) {
								Flipper flipper = new Flipper(null, fRequest.getU_id(), fRequest.getU_nickname(),
										fRequest.getU_realname(), fRequest.getU_gender(), fRequest.getU_email(),
										fRequest.getU_large_avatar(), fRequest.getU_small_avatar(),
										fRequest.getU_blood_type(), fRequest.getU_constell(),
										fRequest.getU_introduce(), fRequest.getU_birthday(), fRequest.getTime(),
										fRequest.getU_age(), fRequest.getU_vocationid(), fRequest.getU_stateid(),
										fRequest.getU_provinceid(), fRequest.getU_cityid(), fRequest.getU_schoolid(),
										fRequest.getU_height(), fRequest.getU_weight(), fRequest.getU_image_pass(),
										fRequest.getU_salary(), false, fRequest.getU_tel());
								flipperDbService.flipperDao.insert(flipper);
							}
						}
					}
					getTodayRecommend(context, isFinished);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
					// TODO Auto-generated method stub
					getTodayRecommend(context, isFinished);
				}
			};
			AsyncHttpClientTool.post(context, "getflipperrequest", params, responseHandler);
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
	 * 获取今日推荐
	 */
	public void getTodayRecommend(final Context context, final boolean isFinished) {
		final TodayRecommendDbService todayRecommendDbService = TodayRecommendDbService.getInstance(context);
		todayRecommendDbService.todayRecommendDao.deleteAll();
		//		sharePreferenceUtil.setTodayRecommend("");

		//如果没有推荐过
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
							((Activity) context).overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
						} else {
							intent = new Intent(context, MainActivity.class);
							context.startActivity(intent);
							((Activity) context).overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
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
	}

	/**
	 * 获取头像地址
	 * @param context
	 */
	public void getHeadImage(final ImageView imageView, final TextView textView) {
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
						//显示头像
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
	 * 显示头像，如果本地审核通过
	 */
	public void disPlayHeadImage(final ImageView imageView, final TextView textView) {
		int state = userPreference.getHeadImagePassed();
		//显示头像
		ImageLoader.getInstance().displayImage(
				AsyncHttpClientImageSound.getAbsoluteUrl(userPreference.getU_small_avatar()), imageView,
				ImageLoaderTool.getHeadImageOptions(10));
		//如果为待审核
		if (state == 0) {
			textView.setVisibility(View.VISIBLE);
		} else if (state == -1) {
			textView.setVisibility(View.VISIBLE);
			textView.setText("未通过");
			textView.setTextColor(Color.RED);
		}
	}

	/**
	 * 获取头像是否通过
	 * @param context
	 */
	public void getHeadImagePass() {
		RequestParams params = new RequestParams();
		params.put(UserTable.U_ID, userPreference.getU_id());

		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {
			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					if (response.equals("1")) {
						userPreference.setHeadImagePassed(1);
					} else if (response.equals("0")) {
						userPreference.setHeadImagePassed(0);
					} else if (response.equals("-1")) {
						userPreference.setHeadImagePassed(-1);
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
			}
		};
		AsyncHttpClientTool.post("getuserimagepass", params, responseHandler);
	}
}
