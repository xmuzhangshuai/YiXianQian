package com.yixianqian.adapter;

import java.util.List;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yixianqian.R;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.entities.Conversation;
import com.yixianqian.entities.MessageItem;
import com.yixianqian.utils.DateTimeTools;
import com.yixianqian.utils.ImageLoaderTool;
import com.yixianqian.utils.UserPreference;

/**
 * 类名称：MessageAdapter
 * 类描述：消息列表适配器
 * 创建人： 张帅
 * 创建时间：2014年7月18日 下午5:38:34
 *
 */
public class MessageAdapter extends BaseAdapter {
	public static final Pattern EMOTION_URL = Pattern.compile("\\[(\\S+?)\\]");

	private Context mContext;
	private LayoutInflater mInflater;
	private List<MessageItem> mMsgList;
	private UserPreference userPreference;
	private ImageLoader imageLoader;
	private Conversation conversation;

	public MessageAdapter(Context context, List<MessageItem> msgList, Conversation conversation) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		mMsgList = msgList;
		mInflater = LayoutInflater.from(context);
		userPreference = BaseApplication.getInstance().getUserPreference();
		imageLoader = ImageLoader.getInstance();
		this.conversation = conversation;
	}

	public void removeHeadMsg() {
		if (mMsgList.size() - 10 > 10) {
			for (int i = 0; i < 10; i++) {
				mMsgList.remove(i);
			}
			notifyDataSetChanged();
		}
	}

	public void setMessageList(List<MessageItem> msgList) {
		mMsgList = msgList;
		notifyDataSetChanged();
	}

	public void upDateMsg(MessageItem msg) {
		mMsgList.add(msg);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mMsgList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mMsgList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		MessageItem item = mMsgList.get(position);
		boolean isComMsg = item.getIsCome();
		ViewHolder holder;

		if (convertView == null || convertView.getTag(R.drawable.ic_launcher + position) == null) {
			holder = new ViewHolder();
			if (isComMsg) {
				convertView = mInflater.inflate(R.layout.chat_item_left, null);
			} else {
				convertView = mInflater.inflate(R.layout.chat_item_right, null);
			}
			holder.head = (ImageView) convertView.findViewById(R.id.icon);
			holder.time = (TextView) convertView.findViewById(R.id.datetime);
			holder.msg = (TextView) convertView.findViewById(R.id.textView2);
			holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar1);
			convertView.setTag(R.drawable.ic_launcher + position);
		} else {
			holder = (ViewHolder) convertView.getTag(R.drawable.ic_launcher + position);
		}
		holder.time.setText(DateTimeTools.getChatTime(item.getTime()));
		holder.time.setVisibility(View.VISIBLE);
		if (isComMsg) {
			if (conversation != null) {
				imageLoader.displayImage(conversation.getSmallAvatar(), holder.head,
						ImageLoaderTool.getHeadImageOptions(10));
			}
		} else {
			imageLoader.displayImage(userPreference.getU_small_avatar(), holder.head,
					ImageLoaderTool.getHeadImageOptions(10));
		}

		holder.msg.setText(convertNormalStringToSpannableString(item.getMsgContent()), BufferType.SPANNABLE);
		holder.progressBar.setVisibility(View.GONE);
		holder.progressBar.setProgress(50);
		return convertView;
	}

	/**
	 * 另外一种方法解析表情
	 * @param message
	 * 传入的需要处理的String
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
						if (bitmap != null) {
							ImageSpan localImageSpan = new ImageSpan(mContext, bitmap, ImageSpan.ALIGN_BASELINE);
							value.setSpan(localImageSpan, k, m, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
					}
				}
			}
			return value;
		}
		return null;

	}

	static class ViewHolder {
		ImageView head;
		TextView time;
		TextView msg;
		ImageView imageView;
		ProgressBar progressBar;

	}
}
