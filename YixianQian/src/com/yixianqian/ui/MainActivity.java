package com.yixianqian.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
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
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.base.BaseFragmentActivity;
import com.yixianqian.config.Constants;
import com.yixianqian.customewidget.MyAlertDialog;
import com.yixianqian.db.FlipperDbService;
import com.yixianqian.entities.Flipper;
import com.yixianqian.jsonobject.JsonFlipperRequest;
import com.yixianqian.table.FlipperRequestTable;
import com.yixianqian.table.UserTable;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.FastJsonTool;
import com.yixianqian.utils.LogTool;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		userPreference = BaseApplication.getInstance().getUserPreference();
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

		//������Ķ���ϵʱ
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

		/**
		 * ����Ķ���ϵʱ
		 */
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
			for (String string : usernameList) {
				System.out.println(string + "ɾ��");
			}
		}

		//�յ��Ķ�����
		@Override
		public void onContactInvited(String username, String reason) {
			// �ӵ��������Ϣ�����������(ͬ���ܾ�)�����ߺ󣬷��������Զ��ٷ����������Կͻ��˲�Ҫ�ظ�����
			if (userPreference.getU_stateid() == 4 && !TextUtils.isEmpty(username)) {
				RequestParams params = new RequestParams();
				params.put(FlipperRequestTable.FR_USERID, userPreference.getU_id());
				params.put(FlipperRequestTable.FR_FLIPPERID, Integer.parseInt(username));

				TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {
					@Override
					public void onSuccess(int statusCode, Header[] headers, String response) {
						// TODO Auto-generated method stub
						if (statusCode == 200) {
							JsonFlipperRequest fRequest = FastJsonTool.getObject(response, JsonFlipperRequest.class);
							if (fRequest != null) {
								notifyNewFlipper(fRequest);
							} else {
								LogTool.d("�Ķ�����", "Ϊ��");
							}
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
						// TODO Auto-generated method stub
					}
				};
				AsyncHttpClientTool.post(MainActivity.this, "getflipperrequest", params, responseHandler);
			}

			Log.d("�Ķ�����", username + "�Ķ�����,reason: " + reason);

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
			Log.d("�Ķ�����", username + "ͬ��");
		}

		//�Ķ����󱻾ܾ�
		@Override
		public void onContactRefused(String username) {
			Log.d("�Ķ�����", username + "�ܾ�");
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
	}

	/**
	 * ��������
	 * @param flipper
	 */
	private void saveFlipper(JsonFlipperRequest fRequest) {
		FlipperDbService flipperDbService = FlipperDbService.getInstance(MainActivity.this);
		Flipper flipper = new Flipper(null, fRequest.getU_id(), fRequest.getU_nickname(), fRequest.getU_realname(),
				fRequest.getU_gender(), fRequest.getU_email(), fRequest.getU_large_avatar(),
				fRequest.getU_small_avatar(), fRequest.getU_blood_type(), fRequest.getU_constell(),
				fRequest.getU_introduce(), fRequest.getU_birthday(), fRequest.getTime(), fRequest.getU_age(),
				fRequest.getU_vocationid(), fRequest.getU_stateid(), fRequest.getU_provinceid(),
				fRequest.getU_cityid(), fRequest.getU_schoolid(), fRequest.getU_height(), fRequest.getU_weight(),
				fRequest.getU_image_pass(), fRequest.getU_salary(), false, fRequest.getU_tel(),
				Constants.FlipperStatus.BEINVITEED, Constants.FlipperType.FROM);
		flipperDbService.flipperDao.insert(flipper);
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
