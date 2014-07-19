package com.yixianqian.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.yixianqian.R;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.utils.UserPreference;

/**
 * 类名称：PersonalDialogFragment
 * 类描述：个人中心菜单
 * 创建人： 张帅
 * 创建时间：2014年7月15日 上午9:55:13
 *
 */
public class PersonalDialogFragment extends DialogFragment implements OnItemClickListener {
	private View rootView;
	private ListView menuitemListView;
	private List<String> menuitemList;
	private UserPreference userPreference;

	/**
	 * 创建实例
	 * @return
	 */
	static PersonalDialogFragment newInstance() {
		PersonalDialogFragment f = new PersonalDialogFragment();
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		userPreference = BaseApplication.getInstance().getUserPreference();
		setStyle(DialogFragment.STYLE_NO_TITLE, 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_dialog, container, false);
		menuitemListView = (ListView) rootView.findViewById(R.id.dialog_listview);
		menuitemList = new ArrayList<String>();

		menuitemList.add("编辑资料");
		menuitemList.add("关于");
		menuitemList.add("注销");

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.dialog_listview_item,
				R.id.item_name, menuitemList);
		menuitemListView.setAdapter(adapter);
		menuitemListView.setOnItemClickListener(this);
		return rootView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		if (position == 0) {

		} else if (position == 1) {

		} else if (position == 2) {
			//设置用户不曾登录
			userPreference.setUserLogin(false);
			Intent intent = new Intent(getActivity(),LoginOrRegisterActivity.class);
			getActivity().startActivity(intent);
			getActivity().finish();
		}
	}

}
