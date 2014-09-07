package com.yixianqian.utils;

import java.util.Calendar;

public class ConstellUtil {
	//	public static final String Aries = "°×Ñò×ù";
	//	public static final String Taurus = "½ğÅ£×ù";
	//	public static final String Gemini = "Ë«×Ó×ù";
	//	public static final String Cancer = "¾ŞĞ·×ù";
	//	public static final String Leo = "Ê¨×Ó×ù";
	//	public static final String Virgo = "´¦Å®×ù";
	//	public static final String Libra = "Ìì³Ó×ù";
	//	public static final String Scorpius = "ÌìĞ«×ù";
	//	public static final String Sagittarius = "ÉäÊÖ×ù";
	//	public static final String Capricorn = "Ä¦ôÉ×ù";
	//	public static final String Aquarius = "Ë®Æ¿×ù";
	//	public static final String Pisces = "Ë«Óã×ù";
	public static final String[] constellationArr = { "Ë®Æ¿×ù", "Ë«Óã×ù", "°×Ñò×ù", "½ğÅ£×ù", "Ë«×Ó×ù", "¾ŞĞ·×ù", "Ê¨×Ó×ù", "´¦Å®×ù", "Ìì³Ó×ù",
			"ÌìĞ«×ù", "ÉäÊÖ×ù", "Ä§ôÉ×ù" };
	public static final int[] constellationEdgeDay = { 20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22, 22 };

	/**  
	 * ¸ù¾İÈÕÆÚ»ñÈ¡ĞÇ×ù  
	 * @param time  
	 * @return  
	 */
	public static String date2Constellation(Calendar time) {
		int month = time.get(Calendar.MONTH);
		int day = time.get(Calendar.DAY_OF_MONTH);
		if (day < constellationEdgeDay[month]) {
			month = month - 1;
		}
		if (month >= 0) {
			return constellationArr[month];
		}
		//default to return Ä§ôÉ   
		return constellationArr[11];
	}
}
