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
	private View topNavLeftBtn;//��������߰�ť
	private TextView topNavText;//����������
	private Button sendBtn;//���Ͱ�ť
	private ImageView faceBtn;//����
	private ImageView moreBtn;//���ͼƬ
	private boolean isFaceShow = false;//�Ƿ���ʾ����
	private boolean isMoreShow = false;//�Ƿ���ʾ����
	private JazzyViewPager faceViewPager;//���鷭ҳ
	private EditText msgEt;//�����
	private LinearLayout faceLinearLayout;//��������
	private FrameLayout morePanel;//��������
	private GridView moreGridView;
	private WindowManager.LayoutParams params;
	private InputMethodManager inputMethodManager;
	private int currentPage = 0;//��ǰ����ҳ
	private List<String> keys;
	private BaseApplication baseApplication;
	private FriendPreference friendPreference;
	private UserPreference userPreference;
	private Drawable[] micImages;//¼������
	private String toChatUsername;// ��˭������Ϣ
	private VoiceRecorder voiceRecorder;//¼��
	private ImageView micImage;//¼��ͼƬ
	private View buttonPressToSpeak;//��ס˵��
	private View recordingContainer;
	private TextView recordingHint;//¼����ʾ
	private PowerManager.WakeLock wakeLock;//������Ļ����
	private EMConversation emConversation;//����Ự
	private MessageAdapter adapter;//��Ϣ������
	public static int resendPos;//���·�����Ϣλ��
	private View buttonSetModeKeyboard;//���̼�
	private ImageView voiceBtn;//����������ť
	private NewMessageBroadcastReceiver receiver;//����Ϣ�㲥
	private ClipboardManager clipboard;//������Ϣճ����
	private File cameraFile;//����ļ�
	public static ChatActivity activityInstance = null;

	private Handler micImageHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			// �л�msg�л�ͼƬ
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
			// ֹͣ��������
			VoicePlayClickListener.currentPlayListener.stopPlayVoice();
		}
		// ֹͣ¼��
		if (voiceRecorder.isRecording()) {
			voiceRecorder.discardRecording();
			recordingContainer.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// ���notification bar��������ҳ�棬��ֻ֤��һ������ҳ��
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
		// ע���㲥
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
		// ������Դ�ļ�,����¼������ʱ
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
		// �Ѵ˻Ự��δ������Ϊ0
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
		// ����ListView���ر�������뷨
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

		// ע�������Ϣ�㲥
		receiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
		// ���ù㲥�����ȼ������Mainacitivity,���������Ϣ����ʱ��������chatҳ�棬ֱ����ʾ��Ϣ����������ʾ��Ϣδ��
		intentFilter.setPriority(5);
		registerReceiver(receiver, intentFilter);

		// ע��һ��ack��ִ��Ϣ��BroadcastReceiver
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
			case RESULT_CODE_COPY: // ������Ϣ
				EMMessage copyMsg = ((EMMessage) adapter.getItem(data.getIntExtra("position", -1)));
				if (copyMsg.getType() == EMMessage.Type.IMAGE) {
					ImageMessageBody imageBody = (ImageMessageBody) copyMsg.getBody();
					// ����һ���ض�ǰ׺��ճ��ʱ֪������Ҫճ��һ��ͼƬ
					clipboard.setText(COPY_IMAGE + imageBody.getLocalUrl());
				} else {
					// clipboard.setText(SmileUtils.getSmiledText(ChatActivity.this,
					// ((TextMessageBody) copyMsg.getBody()).getMessage()));
					clipboard.setText(((TextMessageBody) copyMsg.getBody()).getMessage());
				}
				break;
			case RESULT_CODE_DELETE: // ɾ����Ϣ
				EMMessage deleteMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", -1));
				emConversation.removeMessage(deleteMsg.getMsgId());
				adapter.refresh();
				mMsgListView.setSelection(data.getIntExtra("position", adapter.getCount()) - 1);
				break;

			default:
				break;
			}
		}
		if (resultCode == RESULT_OK) { // �����Ϣ
			if (requestCode == REQUEST_CODE_EMPTY_HISTORY) {
				// ��ջỰ
				EMChatManager.getInstance().clearConversation(toChatUsername);
				adapter.refresh();
			} else if (requestCode == REQUEST_CODE_CAMERA) { // ������Ƭ
				if (cameraFile != null && cameraFile.exists())
					sendPicture(cameraFile.getAbsolutePath());
			} else if (requestCode == REQUEST_CODE_LOCAL) { // ���ͱ���ͼƬ
				if (data != null) {
					Uri selectedImage = data.getData();
					if (selectedImage != null) {
						sendPicByUri(selectedImage);
					}
				}
			}
			//			else if (requestCode == REQUEST_CODE_MAP) { // ��ͼ
			//				double latitude = data.getDoubleExtra("latitude", 0);
			//				double longitude = data.getDoubleExtra("longitude", 0);
			//				String locationAddress = data.getStringExtra("address");
			//				if (locationAddress != null && !locationAddress.equals("")) {
			//					more(more);
			//					sendLocationMsg(latitude, longitude, "", locationAddress);
			//				} else {
			//					Toast.makeText(this, "�޷���ȡ������λ����Ϣ��", 0).show();
			//				}
			//				// �ط���Ϣ
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
				// ճ��
				if (!TextUtils.isEmpty(clipboard.getText())) {
					String pasteText = clipboard.getText().toString();
					if (pasteText.startsWith(COPY_IMAGE)) {
						// ��ͼƬǰ׺ȥ������ԭ��������path
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
	 * �����ȡͼƬ
	 */
	public void selectPicFromCamera() {
		if (!CommonTools.isExitsSdcard()) {
			ToastTool.showShort(getApplicationContext(), "SD�������ڣ���������");
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
	 * ��ͼ���ȡͼƬ
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
	 * ���������
	 */
	private void hideKeyboard() {
		inputMethodManager.hideSoftInputFromWindow(msgEt.getWindowToken(), 0);
	}

	/**
	 * ��ʼ������ͼƬ�ȴ���
	 */
	private void initMorePage() {
		int[] imageIds = new int[] { R.drawable.sel_chat_take_photo, R.drawable.sel_chat_choose_photo };
		String[] names = new String[] { "����", "���" };
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
					selectPicFromCamera();// �������ͼ��
					break;
				case 1:
					selectPicFromLocal(); // ���ͼƬͼ��
					break;
				default:
					break;
				}
			}
		});
	}

	/**
	 * ��ʼ�����鴰��
	 */
	private void initFacePage() {
		// TODO Auto-generated method stub
		List<View> lv = new ArrayList<View>();
		for (int i = 0; i < DefaultSetting.NUM_PAGE; ++i)
			lv.add(getGridView(i));
		FacePageAdeapter adapter = new FacePageAdeapter(lv, faceViewPager);
		faceViewPager.setAdapter(adapter);
		faceViewPager.setCurrentItem(currentPage);
		//���÷�ҳЧ��
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
	 * ���ر�����ͼ
	 * @param i
	 * @return
	 */
	private GridView getGridView(int i) {
		// TODO Auto-generated method stub
		GridView gv = new GridView(this);
		gv.setNumColumns(7);
		gv.setSelector(new ColorDrawable(Color.TRANSPARENT));// ����GridViewĬ�ϵ��Ч��
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
				if (arg2 == DefaultSetting.NUM) {// ɾ������λ��
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
					// ע�͵Ĳ��֣���EditText����ʾ�ַ���
					// String ori = msgEt.getText().toString();
					// int index = msgEt.getSelectionStart();
					// StringBuilder stringBuilder = new StringBuilder(ori);
					// stringBuilder.insert(index, keys.get(count));
					// msgEt.setText(stringBuilder.toString());
					// msgEt.setSelection(index + keys.get(count).length());

					// �����ⲿ�֣���EditText����ʾ����
					Bitmap bitmap = BitmapFactory.decodeResource(getResources(), (Integer) BaseApplication
							.getInstance().getFaceMap().values().toArray()[count]);
					if (bitmap != null) {
						int rawHeigh = bitmap.getHeight();
						int rawWidth = bitmap.getHeight();
						int newHeight = 60;
						int newWidth = 60;
						// ������������
						float heightScale = ((float) newHeight) / rawHeigh;
						float widthScale = ((float) newWidth) / rawWidth;
						// �½�������
						Matrix matrix = new Matrix();
						matrix.postScale(heightScale, widthScale);
						// ����ͼƬ����ת�Ƕ�
						// matrix.postRotate(-30);
						// ����ͼƬ����б
						// matrix.postSkew(0.1f, 0.1f);
						// ��ͼƬ��Сѹ��
						// ѹ����ͼƬ�Ŀ�͸��Լ�kB��С����仯
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
	 * �ط���Ϣ
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
	 * �����ı���Ϣ
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
					// ������Ϣbody
					message.addBody(txtBody);
					// ����Ҫ����˭,�û�username
					message.setReceipt(toChatUsername);
					// ��messgage�ӵ�conversation��
					emConversation.addMessage(message);
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					// ֪ͨadapter����Ϣ�䶯��adapter����ݼ��������message��ʾ��Ϣ�͵���sdk�ķ��ͷ���
					adapter.refresh();
					mMsgListView.setSelection(mMsgListView.getCount() - 1);
					msgEt.setText("");
					setResult(RESULT_OK);
				}
			}.execute();
		}
	}

	/**
	 * ��������
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
	 * ����ͼƬ
	 * 
	 * @param filePath
	 */
	private void sendPicture(final String filePath) {
		String to = toChatUsername;
		// create and add image message in view
		final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
		message.setReceipt(to);
		ImageMessageBody body = new ImageMessageBody(new File(filePath));
		// Ĭ�ϳ���100k��ͼƬ��ѹ���󷢸��Է����������óɷ���ԭͼ
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
	 * ����ͼ��ͼƬuri����ͼƬ
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
				Toast toast = Toast.makeText(this, "�Ҳ���ͼƬ", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
			sendPicture(picturePath);
		} else {
			File file = new File(selectedImage.getPath());
			if (!file.exists()) {
				Toast toast = Toast.makeText(this, "�Ҳ���ͼƬ", Toast.LENGTH_SHORT);
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
		case R.id.send_btn:// ������Ϣ
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
	 * ��ʾ����
	 */
	public void showFace() {
		if (!isFaceShow) {
			inputMethodManager.hideSoftInputFromWindow(msgEt.getWindowToken(), 0);
			try {
				Thread.sleep(80);// �����ʱ���һ����Ļ������
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
	 * ��ʾ����
	 */
	public void showMore() {
		if (!isMoreShow) {
			inputMethodManager.hideSoftInputFromWindow(msgEt.getWindowToken(), 0);
			try {
				Thread.sleep(80);// �����ʱ���һ����Ļ������
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
	 * ��ʾ����ͼ�갴ť
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
	 * ��ʾ����ͼ��
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
	 * ��Ϣ�㲥������
	 * 
	 */
	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String username = intent.getStringExtra("from");
			//			String msgid = intent.getStringExtra("msgid");
			// �յ�����㲥��ʱ��message�Ѿ���db���ڴ����ˣ�����ͨ��id��ȡmesage����
			//			EMMessage message = EMChatManager.getInstance().getMessage(msgid);

			if (!username.equals(toChatUsername)) {
				// ��Ϣ���Ƿ�����ǰ�Ự��return
				return;
			}

			// conversation =
			// EMChatManager.getInstance().getConversation(toChatUsername);
			// ֪ͨadapter������Ϣ������ui
			adapter.refresh();
			mMsgListView.setSelection(mMsgListView.getCount() - 1);
			// �ǵðѹ㲥���ս��
			abortBroadcast();
		}
	}

	/**
	 * ��Ϣ��ִBroadcastReceiver
	 */
	private BroadcastReceiver ackMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String msgid = intent.getStringExtra("msgid");
			String from = intent.getStringExtra("from");
			EMConversation conversation = EMChatManager.getInstance().getConversation(from);
			if (conversation != null) {
				// ��message��Ϊ�Ѷ�
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
	 * ��ס˵��listener
	 * 
	 */
	class PressToSpeakListen implements View.OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!CommonTools.isExitsSdcard()) {
					ToastTool.showShort(ChatActivity.this, "����������Ҫsdcard֧�֣�");
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
							ToastTool.showShort(getApplicationContext(), "¼��ʱ��̫��");
						}
					} catch (Exception e) {
						e.printStackTrace();
						ToastTool.showShort(ChatActivity.this, "����ʧ�ܣ�����������Ƿ�����");
					}
				}
				return true;
			default:
				return false;
			}
		}
	}
}