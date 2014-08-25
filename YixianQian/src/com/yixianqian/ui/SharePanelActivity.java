package com.yixianqian.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.EmailHandler;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.utils.OauthHelper;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.customewidget.MyAlertDialog;
import com.yixianqian.table.LoverTimeCapsuleTable;
import com.yixianqian.table.SingleTimeCapsuleTable;
import com.yixianqian.utils.AsyncHttpClientTool;
import com.yixianqian.utils.LogTool;
import com.yixianqian.utils.ToastTool;
import com.yixianqian.utils.UserPreference;

/**
 * �����ƣ�SharePanelActivity
 * ���������������
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��8��25�� ����9:11:41
 *
 */
public class SharePanelActivity extends BaseActivity {
	public static final int REQUEST_CODE_SHAREPANEL = 3;
	public static final int RESULT_CODE_DELETE = 4;
	public static final String IMAGE_URL = "iamge_url";

	private int timeCapsulePosition = -1;
	private UserPreference userPreference;
	private View shareView;
	private int stateID = 0;
	private int msgID = -1;
	private int userID = -1;
	private int loverID = -1;
	final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
	private String imageUrl;
	private boolean showShare = false;

	GridView sharePanelGridView;
	private int[] shareItemDrawable = { R.drawable.weixin_popover, R.drawable.weinxinpengyou_popover,
			R.drawable.qq_popover, R.drawable.qzone_popover, R.drawable.weibo_popover, R.drawable.qq_weibo_popover,
			R.drawable.mail_popover, };
	private String[] shareItemName = { "΢�ź���", "΢������Ȧ", "QQ����", "QQ�ռ�", "����΢��", "��Ѷ΢��", "�ʼ�", };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_panel);

		userPreference = BaseApplication.getInstance().getUserPreference();
		stateID = userPreference.getU_stateid();
		timeCapsulePosition = getIntent().getIntExtra("position", -1);
		showShare = getIntent().getBooleanExtra("showShare", false);
		if (stateID == 2) {
			msgID = getIntent().getIntExtra(LoverTimeCapsuleTable.LTC_MSGID, -1);
			userID = getIntent().getIntExtra(LoverTimeCapsuleTable.LTC_USERID, -1);
			loverID = getIntent().getIntExtra(LoverTimeCapsuleTable.LTC_LOVERID, -1);
		} else if (stateID == 3 || stateID == 4) {
			msgID = getIntent().getIntExtra(SingleTimeCapsuleTable.STC_MSGID, -1);
			userID = getIntent().getIntExtra(SingleTimeCapsuleTable.STC_USERID, -1);
		}

		findViewById();
		initView();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		sharePanelGridView = (GridView) findViewById(R.id.share_panel);
		shareView = findViewById(R.id.share_view);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		if (showShare) {
			initSharePanel();
			initShareContent();
		} else {
			shareView.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/**ʹ��SSO��Ȩ����������´��� */
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	public void delete(View view) {
		final MyAlertDialog myAlertDialog = new MyAlertDialog(this);
		myAlertDialog.setTitle("��ʾ");
		myAlertDialog.setMessage("�Ƿ�ɾ��ʱ�佺�Ҹ�����¼��");
		View.OnClickListener comfirm = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myAlertDialog.dismiss();
				deleteTimeCapsule();
			}
		};
		View.OnClickListener cancle = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myAlertDialog.dismiss();
			}
		};
		myAlertDialog.setPositiveButton("ȷ��", comfirm);
		myAlertDialog.setNegativeButton("ȡ��", cancle);
		myAlertDialog.show();

	}

	public void cancel(View view) {
		finish();
	}

	/**
	 * ��ʼ���������
	 */
	private void initSharePanel() {
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < shareItemName.length; i++) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			listItem.put("image", shareItemDrawable[i]);
			listItem.put("name", shareItemName[i]);
			listItems.add(listItem);
		}

		SimpleAdapter adapter = new SimpleAdapter(SharePanelActivity.this, listItems, R.layout.share_item,
				new String[] { "image", "name" }, new int[] { R.id.share_item_image, R.id.share_item_name });
		sharePanelGridView.setAdapter(adapter);
		sharePanelGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0://΢�ź���

					break;
				case 1://΢������Ȧ

					break;
				case 2://QQ����
					shareToQQ();
					break;
				case 3://QQ�ռ�
					shareToQZone();
					break;
				case 4://����΢��
					shareToSina();
					break;
				case 5://��Ѷ΢��
					shareToTengXunWB();
					break;
				case 6://�ʼ�
					shareToMail();
					break;
				default:
					break;
				}
			}
		});

		// wx967daebe835fbeac������΢�ſ���ƽ̨ע��Ӧ�õ�AppID, ������Ҫ�滻����ע���AppID
		String appID = "wx967daebe835fbeac";
		// ���΢��ƽ̨
		UMWXHandler wxHandler = new UMWXHandler(this, appID);
		wxHandler.addToSocialSDK();
		// ֧��΢������Ȧ
		UMWXHandler wxCircleHandler = new UMWXHandler(this, appID);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();

		//����1Ϊ��ǰActivity������2Ϊ��������QQ���������APP ID������3Ϊ��������QQ���������APP kEY.
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, "100424468", "c7394704798a158208a74ab60104f0ba");
		qqSsoHandler.addToSocialSDK();

		//����1Ϊ��ǰActivity������2Ϊ��������QQ���������APP ID������3Ϊ��������QQ���������APP kEY.
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, "100424468", "c7394704798a158208a74ab60104f0ba");
		qZoneSsoHandler.addToSocialSDK();

		//��������SSO handler
		mController.getConfig().setSsoHandler(new SinaSsoHandler());

		//������Ѷ΢��SSO handler
		mController.getConfig().setSsoHandler(new TencentWBSsoHandler());

		// ���email
		EmailHandler emailHandler = new EmailHandler();
		emailHandler.addToSocialSDK();
	}

	/**
	 * ��ʼ����������
	 */
	private void initShareContent() {
		imageUrl = getIntent().getStringExtra(imageUrl);
		if (!TextUtils.isEmpty(imageUrl)) {
			// ���÷�������
			mController.setShareContent("������ữ�����SDK�����ƶ�Ӧ�ÿ��������罻�����ܣ�http://www.umeng.com/social");
			// ���÷���ͼƬ, ����2ΪͼƬ��url��ַ
			mController.setShareMedia(new UMImage(SharePanelActivity.this, imageUrl));
		}
	}

	/**
	 * ��������΢��
	 */
	private void shareToSina() {
		if (OauthHelper.isAuthenticated(SharePanelActivity.this, SHARE_MEDIA.SINA)) {
			//ֱ�ӷ���
			mController.directShare(SharePanelActivity.this, SHARE_MEDIA.SINA, new SnsPostListener() {

				@Override
				public void onStart() {
					LogTool.d("share", "SINA����ʼ");
				}

				@Override
				public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
					if (eCode == StatusCode.ST_CODE_SUCCESSED) {
						LogTool.d("share", "SINA����ɹ�");
					} else {
						LogTool.e("share", "SINA����ʧ��");
					}
				}
			});
		} else {
			//ȡ����Ȩ
			mController.doOauthVerify(SharePanelActivity.this, SHARE_MEDIA.SINA, new UMAuthListener() {
				@Override
				public void onStart(SHARE_MEDIA platform) {
					LogTool.d("share", "SINA��Ȩ��ʼ");
				}

				@Override
				public void onError(SocializeException e, SHARE_MEDIA platform) {
					LogTool.e("share", "SINA��Ȩ����");
				}

				@Override
				public void onComplete(Bundle value, SHARE_MEDIA platform) {
					LogTool.d("share", "SINA��Ȩ���");
					//��ȡ�����Ȩ��Ϣ������ת���Զ���ķ���༭ҳ��
					String uid = value.getString("uid");
					shareToSina();
				}

				@Override
				public void onCancel(SHARE_MEDIA platform) {
					LogTool.d("share", "SINA��Ȩȡ��");
				}
			});
		}
	}

	/**
	 * ����QQ����
	 */
	private void shareToQQ() {
		if (OauthHelper.isAuthenticated(SharePanelActivity.this, SHARE_MEDIA.QQ)) {
			//ֱ�ӷ���
			mController.directShare(SharePanelActivity.this, SHARE_MEDIA.QQ, new SnsPostListener() {

				@Override
				public void onStart() {
					LogTool.d("share", "QQ����ʼ");
				}

				@Override
				public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
					if (eCode == StatusCode.ST_CODE_SUCCESSED) {
						LogTool.d("share", "QQ����ɹ�");
					} else {
						LogTool.e("share", "QQ����ʧ��");
					}
				}
			});
		} else {
			//ȡ����Ȩ
			mController.doOauthVerify(SharePanelActivity.this, SHARE_MEDIA.QQ, new UMAuthListener() {
				@Override
				public void onStart(SHARE_MEDIA platform) {
					LogTool.d("share", "QQ��Ȩ��ʼ");
				}

				@Override
				public void onError(SocializeException e, SHARE_MEDIA platform) {
					LogTool.e("share", "QQ��Ȩ����");
				}

				@Override
				public void onComplete(Bundle value, SHARE_MEDIA platform) {
					LogTool.d("share", "QQ��Ȩ���");
					//��ȡ�����Ȩ��Ϣ������ת���Զ���ķ���༭ҳ��
					String uid = value.getString("uid");
					shareToQQ();
				}

				@Override
				public void onCancel(SHARE_MEDIA platform) {
					LogTool.d("share", "QQ��Ȩȡ��");
				}
			});
		}
	}

	/**
	 * ����QQ�ռ�
	 */
	private void shareToQZone() {
		if (OauthHelper.isAuthenticated(SharePanelActivity.this, SHARE_MEDIA.QZONE)) {
			//ֱ�ӷ���
			mController.directShare(SharePanelActivity.this, SHARE_MEDIA.QZONE, new SnsPostListener() {

				@Override
				public void onStart() {
					LogTool.d("share", "QZONE����ʼ");
				}

				@Override
				public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
					if (eCode == StatusCode.ST_CODE_SUCCESSED) {
						LogTool.d("share", "QZONE����ɹ�");
					} else {
						LogTool.e("share", "QZONE����ʧ��");
					}
				}
			});
		} else {
			//ȡ����Ȩ
			mController.doOauthVerify(SharePanelActivity.this, SHARE_MEDIA.QZONE, new UMAuthListener() {
				@Override
				public void onStart(SHARE_MEDIA platform) {
					LogTool.d("share", "QZONE��Ȩ��ʼ");
				}

				@Override
				public void onError(SocializeException e, SHARE_MEDIA platform) {
					LogTool.e("share", "QZONE��Ȩ����");
				}

				@Override
				public void onComplete(Bundle value, SHARE_MEDIA platform) {
					LogTool.d("share", "QZONE��Ȩ���");
					//��ȡ�����Ȩ��Ϣ������ת���Զ���ķ���༭ҳ��
					String uid = value.getString("uid");
					shareToQZone();
				}

				@Override
				public void onCancel(SHARE_MEDIA platform) {
					LogTool.d("share", "QZONE��Ȩȡ��");
				}
			});
		}
	}

	/**
	 * ������Ѷ΢��
	 */
	private void shareToTengXunWB() {
		if (OauthHelper.isAuthenticated(SharePanelActivity.this, SHARE_MEDIA.TENCENT)) {
			//ֱ�ӷ���
			mController.directShare(SharePanelActivity.this, SHARE_MEDIA.TENCENT, new SnsPostListener() {

				@Override
				public void onStart() {
					LogTool.d("share", "TENCENT����ʼ");
				}

				@Override
				public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
					if (eCode == StatusCode.ST_CODE_SUCCESSED) {
						LogTool.d("share", "TENCENT����ɹ�");
					} else {
						LogTool.e("share", "TENCENT����ʧ��");
					}
				}
			});
		} else {
			//ȡ����Ȩ
			mController.doOauthVerify(SharePanelActivity.this, SHARE_MEDIA.TENCENT, new UMAuthListener() {
				@Override
				public void onStart(SHARE_MEDIA platform) {
					LogTool.d("share", "TENCENT��Ȩ��ʼ");
				}

				@Override
				public void onError(SocializeException e, SHARE_MEDIA platform) {
					LogTool.e("share", "TENCENT��Ȩ����");
				}

				@Override
				public void onComplete(Bundle value, SHARE_MEDIA platform) {
					LogTool.d("share", "TENCENT��Ȩ���");
					//��ȡ�����Ȩ��Ϣ������ת���Զ���ķ���༭ҳ��
					String uid = value.getString("uid");
					shareToTengXunWB();
				}

				@Override
				public void onCancel(SHARE_MEDIA platform) {
					LogTool.d("share", "TENCENT��Ȩȡ��");
				}
			});
		}
	}

	/**
	 * �����ʼ�
	 */
	private void shareToMail() {
		// ����1ΪContext���Ͷ��� ����2ΪҪ������Ŀ��ƽ̨�� ����3Ϊ��������Ļص��ӿ�
		if (OauthHelper.isAuthenticated(SharePanelActivity.this, SHARE_MEDIA.EMAIL)) {
			//ֱ�ӷ���
			mController.directShare(SharePanelActivity.this, SHARE_MEDIA.EMAIL, new SnsPostListener() {

				@Override
				public void onStart() {
					LogTool.d("share", "EMAIL����ʼ");
				}

				@Override
				public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
					if (eCode == StatusCode.ST_CODE_SUCCESSED) {
						LogTool.d("share", "EMAIL����ɹ�");
					} else {
						LogTool.e("share", "EMAIL����ʧ��");
					}
				}
			});
		} else {
			//ȡ����Ȩ
			mController.doOauthVerify(SharePanelActivity.this, SHARE_MEDIA.EMAIL, new UMAuthListener() {
				@Override
				public void onStart(SHARE_MEDIA platform) {
					LogTool.d("share", "EMAIL��Ȩ��ʼ");
				}

				@Override
				public void onError(SocializeException e, SHARE_MEDIA platform) {
					LogTool.e("share", "EMAIL��Ȩ����");
				}

				@Override
				public void onComplete(Bundle value, SHARE_MEDIA platform) {
					LogTool.d("share", "EMAIL��Ȩ���");
					//��ȡ�����Ȩ��Ϣ������ת���Զ���ķ���༭ҳ��
					String uid = value.getString("uid");
					shareToMail();
				}

				@Override
				public void onCancel(SHARE_MEDIA platform) {
					LogTool.d("share", "EMAIL��Ȩȡ��");
				}
			});
		}
	}

	/**
	 * ɾ����¼
	 */
	private void deleteTimeCapsule() {
		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				setResult(RESULT_CODE_DELETE, new Intent().putExtra("position", timeCapsulePosition));
				finish();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				ToastTool.showShort(BaseApplication.getInstance(), "ɾ��ʧ��");
			}
		};
		RequestParams params = new RequestParams();
		if (stateID == 2) {
			params.put(LoverTimeCapsuleTable.LTC_MSGID, msgID);
			params.put(LoverTimeCapsuleTable.LTC_USERID, userID);
			params.put(LoverTimeCapsuleTable.LTC_LOVERID, loverID);
			AsyncHttpClientTool.post("deleteloverrecord", params, responseHandler);
		} else if (stateID == 3 || stateID == 4) {
			params.put(SingleTimeCapsuleTable.STC_MSGID, msgID);
			params.put(SingleTimeCapsuleTable.STC_USERID, userID);
			AsyncHttpClientTool.post("deletesinglerecord", params, responseHandler);
		}
	}
}
