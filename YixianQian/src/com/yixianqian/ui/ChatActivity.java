package com.yixianqian.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
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
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yixianqian.R;
import com.yixianqian.adapter.FaceAdapter;
import com.yixianqian.adapter.FacePageAdeapter;
import com.yixianqian.adapter.MessageAdapter;
import com.yixianqian.baidupush.MyPushMessageReceiver;
import com.yixianqian.baidupush.MyPushMessageReceiver.EventHandler;
import com.yixianqian.baidupush.SendMsgAsyncTask;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.config.Constants;
import com.yixianqian.config.DefaultSetting;
import com.yixianqian.customewidget.CirclePageIndicator;
import com.yixianqian.customewidget.JazzyViewPager;
import com.yixianqian.customewidget.JazzyViewPager.TransitionEffect;
import com.yixianqian.db.ConversationDbService;
import com.yixianqian.db.MessageItemDbService;
import com.yixianqian.entities.MessageItem;
import com.yixianqian.jsonobject.JsonMessage;
import com.yixianqian.utils.FastJsonTool;
import com.yixianqian.utils.FriendPreference;
import com.yixianqian.xlistview.MsgListView;
import com.yixianqian.xlistview.MsgListView.IXListViewListener;

public class ChatActivity extends BaseActivity implements OnTouchListener, IXListViewListener, OnClickListener,
		EventHandler {
	/*************VIEWS*****************/
	private MsgListView mMsgListView;
	private ImageView topNavLeftBtn;//导航条左边按钮
	private TextView topNavText;//导航条文字
	private Button sendBtn;//发送按钮
	private ImageButton faceBtn;//表情
	private boolean isFaceShow = false;//是否显示表情
	private JazzyViewPager faceViewPager;//表情翻页
	private EditText msgEt;//输入框
	private LinearLayout faceLinearLayout;//表情区域
	private WindowManager.LayoutParams params;

	private InputMethodManager inputMethodManager;
	private int currentPage = 0;//当前表情页
	private List<String> keys;
	private BaseApplication baseApplication;
	private long conversationID;//对话ID
	private MessageAdapter adapter;//消息适配器
	private ConversationDbService conversationDbService;
	private MessageItemDbService messageItemDbService;
	private static int MsgPagerNum;//消息页数
	private FriendPreference friendSharePreference;
	public static final int NEW_MESSAGE = 0x001;// 收到消息

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == NEW_MESSAGE) {
				JsonMessage jsonMessage = (JsonMessage) msg.obj;
				String userId = jsonMessage.getBpushUserID();
				//				if (!userId.equals(mFromUser.getUserId()))// 如果不是当前正在聊天对象的消息，不处理
				//					return;

				//				int headId = msgItem.getHead_id();
				// TODO Auto-generated method stub
				MessageItem item = new MessageItem(null, Constants.MessageType.MESSAGE_TYPE_TEXT,
						jsonMessage.getMessageContent(), System.currentTimeMillis(), true, true, true, conversationID);
				System.out.println("内容为" + jsonMessage.getMessageContent());
				adapter.upDateMsg(item);
				messageItemDbService.messageItemDao.insert(item);
				//				RecentItem recentItem = new RecentItem(userId, headId, msgItem.getNick(), msgItem.getMessage(), 0,
				//						System.currentTimeMillis());
				//				mRecentDB.saveRecent(recentItem);
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.chat_main);

		conversationID = getIntent().getLongExtra("conversationID", (long) -1);

		findViewById();
		initData();
		initView();
		initFacePage();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MyPushMessageReceiver.ehList.add(this);// 监听推送的消息
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		faceLinearLayout.setVisibility(View.GONE);
		super.onPause();
		MyPushMessageReceiver.ehList.remove(this);// 移除监听
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		mMsgListView = (MsgListView) findViewById(R.id.msg_listView);
		topNavLeftBtn = (ImageView) findViewById(R.id.nav_left_btn);
		topNavText = (TextView) findViewById(R.id.nav_text);
		sendBtn = (Button) findViewById(R.id.send_btn);
		faceBtn = (ImageButton) findViewById(R.id.face_btn);
		msgEt = (EditText) findViewById(R.id.msg_et);
		faceLinearLayout = (LinearLayout) findViewById(R.id.face_ll);
		faceViewPager = (JazzyViewPager) findViewById(R.id.face_pager);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavText.setText("赵奕欢");
		inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		params = getWindow().getAttributes();

		msgEt.setOnTouchListener(this);
		// 触摸ListView隐藏表情和输入法
		mMsgListView.setOnTouchListener(this);
		mMsgListView.setPullLoadEnable(false);
		mMsgListView.setXListViewListener(this);
		mMsgListView.setAdapter(adapter);
		mMsgListView.setSelection(adapter.getCount() - 1);
		topNavLeftBtn.setOnClickListener(this);

		msgEt.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (params.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE || isFaceShow) {
						faceLinearLayout.setVisibility(View.GONE);
						isFaceShow = false;
						// imm.showSoftInput(msgEt, 0);
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

	}

	private void initData() {
		baseApplication = BaseApplication.getInstance();
		Set<String> keySet = baseApplication.getFaceMap().keySet();
		keys = new ArrayList<String>();
		keys.addAll(keySet);
		MsgPagerNum = 0;
		conversationDbService = ConversationDbService.getInstance(ChatActivity.this);
		messageItemDbService = MessageItemDbService.getInstance(ChatActivity.this);
		adapter = new MessageAdapter(this, initMsgData(), conversationDbService.conversationDao.load(conversationID));
		friendSharePreference = BaseApplication.getInstance().getFriendPreference();
	}

	/**
	 * 加载消息历史，从数据库中读出
	 */
	private List<MessageItem> initMsgData() {
		List<MessageItem> msgList = new ArrayList<MessageItem>();
		ConversationDbService conversationDbService = ConversationDbService.getInstance(ChatActivity.this);
		msgList = conversationDbService.getMsgItemListByConId(conversationID);
		return msgList;
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
		faceViewPager.setTransitionEffect(TransitionEffect.Accordion);
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
		gv.setVerticalSpacing(1);
		gv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		gv.setGravity(Gravity.CENTER);
		gv.setAdapter(new FaceAdapter(this, i));
		gv.setOnTouchListener(forbidenScroll());
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
						int newHeight = 40;
						int newWidth = 40;
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

	// 防止乱pageview乱滚动
	private OnTouchListener forbidenScroll() {
		return new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					return true;
				}
				return false;
			}
		};
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.msg_listView:
			inputMethodManager.hideSoftInputFromWindow(msgEt.getWindowToken(), 0);
			faceLinearLayout.setVisibility(View.GONE);
			isFaceShow = false;
			break;
		case R.id.msg_et:
			inputMethodManager.showSoftInput(msgEt, 0);
			faceLinearLayout.setVisibility(View.GONE);
			isFaceShow = false;
			break;

		default:
			break;
		}
		return false;
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		MsgPagerNum++;
		List<MessageItem> msgList = initMsgData();
		int position = adapter.getCount();
		adapter.setMessageList(msgList);
		mMsgListView.stopRefresh();
		mMsgListView.setSelection(adapter.getCount() - position - 1);
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.face_btn:
			if (!isFaceShow) {
				inputMethodManager.hideSoftInputFromWindow(msgEt.getWindowToken(), 0);
				try {
					Thread.sleep(80);// 解决此时会黑一下屏幕的问题
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				faceLinearLayout.setVisibility(View.VISIBLE);
				isFaceShow = true;
			} else {
				faceLinearLayout.setVisibility(View.GONE);
				isFaceShow = false;
			}
			break;
		case R.id.send_btn:// 发送消息
			String msg = msgEt.getText().toString();
			MessageItem item = new MessageItem(null, Constants.MessageType.MESSAGE_TYPE_TEXT, msg,
					System.currentTimeMillis(), true, false, false, conversationID);
			adapter.upDateMsg(item);
			mMsgListView.setSelection(adapter.getCount() - 1);
			messageItemDbService.messageItemDao.insert(item);
			msgEt.setText("");

			JsonMessage message = new JsonMessage(System.currentTimeMillis(), msg, "");
			new SendMsgAsyncTask(FastJsonTool.createJsonString(message), friendSharePreference.getBpush_UserID())
					.send();
			//			Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
			//			System.out.println(gson.toJson(message));
			//			new SendMsgAsyncTask(gson.toJson(message), friendSharePreference.getBpush_UserID()).send();;

			//			new SendMsgAsyncTask(mGson.toJson(msgItem), mFromUser.getUserId()).send();
			//			RecentItem recentItem = new RecentItem(mFromUser.getUserId(), mFromUser.getHeadIcon(), mFromUser.getNick(),
			//					msg, 0, System.currentTimeMillis());
			//			mRecentDB.saveRecent(recentItem);
			break;
		case R.id.nav_left_btn:
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void onMessage(JsonMessage jsonMessage) {
		// TODO Auto-generated method stub
		Message handlerMsg = handler.obtainMessage(NEW_MESSAGE);
		handlerMsg.obj = jsonMessage;
		handler.sendMessage(handlerMsg);
	}

	@Override
	public void onBind(String method, int errorCode, String content) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNotify(String title, String content) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNetChange(boolean isNetConnected) {
		// TODO Auto-generated method stub
	}

}