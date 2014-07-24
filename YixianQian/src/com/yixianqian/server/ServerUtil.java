package com.yixianqian.server;

import java.util.List;

import org.apache.http.Header;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
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
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.DateTimeTools;
import com.yixianqian.utils.FastJsonTool;
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
									fRequest.getU_blood_type(), fRequest.getU_constell(), fRequest.getU_introduce(),
									fRequest.getU_birthday(), fRequest.getTime(), fRequest.getU_age(),
									fRequest.getU_vocationid(), fRequest.getU_stateid(), fRequest.getU_provinceid(),
									fRequest.getU_schoolid(), fRequest.getU_height(), fRequest.getU_weight(),
									fRequest.getU_image_pass(), fRequest.getU_salary(), false);
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
	}

	/**
	 * 获取今日推荐
	 */
	public void getTodayRecommend(final Context context, final boolean isFinished) {
		final TodayRecommendDbService todayRecommendDbService = TodayRecommendDbService.getInstance(context);
		todayRecommendDbService.todayRecommendDao.deleteAll();

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
}
