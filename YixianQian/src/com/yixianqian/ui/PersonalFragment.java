package com.yixianqian.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yixianqian.R;
import com.yixianqian.base.BaseV4Fragment;

/**
 * �����ƣ�PersonalFragment
 * ����������������ҳ��
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��7��6�� ����8:51:30
 *
 */
public class PersonalFragment extends BaseV4Fragment {
	/*************Views************/
	private View rootView;// ��View
	private TextView timeCapsuleBtn;//ʱ�佺�Ұ�ť
	private ImageView topNavLeftBtn;//��������߰�ť
	private ImageView topNavRightBtn;//�������ұ߰�ť
	private TextView topNavText;//����������
	private View right_btn_bg;
	private ImageView photo;//���հ�ť
	private ImageView tape;//¼��
	private View tapeView;//��ת�Ŵ�
	private ImageView progressImage1;
	private ImageView progressImage2;
	private int count = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_personal, container, false);
		findViewById();// ��ʼ��views
		initView();
		return rootView;
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		timeCapsuleBtn = (TextView) rootView.findViewById(R.id.time_capsule);
		topNavLeftBtn = (ImageView) rootView.findViewById(R.id.nav_left_btn);
		topNavRightBtn = (ImageView) rootView.findViewById(R.id.nav_right_btn);
		right_btn_bg = (View) rootView.findViewById(R.id.right_btn_bg);
		photo = (ImageView) rootView.findViewById(R.id.photo);
		tape = (ImageView) rootView.findViewById(R.id.tape);
		topNavText = (TextView) rootView.findViewById(R.id.nav_text);
		progressImage1 = (ImageView) rootView.findViewById(R.id.progressimage1);
		progressImage2 = (ImageView) rootView.findViewById(R.id.progressimage2);
		tapeView = (View) rootView.findViewById(R.id.tape_view);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavLeftBtn.setImageResource(R.drawable.home);
		topNavRightBtn.setImageResource(R.drawable.ic_action_overflow);
		right_btn_bg.setBackgroundResource(R.drawable.sel_topnav_btn_bg);
		topNavText.setText("������Ϣ");

		//�������Ҳఴť
		right_btn_bg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog();
			}
		});

		//ʱ�佺�Ұ�ť
		timeCapsuleBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});

		//����
		photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});

		//¼��
		tape.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (count % 2 == 0) {
					showRecordingTape();
				} else {
					shutRecordingTape();
				}
				count++;
			}
		});
	}

	/**
	 * �˵���ʾ
	 */
	void showDialog() {

		// DialogFragment.show() will take care of adding the fragment
		// in a transaction.  We also want to remove any currently showing
		// dialog, so make our own transaction and take care of that here.
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		Fragment prev = getFragmentManager().findFragmentByTag("dialog");
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);

		// Create and show the dialog.
		PersonalDialogFragment newFragment = PersonalDialogFragment.newInstance();
		newFragment.show(ft, "dialog");
	}

	/**
	 * ��ʾ������ת�ĴŴ�
	 */
	private void showRecordingTape() {
		tapeView.setVisibility(View.VISIBLE);
		tape.setImageResource(R.drawable.sel_tape_btn_recording);

		final Animation operatingAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.record);
		operatingAnim.setInterpolator(new LinearInterpolator());

		if (operatingAnim != null) {
			progressImage1.startAnimation(operatingAnim);
			progressImage2.startAnimation(operatingAnim);

		}
	}

	/**
	 * �ر�������ת�ĴŴ�
	 */
	private void shutRecordingTape() {
		tapeView.setVisibility(View.GONE);
		tape.setImageResource(R.drawable.sel_tabp_btn);
		progressImage1.clearAnimation();
		progressImage2.clearAnimation();
	}
}
