package com.yixianqian.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;

import u.aly.co;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.baidu.android.pushservice.PushManager;
import com.easemob.chat.ConnectionListener;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMNotifier;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.baidupush.SendNotifyTask;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.base.BaseFragmentActivity;
import com.yixianqian.config.Constants;
import com.yixianqian.customewidget.MyAlertDialog;
import com.yixianqian.db.ConversationDbService;
import com.yixianqian.db.FlipperDbService;
import com.yixianqian.entities.Conversation;
import com.yixianqian.entities.Flipper;
import com.yixianqian.jsonobject.JsonFlipperRequest;
import com.yixianqian.jsonobject.JsonUser;
import com.yixianqian.table.FlipperRequestTable;
import com.yixianqian.table.FlipperTable;
import com.yixianqian.table.LoversTable;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.FastJsonTool;
import com.yixianqian.utils.FriendPreference;
import com.yixianqian.utils.HttpUtil;
import com.yixianqian.utils.LogTool;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

/**
 * �����ƣ�MainActivity
 * ����������ҳ�棬��������Fragment��һ���������Fragment��һ���Ǹ������ĵ�Fragment
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��7��16�� ����3:25:58
 *
 */
public class MainActivity extends BaseFragmentActivity {
	private View[] mTabs;
	private UserPreference userPreference;
	private FriendPreference friendpreference;
	//��Ϣ���չ㲥
	private NewMessageBroadcastReceiver msgReceiver;
	// �˺��ڱ𴦵�¼
	private boolean isConflict = false;
	private android.app.AlertDialog.Builder conflictBuilder;
	private boolean isConflictDialogShow;
	private HomeFragment homeFragment;
	private PersonalFragment personalFragment;
	private int index;
	// ��ǰfragment��index
	private int currentTabIndex;
	private Fragment[] fragments;
	private JsonUser jsonUser;
	private MyAlertDialog myAlertDialog;
	private ConversationDbService conversationDbService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		userPreference = BaseApplication.getInstance().getUserPreference();
		friendpreference = BaseApplication.getInstance().getFriendPreference();
		conversationDbService = ConversationDbService.getInstance(MainActivity.this);
		homeFragment = new HomeFragment();
		personalFragment = new PersonalFragment();
		fragments = new Fragment[] { homeFragment, personalFragment };

		//���ñ�ǩ
		List<String> tags = new ArrayList<String>();
		tags.add(userPreference.getU_gender());
		PushManager.setTags(this, tags);

		findViewById();
		initView();

