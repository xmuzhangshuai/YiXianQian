package com.yixianqian.base;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.ActivityManager;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.StrictMode;
import android.preference.PreferenceManager;

import com.baidu.frontia.FrontiaApplication;
import com.easemob.chat.ConnectionListener;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.OnNotificationClickListener;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.yixianqian.R;
import com.yixianqian.baidupush.BaiduPush;
import com.yixianqian.config.Constants;
import com.yixianqian.config.Constants.Config;
import com.yixianqian.dao.DaoMaster;
import com.yixianqian.dao.DaoMaster.OpenHelper;
import com.yixianqian.dao.DaoSession;
import com.yixianqian.jsonobject.JsonUser;
import com.yixianqian.ui.ChatActivity2;
import com.yixianqian.ui.MainActivity;
import com.yixianqian.utils.FriendPreference;
import com.yixianqian.utils.LogTool;
import com.yixianqian.utils.UserPreference;

/**   
 *    
 * ��Ŀ���ƣ�ExamHelper   
 * �����ƣ�BaseApplication   
 * ��������   ��ȡ��DaoMaster����ķ����ŵ�Application�����������δ�������Session����
 * �����ˣ���˧     
 * ����ʱ�䣺2013-12-20 ����9:10:55   
 * �޸��ˣ���˧     
 * �޸�ʱ�䣺2013-12-20 ����9:10:55   
 * �޸ı�ע��   
 * @version    
 *    
 */
public class BaseApplication extends Application {
	private static BaseApplication myApplication;
	private static DaoMaster daoMaster;
	private static DaoSession daoSession;
	private Map<String, Integer> mFaceMap = new LinkedHashMap<String, Integer>();
	private FriendPreference friendSharePreference;
	private UserPreference userPreference;
	private MediaPlayer messagePlayer;
	private BaiduPush mBaiduPushServer;
	private NotificationManager mNotificationManager;
	public static Context applicationContext;
	private String huanXinUserName;
	private String huanxinPassword;

	public synchronized static BaseApplication getInstance() {
		return myApplication;
	}

	@SuppressWarnings("unused")
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		if (Config.DEVELOPER_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());
		}
		super.onCreate();

		applicationContext = this;
		if (myApplication == null)
			myApplication = this;
		initImageLoader(getApplicationContext());
