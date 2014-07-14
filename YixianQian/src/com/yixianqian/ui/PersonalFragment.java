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
import com.yixianqian.base.BaseV4Fragment;
import com.yixianqian.config.DefaultKeys;
import com.yixianqian.utils.ImageLoaderTool;

/**
 * 类名称：PersonalFragment
 * 类描述：个人中心页面
 * 创建人： 张帅
 * 创建时间：2014年7月6日 上午8:51:30
 *
 */
public class PersonalFragment extends BaseV4Fragment {
	/*************Views************/
	private View rootView;// 根View
	private TextView timeCapsuleBtn;//时间胶囊按钮
	private ImageView topNavLeftBtn;//导航条左边按钮
	private ImageView topNavRightBtn;//导航条右边按钮
	private TextView topNavText;//导航条文字
	private View right_btn_bg;
	private ImageView photo;//拍照按钮
	private ImageView tape;//录音
	private View tapeView;//旋转磁带
	private ImageView progressImage1;
	private ImageView progressImage2;
	private ImageView headImageView;
	private int count = 0;
	private Uri takePhotoUri;
	private String takePhotoPath;

	File soundFileDir;//文件目录
	File soundFile;//录音文件
	String soundName = "audio";//文件名称
	MediaRecorder mRecorder;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_personal, container, false);

		findViewById();// 初始化views
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
		headImageView = (ImageView) rootView.findViewById(R.id.head_image);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavLeftBtn.setImageResource(R.drawable.home);
		topNavRightBtn.setImageResource(R.drawable.ic_action_overflow);
		right_btn_bg.setBackgroundResource(R.drawable.sel_topnav_btn_bg);
		topNavText.setText("个人信息");
		initRecorder();

		//设置头像
		imageLoader.displayImage("http://99touxiang.com/public/upload/nvsheng/18/04-072110_356.jpg", headImageView,
				ImageLoaderTool.getHeadImageOptions(10));

		//导航条右侧按钮
		right_btn_bg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog();
			}
		});

		//时间胶囊按钮
		timeCapsuleBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getActivity().startActivity(new Intent(getActivity(), TimeCapsuleActivity.class));
				getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});

		//拍照
		photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showPicturePicker(getActivity());
			}
		});

		//录音
		tape.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (count % 2 == 0) {
					showRecordingTape();
					try {
						mRecorder.prepare();
					} catch (IllegalStateException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mRecorder.start();
				} else {
					shutRecordingTape();
					mRecorder.stop();
					mRecorder.release();
					mRecorder = null;
					String path = null;
					if (soundFile != null && soundFile.exists()) {
						path = soundFile.getAbsolutePath();
					}
					//跳转到PublishTimeCapActivity页面
					Intent intent = new Intent(getActivity(), PublishTimeCapActivity.class);
					intent.putExtra("type", "audio");
					intent.putExtra(DefaultKeys.PHOTO_URI, path);
					startActivity(intent);
				}
				count++;
			}
		});
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub

		super.onDestroy();
	}

	/**
	 * 初始化录音
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

			mRecorder = new MediaRecorder();
			//设置录音来源
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

			//设置录音的输出格式
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

			//设置声音编码格式
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

			mRecorder.setOutputFile(soundFile.getAbsolutePath());
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 菜单显示
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
	* 显示对话框，从拍照和相册选择图片来源
	* 
	* @param context
	* @param isCrop
	*/
	public void showPicturePicker(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("图片来源");
		builder.setItems(new String[] { "拍照", "相册" }, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					String status = Environment.getExternalStorageState();
					if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
						takePhoto();// 用户点击了从照相机获取
					}
					break;
				case 1:
					choosePhoto();// 从相册中去获取
					break;

				default:
					break;
				}
			}
		});
		builder.create().show();
	}

	/**
	 * 从相册选择图片
	 */
	private void choosePhoto() {
		Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, 2);
	}

	/**
	 * 拍照
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
		Intent intent;

		switch (requestCode) {
		case 1://拍照
			intent = new Intent(getActivity(), PublishTimeCapActivity.class);
			intent.putExtra(DefaultKeys.PHOTO_URI, takePhotoPath);
			startActivity(intent);
			break;
		case 2://从相册选择
			try {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null,
						null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();

				//跳转到PublishTimeCapActivity页面
				intent = new Intent(getActivity(), PublishTimeCapActivity.class);
				intent.putExtra("type", "picture");
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
	 * 显示正在旋转的磁带
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
	 * 关闭正在旋转的磁带
	 */
	private void shutRecordingTape() {
		tapeView.setVisibility(View.GONE);
		tape.setImageResource(R.drawable.sel_tabp_btn);
		progressImage1.clearAnimation();
		progressImage2.clearAnimation();
	}

}
