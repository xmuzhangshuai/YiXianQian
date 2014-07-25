package com.yixianqian.ui;

import java.util.LinkedList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yixianqian.R;
import com.yixianqian.adapter.HomeListAdapter;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.base.BaseV4Fragment;
import com.yixianqian.db.ConversationDbService;
import com.yixianqian.entities.Conversation;
import com.yixianqian.utils.FriendPreference;

/**
 * 类名称：HomeFragment 类描述：主页 创建人： 张帅 创建时间：2014年7月4日 下午5:18:18
 *
 */
public class HomeFragment extends BaseV4Fragment {
	private View rootView;// 根View
	private ImageView topNavLeftBtn;//导航条左边按钮
	private ImageView topNavRightBtn;//导航条右边按钮
	private View right_btn_bg;
	private ListView mHomeListView;
	private TextView mEmpty;
	private HomeListAdapter mAdapter;
	private FriendPreference friendPreference;
	private ConversationDbService conversationDbService;
	private LinkedList<Conversation> conversationList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		friendPreference = BaseApplication.getInstance().getFriendPreference();
		conversationDbService = ConversationDbService.getInstance(getActivity());
		conversationList = new LinkedList<Conversation>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_home, container, false);

		findViewById();// 初始化views
		initView();

		conversationList.addAll(conversationDbService.conversationDao.loadAll());
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
		topNavLeftBtn.setImageResource(R.drawable.home);
		topNavRightBtn.setImageResource(R.drawable.ic_action_overflow);
		right_btn_bg.setBackgroundResource(R.drawable.sel_topnav_btn_bg);

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
	}

	/**
	 * 菜单显示
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
