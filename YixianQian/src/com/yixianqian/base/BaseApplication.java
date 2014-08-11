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
 * 项目名称：ExamHelper   
 * 类名称：BaseApplication   
 * 类描述：   将取得DaoMaster对象的方法放到Application层这样避免多次创建生成Session对象。
 * 创建人：张帅     
 * 创建时间：2013-12-20 下午9:10:55   
 * 修改人：张帅     
 * 修改时间：2013-12-20 下午9:10:55   
 * 修改备注：   
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
//		//使用百度push接口
		FrontiaApplication.initFrontiaApplication(applicationContext);
		initFaceMap();
		initData();

		int pid = android.os.Process.myPid();
		String processAppName = getAppName(pid);
		//如果使用到百度地图或者类似启动remote service的第三方库，这个if判断不能少
		if (processAppName == null || processAppName.equals("")) {
			// workaround for baidu location sdk 
			// 百度定位sdk，定位服务运行在一个单独的进程，每次定位服务启动的时候，都会调用application::onCreate
			// 创建新的进程。
			// 但环信的sdk只需要在主进程中初始化一次。 这个特殊处理是，如果从pid 找不到对应的processInfo
			// processName，
			// 则此application::onCreate 是被service 调用的，直接返回
			return;
		}

		// 初始化环信SDK,一定要先调用init()
		EMChat.getInstance().init(applicationContext);
		EMChat.getInstance().setDebugMode(true);
		// 获取到EMChatOptions对象
		EMChatOptions options = EMChatManager.getInstance().getChatOptions();
		// 默认添加好友时，是不需要验证的，改成需要验证
		options.setAcceptInvitationAlways(false);
		// 设置收到消息是否有新消息通知，默认为true
		options.setNotificationEnable(true);
		// 设置收到消息是否有声音提示，默认为true
		options.setNoticeBySound(true);
		// 设置收到消息是否震动 默认为true
		options.setNoticedByVibrate(true);
		// 设置语音消息播放是否设置为扬声器播放 默认为true
		options.setUseSpeaker(true);

		//设置notification消息点击时，跳转的intent为自定义的intent
		options.setOnNotificationClickListener(new OnNotificationClickListener() {

			@Override
			public Intent onNotificationClick(EMMessage message) {
				Intent intent = new Intent(applicationContext, ChatActivity2.class);
				ChatType chatType = message.getChatType();
				if (chatType == ChatType.Chat) { //单聊信息
					intent.putExtra("userId", message.getFrom());
					intent.putExtra("chatType", ChatActivity2.CHATTYPE_SINGLE);
				}
				return intent;
			}
		});
		//设置一个connectionlistener监听账户重复登陆
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
	 * 获取当前登陆用户名
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
	 * 获取密码
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
	 * 设置用户名
	 * 
	 * @param user
	 */
	public void setHuanXinUserName(String username) {
		if (username != null) {
			userPreference.setHuanXinUserName(username);
		}
	}

	/**
	 * 设置密码
	 * 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference
	 * 环信sdk 内部的自动登录需要的密码，已经加密存储了
	 * @param pwd
	 */
	public void setHuanXinPassword(String pwd) {
		userPreference.setHuanXinPassword(pwd);
	}

	/**
	 * 退出登录,清空数据
	 */
	public void logout() {
		// 先调用sdk logout，在清理app中自己的数据
		EMChatManager.getInstance().logout();
		userPreference.setHuanXinUserName(null);
		userPreference.setHuanXinPassword(null);
	}

	/**
	 * 返回消息提示声音
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
	 * 取得DaoMaster 
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
	 * 取得DaoSession 
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
	 * 初始化表情
	 */
	private void initFaceMap() {
		// TODO Auto-generated method stub
		mFaceMap.put("[呲牙]", R.drawable.f000);
		mFaceMap.put("[调皮]", R.drawable.f001);
		mFaceMap.put("[流汗]", R.drawable.f002);
		mFaceMap.put("[偷笑]", R.drawable.f003);
		mFaceMap.put("[再见]", R.drawable.f004);
		mFaceMap.put("[敲打]", R.drawable.f005);
		mFaceMap.put("[擦汗]", R.drawable.f006);
		mFaceMap.put("[猪头]", R.drawable.f007);
		mFaceMap.put("[玫瑰]", R.drawable.f008);
		mFaceMap.put("[流泪]", R.drawable.f009);
		mFaceMap.put("[大哭]", R.drawable.f010);
		mFaceMap.put("[嘘]", R.drawable.f011);
		mFaceMap.put("[酷]", R.drawable.f012);
		mFaceMap.put("[抓狂]", R.drawable.f013);
		mFaceMap.put("[委屈]", R.drawable.f014);
		mFaceMap.put("[便便]", R.drawable.f015);
		mFaceMap.put("[炸弹]", R.drawable.f016);
		mFaceMap.put("[菜刀]", R.drawable.f017);
		mFaceMap.put("[可爱]", R.drawable.f018);
		mFaceMap.put("[色]", R.drawable.f019);
		mFaceMap.put("[害羞]", R.drawable.f020);

		mFaceMap.put("[得意]", R.drawable.f021);
		mFaceMap.put("[吐]", R.drawable.f022);
		mFaceMap.put("[微笑]", R.drawable.f023);
		mFaceMap.put("[发怒]", R.drawable.f024);
		mFaceMap.put("[尴尬]", R.drawable.f025);
		mFaceMap.put("[惊恐]", R.drawable.f026);
		mFaceMap.put("[冷汗]", R.drawable.f027);
		mFaceMap.put("[爱心]", R.drawable.f028);
		mFaceMap.put("[示爱]", R.drawable.f029);
		mFaceMap.put("[白眼]", R.drawable.f030);
		mFaceMap.put("[傲慢]", R.drawable.f031);
		mFaceMap.put("[难过]", R.drawable.f032);
		mFaceMap.put("[惊讶]", R.drawable.f033);
		mFaceMap.put("[疑问]", R.drawable.f034);
		mFaceMap.put("[睡]", R.drawable.f035);
		mFaceMap.put("[亲亲]", R.drawable.f036);
		mFaceMap.put("[憨笑]", R.drawable.f037);
		mFaceMap.put("[爱情]", R.drawable.f038);
		mFaceMap.put("[衰]", R.drawable.f039);
		mFaceMap.put("[撇嘴]", R.drawable.f040);
		mFaceMap.put("[阴险]", R.drawable.f041);

		mFaceMap.put("[奋斗]", R.drawable.f042);
		mFaceMap.put("[发呆]", R.drawable.f043);
		mFaceMap.put("[右哼哼]", R.drawable.f044);
		mFaceMap.put("[拥抱]", R.drawable.f045);
		mFaceMap.put("[坏笑]", R.drawable.f046);
		mFaceMap.put("[飞吻]", R.drawable.f047);
		mFaceMap.put("[鄙视]", R.drawable.f048);
		mFaceMap.put("[晕]", R.drawable.f049);
		mFaceMap.put("[大兵]", R.drawable.f050);
		mFaceMap.put("[可怜]", R.drawable.f051);
		mFaceMap.put("[强]", R.drawable.f052);
		mFaceMap.put("[弱]", R.drawable.f053);
		mFaceMap.put("[握手]", R.drawable.f054);
		mFaceMap.put("[胜利]", R.drawable.f055);
		mFaceMap.put("[抱拳]", R.drawable.f056);
		mFaceMap.put("[凋谢]", R.drawable.f057);
		mFaceMap.put("[饭]", R.drawable.f058);
		mFaceMap.put("[蛋糕]", R.drawable.f059);
		mFaceMap.put("[西瓜]", R.drawable.f060);
		mFaceMap.put("[啤酒]", R.drawable.f061);
		mFaceMap.put("[飘虫]", R.drawable.f062);

		mFaceMap.put("[勾引]", R.drawable.f063);
		mFaceMap.put("[OK]", R.drawable.f064);
		mFaceMap.put("[爱你]", R.drawable.f065);
		mFaceMap.put("[咖啡]", R.drawable.f066);
		mFaceMap.put("[钱]", R.drawable.f067);
		mFaceMap.put("[月亮]", R.drawable.f068);
		mFaceMap.put("[美女]", R.drawable.f069);
		mFaceMap.put("[刀]", R.drawable.f070);
		mFaceMap.put("[发抖]", R.drawable.f071);
		mFaceMap.put("[差劲]", R.drawable.f072);
		mFaceMap.put("[拳头]", R.drawable.f073);
		mFaceMap.put("[心碎]", R.drawable.f074);
		mFaceMap.put("[太阳]", R.drawable.f075);
		mFaceMap.put("[礼物]", R.drawable.f076);
		mFaceMap.put("[足球]", R.drawable.f077);
		mFaceMap.put("[骷髅]", R.drawable.f078);
		mFaceMap.put("[挥手]", R.drawable.f079);
		mFaceMap.put("[闪电]", R.drawable.f080);
		mFaceMap.put("[饥饿]", R.drawable.f081);
		mFaceMap.put("[困]", R.drawable.f082);
		mFaceMap.put("[咒骂]", R.drawable.f083);

		mFaceMap.put("[折磨]", R.drawable.f084);
		mFaceMap.put("[抠鼻]", R.drawable.f085);
		mFaceMap.put("[鼓掌]", R.drawable.f086);
		mFaceMap.put("[糗大了]", R.drawable.f087);
		mFaceMap.put("[左哼哼]", R.drawable.f088);
		mFaceMap.put("[哈欠]", R.drawable.f089);
		mFaceMap.put("[快哭了]", R.drawable.f090);
		mFaceMap.put("[吓]", R.drawable.f091);
		mFaceMap.put("[篮球]", R.drawable.f092);
		mFaceMap.put("[乒乓球]", R.drawable.f093);
		mFaceMap.put("[NO]", R.drawable.f094);
		mFaceMap.put("[跳跳]", R.drawable.f095);
		mFaceMap.put("[怄火]", R.drawable.f096);
		mFaceMap.put("[转圈]", R.drawable.f097);
		mFaceMap.put("[磕头]", R.drawable.f098);
		mFaceMap.put("[回头]", R.drawable.f099);
		mFaceMap.put("[跳绳]", R.drawable.f100);
		mFaceMap.put("[激动]", R.drawable.f101);
		mFaceMap.put("[街舞]", R.drawable.f102);
		mFaceMap.put("[献吻]", R.drawable.f103);
		mFaceMap.put("[左太极]", R.drawable.f104);

		mFaceMap.put("[右太极]", R.drawable.f105);
		mFaceMap.put("[闭嘴]", R.drawable.f106);
	}
}
