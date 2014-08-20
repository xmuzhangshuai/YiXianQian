package com.yixianqian.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yixianqian.R;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.base.BaseV4Fragment;
import com.yixianqian.customewidget.MyAlertDialog;
import com.yixianqian.db.FlipperDbService;
import com.yixianqian.utils.FileSizeUtil;
import com.yixianqian.utils.FriendPreference;
import com.yixianqian.utils.UserPreference;

/**
 * 
 * 类名称：SettingMainFragment
 * 类描述：设置主页面
 * 创建人： 张帅
 * 创建时间：2014年8月17日 下午4:26:06
 *
 */
public class SettingMainFragment extends BaseV4Fragment implements OnClickListener {
	private View rootView;// 根View
	private TextView topNavigation;//导航栏文字
	private View leftImageButton;//导航栏左侧按钮
	private View rightBtnBg;//导航栏右侧按钮

	private View settingNewMsg;//新消息提醒
	private View settingChat;//聊天设置
	private View settingClearCache;//清空缓存
	private View settingFeedback;//反馈
	private View settingCheckUpdate;//检查更新
	private View settingAbout;//关于
	private View settingLogout;//退出
	private FragmentTransaction transaction;
	private UserPreference userPreference;
	private FriendPreference friendPreference;
	private TextView cacheSize;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		transaction = getFragmentManager().beginTransaction();
		userPreference = BaseApplication.getInstance().getUserPreference();
		friendPreference = BaseApplication.getInstance().getFriendPreference();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_setting_main, container, false);

		findViewById();
		initView();
		return rootView;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		transaction = null;
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		topNavigation = (TextView) rootView.findViewById(R.id.nav_text);
		leftImageButton = (View) rootView.findViewById(R.id.left_btn_bg);
		rightBtnBg = (View) rootView.findViewById(R.id.right_btn_bg);
		settingNewMsg = rootView.findViewById(R.id.setting_newmsg);
		settingChat = rootView.findViewById(R.id.setting_chat);
		settingClearCache = rootView.findViewById(R.id.setting_clear_cache);
		settingFeedback = rootView.findViewById(R.id.setting_feedback);
		settingCheckUpdate = rootView.findViewById(R.id.setting_check_update);
		settingAbout = rootView.findViewById(R.id.setting_about);
		settingLogout = rootView.findViewById(R.id.setting_logout);
		cacheSize = (TextView) rootView.findViewById(R.id.cache_size);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavigation.setText("设置");
		rightBtnBg.setVisibility(View.GONE);
		leftImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getActivity().finish();
			}
		});

		cacheSize.setText(""
				+ FileSizeUtil.getFileOrFilesSize(imageLoader.getDiskCache().getDirectory().getAbsolutePath(),
						FileSizeUtil.SIZETYPE_MB) + "MB");

		settingNewMsg.setOnClickListener(this);
		settingChat.setOnClickListener(this);
		settingClearCache.setOnClickListener(this);
		settingFeedback.setOnClickListener(this);
		settingCheckUpdate.setOnClickListener(this);
		settingAbout.setOnClickListener(this);
		settingLogout.setOnClickListener(this);
	}

	/**
	 * 退出登录
	 */
	private void logout() {
		//设置用户不曾登录

		final MyAlertDialog myAlertDialog = new MyAlertDialog(getActivity());
		myAlertDialog.setTitle("提示");
		myAlertDialog.setMessage("是否注销用户？");
		View.OnClickListener comfirm = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myAlertDialog.dismiss();

				Intent intent;
				BaseApplication.getInstance().logout();
				userPreference.clear();
				friendPreference.clear();
				FlipperDbService flipperDbService = FlipperDbService.getInstance(getActivity());
				flipperDbService.flipperDao.deleteAll();
				intent = new Intent(getActivity(), LoginOrRegisterActivity.class);
				getActivity().startActivity(intent);
				getActivity().finish();
			}
		};
		View.OnClickListener cancle = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myAlertDialog.dismiss();
			}
		};
		myAlertDialog.setPositiveButton("确定", comfirm);
		myAlertDialog.setNegativeButton("取消", cancle);
		myAlertDialog.show();

	}

	/**
	 * 清楚缓存
	 */
	private void clearCache() {

		final MyAlertDialog myAlertDialog = new MyAlertDialog(getActivity());
		myAlertDialog.setTitle("提示");
		myAlertDialog.setMessage("是否清除缓存？");
		View.OnClickListener comfirm = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myAlertDialog.dismiss();
				imageLoader.clearMemoryCache();
				imageLoader.clearDiskCache();
				cacheSize.setText(""
						+ FileSizeUtil.getFileOrFilesSize(imageLoader.getDiskCache().getDirectory().getAbsolutePath(),
								FileSizeUtil.SIZETYPE_MB) + "MB");
			}
		};
		View.OnClickListener cancle = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myAlertDialog.dismiss();
			}
		};
		myAlertDialog.setPositiveButton("确定", comfirm);
		myAlertDialog.setNegativeButton("取消", cancle);
		myAlertDialog.show();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.setting_newmsg:
			transaction.setCustomAnimations(R.anim.zoomin2, R.anim.zoomout);
			transaction.replace(R.id.container, new SettingNewMsgFragment());
			transaction.addToBackStack("setting");
			transaction.commit();
			break;
		case R.id.setting_chat:
			transaction.setCustomAnimations(R.anim.zoomin2, R.anim.zoomout);
			transaction.replace(R.id.container, new SettingChatFragment());
			transaction.addToBackStack("setting");
			transaction.commit();
			break;
		case R.id.setting_clear_cache:
			clearCache();
			break;
		case R.id.setting_feedback:

			break;
		case R.id.setting_check_update:

			break;
		case R.id.setting_about:

			break;
		case R.id.setting_logout:
			logout();
			break;
		default:
			break;
		}
	}

}
