package com.yixianqian.test;

import org.apache.http.Header;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.yixianqian.R;

public class TestActivity extends Activity {
	ImageView testImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		testImage = (ImageView) findViewById(R.id.test);

		AsyncHttpClient client = new AsyncHttpClient();
		client.get("http://e.hiphotos.baidu.com/image/pic/item/d0c8a786c9177f3e2396bf0572cf3bc79f3d5613.jpg",
				new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] response) {
						// TODO Auto-generated method stub
						if (statusCode == 200) {
							Bitmap bitmap = BitmapFactory.decodeByteArray(response, 0, response.length);
							testImage.setImageBitmap(bitmap);
						}
					}
				});
	}
}
