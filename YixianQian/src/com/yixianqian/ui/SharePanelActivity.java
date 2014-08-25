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
 * 类名称：SharePanelActivity
 * 类描述：分享面板
 * 创建人： 张帅
 * 创建时间：2014年8月25日 上午9:11:41
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
	private String[] shareItemName = { "微信好友", "微信朋友圈", "QQ好友", "QQ空间", "新浪微博", "腾讯微博", "邮件", };

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
		/**使用SSO授权必须添加如下代码 */
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	public void delete(View view) {
		final MyAlertDialog myAlertDialog = new MyAlertDialog(this);
		myAlertDialog.setTitle("提示");
		myAlertDialog.setMessage("是否删除时间胶囊该条记录？");
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
		myAlertDialog.setPositiveButton("确定", comfirm);
		myAlertDialog.setNegativeButton("取消", cancle);
		myAlertDialog.show();

	}

	public void cancel(View view) {
		finish();
	}

	/**
	 * 初始化分享面板
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
				case 0://微信好友

					break;
				case 1://微信朋友圈

					break;
				case 2://QQ好友
					shareToQQ();
					break;
				case 3://QQ空间
					shareToQZone();
					break;
				case 4://新浪微博
					shareToSina();
					break;
				case 5://腾讯微博
					shareToTengXunWB();
					break;
				case 6://邮件
					shareToMail();
					break;
				default:
					break;
				}
			}
		});

		// wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
		String appID = "wx967daebe835fbeac";
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(this, appID);
		wxHandler.addToSocialSDK();
		// 支持微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(this, appID);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();

		//参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, "100424468", "c7394704798a158208a74ab60104f0ba");
		qqSsoHandler.addToSocialSDK();

		//参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, "100424468", "c7394704798a158208a74ab60104f0ba");
		qZoneSsoHandler.addToSocialSDK();

		//设置新浪SSO handler
		mController.getConfig().setSsoHandler(new SinaSsoHandler());

		//设置腾讯微博SSO handler
		mController.getConfig().setSsoHandler(new TencentWBSsoHandler());

		// 添加email
		EmailHandler emailHandler = new EmailHandler();
		emailHandler.addToSocialSDK();
	}

	/**
	 * 初始化分享内容
	 */
	private void initShareContent() {
		imageUrl = getIntent().getStringExtra(imageUrl);
		if (!TextUtils.isEmpty(imageUrl)) {
			// 设置分享内容
			mController.setShareContent("友盟社会化组件（SDK）让移动应用快速整合社交分享功能，http://www.umeng.com/social");
			// 设置分享图片, 参数2为图片的url地址
			mController.setShareMedia(new UMImage(SharePanelActivity.this, imageUrl));
		}
	}

	/**
	 * 分享到新浪微博
	 */
	private void shareToSina() {
		if (OauthHelper.isAuthenticated(SharePanelActivity.this, SHARE_MEDIA.SINA)) {
			//直接分享
			mController.directShare(SharePanelActivity.this, SHARE_MEDIA.SINA, new SnsPostListener() {

				@Override
				public void onStart() {
					LogTool.d("share", "SINA分享开始");
				}

				@Override
				public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
					if (eCode == StatusCode.ST_CODE_SUCCESSED) {
						LogTool.d("share", "SINA分享成功");
					} else {
						LogTool.e("share", "SINA分享失败");
					}
				}
			});
		} else {
			//取得授权
			mController.doOauthVerify(SharePanelActivity.this, SHARE_MEDIA.SINA, new UMAuthListener() {
				@Override
				public void onStart(SHARE_MEDIA platform) {
					LogTool.d("share", "SINA授权开始");
				}

				@Override
				public void onError(SocializeException e, SHARE_MEDIA platform) {
					LogTool.e("share", "SINA授权错误");
				}

				@Override
				public void onComplete(Bundle value, SHARE_MEDIA platform) {
					LogTool.d("share", "SINA授权完成");
					//获取相关授权信息或者跳转到自定义的分享编辑页面
					String uid = value.getString("uid");
					shareToSina();
				}

				@Override
				public void onCancel(SHARE_MEDIA platform) {
					LogTool.d("share", "SINA授权取消");
				}
			});
		}
	}

	/**
	 * 分享到QQ好友
	 */
	private void shareToQQ() {
		if (OauthHelper.isAuthenticated(SharePanelActivity.this, SHARE_MEDIA.QQ)) {
			//直接分享
			mController.directShare(SharePanelActivity.this, SHARE_MEDIA.QQ, new SnsPostListener() {

				@Override
				public void onStart() {
					LogTool.d("share", "QQ分享开始");
				}

				@Override
				public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
					if (eCode == StatusCode.ST_CODE_SUCCESSED) {
						LogTool.d("share", "QQ分享成功");
					} else {
						LogTool.e("share", "QQ分享失败");
					}
				}
			});
		} else {
			//取得授权
			mController.doOauthVerify(SharePanelActivity.this, SHARE_MEDIA.QQ, new UMAuthListener() {
				@Override
				public void onStart(SHARE_MEDIA platform) {
					LogTool.d("share", "QQ授权开始");
				}

				@Override
				public void onError(SocializeException e, SHARE_MEDIA platform) {
					LogTool.e("share", "QQ授权错误");
				}

				@Override
				public void onComplete(Bundle value, SHARE_MEDIA platform) {
					LogTool.d("share", "QQ授权完成");
					//获取相关授权信息或者跳转到自定义的分享编辑页面
					String uid = value.getString("uid");
					shareToQQ();
				}

				@Override
				public void onCancel(SHARE_MEDIA platform) {
					LogTool.d("share", "QQ授权取消");
				}
			});
		}
	}

	/**
	 * 分享到QQ空间
	 */
	private void shareToQZone() {
		if (OauthHelper.isAuthenticated(SharePanelActivity.this, SHARE_MEDIA.QZONE)) {
			//直接分享
			mController.directShare(SharePanelActivity.this, SHARE_MEDIA.QZONE, new SnsPostListener() {

				@Override
				public void onStart() {
					LogTool.d("share", "QZONE分享开始");
				}

				@Override
				public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
					if (eCode == StatusCode.ST_CODE_SUCCESSED) {
						LogTool.d("share", "QZONE分享成功");
					} else {
						LogTool.e("share", "QZONE分享失败");
					}
				}
			});
		} else {
			//取得授权
			mController.doOauthVerify(SharePanelActivity.this, SHARE_MEDIA.QZONE, new UMAuthListener() {
				@Override
				public void onStart(SHARE_MEDIA platform) {
					LogTool.d("share", "QZONE授权开始");
				}

				@Override
				public void onError(SocializeException e, SHARE_MEDIA platform) {
					LogTool.e("share", "QZONE授权错误");
				}

				@Override
				public void onComplete(Bundle value, SHARE_MEDIA platform) {
					LogTool.d("share", "QZONE授权完成");
					//获取相关授权信息或者跳转到自定义的分享编辑页面
					String uid = value.getString("uid");
					shareToQZone();
				}

				@Override
				public void onCancel(SHARE_MEDIA platform) {
					LogTool.d("share", "QZONE授权取消");
				}
			});
		}
	}

	/**
	 * 分享到腾讯微博
	 */
	private void shareToTengXunWB() {
		if (OauthHelper.isAuthenticated(SharePanelActivity.this, SHARE_MEDIA.TENCENT)) {
			//直接分享
			mController.directShare(SharePanelActivity.this, SHARE_MEDIA.TENCENT, new SnsPostListener() {

				@Override
				public void onStart() {
					LogTool.d("share", "TENCENT分享开始");
				}

				@Override
				public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
					if (eCode == StatusCode.ST_CODE_SUCCESSED) {
						LogTool.d("share", "TENCENT分享成功");
					} else {
						LogTool.e("share", "TENCENT分享失败");
					}
				}
			});
		} else {
			//取得授权
			mController.doOauthVerify(SharePanelActivity.this, SHARE_MEDIA.TENCENT, new UMAuthListener() {
				@Override
				public void onStart(SHARE_MEDIA platform) {
					LogTool.d("share", "TENCENT授权开始");
				}

				@Override
				public void onError(SocializeException e, SHARE_MEDIA platform) {
					LogTool.e("share", "TENCENT授权错误");
				}

				@Override
				public void onComplete(Bundle value, SHARE_MEDIA platform) {
					LogTool.d("share", "TENCENT授权完成");
					//获取相关授权信息或者跳转到自定义的分享编辑页面
					String uid = value.getString("uid");
					shareToTengXunWB();
				}

				@Override
				public void onCancel(SHARE_MEDIA platform) {
					LogTool.d("share", "TENCENT授权取消");
				}
			});
		}
	}

	/**
	 * 分享到邮件
	 */
	private void shareToMail() {
		// 参数1为Context类型对象， 参数2为要分享到的目标平台， 参数3为分享操作的回调接口
		if (OauthHelper.isAuthenticated(SharePanelActivity.this, SHARE_MEDIA.EMAIL)) {
			//直接分享
			mController.directShare(SharePanelActivity.this, SHARE_MEDIA.EMAIL, new SnsPostListener() {

				@Override
				public void onStart() {
					LogTool.d("share", "EMAIL分享开始");
				}

				@Override
				public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
					if (eCode == StatusCode.ST_CODE_SUCCESSED) {
						LogTool.d("share", "EMAIL分享成功");
					} else {
						LogTool.e("share", "EMAIL分享失败");
					}
				}
			});
		} else {
			//取得授权
			mController.doOauthVerify(SharePanelActivity.this, SHARE_MEDIA.EMAIL, new UMAuthListener() {
				@Override
				public void onStart(SHARE_MEDIA platform) {
					LogTool.d("share", "EMAIL授权开始");
				}

				@Override
				public void onError(SocializeException e, SHARE_MEDIA platform) {
					LogTool.e("share", "EMAIL授权错误");
				}

				@Override
				public void onComplete(Bundle value, SHARE_MEDIA platform) {
					LogTool.d("share", "EMAIL授权完成");
					//获取相关授权信息或者跳转到自定义的分享编辑页面
					String uid = value.getString("uid");
					shareToMail();
				}

				@Override
				public void onCancel(SHARE_MEDIA platform) {
					LogTool.d("share", "EMAIL授权取消");
				}
			});
		}
	}

	/**
	 * 删除记录
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
				ToastTool.showShort(BaseApplication.getInstance(), "删除失败");
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
