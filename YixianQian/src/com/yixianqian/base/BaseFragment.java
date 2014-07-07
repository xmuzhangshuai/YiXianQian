package com.yixianqian.base;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**   
*    
* ��Ŀ���ƣ�ExamHelper   
* �����ƣ�BaseFragment   
* ��������   �Զ���ļ̳���android.app.Fragment�ĳ����࣬ʵ����findViewById��initView�������󷽷����������ʵ�ָ���
* �����ˣ���˧  
* ����ʱ�䣺2014-1-5 ����10:33:44   
* �޸��ˣ���˧   
* �޸�ʱ�䣺2014-1-5 ����10:33:44   
* �޸ı�ע��   
* @version    
*    
*/
public abstract class BaseFragment extends Fragment {
	public static final String TAG = BaseFragment.class.getSimpleName();

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	/**
	 * �󶨿ؼ�id
	 */
	protected abstract void findViewById();

	/**
	 * ��ʼ���ؼ�
	 */
	protected abstract void initView();
}
