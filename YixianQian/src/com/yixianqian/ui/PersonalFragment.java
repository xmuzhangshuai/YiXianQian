package com.yixianqian.ui;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.yixianqian.R;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.base.BaseV4Fragment;
import com.yixianqian.config.Constants.UserStateType;
import com.yixianqian.config.Constants;
import com.yixianqian.config.DefaultKeys;
import com.yixianqian.server.ServerUtil;
import com.yixianqian.utils.AsyncHttpClientImageSound;
import com.yixianqian.utils.FriendPreference;
import com.yixianqian.utils.ImageLoaderTool;
import com.yixianqian.utils.UserPreference;

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
	private ImageView smHeadImageView;//����ʱ�ҵ�ͷ��
	private TextView smNnameTextView;//����ʱ�ҵ�����
	private ImageView lmHeadImageView;//����ʷ�ҵ�ͷ��
	private TextView lmNameTextView;//����ʱ�ҵ�����
	private ImageView lHeadImageView;//����ͷ��
	private TextView lNameTextView;//��������
	private TextView provinceTextView;//ʡ��
	private TextView schoolTextView;//ѧУ

	private TextView waitCheckView;
	private Uri takePhotoUri;
	private String takePhotoPath;
	private UserPreference userPreference;
	private FriendPreference friendPreference;
	private View SinglePanel;
	private View LoverPanel;

	private File soundFileDir;//�ļ�Ŀ¼
	private File soundFile;//¼���ļ�
	private String soundName = "audio";//�ļ�����
	private MediaRecorder mRecorder;
	private boolean recording = false;//�Ƿ�����¼��
	private int state;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		userPreference = BaseApplication.getInstance().getUserPreference();
		friendPreference = BaseApplication.getInstance().getFriendPreference();
		state = userPreference.getU_stateid();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_personal, container, false);
		mRecorder = new MediaRecorder();
		findViewById();// ��ʼ��views
		initView();
		return rootView;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		recording = false;
		refreshPersonStatue();
		refreshPersonData();
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
		SinglePanel = rootView.findViewById(R.id.singel_panel);
		LoverPanel = rootView.findViewById(R.id.lover_panel);
		lmHeadImageView = (ImageView) rootView.findViewById(R.id.male_headiamge);
		lmNameTextView = (TextView) rootView.findViewById(R.id.male_name);
		lHeadImageView = (ImageView) rootView.findViewById(R.id.female_headimage);
		lNameTextView = (TextView) rootView.findViewById(R.id.female_name);
		smHeadImageView = (ImageView) rootView.findViewById(R.id.head_image);
		smNnameTextView = (TextView) rootView.findViewById(R.id.name);
		provinceTextView = (TextView) rootView.findViewById(R.id.province);
		schoolTextView = (TextView) rootView.findViewById(R.id.school);
		waitCheckView = (TextView) rootView.findViewById(R.id.waitcheck);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavLeftBtn.setImageResource(R.drawable.home);
		topNavRightBtn.setImageResource(R.drawable.ic_action_overflow);
		right_btn_bg.setBackgroundResource(R.drawable.sel_topnav_btn_bg);

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
				getActivity().startActivity(new Intent(getActivity(), TimeCapsuleActivity.class));
				getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});

		//����
		photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showPicturePicker(getActivity());
			}
		});

		//¼��
		tape.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!recording) {
					initRecorder();
					showRecordingTape();
					mRecorder.start();
					recording = true;
				} else {
					shutRecordingTape();
					mRecorder.stop();
					recording = false;
					String path = null;
					if (soundFile != null && soundFile.exists()) {
						path = soundFile.getAbsolutePath();
					}
					//��ת��PublishTimeCapActivityҳ��
					Intent intent = new Intent(getActivity(), PublishTimeCapActivity.class);
					intent.putExtra("type", "audio");
					intent.putExtra(DefaultKeys.PHOTO_URI, path);
					startActivity(intent);
				}

			}
		});
	}

	/**
	 * ���¸�������
	 */
	public void refreshPersonData() {
		int state = userPreference.getU_stateid();
		if (state == UserStateType.LOVER) {
			SinglePanel.setVisibility(View.GONE);
			LoverPanel.setVisibility(View.VISIBLE);
			//����ͷ��
			if (!TextUtils.isEmpty(userPreference.getU_small_avatar())) {
				ServerUtil.getInstance(getActivity()).disPlayHeadImage(lmHeadImageView, waitCheckView);
			} else {
				//��ȡ��ͷ���ַ
				ServerUtil.getInstance(getActivity()).getHeadImage(lmHeadImageView, waitCheckView);
			}

			if (!TextUtils.isEmpty(userPreference.getU_small_avatar())) {
				//�����ʾ����ͷ��
				lmHeadImageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(getActivity(), ImageShowerActivity.class);
						intent.putExtra(ImageShowerActivity.SHOW_BIG_IMAGE,
								AsyncHttpClientImageSound.getAbsoluteUrl(userPreference.getU_large_avatar()));
						startActivity(intent);
						getActivity().overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
					}
				});
			}
			lmNameTextView.setText(userPreference.getName());//����ʱ�ҵ�����
			lNameTextView.setText(friendPreference.getName());//����ʱ��������
			//����ͷ��
			if (!TextUtils.isEmpty(friendPreference.getF_small_avatar())) {
				imageLoader.displayImage(
						AsyncHttpClientImageSound.getAbsoluteUrl(friendPreference.getF_small_avatar()), lHeadImageView,
						ImageLoaderTool.getHeadImageOptions(10));

				//�����ʾ����ͷ��
				lHeadImageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(getActivity(), PersonDetailActivity.class);
						if (userPreference.getU_stateid() == 3) {//������Ķ�
							intent.putExtra(PersonDetailActivity.PERSON_TYPE, Constants.PersonDetailType.FLIPPER);
						} else if (userPreference.getU_stateid() == 2) {//���������
							intent.putExtra(PersonDetailActivity.PERSON_TYPE, Constants.PersonDetailType.LOVER);
						}
						getActivity().startActivity(intent);
						getActivity().overridePendingTransition(R.anim.zoomin2, R.anim.zoomout);
					}
				});
			}

		} else {
			SinglePanel.setVisibility(View.VISIBLE);
			LoverPanel.setVisibility(View.GONE);

			//����������ʡ�ݡ���ѧУ
			//������ʾ��ʵ����
			smNnameTextView.setText(userPreference.getName());
			provinceTextView.setText(userPreference.getProvinceName());
			schoolTextView.setText(userPreference.getSchoolName());

			//����ͷ��
			if (!TextUtils.isEmpty(userPreference.getU_small_avatar())) {
				ServerUtil.getInstance(getActivity()).disPlayHeadImage(smHeadImageView, waitCheckView);
			} else {
				//��ȡ��ͷ���ַ
				ServerUtil.getInstance(getActivity()).getHeadImage(smHeadImageView, waitCheckView);
			}

			if (!TextUtils.isEmpty(userPreference.getU_small_avatar())) {
				//�����ʾ����ͷ��
				smHeadImageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(getActivity(), ImageShowerActivity.class);
						intent.putExtra(ImageShowerActivity.SHOW_BIG_IMAGE,
								AsyncHttpClientImageSound.getAbsoluteUrl(userPreference.getU_large_avatar()));
						startActivity(intent);
						getActivity().overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
					}
				});
			}
		}
	}

	/**
	 * ���¶����û�״̬
	 */
	public void refreshPersonStatue() {
		int state = userPreference.getU_stateid();
		switch (state) {
		case UserStateType.SINGLE:
			topNavText.setText("��ǰ״̬������");
			break;
		case UserStateType.FLIPPER:
			topNavText.setText("��ǰ״̬���Ķ�");
			break;
		case UserStateType.LOVER:
			topNavText.setText("��ǰ״̬������");
			break;
		case UserStateType.FREEZE:
			topNavText.setText("��ǰ״̬������");
			break;
		default:
			break;
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if (mRecorder != null) {
			mRecorder.release();
			mRecorder = null;
		}
		super.onDestroy();
	}

	/**
	 * ��ʼ��¼��
	 */
	void initRecorder() {
		try {
			soundFileDir = new File(Environment.getExternalStorageDirectory(), "/yixianqian/audio");
			if (!soundFileDir.exists()) {
				soundFileDir.mkdirs();
			}

			soundFile = new File(soundFileDir, soundName + ".amr");
			if (!soundFile.exists()) {
				soundFile.createNewFile();
			}

			//����¼����Դ
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

			//����¼���������ʽ
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

			//�������������ʽ
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

			mRecorder.setOutputFile(soundFile.getAbsolutePath());

			mRecorder.prepare();
		} catch (Exception e) {
			// TODO: handle exception
		}
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
	* ��ʾ�Ի��򣬴����պ����ѡ��ͼƬ��Դ
	* 
	* @param context
	* @param isCrop
	*/
	public void showPicturePicker(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("ͼƬ��Դ");
		builder.setItems(new String[] { "����", "���" }, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					String status = Environment.getExternalStorageState();
					if (status.equals(Environment.MEDIA_MOUNTED)) {// �ж��Ƿ���SD��
						takePhoto();// �û�����˴��������ȡ
					}
					break;
				case 1:
					choosePhoto();// �������ȥ��ȡ
					break;

				default:
					break;
				}
			}
		});
		builder.create().show();
	}

	/**
	 * �����ѡ��ͼƬ
	 */
	private void choosePhoto() {
		Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, 2);
	}

	/**
	 * ����
	 */
	private void takePhoto() {
		try {
			File uploadFileDir = new File(Environment.getExternalStorageDirectory(), "/yixianqian/image");
			Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			if (!uploadFileDir.exists()) {
				uploadFileDir.mkdirs();
			}
			File picFile = new File(uploadFileDir, "yixianqian.jpeg");

			if (!picFile.exists()) {
				picFile.createNewFile();
			}

			takePhotoPath = picFile.getAbsolutePath();
			takePhotoUri = Uri.fromFile(picFile);
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, takePhotoUri);
			startActivityForResult(cameraIntent, 1);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK)
			return;
		Intent intent = new Intent(getActivity(), PublishTimeCapActivity.class);
		intent.putExtra("type", "picture");

		switch (requestCode) {
		case 1://����
			intent.putExtra(DefaultKeys.PHOTO_URI, takePhotoPath);
			startActivity(intent);
			break;
		case 2://�����ѡ��
			try {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null,
						null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();

				//��ת��PublishTimeCapActivityҳ��
				intent.putExtra(DefaultKeys.PHOTO_URI, picturePath);
				startActivity(intent);
			} catch (Exception e) {
				// TODO: handle exception   
				e.printStackTrace();
			}
			break;
		}
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
