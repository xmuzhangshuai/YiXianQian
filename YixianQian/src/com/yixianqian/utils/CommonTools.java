package com.yixianqian.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yixianqian.R;
import com.yixianqian.config.Constants;
import com.yixianqian.config.DefaultKeys;

/**   
*    
* ��Ŀ���ƣ�ExamHelper   
* �����ƣ�CommonTools   
* ��������   ͨ�õĹ�����
* �����ˣ���˧  
* ����ʱ�䣺2013-12-22 ����7:26:29   
* �޸��ˣ���˧    
* �޸�ʱ�䣺2013-12-22 ����7:26:29   
* �޸ı�ע��   
* @version    
*    
*/
public class CommonTools {

	/**
	* �жϳ����Ƿ�������
	* @param context
	* @return
	*/
	public static boolean isAppRunning(Context context) {
		boolean isAppRunning = false;
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(100);
		for (RunningTaskInfo info : list) {
			if (info.topActivity.getPackageName().equals(Constants.PACKAGENAME)
					&& info.baseActivity.getPackageName().equals(Constants.PACKAGENAME)) {
				isAppRunning = true;
				//find it, break 
				break;
			}
		}
		return isAppRunning;
	}

	/**
	 * ���Sdcard�Ƿ����
	 * 
	 * @return
	 */
	public static boolean isExitsSdcard() {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	public static String getTopActivity(Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

		if (runningTaskInfos != null)
			return runningTaskInfos.get(0).topActivity.getClassName();
		else
			return "";
	}

	/**
	 * ������ʾToast��Ϣ
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
	* �ж��ֻ�����
	*/
	public static boolean isMobileNO(String mobiles) {
		Pattern pattern = Pattern.compile("^((13[0-9])|(15[0-9])|(18[0-9])|(17[0-9]))\\d{8}$");
		Matcher matcher = pattern.matcher(mobiles);
		return matcher.matches();
	}

	/**
	 * ��֤�����Ƿ����Ҫ��
	 * ����ĸ��ͷ��������6~18֮�䣬ֻ�ܰ����ַ������ֺ��»���
	 * @return
	 */
	public static boolean isPassValid(String pass) {
		Pattern pattern = Pattern.compile("^[0-9a-zA-Z]{6,18}$");
		Matcher matcher = pattern.matcher(pass);
		return matcher.matches();
	}

	/**
	 * �����û�ʡ
	 * @param context
	 * @return
	 */
	public static String getMyProvince(Context context) {
		String location = "";//ʡ��

		SharedPreferences locationPreferences = context.getSharedPreferences("location", Context.MODE_PRIVATE);
		location = locationPreferences.getString(DefaultKeys.USER_PROVINCE, "������");
		return location;
	}

	/**
	 * �����û���ϸ��ַ
	 * @param context
	 * @return
	 */
	public static String getMyDetailLocation(Context context) {
		String location = "";
		SharedPreferences locationPreferences = context.getSharedPreferences("location", Context.MODE_PRIVATE);
		location = locationPreferences.getString(DefaultKeys.USER_DETAIL_LOCATION, "������");
		return location;
	}

	/**
	 * �����û���ϸ��ַ
	 * @param context
	 * @return
	 */
	public static String getMyCity(Context context) {
		String location = "";
		SharedPreferences locationPreferences = context.getSharedPreferences("location", Context.MODE_PRIVATE);
		location = locationPreferences.getString(DefaultKeys.USER_CITY, "������");
		return location;
	}

}
