package com.yixianqian.utils;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yixianqian.config.Constants;

public class AsyncHttpClientTool {
	private static final String BASE_URL = Constants.AppliactionServerDomain+"YXQServer/";

	private static AsyncHttpClient client = new AsyncHttpClient();

	public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}

	public static void get(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.get(context, getAbsoluteUrl(url), params, responseHandler);
	}

	public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.post(getAbsoluteUrl(url), params, responseHandler);
	}

	public static void post(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.post(context, getAbsoluteUrl(url), params, responseHandler);
	}

	public static String getAbsoluteUrl(String relativeUrl) {
		return BASE_URL + relativeUrl;
	}
}
