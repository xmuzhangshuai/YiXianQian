package com.yixianqian.ui;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yixianqian.R;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.config.Constants;
import com.yixianqian.utils.UserPreference;

/**
 * �����ƣ�ConstellDialogFragment
 * ��������ѡ�������Ի���
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��7��28�� ����9:59:55
 *
 */
public class ConstellDialogFragment extends DialogFragment {
	private View rootView;
	private ListView menuitemListView;
	private List<String> menuitemList;
	private OnConstellChangedListener onConstellChangedListener;

	public interface OnConstellChangedListener {
		public void onConstellChaged(String constell);
	}

	/**
	 * ����ʵ��
	 * @return
	 */
	static ConstellDialogFragment newInstance() {
		ConstellDialogFragment f = new ConstellDialogFragment();
		return f;
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		onConstellChangedListener = (OnConstellChangedListener) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_TITLE, 0);
		menuitemList = Constants.Constell.getConstellList();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_dialog, container, false);
		menuitemListView = (ListView) rootView.findViewById(R.id.dialog_listview);
		View view = rootView.findViewById(R.id.divider);
		TextView title = (TextView) rootView.findViewById(R.id.title);
		title.setText("����");
		view.setVisibility(View.VISIBLE);
		title.setVisibility(View.VISIBLE);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.dialog_listview_item,
				R.id.item_name, menuitemList);
		menuitemListView.setAdapter(adapter);

		menuitemListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				ConstellDialogFragment.this.dismiss();
				onConstellChangedListener.onConstellChaged(menuitemList.get(position));
			}
		});

		return rootView;
	}

}
