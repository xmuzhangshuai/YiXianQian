package com.yixianqian.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yixianqian.R;
import com.yixianqian.config.DefaultKeys;

/**   
*    
* 项目名称：ExamHelper   
* 类名称：CommonTools   
* 类描述：   通用的工具类
* 创建人：张帅  
* 创建时间：2013-12-22 下午7:26:29   
* 修改人：张帅    
* 修改时间：2013-12-22 下午7:26:29   
* 修改备注：   
* @version    
*    
*/
public class CommonTools {

	/**
	 * 短暂显示Toast消息
	 * 
	 * @param context
	 * @param message
	 */
	public static void showShortToast(Context context, String message) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.custom_toast, null);
		TextView text = (TextView) view.findViewById(R.id.toast_message);
		text.setText(message);
		Toast toast = new Toast(context);
		toast.setDuration(2000);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.BOTTOM, 0, 300);
		toast.setView(view);
		toast.show();
	}

	/**
	 * 判断手机号码
	 */
	public static boolean isMobileNO(String mobiles) {

		Pattern pattern = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher matcher = pattern.matcher(mobiles);

		return matcher.matches();

	}

	/**
	 * 返回用户省
	 * @param context
	 * @return
	 */
	public static String getMyProvince(Context context) {
		String location = "";//省市

		SharedPreferences locationPreferences = context.getSharedPreferences("location", Context.MODE_PRIVATE);
		location = locationPreferences.getString(DefaultKeys.USER_PROVINCE, "北京市");
		return location;
	}

	/**
	 * 返回用户详细地址
	 * @param context
	 * @return
	 */
	public static String getMyDetailLocation(Context context) {
		String location = "";
		SharedPreferences locationPreferences = context.getSharedPreferences("location", Context.MODE_PRIVATE);
		location = locationPreferences.getString(DefaultKeys.USER_DETAIL_LOCATION, "北京市");
		return location;
	}

	/**
	 * 返回用户详细地址
	 * @param context
	 * @return
	 */
	public static String getMyCity(Context context) {
		String location = "";
		SharedPreferences locationPreferences = context.getSharedPreferences("location", Context.MODE_PRIVATE);
		location = locationPreferences.getString(DefaultKeys.USER_CITY, "北京市");
		return location;
	}

}
