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
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yixianqian.R;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.db.FlipperDbService;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

/**
 * 类名称：HomeDialogFragment
 * 类描述：主页面聊天界面
 * 创建人： 张帅
 * 创建时间：2014年7月14日 下午3:54:20
 *
 */
public class HomeDialogFragment extends DialogFragment {
	private View rootView;
	private ListView menuitemListView;
	private List<String> menuitemList;
	private int flipperCount = -1;
	private FlipperDbService flipperDbService;
	private UserPreference userPreference;
	private HomeDialogAdapter mAdapter;

	/**
	 * 创建实例
	 * @return
	 */
	static HomeDialogFragment newInstance() {
		HomeDialogFragment f = new HomeDialogFragment();
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setStyle(DialogFragment.STYLE_NO_TITLE, 0);
		flipperDbService = FlipperDbService.getInstance(getActivity());
		userPreference = BaseApplication.getInstance().getUserPreference();
		mAdapter = new HomeDialogAdapter();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		flipperCount = flipperDbService.getFlipperCount();
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_dialog, container, false);
		menuitemListView = (ListView) rootView.findViewById(R.id.dialog_listview);
		menuitemList = new ArrayList<String>();

		menuitemList.add("添加情侣");
		menuitemList.add("爱情验证");

		menuitemListView.setAdapter(mAdapter);
		menuitemListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = null;
				if (position == 1) {
					//如果是单身状态
					if (userPreference.getU_stateid() == 4) {
						intent = new Intent(getActivity(), LoveVertifyActivity.class);
					} else if (userPreference.getU_stateid() == 2) {//如果是情侣
						ToastTool.showLong(getActivity(), "您正在恋爱，不能接受爱情验证哦~~！");
					} else if (userPreference.getU_stateid() == 3) {//如果是情侣
						ToastTool.showLong(getActivity(), "您正在和ta砰然心动，不能贪心哦~~！");
					}
				} else if (position == 0) {
					//如果是单身状态
					if (userPreference.getU_stateid() == 4) {
						intent = new Intent(getActivity(), AddLoverActivity.class);
					} else {
						ToastTool.showLong(getActivity(), "只有单身状态才可以添加情侣哦~！");
					}
				}
				if (intent != null) {
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
					HomeDialogFragment.this.dismiss();
				}
			}
		});
		return rootView;
	}

	/**
	 * 
	 * 类名称：HomeDialogAdapter
	 * 类描述：适配器
	 * 创建人： 张帅
	 * 创建时间：2014年7月14日 下午3:27:58
	 *
	 */
	private class HomeDialogAdapter extends BaseAdapter {
		private class ViewHolder {
			public TextView itemName;
			public TextView righttext;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return menuitemList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = convertView;
			final ViewHolder holder;

			if (convertView == null) {
				view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_listview_item, null);
				holder = new ViewHolder();
				holder.itemName = (TextView) view.findViewById(R.id.item_name);
				holder.righttext = (TextView) view.findViewById(R.id.righttext);
				view.setTag(holder); // 给View添加一个格外的数据 
			} else {
				holder = (ViewHolder) view.getTag(); // 把数据取出来  
			}
			holder.itemName.setText(menuitemList.get(position));

			//如果是爱情验证
			if (position == 1) {
				if (flipperCount > 0) {
					holder.righttext.setText("" + flipperCount);
					holder.righttext.setVisibility(View.VISIBLE);
				} else {
					holder.righttext.setVisibility(View.GONE);
				}
			} else {
				holder.righttext.setVisibility(View.GONE);
			}
			return view;
		}
	}
}
