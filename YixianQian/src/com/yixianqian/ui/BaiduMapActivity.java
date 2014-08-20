package com.yixianqian.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.utils.CoordinateConvert;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.easemob.util.EMLog;
import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;

/**
 * �����ƣ�BaiduMapActivity
 * ���������ٶȵ�ͼҳ��
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��8��20�� ����9:43:10
 *
 */
public class BaiduMapActivity extends BaseActivity {
	private View topNavLeftBtn;//��������߰�ť
	private TextView topNavText;//����������
	private View right_btn_bg;

	private final static String TAG = "map";
	static MapView mMapView = null;
	private MapController mMapController = null;
	public MKMapViewListener mMapListener = null;
	FrameLayout mMapViewContainer = null;

	// ��λ���
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	public NotifyLister mNotifyer = null;

	//	Button sendButton = null;
	EditText indexText = null;
	int index = 0;
	// LocationData locData = null;
	static BDLocation lastLocation = null;

	public static BaiduMapActivity instance = null;

	ProgressDialog progressDialog;

	ItemizedOverlay<OverlayItem> mAddrOverlay = null;

	// for baidu map
	public BMapManager mBMapManager = null;
	public static final String strKey = "575EA7C9A16973077FD89C494CDD2733F66ED875";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		// Gl app = (Gl)this.getApplication();
		if (mBMapManager == null) {
			initEngineManager(this.getApplicationContext());
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_baidumap);
		findViewById();
		initView();

		mMapController = mMapView.getController();
		initMapView();

