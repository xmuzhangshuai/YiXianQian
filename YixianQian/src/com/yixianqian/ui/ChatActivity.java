package com.yixianqian.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.util.PathUtil;
import com.easemob.util.VoiceRecorder;
import com.yixianqian.R;
import com.yixianqian.Listener.VoicePlayClickListener;
import com.yixianqian.adapter.FaceAdapter;
import com.yixianqian.adapter.FacePageAdeapter;
import com.yixianqian.adapter.MessageAdapter;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.base.BaseFragmentActivity;
import com.yixianqian.config.DefaultSetting;
import com.yixianqian.customewidget.CirclePageIndicator;
import com.yixianqian.customewidget.JazzyViewPager;
import com.yixianqian.customewidget.JazzyViewPager.TransitionEffect;
import com.yixianqian.utils.CommonTools;
import com.yixianqian.utils.DensityUtil;
import com.yixianqian.utils.FriendPreference;
import com.yixianqian.utils.LogTool;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;
import com.yixianqian.xlistview.MsgListView;
import com.yixianqian.xlistview.MsgListView.IXListViewListener;

public class ChatActivity extends BaseFragmentActivity implements OnTouchListener, IXListViewListener, OnClickListener {
	/*************VIEWS*****************/
	private static final int REQUEST_CODE_EMPTY_HISTORY = 2;
	public static final int REQUEST_CODE_CONTEXT_MENU = 3;
	private static final int REQUEST_CODE_MAP = 4;
	public static final int REQUEST_CODE_TEXT = 5;
	public static final int REQUEST_CODE_VOICE = 6;
	public static final int REQUEST_CODE_PICTURE = 7;
	public static final int REQUEST_CODE_LOCATION = 8;
	public static final int REQUEST_CODE_NET_DISK = 9;
	public static final int REQUEST_CODE_COPY_AND_PASTE = 11;
	public static final int REQUEST_CODE_DOWNLOAD_VOICE = 15;
	public static final int REQUEST_CODE_SELECT_USER_CARD = 16;
	public static final int REQUEST_CODE_SEND_USER_CARD = 17;
	public static final int REQUEST_CODE_CAMERA = 18;
	public static final int REQUEST_CODE_LOCAL = 19;
	public static final int REQUEST_CODE_CLICK_DESTORY_IMG = 20;
	public static final int REQUEST_CODE_ADD_TO_BLACKLIST = 25;
	public static final int RESULT_CODE_COPY = 1;
	public static final int RESULT_CODE_DELETE = 2;
	public static final int RESULT_CODE_OPEN = 4;
	public static final int RESULT_CODE_DWONLOAD = 5;
	public static final int RESULT_CODE_TO_CLOUD = 6;
	public static final String COPY_IMAGE = "EASEMOBIMG";
	public static final int CHATTYPE_SINGLE = 1;

	private MsgListView mMsgListView;
	private View topNavLeftBtn;//导航条左边按钮
	private TextView topNavText;//导航条文字
	private Button sendBtn;//发送按钮
	private ImageView faceBtn;//表情
	private ImageView moreBtn;//添加图片
	private boolean isFaceShow = false;//是否显示表情
	private boolean isMoreShow = false;//是否显示更多
	private JazzyViewPager faceViewPager;//表情翻页
	private EditText msgEt;//输入框
	private LinearLayout faceLinearLayout;//表情区域
	private FrameLayout morePanel;//更多区域
	private GridView moreGridView;
	private WindowManager.LayoutParams params;
	private InputMethodManager inputMethodManager;
	private int currentPage = 0;//当前表情页
	private List<String> keys;
	private BaseApplication baseApplication;
	private FriendPreference friendPreference;
	private UserPreference userPreference;
	private Drawable[] micImages;//录音动画
	private String toChatUsername;// 给谁发送消息
	private VoiceRecorder voiceRecorder;//录音
	private ImageView micImage;//录音图片
	private View buttonPressToSpeak;//按住说话
	private View recordingContainer;
	private TextView recordingHint;//录音提示
	private PowerManager.WakeLock wakeLock;//保持屏幕常亮
	private EMConversation emConversation;//聊天会话
	private MessageAdapter adapter;//消息适配器
	public static int resendPos;//重新发送消息位置
	private View buttonSetModeKeyboard;//键盘键
	private ImageView voiceBtn;//发送声音按钮
	private NewMessageBroadcastReceiver receiver;//新消息广播
	private ClipboardManager clipboard;//复制消息粘贴板
	private File cameraFile;//相机文件
	public static ChatActivity activityInstance = null;

