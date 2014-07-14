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

/**
 * �����ƣ�HomeDialogFragment
 * ����������ҳ���������
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��7��14�� ����3:54:20
 *
 */
public class HomeDialogFragment extends DialogFragment {
	private View rootView;
	private ListView menuitemListView;
	private List<String> menuitemList;

	/**
	 * ����ʵ��
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
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_dialog, container, false);
		menuitemListView = (ListView) rootView.findViewById(R.id.dialog_listview);
		menuitemList = new ArrayList<String>();

		menuitemList.add("��������");
		menuitemList.add("������֤");

		menuitemListView.setAdapter(new HomeDialogAdapter());
		menuitemListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				if (position == 1) {
					Intent intent = new Intent(getActivity(), LoveVertifyActivity.class);
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				}
			}
		});
		return rootView;
	}

	/**
	 * 
	 * �����ƣ�HomeDialogAdapter
	 * ��������������
	 * �����ˣ� ��˧
	 * ����ʱ�䣺2014��7��14�� ����3:27:58
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
				view.setTag(holder); // ��View���һ����������� 
			} else {
				holder = (ViewHolder) view.getTag(); // ������ȡ����  
			}
			holder.itemName.setText(menuitemList.get(position));

			//����ǰ�����֤
			if (position == 1) {
				holder.righttext.setText("2");
				holder.righttext.setVisibility(View.VISIBLE);
			} else {
				holder.righttext.setVisibility(View.GONE);
			}
			return view;
		}
	}
}