		mMapView.getController().setZoom(17);
		mMapView.getController().enableClick(true);
		mMapView.setBuiltInZoomControls(true);
		Intent intent = getIntent();
		double latitude = intent.getDoubleExtra("latitude", 0);
		if (latitude == 0) {
			showMapWithLocationClient();
		} else {
			double longtitude = intent.getDoubleExtra("longitude", 0);
			String address = intent.getStringExtra("address");
			showMap(latitude, longtitude, address);
		}
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		mMapView = (MapView) findViewById(R.id.bmapView);
		topNavLeftBtn = findViewById(R.id.left_btn_bg);
		right_btn_bg = (View) findViewById(R.id.right_btn_bg);
		topNavText = (TextView) findViewById(R.id.nav_text);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavText.setText("λ����Ϣ");
		topNavLeftBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			}
		});
		right_btn_bg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendLocation(v);
			}
		});
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	private void showMap(double latitude, double longtitude, String address) {
		right_btn_bg.setVisibility(View.GONE);

		GeoPoint point1 = new GeoPoint((int) (latitude * 1e6), (int) (longtitude * 1e6));
		point1 = CoordinateConvert.fromGcjToBaidu(point1);
		mMapController.setCenter(point1);
		Drawable marker = this.getResources().getDrawable(R.drawable.icon_marka);
		// Ϊmaker����λ�úͱ߽�
		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
		mAddrOverlay = new ItemizedOverlay<OverlayItem>(marker, mMapView);
		GeoPoint point = new GeoPoint((int) (latitude * 1e6), (int) (longtitude * 1e6));
		point = CoordinateConvert.fromGcjToBaidu(point);
		OverlayItem addrItem = new OverlayItem(point, "", address);
		mAddrOverlay.addItem(addrItem);
		mMapView.getOverlays().add(mAddrOverlay);
		mMapView.refresh();
	}

	private void showMapWithLocationClient() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("����ȷ�����λ��...");

		progressDialog.setOnCancelListener(new OnCancelListener() {

			public void onCancel(DialogInterface arg0) {
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				Log.d("map", "cancel retrieve location");
				finish();
			}
		});

		progressDialog.show();

		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);

		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// ��gps
		// option.setCoorType("bd09ll"); //������������
		// Johnson change to use gcj02 coordination. chinese national standard
		// so need to conver to bd09 everytime when draw on baidu map
		option.setCoorType("gcj02");
		option.setScanSpan(30000);
		//		option.setAddrType("all");
		mLocClient.setLocOption(option);

		Drawable marker = this.getResources().getDrawable(R.drawable.icon_marka);
		// Ϊmaker����λ�úͱ߽�
		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
		mAddrOverlay = new ItemizedOverlay<OverlayItem>(marker, mMapView);
		mMapView.getOverlays().add(mAddrOverlay);

		mMapListener = new MKMapViewListener() {

			@Override
			public void onMapMoveFinish() {
				// TODO Auto-generated method stub
			}

			@Override
			public void onClickMapPoi(MapPoi mapPoiInfo) {
				// TODO Auto-generated method stub
				String title = "";
				if (mapPoiInfo != null) {
					title = mapPoiInfo.strText;
					Toast.makeText(BaiduMapActivity.this, title, Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onGetCurrentMap(Bitmap b) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onMapAnimationFinish() {
			}
		};
		mMapView.regMapViewListener(mBMapManager, mMapListener);

		if (lastLocation != null) {
			GeoPoint point1 = new GeoPoint((int) (lastLocation.getLatitude() * 1e6),
					(int) (lastLocation.getLongitude() * 1e6));
			point1 = CoordinateConvert.fromGcjToBaidu(point1);
			mMapController.setCenter(point1);
		}
		mMapView.refresh();
		mMapView.invalidate();
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		if (mLocClient != null) {
			mLocClient.stop();
		}
		super.onPause();
		lastLocation = null;
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		if (mLocClient != null) {
			mLocClient.start();
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if (mLocClient != null)
			mLocClient.stop();
		mMapView.destroy();

		// Gl app = (Gl)this.getApplication();
		if (mBMapManager != null) {
			mBMapManager.destroy();
			mBMapManager = null;
		}
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mMapView.onRestoreInstanceState(savedInstanceState);
	}

	private void initMapView() {
		mMapView.setLongClickable(true);
	}

	/**
	 * ��������������λ�õ�ʱ�򣬸�ʽ�����ַ������������Ļ��
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				return;
			}
			Log.d("map", "On location change received:" + location);
			Log.d("map", "addr:" + location.getAddrStr());
			right_btn_bg.setEnabled(true);
			if (progressDialog != null) {
				progressDialog.dismiss();
			}

			if (lastLocation != null) {
				if (lastLocation.getLatitude() == location.getLatitude()
						&& lastLocation.getLongitude() == location.getLongitude()) {
					Log.d("map", "same location, skip refresh");
					// mMapView.refresh(); //need this refresh?
					return;
				}
			}

			lastLocation = location;

			GeoPoint gcj02Point = new GeoPoint((int) (location.getLatitude() * 1e6),
					(int) (location.getLongitude() * 1e6));
			EMLog.d(TAG, "GCJ-02 loc:" + gcj02Point);
			GeoPoint point = CoordinateConvert.fromGcjToBaidu(gcj02Point);
			EMLog.d(TAG, "converted BD-09 loc:" + point);

			// GeoPoint p1 = gcjToBaidu(location.getLatitude(),
			// location.getLongitude());
			// System.err.println("johnson change to baidu:" + p1);
			// GeoPoint p2 = baiduToGcj(location.getLatitude(),
			// location.getLongitude());
			// System.err.println("johnson change to gcj:" + p2);

			OverlayItem addrItem = new OverlayItem(point, "title", location.getAddrStr());
			mAddrOverlay.removeAll();
			mAddrOverlay.addItem(addrItem);
			mMapView.getController().setZoom(17);
			mMapView.refresh();
			mMapController.animateTo(point);
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}

	public class NotifyLister extends BDNotifyListener {
		public void onNotify(BDLocation mlocation, float distance) {
		}
	}

	public void sendLocation(View view) {
		Intent intent = this.getIntent();
		intent.putExtra("latitude", lastLocation.getLatitude());
		intent.putExtra("longitude", lastLocation.getLongitude());
		intent.putExtra("address", lastLocation.getAddrStr());
		this.setResult(RESULT_OK, intent);
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	public void initEngineManager(Context context) {
		if (mBMapManager == null) {
			mBMapManager = new BMapManager(context);
		}

		if (!mBMapManager.init(strKey, new MyGeneralListener())) {
			Toast.makeText(this.getApplicationContext(), "BMapManager  ��ʼ������!", Toast.LENGTH_LONG).show();
		}
	}

	class MyGeneralListener implements MKGeneralListener {

		@Override
		public void onGetNetworkState(int iError) {
			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
				Toast.makeText(BaiduMapActivity.this, "���������������", Toast.LENGTH_LONG).show();
			} else if (iError == MKEvent.ERROR_NETWORK_DATA) {
				Toast.makeText(BaiduMapActivity.this, "������ȷ�ļ���������", Toast.LENGTH_LONG).show();
			}
			// ...
		}

		@Override
		public void onGetPermissionState(int iError) {
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				// ��ȨKey����
				Log.e("map", "permissio denied. check your app key");
			}
		}
	}

	static final double x_pi = 3.14159265358979324 * 3000.0 / 180.0;

	public static GeoPoint gcjToBaidu(double lat, double lng) {
		double x = lng, y = lat;
		double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
		double bdLng = z * Math.cos(theta) + 0.0065;
		double bdLat = z * Math.sin(theta) + 0.006;
		return new GeoPoint((int) (bdLat * 1e6), (int) (bdLng * 1e6));
	}

	public static GeoPoint baiduToGcj(double lat, double lng) {
		double x = lng - 0.0065, y = lat - 0.006;
		double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
		double gcjLng = z * Math.cos(theta);
		double gcjLat = z * Math.sin(theta);
		return new GeoPoint((int) (gcjLat * 1e6), (int) (gcjLng * 1e6));
	}
}