	private Handler micImageHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			// 切换msg切换图片
			micImage.setImageDrawable(micImages[msg.what]);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.chat_main);

		findViewById();
		initData();
		initView();
		initFacePage();
		initMorePage();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		adapter.refresh();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		faceLinearLayout.setVisibility(View.GONE);
		morePanel.setVisibility(View.GONE);
		super.onPause();
		if (wakeLock.isHeld())
			wakeLock.release();
		if (VoicePlayClickListener.isPlaying && VoicePlayClickListener.currentPlayListener != null) {
			// 停止语音播放
			VoicePlayClickListener.currentPlayListener.stopPlayVoice();
		}
		// 停止录音
		if (voiceRecorder.isRecording()) {
			voiceRecorder.discardRecording();
			recordingContainer.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// 点击notification bar进入聊天页面，保证只有一个聊天页面
		String username = intent.getStringExtra("userId");
		if (toChatUsername.equals(username))
			super.onNewIntent(intent);
		else {
			finish();
			startActivity(intent);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		activityInstance = null;
		// 注销广播
		try {
			unregisterReceiver(receiver);
			receiver = null;
		} catch (Exception e) {
		}
		try {
			unregisterReceiver(ackMessageReceiver);
			ackMessageReceiver = null;
		} catch (Exception e) {
		}
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		mMsgListView = (MsgListView) findViewById(R.id.msg_listView);
		topNavLeftBtn = findViewById(R.id.left_btn_bg);
		topNavText = (TextView) findViewById(R.id.nav_text);
		sendBtn = (Button) findViewById(R.id.send_btn);
		faceBtn = (ImageView) findViewById(R.id.face_btn);
		msgEt = (EditText) findViewById(R.id.msg_et);
		faceLinearLayout = (LinearLayout) findViewById(R.id.face_ll);
		faceViewPager = (JazzyViewPager) findViewById(R.id.face_pager);
		moreBtn = (ImageView) findViewById(R.id.more_btn);
		morePanel = (FrameLayout) findViewById(R.id.panelLayout);
		moreGridView = (GridView) findViewById(R.id.panel);
		micImage = (ImageView) findViewById(R.id.mic_image);
		buttonPressToSpeak = findViewById(R.id.btn_press_to_speak);
		recordingContainer = findViewById(R.id.recording_container);
		recordingHint = (TextView) findViewById(R.id.recording_hint);
		buttonSetModeKeyboard = findViewById(R.id.btn_set_mode_keyboard);
		voiceBtn = (ImageView) findViewById(R.id.voice_btn);
	}

	private void initData() {
		baseApplication = BaseApplication.getInstance();
		Set<String> keySet = baseApplication.getFaceMap().keySet();
		keys = new ArrayList<String>();
		keys.addAll(keySet);
		friendPreference = BaseApplication.getInstance().getFriendPreference();
		userPreference = baseApplication.getInstance().getUserPreference();
		// 动画资源文件,用于录制语音时
		micImages = new Drawable[] { getResources().getDrawable(R.drawable.record_animate_01),
				getResources().getDrawable(R.drawable.record_animate_02),
				getResources().getDrawable(R.drawable.record_animate_03),
				getResources().getDrawable(R.drawable.record_animate_04),
				getResources().getDrawable(R.drawable.record_animate_05),
				getResources().getDrawable(R.drawable.record_animate_06),
				getResources().getDrawable(R.drawable.record_animate_07),
				getResources().getDrawable(R.drawable.record_animate_08),
				getResources().getDrawable(R.drawable.record_animate_09),
				getResources().getDrawable(R.drawable.record_animate_10),
				getResources().getDrawable(R.drawable.record_animate_11),
				getResources().getDrawable(R.drawable.record_animate_12),
				getResources().getDrawable(R.drawable.record_animate_13),
				getResources().getDrawable(R.drawable.record_animate_14), };

		toChatUsername = getIntent().getStringExtra("userId");
		emConversation = EMChatManager.getInstance().getConversation(toChatUsername);
		// 把此会话的未读数置为0
		emConversation.resetUnsetMsgCount();

		adapter = new MessageAdapter(this, toChatUsername);

		wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(
				PowerManager.SCREEN_DIM_WAKE_LOCK, "yixianqian");
		voiceRecorder = new VoiceRecorder(micImageHandler);
		clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		activityInstance = this;
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavText.setText(friendPreference.getName());
		inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		params = getWindow().getAttributes();

		msgEt.setOnTouchListener(this);
		// 触摸ListView隐藏表情和输入法
		mMsgListView.setOnTouchListener(this);
		mMsgListView.setPullLoadEnable(false);
		mMsgListView.setXListViewListener(this);
		moreBtn.setOnClickListener(this);
		voiceBtn.setOnClickListener(this);
		buttonSetModeKeyboard.setOnClickListener(this);
		mMsgListView.setAdapter(adapter);
		mMsgListView.setSelection(adapter.getCount() - 1);

		topNavLeftBtn.setOnClickListener(this);

		buttonPressToSpeak.setOnTouchListener(new PressToSpeakListen());

		msgEt.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (params.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE || isFaceShow
							|| isMoreShow) {
						faceLinearLayout.setVisibility(View.GONE);
						morePanel.setVisibility(View.GONE);
						isFaceShow = false;
						isMoreShow = false;
						return true;
					}
				}
				return false;
			}
		});
		msgEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if (!TextUtils.isEmpty(s)) {
					sendBtn.setVisibility(View.VISIBLE);
					moreBtn.setVisibility(View.GONE);
				} else {
					if (buttonSetModeKeyboard.getVisibility() != View.VISIBLE) {
						sendBtn.setVisibility(View.GONE);
						moreBtn.setVisibility(View.VISIBLE);
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (s.length() > 0) {
					sendBtn.setEnabled(true);
				} else {
					sendBtn.setEnabled(false);
				}
			}
		});
		faceBtn.setOnClickListener(this);
		sendBtn.setOnClickListener(this);

		// 注册接收消息广播
		receiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
		// 设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
		intentFilter.setPriority(5);
		registerReceiver(receiver, intentFilter);

		// 注册一个ack回执消息的BroadcastReceiver
		IntentFilter ackMessageIntentFilter = new IntentFilter(EMChatManager.getInstance()
				.getAckMessageBroadcastAction());
		ackMessageIntentFilter.setPriority(5);
		registerReceiver(ackMessageReceiver, ackMessageIntentFilter);
	}

	/**
	 * onActivityResult
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
			switch (resultCode) {
			case RESULT_CODE_COPY: // 复制消息
				EMMessage copyMsg = ((EMMessage) adapter.getItem(data.getIntExtra("position", -1)));
				if (copyMsg.getType() == EMMessage.Type.IMAGE) {
					ImageMessageBody imageBody = (ImageMessageBody) copyMsg.getBody();
					// 加上一个特定前缀，粘贴时知道这是要粘贴一个图片
					clipboard.setText(COPY_IMAGE + imageBody.getLocalUrl());
				} else {
					// clipboard.setText(SmileUtils.getSmiledText(ChatActivity.this,
					// ((TextMessageBody) copyMsg.getBody()).getMessage()));
					clipboard.setText(((TextMessageBody) copyMsg.getBody()).getMessage());
				}
				break;
			case RESULT_CODE_DELETE: // 删除消息
				EMMessage deleteMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", -1));
				emConversation.removeMessage(deleteMsg.getMsgId());
				adapter.refresh();
				mMsgListView.setSelection(data.getIntExtra("position", adapter.getCount()) - 1);
				break;

			default:
				break;
			}
		}
		if (resultCode == RESULT_OK) { // 清空消息
			if (requestCode == REQUEST_CODE_EMPTY_HISTORY) {
				// 清空会话
				EMChatManager.getInstance().clearConversation(toChatUsername);
				adapter.refresh();
			} else if (requestCode == REQUEST_CODE_CAMERA) { // 发送照片
				if (cameraFile != null && cameraFile.exists())
					sendPicture(cameraFile.getAbsolutePath());
			} else if (requestCode == REQUEST_CODE_LOCAL) { // 发送本地图片
				if (data != null) {
					Uri selectedImage = data.getData();
					if (selectedImage != null) {
						sendPicByUri(selectedImage);
					}
				}
			}
			//			else if (requestCode == REQUEST_CODE_MAP) { // 地图
			//				double latitude = data.getDoubleExtra("latitude", 0);
			//				double longitude = data.getDoubleExtra("longitude", 0);
			//				String locationAddress = data.getStringExtra("address");
			//				if (locationAddress != null && !locationAddress.equals("")) {
			//					more(more);
			//					sendLocationMsg(latitude, longitude, "", locationAddress);
			//				} else {
			//					Toast.makeText(this, "无法获取到您的位置信息！", 0).show();
			//				}
			//				// 重发消息
			//			} 

			else if (requestCode == REQUEST_CODE_TEXT) {
				resendMessage();
			} else if (requestCode == REQUEST_CODE_VOICE) {
				resendMessage();
			} else if (requestCode == REQUEST_CODE_PICTURE) {
				resendMessage();
			} else if (requestCode == REQUEST_CODE_LOCATION) {
				resendMessage();
			} else if (requestCode == REQUEST_CODE_COPY_AND_PASTE) {
				// 粘贴
				if (!TextUtils.isEmpty(clipboard.getText())) {
					String pasteText = clipboard.getText().toString();
					if (pasteText.startsWith(COPY_IMAGE)) {
						// 把图片前缀去掉，还原成正常的path
						sendPicture(pasteText.replace(COPY_IMAGE, ""));
					}
				}
			} else if (emConversation.getMsgCount() > 0) {
				adapter.refresh();
				setResult(RESULT_OK);
			}
		}
	}

	/**
	 * 照相获取图片
	 */
	public void selectPicFromCamera() {
		if (!CommonTools.isExitsSdcard()) {
			ToastTool.showShort(getApplicationContext(), "SD卡不存在，不能拍照");
			return;
		}

		//		System.out.println(PathUtil.getInstance().getImagePath() + "   " + userPreference.getU_id()
		//				+ System.currentTimeMillis() + ".jpg");
		cameraFile = new File(PathUtil.getInstance().getImagePath(), "" + userPreference.getU_id()
				+ System.currentTimeMillis() + ".jpg");
		cameraFile.getParentFile().mkdirs();
		startActivityForResult(
				new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
				REQUEST_CODE_CAMERA);
	}

	/**
	 * 从图库获取图片
	 */
	public void selectPicFromLocal() {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");

		} else {
			intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, REQUEST_CODE_LOCAL);
	}

	/**
	 * 隐藏软键盘
	 */
	private void hideKeyboard() {
		inputMethodManager.hideSoftInputFromWindow(msgEt.getWindowToken(), 0);
	}

	/**
	 * 初始化发送图片等窗口
	 */
	private void initMorePage() {
		int[] imageIds = new int[] { R.drawable.sel_chat_take_photo, R.drawable.sel_chat_choose_photo };
		String[] names = new String[] { "拍照", "相册" };
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < imageIds.length; i++) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			listItem.put("image", imageIds[i]);
			listItem.put("name", names[i]);
			listItems.add(listItem);
		}
		SimpleAdapter adapter = new SimpleAdapter(ChatActivity.this, listItems, R.layout.more_gridview_cell,
				new String[] { "image", "name" }, new int[] { R.id.cellimage, R.id.celltext });
		moreGridView.setAdapter(adapter);
		moreGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					selectPicFromCamera();// 点击照相图标
					break;
				case 1:
					selectPicFromLocal(); // 点击图片图标
					break;
				default:
					break;
				}
			}
		});
	}

	/**
	 * 初始化表情窗口
	 */
	private void initFacePage() {
		// TODO Auto-generated method stub
		List<View> lv = new ArrayList<View>();
		for (int i = 0; i < DefaultSetting.NUM_PAGE; ++i)
			lv.add(getGridView(i));
		FacePageAdeapter adapter = new FacePageAdeapter(lv, faceViewPager);
		faceViewPager.setAdapter(adapter);
		faceViewPager.setCurrentItem(currentPage);
		//设置翻页效果
		faceViewPager.setTransitionEffect(TransitionEffect.Standard);
		CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(faceViewPager);
		adapter.notifyDataSetChanged();
		faceLinearLayout.setVisibility(View.GONE);
		indicator.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				currentPage = arg0;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// do nothing
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// do nothing
			}
		});

	}

	/**
	 * 返回表情视图
	 * @param i
	 * @return
	 */
	private GridView getGridView(int i) {
		// TODO Auto-generated method stub
		GridView gv = new GridView(this);
		gv.setNumColumns(7);
		gv.setSelector(new ColorDrawable(Color.TRANSPARENT));// 屏蔽GridView默认点击效果
		gv.setBackgroundColor(Color.TRANSPARENT);
		gv.setCacheColorHint(Color.TRANSPARENT);
		gv.setHorizontalSpacing(1);
		gv.setVerticalSpacing(DensityUtil.dip2px(ChatActivity.this, 25));
		gv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		gv.setGravity(Gravity.CENTER);
		gv.setAdapter(new FaceAdapter(this, i));
		//		gv.setOnTouchListener(forbidenScroll());
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				if (arg2 == DefaultSetting.NUM) {// 删除键的位置
					int selection = msgEt.getSelectionStart();
					String text = msgEt.getText().toString();
					if (selection > 0) {
						String text2 = text.substring(selection - 1);
						if ("]".equals(text2)) {
							int start = text.lastIndexOf("[");
							int end = selection;
							msgEt.getText().delete(start, end);
							return;
						}
						msgEt.getText().delete(selection - 1, selection);
					}
				} else {
					int count = currentPage * DefaultSetting.NUM + arg2;
					// 注释的部分，在EditText中显示字符串
					// String ori = msgEt.getText().toString();
					// int index = msgEt.getSelectionStart();
					// StringBuilder stringBuilder = new StringBuilder(ori);
					// stringBuilder.insert(index, keys.get(count));
					// msgEt.setText(stringBuilder.toString());
					// msgEt.setSelection(index + keys.get(count).length());

					// 下面这部分，在EditText中显示表情
					Bitmap bitmap = BitmapFactory.decodeResource(getResources(), (Integer) BaseApplication
							.getInstance().getFaceMap().values().toArray()[count]);
					if (bitmap != null) {
						int rawHeigh = bitmap.getHeight();
						int rawWidth = bitmap.getHeight();
						int newHeight = 60;
						int newWidth = 60;
						// 计算缩放因子
						float heightScale = ((float) newHeight) / rawHeigh;
						float widthScale = ((float) newWidth) / rawWidth;
						// 新建立矩阵
						Matrix matrix = new Matrix();
						matrix.postScale(heightScale, widthScale);
						// 设置图片的旋转角度
						// matrix.postRotate(-30);
						// 设置图片的倾斜
						// matrix.postSkew(0.1f, 0.1f);
						// 将图片大小压缩
						// 压缩后图片的宽和高以及kB大小均会变化
						Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, rawWidth, rawHeigh, matrix, true);
						ImageSpan imageSpan = new ImageSpan(ChatActivity.this, newBitmap);
						String emojiStr = keys.get(count);
						SpannableString spannableString = new SpannableString(emojiStr);
						spannableString.setSpan(imageSpan, emojiStr.indexOf('['), emojiStr.indexOf(']') + 1,
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						msgEt.append(spannableString);
					} else {
						String ori = msgEt.getText().toString();
						int index = msgEt.getSelectionStart();
						StringBuilder stringBuilder = new StringBuilder(ori);
						stringBuilder.insert(index, keys.get(count));
						msgEt.setText(stringBuilder.toString());
						msgEt.setSelection(index + keys.get(count).length());
					}
				}
			}
		});
		return gv;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.msg_listView:
			inputMethodManager.hideSoftInputFromWindow(msgEt.getWindowToken(), 0);
			faceLinearLayout.setVisibility(View.GONE);
			morePanel.setVisibility(View.GONE);
			isFaceShow = false;
			isMoreShow = false;
			break;
		case R.id.msg_et:
			inputMethodManager.showSoftInput(msgEt, 0);
			faceLinearLayout.setVisibility(View.GONE);
			morePanel.setVisibility(View.GONE);
			isFaceShow = false;
			isMoreShow = false;
			break;

		default:
			break;
		}
		return false;
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		List<EMMessage> messages;
		int position = adapter.getCount();
		if (position > 0) {
			messages = emConversation.loadMoreMsgFromDB(adapter.getItem(0).getMsgId(), DefaultSetting.PAGE_NUM);
			adapter.notifyDataSetChanged();
			mMsgListView.setSelection(adapter.getCount() - position - 1);
		}
		mMsgListView.stopRefresh();
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//		super.onBackPressed();
		startActivity(new Intent(ChatActivity.this, MainActivity.class));
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		ChatActivity.this.finish();
	}

	/**
	 * 重发消息
	 */
	private void resendMessage() {
		EMMessage msg = null;
		msg = emConversation.getMessage(resendPos);
		// msg.setBackSend(true);
		msg.status = EMMessage.Status.CREATE;
		adapter.refresh();
		mMsgListView.setSelection(resendPos);
	}

	/**
	 * 发送文本消息
	 * 
	 * @param content
	 *            message content
	 * @param isResend
	 *            boolean resend
	 */
	private void sendText(final String content) {
		if (content.length() > 0) {
			new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... params) {
					EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
					TextMessageBody txtBody = new TextMessageBody(content);
					// 设置消息body
					message.addBody(txtBody);
					// 设置要发给谁,用户username
					message.setReceipt(toChatUsername);
					// 把messgage加到conversation中
					emConversation.addMessage(message);
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					// 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
					adapter.refresh();
					mMsgListView.setSelection(mMsgListView.getCount() - 1);
					msgEt.setText("");
					setResult(RESULT_OK);
				}
			}.execute();
		}
	}

	/**
	 * 发送语音
	 * 
	 * @param filePath
	 * @param fileName
	 * @param length
	 * @param isResend
	 */
	private void sendVoice(String filePath, String fileName, String length, boolean isResend) {
		if (!(new File(filePath).exists())) {
			return;
		}
		try {
			final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VOICE);
			String to = toChatUsername;
			message.setReceipt(to);
			int len = Integer.parseInt(length);
			VoiceMessageBody body = new VoiceMessageBody(new File(filePath), len);
			message.addBody(body);

			emConversation.addMessage(message);
			adapter.refresh();
			mMsgListView.setSelection(adapter.getCount() - 1);
			setResult(RESULT_OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送图片
	 * 
	 * @param filePath
	 */
	private void sendPicture(final String filePath) {
		String to = toChatUsername;
		// create and add image message in view
		final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
		message.setReceipt(to);
		ImageMessageBody body = new ImageMessageBody(new File(filePath));
		// 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
		// body.setSendOriginalImage(true);
		message.addBody(body);
		emConversation.addMessage(message);

		mMsgListView.setAdapter(adapter);
		adapter.refresh();
		mMsgListView.setSelection(mMsgListView.getCount() - 1);
		setResult(RESULT_OK);
		// more(more);
	}

	/**
	 * 根据图库图片uri发送图片
	 * 
	 * @param selectedImage
	 */
	private void sendPicByUri(Uri selectedImage) {
		// String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex("_data");
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			cursor = null;

			if (picturePath == null || picturePath.equals("null")) {
				Toast toast = Toast.makeText(this, "找不到图片", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
			sendPicture(picturePath);
		} else {
			File file = new File(selectedImage.getPath());
			if (!file.exists()) {
				Toast toast = Toast.makeText(this, "找不到图片", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;

			}
			sendPicture(file.getAbsolutePath());
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.face_btn:
			showFace();
			break;
		case R.id.send_btn:// 发送消息
			String msg = msgEt.getText().toString();
			sendText(msg);
			msgEt.setText("");
			break;
		case R.id.left_btn_bg:
			startActivity(new Intent(ChatActivity.this, MainActivity.class));
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			finish();
			break;
		case R.id.more_btn:
			showMore();
			break;
		case R.id.voice_btn:
			setModeVoice();
			break;
		case R.id.btn_set_mode_keyboard:
			setModeKeyboard();
			break;
		default:
			break;
		}
	}

	/**
	 * 显示表情
	 */
	public void showFace() {
		if (!isFaceShow) {
			inputMethodManager.hideSoftInputFromWindow(msgEt.getWindowToken(), 0);
			try {
				Thread.sleep(80);// 解决此时会黑一下屏幕的问题
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			faceLinearLayout.setVisibility(View.VISIBLE);
			isFaceShow = true;
			morePanel.setVisibility(View.GONE);
			isMoreShow = false;
		} else {
			faceLinearLayout.setVisibility(View.GONE);
			isFaceShow = false;
		}
	}

	/**
	 * 显示更多
	 */
	public void showMore() {
		if (!isMoreShow) {
			inputMethodManager.hideSoftInputFromWindow(msgEt.getWindowToken(), 0);
			try {
				Thread.sleep(80);// 解决此时会黑一下屏幕的问题
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			morePanel.setVisibility(View.VISIBLE);
			isMoreShow = true;
			faceLinearLayout.setVisibility(View.GONE);
			isFaceShow = false;
		} else {
			morePanel.setVisibility(View.GONE);
			isMoreShow = false;
		}
		voiceBtn.setVisibility(View.VISIBLE);
		buttonPressToSpeak.setVisibility(View.GONE);
		buttonSetModeKeyboard.setVisibility(View.GONE);
		faceBtn.setVisibility(View.VISIBLE);
		msgEt.setVisibility(View.VISIBLE);
		msgEt.requestFocus();
	}

	/**
	 * 显示语音图标按钮
	 * 
	 * @param view
	 */
	public void setModeVoice() {
		msgEt.setVisibility(View.GONE);
		hideKeyboard();
		morePanel.setVisibility(View.GONE);
		isMoreShow = false;
		buttonSetModeKeyboard.setVisibility(View.VISIBLE);
		buttonPressToSpeak.setVisibility(View.VISIBLE);
		faceBtn.setVisibility(View.GONE);
		faceLinearLayout.setVisibility(View.GONE);
		isFaceShow = false;
		voiceBtn.setVisibility(View.GONE);
	}

	/**
	 * 显示键盘图标
	 * 
	 * @param view
	 */
	public void setModeKeyboard() {
		msgEt.setVisibility(View.VISIBLE);
		morePanel.setVisibility(View.GONE);
		isMoreShow = false;
		voiceBtn.setVisibility(View.VISIBLE);
		// mEditTextContent.setVisibility(View.VISIBLE);
		msgEt.requestFocus();
		// buttonSend.setVisibility(View.VISIBLE);
		buttonPressToSpeak.setVisibility(View.GONE);
		buttonSetModeKeyboard.setVisibility(View.GONE);
		faceBtn.setVisibility(View.VISIBLE);
	}

	public String getToChatUsername() {
		return toChatUsername;
	}

	/**
	 * 消息广播接收者
	 * 
	 */
	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String username = intent.getStringExtra("from");
			//			String msgid = intent.getStringExtra("msgid");
			// 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
			//			EMMessage message = EMChatManager.getInstance().getMessage(msgid);

			if (!username.equals(toChatUsername)) {
				// 消息不是发给当前会话，return
				return;
			}

			// conversation =
			// EMChatManager.getInstance().getConversation(toChatUsername);
			// 通知adapter有新消息，更新ui
			adapter.refresh();
			mMsgListView.setSelection(mMsgListView.getCount() - 1);
			// 记得把广播给终结掉
			abortBroadcast();
		}
	}

	/**
	 * 消息回执BroadcastReceiver
	 */
	private BroadcastReceiver ackMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String msgid = intent.getStringExtra("msgid");
			String from = intent.getStringExtra("from");
			EMConversation conversation = EMChatManager.getInstance().getConversation(from);
			if (conversation != null) {
				// 把message设为已读
				EMMessage msg = conversation.getMessage(msgid);
				if (msg != null) {
					msg.isAcked = true;
				}
			}
			abortBroadcast();
			adapter.notifyDataSetChanged();
		}
	};

	/**
	 * 按住说话listener
	 * 
	 */
	class PressToSpeakListen implements View.OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!CommonTools.isExitsSdcard()) {
					ToastTool.showShort(ChatActivity.this, "发送语音需要sdcard支持！");
					return false;
				}
				try {
					v.setPressed(true);
					wakeLock.acquire();
					if (VoicePlayClickListener.isPlaying)
						VoicePlayClickListener.currentPlayListener.stopPlayVoice();
					recordingContainer.setVisibility(View.VISIBLE);
					recordingHint.setText(getString(R.string.move_up_to_cancel));
					recordingHint.setBackgroundColor(Color.TRANSPARENT);
					voiceRecorder.startRecording(null, toChatUsername, getApplicationContext());
				} catch (Exception e) {
					e.printStackTrace();
					v.setPressed(false);
					if (wakeLock.isHeld())
						wakeLock.release();
					recordingContainer.setVisibility(View.INVISIBLE);
					ToastTool.showShort(ChatActivity.this, R.string.recoding_fail);
					return false;
				}

				return true;
			case MotionEvent.ACTION_MOVE: {
				if (event.getY() < 0) {
					recordingHint.setText(getString(R.string.release_to_cancel));
					recordingHint.setBackgroundResource(R.drawable.recording_text_hint_bg);
				} else {
					recordingHint.setText(getString(R.string.move_up_to_cancel));
					recordingHint.setBackgroundColor(Color.TRANSPARENT);
				}
				return true;
			}
			case MotionEvent.ACTION_UP:
				v.setPressed(false);
				recordingContainer.setVisibility(View.INVISIBLE);
				if (wakeLock.isHeld())
					wakeLock.release();
				if (event.getY() < 0) {
					// discard the recorded audio.
					voiceRecorder.discardRecording();

				} else {
					// stop recording and send voice file
					try {
						int length = voiceRecorder.stopRecoding();
						if (length > 0) {
							sendVoice(voiceRecorder.getVoiceFilePath(), voiceRecorder.getVoiceFileName(toChatUsername),
									Integer.toString(length), false);
						} else {
							ToastTool.showShort(getApplicationContext(), "录音时间太短");
						}
					} catch (Exception e) {
						e.printStackTrace();
						ToastTool.showShort(ChatActivity.this, "发送失败，请检测服务器是否连接");
					}
				}
				return true;
			default:
				return false;
			}
		}
	}
}