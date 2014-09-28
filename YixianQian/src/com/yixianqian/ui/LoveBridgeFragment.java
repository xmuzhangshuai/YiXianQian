package com.yixianqian.ui;

import org.apache.http.Header;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yixianqian.R;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.base.BaseV4Fragment;
import com.yixianqian.config.Constants;
import com.yixianqian.table.LoveBridgeItemTable;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.LogTool;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

/**
 * �����ƣ�LoverBridgeFragment
 * ��������ȵ��ҳ�棬ֱ��ת��LoverBridgeActivity
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��9��11�� ����9:43:43
 *
 */
public class LoveBridgeFragment extends BaseV4Fragment implements OnClickListener {
	private View rootView;// ��View
	private View[] mTabs;
	private View publishBtn;//����
	//	private View refreshBtn;//ˢ��
	private TextView schoolNameTextView;
	private int index;
	private int currentTabIndex;
	private UserPreference userPreference;
	private TextView msgCountTextView;
	private int msgCount;

	ViewPager mViewPager;
	LoveBridgePagerAdapter myLoveBridgeAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_love_bridge, container, false);
		myLoveBridgeAdapter = new LoveBridgePagerAdapter(getFragmentManager());
		userPreference = BaseApplication.getInstance().getUserPreference();

		findViewById();// ��ʼ��views
		initView();

		mViewPager.setAdapter(myLoveBridgeAdapter);
		return rootView;
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		mViewPager = (ViewPager) rootView.findViewById(R.id.lover_bridge_pager);
		publishBtn = rootView.findViewById(R.id.publish_btn);
		//		refreshBtn = rootView.findViewById(R.id.refresh_btn);
		schoolNameTextView = (TextView) rootView.findViewById(R.id.school_name);
		msgCountTextView = (TextView) rootView.findViewById(R.id.righttext);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		mTabs = new View[3];
		mTabs[0] = (View) rootView.findViewById(R.id.schoolBtn);
		mTabs[1] = (View) rootView.findViewById(R.id.myBtn);
		mTabs[2] = (View) rootView.findViewById(R.id.msgBtn);
		// �ѵ�һ��tab��Ϊѡ��״̬
		mTabs[0].setSelected(true);

		//��ȡδ����Ϣ����
		getMsgCount();

		schoolNameTextView.setText(userPreference.getSchoolName());
		publishBtn.setOnClickListener(this);
		//		refreshBtn.setOnClickListener(this);
		for (View view : mTabs) {
			view.setOnClickListener(this);
		}

		// viewPager���¼�
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				if (position != currentTabIndex) {
					mTabs[currentTabIndex].setSelected(false);
				}
				currentTabIndex = position;
				mTabs[currentTabIndex].setSelected(true);

				if (position == 2) {
					msgCount = 0;
					msgCountTextView.setVisibility(View.GONE);
				}
			}
		});
	}

	/**
	 * button����¼�
	 * 
	 * @param view
	 */
	public void onTabClicked(View view) {
		switch (view.getId()) {
		case R.id.schoolBtn:
			index = 0;
			break;
		case R.id.myBtn:
			index = 1;
			break;
		case R.id.msgBtn:
			index = 2;
			break;
		}
		if (currentTabIndex != index) {
			mViewPager.setCurrentItem(index, true);
		}
		mTabs[currentTabIndex].setSelected(false);
		// �ѵ�ǰtab��Ϊѡ��״̬
		mTabs[index].setSelected(true);
		currentTabIndex = index;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.publish_btn:
			if (userPreference.getU_stateid() == Constants.UserStateType.SINGLE) {
				startActivity(new Intent(getActivity(), PublishLoveBridgeActivity.class));
				getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}else {
				ToastTool.showLong(getActivity(), "ֻ�е�����˲�����ȵ�Ź㳡�Ϸ�����ϢŶ~~");
			}
			break;

		case R.id.schoolBtn:
			onTabClicked(v);
			break;

		case R.id.myBtn:
			onTabClicked(v);
			break;

		case R.id.msgBtn:
			onTabClicked(v);
			msgCount = 0;
			msgCountTextView.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}

	/**
	 * ��ȡ��Ϣ����
	 */
	private void getMsgCount() {
		RequestParams params = new RequestParams();
		params.put(LoveBridgeItemTable.N_USERID, userPreference.getU_id());
		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				if (statusCode == 200) {
					LogTool.e("LoveBridgeFragment", "δ����Ϣ������" + response);
					if (!TextUtils.isEmpty(response)) {
						msgCount = Integer.parseInt(response);
						if (msgCount > 0) {
							msgCountTextView.setText("" + msgCount);
							msgCountTextView.setVisibility(View.VISIBLE);
						} else {
							msgCountTextView.setVisibility(View.GONE);
						}
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				LogTool.e("LoveBridgeFragment", "��ȡ��Ϣ����ʧ��");
			}
		};
		AsyncHttpClientTool.post(getActivity(), "getunreadbridgemessage", params, responseHandler);
	}

	/**
	 * 
	 * �����ƣ�LoveBridgePagerAdapter
	 * ��������
	 * �����ˣ� ��˧
	 * ����ʱ�䣺2014��9��11�� ����4:41:37
	 *
	 */
	public class LoveBridgePagerAdapter extends FragmentPagerAdapter {

		public LoveBridgePagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Fragment getItem(int position) {
			// TODO Auto-generated method stub
			Fragment fragment = null;

			switch (position) {
			case 0:
				fragment = new LoveBridgeSchoolFragment();
				break;
			case 1:
				fragment = new LoveBridgeMyFragment();
				break;
			case 2:
				fragment = new LoveBridgeMsgFragment();
				break;
			default:
				break;
			}
			return fragment;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 3;
		}
	}

}
