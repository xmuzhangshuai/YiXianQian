package com.yixianqian.ui;

import java.util.ArrayList;
import java.util.List;

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
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.yixianqian.R;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.base.BaseFragmentActivity;
import com.yixianqian.customewidget.MyAlertDialog;
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
		//		EMContactManager.getInstance().setContactListener(new MyContactListener());
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
