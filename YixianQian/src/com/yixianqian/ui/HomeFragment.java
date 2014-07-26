package com.yixianqian.ui;

import java.util.LinkedList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yixianqian.R;
import com.yixianqian.adapter.HomeListAdapter;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.base.BaseV4Fragment;
import com.yixianqian.db.ConversationDbService;
import com.yixianqian.entities.Conversation;
import com.yixianqian.utils.FriendPreference;

/**
 * �����ƣ�HomeFragment ����������ҳ �����ˣ� ��˧ ����ʱ�䣺2014��7��4�� ����5:18:18
 *
 */
public class HomeFragment extends BaseV4Fragment {
	private View rootView;// ��View
	private ImageView topNavLeftBtn;//��������߰�ť
	private ImageView topNavRightBtn;//�������ұ߰�ť
	private View right_btn_bg;
	private ListView mHomeListView;
	private TextView mEmpty;
	private HomeListAdapter mAdapter;
	private FriendPreference friendPreference;
	private ConversationDbService conversationDbService;
	private LinkedList<Conversation> conversationList;
	private Vibrator vib;
	private View popBtn;//ɾ����ť
	private int currentItem = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		friendPreference = BaseApplication.getInstance().getFriendPreference();
		conversationDbService = ConversationDbService.getInstance(getActivity());
		conversationList = new LinkedList<Conversation>();
		conversationList.addAll(conversationDbService.conversationDao.loadAll());
		/**�𶯷���*/
		vib = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_home, container, false);

		findViewById();// ��ʼ��views
		initView();

		mAdapter = new HomeListAdapter(getActivity(), mHomeListView, conversationList);
		mHomeListView.setAdapter(mAdapter);
		return rootView;
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		topNavLeftBtn = (ImageView) rootView.findViewById(R.id.nav_left_btn);
		topNavRightBtn = (ImageView) rootView.findViewById(R.id.nav_right_btn);
		right_btn_bg = (View) rootView.findViewById(R.id.right_btn_bg);
		mHomeListView = (ListView) rootView.findViewById(R.id.recent_listview);
		mEmpty = (TextView) rootView.findViewById(R.id.empty);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		//���û�жԻ�
		if (conversationList.size() < 1) {
			mEmpty.setVisibility(View.VISIBLE);
		}
		topNavLeftBtn.setImageResource(R.drawable.home);
		topNavRightBtn.setImageResource(R.drawable.ic_action_overflow);
		right_btn_bg.setBackgroundResource(R.drawable.sel_topnav_btn_bg);
		final View popView = getActivity().getLayoutInflater().inflate(R.layout.popup, null);
		popBtn = popView.findViewById(R.id.popup_btn);
		final PopupWindow popup = new PopupWindow(popView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		// ��Ҫ����һ�´˲����������߿���ʧ 
		popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		//���õ��������ߴ�����ʧ 
		popup.setOutsideTouchable(true);
		popup.setFocusable(true);

		right_btn_bg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog();
			}
		});

		mHomeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Intent toChatIntent = new Intent(getActivity(), ChatActivity.class);
				toChatIntent.putExtra("conversationID", conversationList.get(position).getId());
				startActivity(toChatIntent);
				getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});

		/**
		 * �����¼�
		 */
		mHomeListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				vib.vibrate(50);
				int xoff = view.getWidth() / 2 - popView.getWidth();
				popup.showAsDropDown(view, xoff, 0);
				currentItem = position;
				return false;
			}
		});

		/**
		 * ɾ����ť
		 */
		popBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popup.dismiss();
				if (currentItem > -1) {
					conversationDbService.conversationDao.delete(conversationList.get(currentItem));
					conversationList.remove(currentItem);
					mAdapter.notifyDataSetChanged();
					currentItem = -1;
				}
			}
		});
	}

	/**
	 * �˵���ʾ
	 */
	void showDialog() {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		Fragment fragment = getFragmentManager().findFragmentByTag("dialog");
		if (fragment != null) {
			ft.remove(fragment);
		}
		ft.addToBackStack(null);

		// Create and show the dialog.
		HomeDialogFragment newFragment = HomeDialogFragment.newInstance();
		newFragment.show(ft, "dialog");
	}

}
