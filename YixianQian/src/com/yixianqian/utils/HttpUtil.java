package com.yixianqian.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.yixianqian.config.DefaultSetting;

public class HttpUtil {
	// ����HttpClient����
	public static HttpClient httpClient;;
	public static final String BASE_URL = DefaultSetting.DoMainName + "YXQServer/";

	/**
	 *
	 * @param url ���������URL
	 * @return ��������Ӧ�ַ���
	 * @throws Exception
	 */
	public static String getRequest(final String url) throws Exception {
		httpClient = new DefaultHttpClient();
		FutureTask<String> task = new FutureTask<String>(new Callable<String>() {
			@Override
			public String call() throws Exception {
				// ����HttpGet����
				HttpGet get = new HttpGet(BASE_URL + url);
				// ����GET����
				HttpResponse httpResponse = httpClient.execute(get);
				// ����������ɹ��ط�����Ӧ
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					// ��ȡ��������Ӧ�ַ���
					String result = EntityUtils.toString(httpResponse.getEntity());
					return result;
				}
				return null;
			}
		});
		new Thread(task).start();
		return task.get();
	}

	/**
	 * @param url ���������URL
	 * @param params �������
	 * @return ��������Ӧ�ַ���
	 * @throws Exception
	 */
	public static String postRequest(final String url, final Map<String, String> rawParams) throws Exception {
		httpClient = new DefaultHttpClient();
		FutureTask<String> task = new FutureTask<String>(new Callable<String>() {
			@Override
			public String call() throws Exception {
				// ����HttpPost����
				HttpPost post = new HttpPost(BASE_URL + url);
				// ������ݲ��������Ƚ϶�Ļ����ԶԴ��ݵĲ������з�װ
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				for (String key : rawParams.keySet()) {
					// ��װ�������
					params.add(new BasicNameValuePair(key, rawParams.get(key)));
				}
				// �����������
				post.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
				// ����POST����
				HttpResponse httpResponse = httpClient.execute(post);
				// ����������ɹ��ط�����Ӧ
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					// ��ȡ��������Ӧ�ַ���
					String result = EntityUtils.toString(httpResponse.getEntity());
					return result;
				}
				return null;
			}
		});
		new Thread(task).start();
		return task.get();
	}
}