		// �����ʾ��һ��fragment
		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, homeFragment)
				.add(R.id.fragment_container, personalFragment).hide(personalFragment).show(homeFragment).commit();

		// ע��һ��������Ϣ��BroadcastReceiver
		msgReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
		intentFilter.setPriority(3);
		registerReceiver(msgReceiver, intentFilter);

		// ע��һ��ack��ִ��Ϣ��BroadcastReceiver
		IntentFilter ackMessageIntentFilter = new IntentFilter(EMChatManager.getInstance()
				.getAckMessageBroadcastAction());
		ackMessageIntentFilter.setPriority(3);
		registerReceiver(ackMessageReceiver, ackMessageIntentFilter);

		// setContactListener������ϵ�˵ı仯��
		EMContactManager.getInstance().setContactListener(new MyContactListener());
		// ע��һ����������״̬��listener
		EMChatManager.getInstance().addConnectionListener(new MyConnectionListener());
		// ֪ͨsdk��UI �Ѿ���ʼ����ϣ�ע������Ӧ��receiver��listener, ���Խ���broadcast��
		EMChat.getInstance().setAppInited();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		mTabs = new View[2];
		mTabs[0] = (View) findViewById(R.id.homeBtn);
		mTabs[1] = (View) findViewById(R.id.personalBtn);
		// �ѵ�һ��tab��Ϊѡ��״̬
		mTabs[0].setSelected(true);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (!isConflict) {
			EMChatManager.getInstance().activityResumed();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (getIntent().getBooleanExtra("conflict", false) && !isConflictDialogShow)
			showConflictDialog();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		final MyAlertDialog myAlertDialog = new MyAlertDialog(this);
		myAlertDialog.setTitle("��ʾ");
		myAlertDialog.setMessage("�Ƿ��˳�һ��ǣ�ͻ��ˣ�");
		View.OnClickListener comfirm = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myAlertDialog.dismiss();
				close();
			}
		};
		View.OnClickListener cancle = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myAlertDialog.dismiss();
			}
		};
		myAlertDialog.setPositiveButton("ȷ��", comfirm);
		myAlertDialog.setNegativeButton("ȡ��", cancle);
		myAlertDialog.show();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// ע���㲥������
		try {
			unregisterReceiver(msgReceiver);
		} catch (Exception e) {
		}
		try {
			unregisterReceiver(ackMessageReceiver);
		} catch (Exception e) {
		}

		if (conflictBuilder != null) {
			conflictBuilder.create().dismiss();
			conflictBuilder = null;
		}
	}

	/**
	 * button����¼�
	 * 
	 * @param view
	 */
	public void onTabClicked(View view) {
		switch (view.getId()) {
		case R.id.homeBtn:
			index = 0;
			break;
		case R.id.personalBtn:
			index = 1;
			break;
		}
		if (currentTabIndex != index) {
			FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
			trx.hide(fragments[currentTabIndex]);
			if (!fragments[index].isAdded()) {
				trx.add(R.id.fragment_container, fragments[index]);
			}
			trx.show(fragments[index]).commit();
		}
		mTabs[currentTabIndex].setSelected(false);
		// �ѵ�ǰtab��Ϊѡ��״̬
		mTabs[index].setSelected(true);
		currentTabIndex = index;
	}

	/**
	 * ����Ϣ�㲥������
	 * 
	 * 
	 */
	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// ��Ϣid
			String msgId = intent.getStringExtra("msgid");
			// �յ�����㲥��ʱ��message�Ѿ���db���ڴ����ˣ�����ͨ��id��ȡmesage����
			// EMMessage message =
			// EMChatManager.getInstance().getMessage(msgId);

			// ˢ��bottom bar��Ϣδ����
			//			updateUnreadLabel();
			if (currentTabIndex == 0) {
				// ��ǰҳ�����Ϊ������ʷҳ�棬ˢ�´�ҳ��
				if (homeFragment != null) {
					homeFragment.refresh();
				}
			}
			// ע���㲥��������ChatActivity�л��յ�����㲥
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
		}
	};

	/**
	 * ��ʾ�ʺ��ڱ𴦵�¼dialog
	 */
	private void showConflictDialog() {
		isConflictDialogShow = true;
		BaseApplication.getInstance().logout();

		if (!MainActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (conflictBuilder == null)
					conflictBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
				conflictBuilder.setTitle("����֪ͨ");
				conflictBuilder.setMessage(R.string.connect_conflict);
				conflictBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						conflictBuilder = null;
						finish();
						startActivity(new Intent(MainActivity.this, LoginActivity.class));
					}
				});
				conflictBuilder.setCancelable(false);
				conflictBuilder.create().show();
				isConflict = true;
			} catch (Exception e) {
				Log.e("###", "---------color conflictBuilder error" + e.getMessage());
			}

		}

	}

	/***
	 * ��ϵ�˱仯listener
	 * 
	 */
	private class MyContactListener implements EMContactListener {

		@Override
		public void onContactAdded(List<String> usernameList) {
			// �������ӵ���ϵ��
			//			Map<String, User> localUsers = DemoApplication.getInstance().getContactList();
			//			Map<String, User> toAddUsers = new HashMap<String, User>();
			//			for (String username : usernameList) {
			//				User user = new User();
			//				user.setUsername(username);
			//				String headerName = null;
			//				if (!TextUtils.isEmpty(user.getNick())) {
			//					headerName = user.getNick();
			//				} else {
			//					headerName = user.getUsername();
			//				}
			//				if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
			//					user.setHeader("");
			//				} else if (Character.isDigit(headerName.charAt(0))) {
			//					user.setHeader("#");
			//				} else {
			//					user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(
			//							0, 1).toUpperCase());
			//					char header = user.getHeader().toLowerCase().charAt(0);
			//					if (header < 'a' || header > 'z') {
			//						user.setHeader("#");
			//					}
			//				}
			//				// ��ʱ�и�bug����Ӻ���ʱ���ܻ�ص�added��������
			//				if (!localUsers.containsKey(username)) {
			//					userDao.saveContact(user);
			//				}
			//				toAddUsers.put(username, user);
			//			}
			//			localUsers.putAll(toAddUsers);
			//			// ˢ��ui
			//			if (currentTabIndex == 0)
			//				contactListFragment.refresh();
			for (String string : usernameList) {
				System.out.println(string + "�����ϵ��");
			}

		}

		@Override
		public void onContactDeleted(List<String> usernameList) {
			// ��ɾ��
			//			Map<String, User> localUsers = DemoApplication.getInstance().getContactList();
			//			for (String username : usernameList) {
			//				localUsers.remove(username);
			//				userDao.deleteContact(username);
			//				inviteMessgeDao.deleteMessage(username);
			//			}
			//			// ˢ��ui
			//			if (currentTabIndex == 1)
			//				contactListFragment.refresh();
			//			updateUnreadLabel();
			Log.e("�Ķ�����", "�����Ǳ�ɾ����");

			for (String username : usernameList) {
				Log.e("�Ķ�����", "��" + username + "ɾ��");
				showDeletDialog(Integer.parseInt(username));
			}
		}

		//�յ��Ķ�����
		@Override
		public void onContactInvited(final String username, String reason) {
			Log.e("�Ķ�����", "�յ��Ķ���������" + username + ",reason: " + reason);

			// �ӵ��������Ϣ�����������(ͬ���ܾ�)�����ߺ󣬷��������Զ��ٷ����������Կͻ��˲�Ҫ�ظ�����
			Map<String, String> map = new HashMap<String, String>();
			map.put("" + FlipperRequestTable.FR_USERID, "" + userPreference.getU_id());
			map.put("" + FlipperRequestTable.FR_FLIPPERID, username);
			try {
				String response = HttpUtil.postRequest("getjsonflipperrequest", map);
				JsonFlipperRequest fRequest = FastJsonTool.getObject(response, JsonFlipperRequest.class);
				if (fRequest != null) {
					notifyNewFlipper(fRequest);
				} else {
					LogTool.e("�Ķ�����", "JsonFlipperRequestΪ��");
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			//			new Thread(new Runnable() {
			//
			//				@Override
			//				public void run() {
			//					// TODO Auto-generated method stub
			//					try {
			//						EMChatManager.getInstance().refuseInvitation(username);
			//					} catch (EaseMobException e) {
			//						// TODO Auto-generated catch block
			//						e.printStackTrace();
			//					}
			//				}
			//			}).start();

		}

		//�Ķ�����ͬ��
		@Override
		public void onContactAgreed(String username) {
			//			List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
			//			for (InviteMessage inviteMessage : msgs) {
			//				if (inviteMessage.getFrom().equals(username)) {
			//					return;
			//				}
			//			}
			//			// �Լ���װ��javabean
			//			InviteMessage msg = new InviteMessage();
			//			msg.setFrom(username);
			//			msg.setTime(System.currentTimeMillis());
			//			Log.d(TAG, username + "ͬ������ĺ�������");
			//			msg.setStatus(InviteMesageStatus.BEAGREED);
			//			notifyNewIviteMessage(msg);
			//			if (!TextUtils.isEmpty(username)) {
			//				FlipperDbService flipperDbService = FlipperDbService.getInstance(MainActivity.this);
			//				Flipper flipper = flipperDbService.getFlipperByUserId(Integer.parseInt(username));
			//				flipper.setStatus(Constants.FlipperStatus.BEAGREED);
			//			}
			Log.d("�Ķ�����", username + "��ͬ��");
			ToastTool.showLong(MainActivity.this, "����" + username + "���Ķ������Ѿ���ͬ��");

			FlipperDbService flipperDbService = FlipperDbService.getInstance(MainActivity.this);
			Flipper flipper = flipperDbService.getFlipperByUserId(Integer.parseInt(username));
			if (flipper != null) {
				flipper.setStatus(Constants.FlipperStatus.BEAGREED);
				flipperDbService.flipperDao.update(flipper);
				saveFlipperRelation(flipper);
			} else {
				saveFlipperRealitionById(Integer.parseInt(username));
			}
		}

		//�Ķ����󱻾ܾ�
		@Override
		public void onContactRefused(String username) {
			Log.d("�Ķ�����", username + "�ܾ�");
			ToastTool.showLong(MainActivity.this, "����" + username + "���Ķ����󱻾ܾ�");
		}

	}

	/**
	 * �����Ķ�������ʾ����Ϣ
	 * 
	 * @param msg
	 */
	private void notifyNewFlipper(JsonFlipperRequest flipperRequest) {
		saveFlipper(flipperRequest);
		// ��ʾ������Ϣ
		EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();
		ToastTool.showShort(getApplicationContext(), flipperRequest.getU_nickname() + "������Ȼ�Ķ�");
	}

	/**
	 * ��������
	 * @param flipper
	 */
	private void saveFlipper(JsonFlipperRequest fRequest) {
		FlipperDbService flipperDbService = FlipperDbService.getInstance(MainActivity.this);
		Flipper flipper = flipperDbService.getFlipperByUserId(fRequest.getU_id());
		//������ݿ��д��ڸ��û������������״̬
		if (flipper != null) {
			flipper.setIsRead(false);
			flipper.setTime(new Date());
			flipper.setStatus(Constants.FlipperStatus.BEINVITEED);
			flipper.setType(Constants.FlipperType.FROM);
			flipperDbService.flipperDao.update(flipper);
		} else {
			flipper = new Flipper(null, fRequest.getU_id(), fRequest.getU_bpush_user_id(),
					fRequest.getU_bpush_channel_id(), fRequest.getU_nickname(), fRequest.getU_realname(),
					fRequest.getU_gender(), fRequest.getU_email(), fRequest.getU_large_avatar(),
					fRequest.getU_small_avatar(), fRequest.getU_blood_type(), fRequest.getU_constell(),
					fRequest.getU_introduce(), fRequest.getU_birthday(), fRequest.getTime(), fRequest.getU_age(),
					fRequest.getU_vocationid(), fRequest.getU_stateid(), fRequest.getU_provinceid(),
					fRequest.getU_cityid(), fRequest.getU_schoolid(), fRequest.getU_height(), fRequest.getU_weight(),
					fRequest.getU_image_pass(), fRequest.getU_salary(), false, fRequest.getU_tel(),
					Constants.FlipperStatus.BEINVITEED, Constants.FlipperType.FROM);
			flipperDbService.flipperDao.insert(flipper);
		}

	}

	/**
	 * �����Ķ�����Ϣ
	 */
	private void saveFlipperRelation(Flipper flipper) {
		userPreference.setU_stateid(3);
		friendpreference.setType(0);
		friendpreference.setBpush_ChannelID(flipper.getBpushChannelID());
		friendpreference.setBpush_UserID(flipper.getBpushUserID());
		friendpreference.setF_age(flipper.getAge());
		friendpreference.setF_blood_type(flipper.getBloodType());
		friendpreference.setF_constell(flipper.getConstell());
		friendpreference.setF_email(flipper.getEmail());
		friendpreference.setF_gender(flipper.getGender());
		friendpreference.setF_height(flipper.getHeight());
		friendpreference.setF_id(flipper.getUserID());
		friendpreference.setF_introduce(flipper.getIntroduce());
		friendpreference.setF_large_avatar(flipper.getLargeAvatar());
		friendpreference.setF_nickname(flipper.getNickname());
		friendpreference.setF_realname(flipper.getRealname());
		friendpreference.setF_salary(flipper.getSalary());
		friendpreference.setF_small_avatar(flipper.getSamllAvatar());
		friendpreference.setF_stateid(flipper.getStateID());
		friendpreference.setF_vocationid(flipper.getVocationID());
		friendpreference.setF_weight(flipper.getWeight());
		friendpreference.setU_cityid(flipper.getCityID());
		friendpreference.setU_provinceid(flipper.getProvinceID());
		friendpreference.setU_schoolid(flipper.getSchoolID());
		creatConversation();
	}

	/**
	 * 	�����ȡUser��Ϣ
	 */
	private void saveFlipperRealitionById(int userId) {
		jsonUser = new JsonUser();
		RequestParams params = new RequestParams();
		params.put(UserTable.U_ID, userId);

		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {
			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					jsonUser = FastJsonTool.getObject(response, JsonUser.class);
					if (jsonUser != null) {
						userPreference.setU_stateid(3);
						friendpreference.setType(0);
						friendpreference.setBpush_ChannelID(jsonUser.getU_bpush_channel_id());
						friendpreference.setBpush_UserID(jsonUser.getU_bpush_user_id());
						friendpreference.setF_address(jsonUser.getU_address());
						friendpreference.setF_age(jsonUser.getU_age());
						friendpreference.setF_blood_type(jsonUser.getU_blood_type());
						friendpreference.setF_constell(jsonUser.getU_constell());
						friendpreference.setF_email(jsonUser.getU_email());
						friendpreference.setF_gender(jsonUser.getU_gender());
						friendpreference.setF_height(jsonUser.getU_height());
						friendpreference.setF_id(jsonUser.getU_id());
						friendpreference.setF_introduce(jsonUser.getU_introduce());
						friendpreference.setF_large_avatar(jsonUser.getU_large_avatar());
						friendpreference.setF_nickname(jsonUser.getU_nickname());
						friendpreference.setF_realname(jsonUser.getU_realname());
						friendpreference.setF_salary(jsonUser.getU_salary());
						friendpreference.setF_small_avatar(jsonUser.getU_small_avatar());
						friendpreference.setF_stateid(jsonUser.getU_stateid());
						friendpreference.setF_tel(jsonUser.getU_tel());
						friendpreference.setF_vocationid(jsonUser.getU_vocationid());
						friendpreference.setF_weight(jsonUser.getU_weight());
						friendpreference.setU_cityid(jsonUser.getU_cityid());
						friendpreference.setU_provinceid(jsonUser.getU_provinceid());
						friendpreference.setU_schoolid(jsonUser.getU_schoolid());

						creatConversation();
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				ToastTool.showLong(MainActivity.this, "����������");
			}
		};
		AsyncHttpClientTool.post(MainActivity.this, "getuserbyid", params, responseHandler);
	}

	/**
	 * �����Ự
	 */
	private void creatConversation() {
		conversationDbService.conversationDao.deleteAll();
		Conversation conversation = new Conversation(null, Long.valueOf(friendpreference.getF_id()),
				friendpreference.getName(), friendpreference.getF_small_avatar(), "", 0, System.currentTimeMillis());
		conversationDbService.conversationDao.insert(conversation);

		//���Է�����һ����Ϣ
		String msg = "�Һ���һ�����飬��ʼ�����~��";
		//��ȡ���������˵ĻỰ���󡣲���usernameΪ�����˵�userid����groupid�������е�username�������
		EMConversation emConversation = EMChatManager.getInstance().getConversation("" + friendpreference.getF_id());
		//����һ���ı���Ϣ
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
		//������Ϣbody
		TextMessageBody txtBody = new TextMessageBody(msg);
		message.addBody(txtBody);
		//���ý�����
		message.setReceipt("" + friendpreference.getF_id());
		//����Ϣ���뵽�˻Ự������
		emConversation.addMessage(message);

		if (currentTabIndex == 0) {
			// ��ǰҳ�����Ϊ������ʷҳ�棬ˢ�´�ҳ��
			if (homeFragment != null) {
				homeFragment.refresh();
			}
		}
	}

	//��ʾɾ���Ķ������¶Ի�����
	private void showDeletDialog(final int userID) {
		//������Ķ���ϵ
		if (userPreference.getU_stateid() == 3) {
			myAlertDialog = new MyAlertDialog(MainActivity.this);
			myAlertDialog.setTitle("��ʾ");
			myAlertDialog.setMessage(friendpreference.getName() + "����˺������Ķ���ϵ");
			View.OnClickListener comfirm = new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					myAlertDialog.dismiss();
					conversationDbService.conversationDao.delete(conversationDbService.getConversationByUser(userID));

					if (currentTabIndex == 0) {
						// ��ǰҳ�����Ϊ������ʷҳ�棬ˢ�´�ҳ��
						if (homeFragment != null) {
							homeFragment.refresh();
						}
					}

					//ɾ���Ự
					EMChatManager.getInstance().deleteConversation("" + friendpreference.getF_id());
					friendpreference.clear();
					userPreference.setU_stateid(4);
				}
			};
			myAlertDialog.setPositiveButton("ȷ��", comfirm);
			myAlertDialog.show();
		}
	}

	/**
	 * ���Ӽ���listener
	 * 
	 */
	private class MyConnectionListener implements ConnectionListener {

		@Override
		public void onConnected() {
			homeFragment.errorItem.setVisibility(View.GONE);
		}

		@Override
		public void onDisConnected(String errorString) {
			if (errorString != null && errorString.contains("conflict")) {
				// ��ʾ�ʺ��������豸��½dialog
				showConflictDialog();
			} else {
				homeFragment.errorItem.setVisibility(View.VISIBLE);
				homeFragment.errorText.setText("���Ӳ������������");
			}
		}

		@Override
		public void onReConnected() {
			homeFragment.errorItem.setVisibility(View.GONE);
		}

		@Override
		public void onReConnecting() {
		}

		@Override
		public void onConnecting(String progress) {
		}

	}
}
