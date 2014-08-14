package com.yixianqian.adapter;

import java.util.Date;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.DateUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yixianqian.R;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.entities.Conversation;
import com.yixianqian.utils.AsyncHttpClientImageSound;
import com.yixianqian.utils.ImageLoaderTool;
import com.yixianqian.utils.ImageTools;

public class HomeListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private LinkedList<Conversation> conversationList;
	private Context mContext;
	public static final Pattern EMOTION_URL = Pattern.compile("\\[(\\S+?)\\]");

	public HomeListAdapter(Context context, ListView listview, LinkedList<Conversation> cList) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		this.conversationList = cList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return conversationList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return conversationList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	private static class ViewHolder {
		/** ��˭�������¼ */
		TextView name;
		/** ��Ϣδ���� */
		TextView unreadLabel;
		/** ���һ����Ϣ������ */
		TextView message;
		/** ���һ����Ϣ��ʱ�� */
		TextView time;
		/** �û�ͷ�� */
		ImageView avatar;
		/** ���һ����Ϣ�ķ���״̬ */
		View msgState;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.home_list_item, null);
		}

		ViewHolder holder = new ViewHolder();

		if (holder != null) {
			holder.name = (TextView) convertView.findViewById(R.id.recent_list_item_name);
			holder.message = (TextView) convertView.findViewById(R.id.recent_list_item_msg);
			holder.unreadLabel = (TextView) convertView.findViewById(R.id.unreadmsg);
			holder.time = (TextView) convertView.findViewById(R.id.recent_list_item_time);
			holder.avatar = (ImageView) convertView.findViewById(R.id.icon);
			holder.msgState = convertView.findViewById(R.id.msg_state);
		}

		Conversation conversation = conversationList.get(position);

		// ��ȡ����û�/Ⱥ��ĻỰ
		EMConversation emCconversation = EMChatManager.getInstance().getConversation("" + conversation.getUserID());

		holder.name.setText(conversation.getName());

		if (emCconversation.getUnreadMsgCount() > 0) {
			// ��ʾ����û�����Ϣδ����
			holder.unreadLabel.setText(String.valueOf(emCconversation.getUnreadMsgCount()));
			holder.unreadLabel.setVisibility(View.VISIBLE);
		} else {
			holder.unreadLabel.setVisibility(View.INVISIBLE);
		}

		if (emCconversation.getMsgCount() != 0) {
			// �����һ����Ϣ��������Ϊitem��message����
			EMMessage lastMessage = emCconversation.getLastMessage();
			holder.message.setText(convertNormalStringToSpannableString(getMessageDigest(lastMessage, mContext)),
					BufferType.SPANNABLE);

			holder.time.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
			if (lastMessage.direct == EMMessage.Direct.SEND && lastMessage.status == EMMessage.Status.FAIL) {
				holder.msgState.setVisibility(View.VISIBLE);
			} else {
				holder.msgState.setVisibility(View.GONE);
			}
		}

		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(AsyncHttpClientImageSound.getAbsoluteUrl(conversation.getSmallAvatar()),
				holder.avatar, ImageLoaderTool.getHeadImageOptions(10));

		return convertView;
	}

	public void remove(int position) {
		if (position < conversationList.size()) {
			conversationList.remove(position);
			notifyDataSetChanged();
		}
	}

	public void remove(Conversation item) {
		if (conversationList.contains(item)) {
			conversationList.remove(item);
			notifyDataSetChanged();
		}
	}

	public void addFirst(Conversation item) {
		if (conversationList.contains(item)) {
			conversationList.remove(item);
		}
		conversationList.addFirst(item);
		notifyDataSetChanged();
	}

	/**
	 * ������Ϣ���ݺ���Ϣ���ͻ�ȡ��Ϣ������ʾ
	 * 
	 * @param message
	 * @param context
	 * @return
	 */
	private String getMessageDigest(EMMessage message, Context context) {
		String digest = "";
		switch (message.getType()) {
		case LOCATION: // λ����Ϣ
			if (message.direct == EMMessage.Direct.RECEIVE) {
				//��sdk���ᵽ��ui�У�ʹ�ø��򵥲�����Ļ�ȡstring����
				//				digest = EasyUtils.getAppResourceString(context, "location_recv");
				digest = getString(context, R.string.location_recv);
				digest = String.format(digest, message.getFrom());
				return digest;
			} else {
				//				digest = EasyUtils.getAppResourceString(context, "location_prefix");
				digest = getString(context, R.string.location_prefix);
			}
			break;
		case IMAGE: // ͼƬ��Ϣ
			ImageMessageBody imageBody = (ImageMessageBody) message.getBody();
			digest = getString(context, R.string.picture) + imageBody.getFileName();
			break;
		case VOICE:// ������Ϣ
			digest = getString(context, R.string.voice);
			break;
		case TXT: // �ı���Ϣ
			TextMessageBody txtBody = (TextMessageBody) message.getBody();
			digest = txtBody.getMessage();
			break;
		default:
			System.err.println("error, unknow type");
			return "";
		}

		return digest;
	}

	/**
	 * ����һ�ַ�����������
	 * @param message
	 * �������Ҫ�����String
	 * @return
	 */
	private CharSequence convertNormalStringToSpannableString(String message) {
		// TODO Auto-generated method stub
		String hackTxt;
		if (message != null) {
			if (message.startsWith("[") && message.endsWith("]")) {
				hackTxt = message + " ";
			} else {
				hackTxt = message;
			}
			SpannableString value = SpannableString.valueOf(hackTxt);

			Matcher localMatcher = EMOTION_URL.matcher(value);
			while (localMatcher.find()) {
				String str2 = localMatcher.group(0);
				int k = localMatcher.start();
				int m = localMatcher.end();
				// k = str2.lastIndexOf("[");
				// Log.i("way", "str2.length = "+str2.length()+", k = " + k);
				// str2 = str2.substring(k, m);
				if (m - k < 8) {
					if (BaseApplication.getInstance().getFaceMap().containsKey(str2)) {
						int face = BaseApplication.getInstance().getFaceMap().get(str2);
						Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), face);
						bitmap = ImageTools.zoomBitmap(bitmap, 0.5f);
						if (bitmap != null) {
							ImageSpan localImageSpan = new ImageSpan(mContext, bitmap, ImageSpan.ALIGN_BOTTOM);
							value.setSpan(localImageSpan, k, m, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
					}
				}
			}
			return value;
		}
		return null;
	}

	String getString(Context context, int resId) {
		return context.getResources().getString(resId);
	}
}
