package com.yixianqian.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**   
*    
* ��Ŀ���ƣ�ExamHelper   
* �����ƣ�BaseV4Fragment   
* ��������   �Զ���ļ̳���android.support.v4.app.Fragment�ĳ����࣬ʵ����findViewById��initView�������󷽷����������ʵ�ָ��ǡ�
* �����ˣ���˧  
* ����ʱ�䣺2014-1-5 ����10:35:08   
* �޸��ˣ���˧   
* �޸�ʱ�䣺2014-1-5 ����10:35:08   
* �޸ı�ע��   
* @version    
*    
*/
public abstract class BaseV4Fragment extends Fragment {
	public static final String TAG = BaseV4Fragment.class.getSimpleName();

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