//		//ʹ�ðٶ�push�ӿ�
		FrontiaApplication.initFrontiaApplication(applicationContext);
		initFaceMap();
		initData();

		int pid = android.os.Process.myPid();
		String processAppName = getAppName(pid);
		//���ʹ�õ��ٶȵ�ͼ������������remote service�ĵ������⣬���if�жϲ�����
		if (processAppName == null || processAppName.equals("")) {
			// workaround for baidu location sdk 
			// �ٶȶ�λsdk����λ����������һ�������Ľ��̣�ÿ�ζ�λ����������ʱ�򣬶������application::onCreate
			// �����µĽ��̡�
			// �����ŵ�sdkֻ��Ҫ���������г�ʼ��һ�Ρ� ������⴦���ǣ������pid �Ҳ�����Ӧ��processInfo
			// processName��
			// ���application::onCreate �Ǳ�service ���õģ�ֱ�ӷ���
			return;
		}

		// ��ʼ������SDK,һ��Ҫ�ȵ���init()
		EMChat.getInstance().init(applicationContext);
		EMChat.getInstance().setDebugMode(true);
		// ��ȡ��EMChatOptions����
		EMChatOptions options = EMChatManager.getInstance().getChatOptions();
		// Ĭ����Ӻ���ʱ���ǲ���Ҫ��֤�ģ��ĳ���Ҫ��֤
		options.setAcceptInvitationAlways(false);
		// �����յ���Ϣ�Ƿ�������Ϣ֪ͨ��Ĭ��Ϊtrue
		options.setNotificationEnable(true);
		// �����յ���Ϣ�Ƿ���������ʾ��Ĭ��Ϊtrue
		options.setNoticeBySound(true);
		// �����յ���Ϣ�Ƿ��� Ĭ��Ϊtrue
		options.setNoticedByVibrate(true);
		// ����������Ϣ�����Ƿ�����Ϊ���������� Ĭ��Ϊtrue
		options.setUseSpeaker(true);

		//����notification��Ϣ���ʱ����ת��intentΪ�Զ����intent
		options.setOnNotificationClickListener(new OnNotificationClickListener() {

			@Override
			public Intent onNotificationClick(EMMessage message) {
				Intent intent = new Intent(applicationContext, ChatActivity2.class);
				ChatType chatType = message.getChatType();
				if (chatType == ChatType.Chat) { //������Ϣ
					intent.putExtra("userId", message.getFrom());
					intent.putExtra("chatType", ChatActivity2.CHATTYPE_SINGLE);
				}
				return intent;
			}
		});
		//����һ��connectionlistener�����˻��ظ���½
		EMChatManager.getInstance().addConnectionListener(new MyConnectionListener());

	}

	private void initData() {
		mBaiduPushServer = new BaiduPush(BaiduPush.HTTP_METHOD_POST, Constants.BaiduPushConfig.SECRIT_KEY,
				Constants.BaiduPushConfig.API_KEY);
		friendSharePreference = new FriendPreference(this);
		userPreference = new UserPreference(this);
		messagePlayer = MediaPlayer.create(this, R.raw.office);
		mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
	}

	public NotificationManager getNotificationManager() {
		if (mNotificationManager == null)
			mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		return mNotificationManager;
	}

	private String getAppName(int pID) {
		String processName = null;
		ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		List l = am.getRunningAppProcesses();
		Iterator i = l.iterator();
		PackageManager pm = this.getPackageManager();
		while (i.hasNext()) {
			ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
			try {
				if (info.pid == pID) {
					CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName,
							PackageManager.GET_META_DATA));
					//					LogTool.d("Process",
					//							"Id: " + info.pid + " ProcessName: " + info.processName + "  Label: " + c.toString());
					processName = c.toString();
					processName = info.processName;
					return processName;
				}
			} catch (Exception e) {
				LogTool.d("Process", "Error>> :" + e.toString());
			}
		}
		return processName;
	}

	/**
	 * ��ȡ��ǰ��½�û���
	 * 
	 * @return
	 */
	public String getHuanXinUserName() {
		if (huanXinUserName == null) {
			userPreference.getHuanXinUserName();
		}
		return huanXinUserName;
	}

	/**
	 * ��ȡ����
	 * 
	 * @return
	 */
	public String getHuanXinPassword() {
		if (huanxinPassword == null) {
			userPreference.getHuanXinPassword();
		}
		return huanxinPassword;
	}

	/**
	 * �����û���
	 * 
	 * @param user
	 */
	public void setHuanXinUserName(String username) {
		if (username != null) {
			userPreference.setHuanXinUserName(username);
		}
	}

	/**
	 * ��������
	 * �����ʵ������ ֻ��demo��ʵ�ʵ�Ӧ������Ҫ��password ���ܺ���� preference
	 * ����sdk �ڲ����Զ���¼��Ҫ�����룬�Ѿ����ܴ洢��
	 * @param pwd
	 */
	public void setHuanXinPassword(String pwd) {
		userPreference.setHuanXinPassword(pwd);
	}

	/**
	 * �˳���¼,�������
	 */
	public void logout() {
		// �ȵ���sdk logout��������app���Լ�������
		EMChatManager.getInstance().logout();
		userPreference.setHuanXinUserName(null);
		userPreference.setHuanXinPassword(null);
	}

	/**
	 * ������Ϣ��ʾ����
	 * @return
	 */
	public synchronized MediaPlayer getMessagePlayer() {
		if (messagePlayer == null)
			messagePlayer = MediaPlayer.create(this, R.raw.office);
		return messagePlayer;
	}

	public synchronized FriendPreference getFriendPreference() {
		if (friendSharePreference == null)
			friendSharePreference = new FriendPreference(this);
		return friendSharePreference;
	}

	public synchronized UserPreference getUserPreference() {
		if (userPreference == null)
			userPreference = new UserPreference(this);
		return userPreference;
	}

	public synchronized BaiduPush getBaiduPush() {
		if (mBaiduPushServer == null)
			mBaiduPushServer = new BaiduPush(BaiduPush.HTTP_METHOD_POST, Constants.BaiduPushConfig.SECRIT_KEY,
					Constants.BaiduPushConfig.API_KEY);
		return mBaiduPushServer;

	}

	/** 
	 * ȡ��DaoMaster 
	 *  
	 * @param context 
	 * @return 
	 */
	public static DaoMaster getDaoMaster(Context context) {
		if (daoMaster == null) {
			OpenHelper openHelper = new DaoMaster.DevOpenHelper(context, "yixianqian.db", null);
			daoMaster = new DaoMaster(openHelper.getWritableDatabase());
		}
		return daoMaster;
	}

	/**
	 * ȡ��DaoSession 
	 * @param context
	 * @return
	 */
	public static DaoSession getDaoSession(Context context) {
		if (daoSession == null) {
			if (daoMaster == null) {
				daoMaster = getDaoMaster(context);
			}
			daoSession = daoMaster.newSession();
		}
		return daoSession;
	}

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	public Map<String, Integer> getFaceMap() {
		if (!mFaceMap.isEmpty())
			return mFaceMap;
		return null;
	}

	class MyConnectionListener implements ConnectionListener {
		@Override
		public void onReConnecting() {
		}

		@Override
		public void onReConnected() {
		}

		@Override
		public void onDisConnected(String errorString) {
			if (errorString != null && errorString.contains("conflict")) {
				Intent intent = new Intent(applicationContext, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("conflict", true);
				startActivity(intent);
			}

		}

		@Override
		public void onConnecting(String progress) {

		}

		@Override
		public void onConnected() {
		}
	}

	/**
	 * ��ʼ������
	 */
	private void initFaceMap() {
		// TODO Auto-generated method stub
		mFaceMap.put("[����]", R.drawable.f000);
		mFaceMap.put("[��Ƥ]", R.drawable.f001);
		mFaceMap.put("[����]", R.drawable.f002);
		mFaceMap.put("[͵Ц]", R.drawable.f003);
		mFaceMap.put("[�ټ�]", R.drawable.f004);
		mFaceMap.put("[�ô�]", R.drawable.f005);
		mFaceMap.put("[����]", R.drawable.f006);
		mFaceMap.put("[��ͷ]", R.drawable.f007);
		mFaceMap.put("[õ��]", R.drawable.f008);
		mFaceMap.put("[����]", R.drawable.f009);
		mFaceMap.put("[���]", R.drawable.f010);
		mFaceMap.put("[��]", R.drawable.f011);
		mFaceMap.put("[��]", R.drawable.f012);
		mFaceMap.put("[ץ��]", R.drawable.f013);
		mFaceMap.put("[ί��]", R.drawable.f014);
		mFaceMap.put("[���]", R.drawable.f015);
		mFaceMap.put("[ը��]", R.drawable.f016);
		mFaceMap.put("[�˵�]", R.drawable.f017);
		mFaceMap.put("[�ɰ�]", R.drawable.f018);
		mFaceMap.put("[ɫ]", R.drawable.f019);
		mFaceMap.put("[����]", R.drawable.f020);

		mFaceMap.put("[����]", R.drawable.f021);
		mFaceMap.put("[��]", R.drawable.f022);
		mFaceMap.put("[΢Ц]", R.drawable.f023);
		mFaceMap.put("[��ŭ]", R.drawable.f024);
		mFaceMap.put("[����]", R.drawable.f025);
		mFaceMap.put("[����]", R.drawable.f026);
		mFaceMap.put("[�亹]", R.drawable.f027);
		mFaceMap.put("[����]", R.drawable.f028);
		mFaceMap.put("[ʾ��]", R.drawable.f029);
		mFaceMap.put("[����]", R.drawable.f030);
		mFaceMap.put("[����]", R.drawable.f031);
		mFaceMap.put("[�ѹ�]", R.drawable.f032);
		mFaceMap.put("[����]", R.drawable.f033);
		mFaceMap.put("[����]", R.drawable.f034);
		mFaceMap.put("[˯]", R.drawable.f035);
		mFaceMap.put("[����]", R.drawable.f036);
		mFaceMap.put("[��Ц]", R.drawable.f037);
		mFaceMap.put("[����]", R.drawable.f038);
		mFaceMap.put("[˥]", R.drawable.f039);
		mFaceMap.put("[Ʋ��]", R.drawable.f040);
		mFaceMap.put("[����]", R.drawable.f041);

		mFaceMap.put("[�ܶ�]", R.drawable.f042);
		mFaceMap.put("[����]", R.drawable.f043);
		mFaceMap.put("[�Һߺ�]", R.drawable.f044);
		mFaceMap.put("[ӵ��]", R.drawable.f045);
		mFaceMap.put("[��Ц]", R.drawable.f046);
		mFaceMap.put("[����]", R.drawable.f047);
		mFaceMap.put("[����]", R.drawable.f048);
		mFaceMap.put("[��]", R.drawable.f049);
		mFaceMap.put("[���]", R.drawable.f050);
		mFaceMap.put("[����]", R.drawable.f051);
		mFaceMap.put("[ǿ]", R.drawable.f052);
		mFaceMap.put("[��]", R.drawable.f053);
		mFaceMap.put("[����]", R.drawable.f054);
		mFaceMap.put("[ʤ��]", R.drawable.f055);
		mFaceMap.put("[��ȭ]", R.drawable.f056);
		mFaceMap.put("[��л]", R.drawable.f057);
		mFaceMap.put("[��]", R.drawable.f058);
		mFaceMap.put("[����]", R.drawable.f059);
		mFaceMap.put("[����]", R.drawable.f060);
		mFaceMap.put("[ơ��]", R.drawable.f061);
		mFaceMap.put("[Ʈ��]", R.drawable.f062);

		mFaceMap.put("[����]", R.drawable.f063);
		mFaceMap.put("[OK]", R.drawable.f064);
		mFaceMap.put("[����]", R.drawable.f065);
		mFaceMap.put("[����]", R.drawable.f066);
		mFaceMap.put("[Ǯ]", R.drawable.f067);
		mFaceMap.put("[����]", R.drawable.f068);
		mFaceMap.put("[��Ů]", R.drawable.f069);
		mFaceMap.put("[��]", R.drawable.f070);
		mFaceMap.put("[����]", R.drawable.f071);
		mFaceMap.put("[�]", R.drawable.f072);
		mFaceMap.put("[ȭͷ]", R.drawable.f073);
		mFaceMap.put("[����]", R.drawable.f074);
		mFaceMap.put("[̫��]", R.drawable.f075);
		mFaceMap.put("[����]", R.drawable.f076);
		mFaceMap.put("[����]", R.drawable.f077);
		mFaceMap.put("[����]", R.drawable.f078);
		mFaceMap.put("[����]", R.drawable.f079);
		mFaceMap.put("[����]", R.drawable.f080);
		mFaceMap.put("[����]", R.drawable.f081);
		mFaceMap.put("[��]", R.drawable.f082);
		mFaceMap.put("[����]", R.drawable.f083);

		mFaceMap.put("[��ĥ]", R.drawable.f084);
		mFaceMap.put("[�ٱ�]", R.drawable.f085);
		mFaceMap.put("[����]", R.drawable.f086);
		mFaceMap.put("[�ܴ���]", R.drawable.f087);
		mFaceMap.put("[��ߺ�]", R.drawable.f088);
		mFaceMap.put("[��Ƿ]", R.drawable.f089);
		mFaceMap.put("[�����]", R.drawable.f090);
		mFaceMap.put("[��]", R.drawable.f091);
		mFaceMap.put("[����]", R.drawable.f092);
		mFaceMap.put("[ƹ����]", R.drawable.f093);
		mFaceMap.put("[NO]", R.drawable.f094);
		mFaceMap.put("[����]", R.drawable.f095);
		mFaceMap.put("[���]", R.drawable.f096);
		mFaceMap.put("[תȦ]", R.drawable.f097);
		mFaceMap.put("[��ͷ]", R.drawable.f098);
		mFaceMap.put("[��ͷ]", R.drawable.f099);
		mFaceMap.put("[����]", R.drawable.f100);
		mFaceMap.put("[����]", R.drawable.f101);
		mFaceMap.put("[����]", R.drawable.f102);
		mFaceMap.put("[����]", R.drawable.f103);
		mFaceMap.put("[��̫��]", R.drawable.f104);

		mFaceMap.put("[��̫��]", R.drawable.f105);
		mFaceMap.put("[����]", R.drawable.f106);
	}
}
