/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yixianqian.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yixianqian.R;

import android.content.Context;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.style.ImageSpan;

public class SmileUtils {
	//	mFaceMap.put("[Ë¯]", R.drawable.f035);
	//	mFaceMap.put("[Ç×Ç×]", R.drawable.f036);
	//	mFaceMap.put("[º©Ð¦]", R.drawable.f037);
	//	mFaceMap.put("[°®Çé]", R.drawable.f038);
	//	mFaceMap.put("[Ë¥]", R.drawable.f039);
	//	mFaceMap.put("[Æ²×ì]", R.drawable.f040);
	//	mFaceMap.put("[ÒõÏÕ]", R.drawable.f041);
	//	mFaceMap.put("[·Ü¶·]", R.drawable.f042);
	//	mFaceMap.put("[·¢´ô]", R.drawable.f043);
	//	mFaceMap.put("[ÓÒºßºß]", R.drawable.f044);
	//	mFaceMap.put("[Óµ±§]", R.drawable.f045);
	//	mFaceMap.put("[»µÐ¦]", R.drawable.f046);
	//	mFaceMap.put("[·ÉÎÇ]", R.drawable.f047);
	//	mFaceMap.put("[±ÉÊÓ]", R.drawable.f048);
	//	mFaceMap.put("[ÔÎ]", R.drawable.f049);
	//	mFaceMap.put("[´ó±ø]", R.drawable.f050);
	//	mFaceMap.put("[¿ÉÁ¯]", R.drawable.f051);
	//	mFaceMap.put("[Ç¿]", R.drawable.f052);
	//	mFaceMap.put("[Èõ]", R.drawable.f053);
	//	mFaceMap.put("[ÎÕÊÖ]", R.drawable.f054);
	//	mFaceMap.put("[Ê¤Àû]", R.drawable.f055);
	//	mFaceMap.put("[±§È­]", R.drawable.f056);
	//	mFaceMap.put("[µòÐ»]", R.drawable.f057);
	//	mFaceMap.put("[·¹]", R.drawable.f058);
	//	mFaceMap.put("[µ°¸â]", R.drawable.f059);
	//	mFaceMap.put("[Î÷¹Ï]", R.drawable.f060);
	//	mFaceMap.put("[Æ¡¾Æ]", R.drawable.f061);
	//	mFaceMap.put("[Æ®³æ]", R.drawable.f062);
	//
	//	mFaceMap.put("[¹´Òý]", R.drawable.f063);
	//	mFaceMap.put("[OK]", R.drawable.f064);
	//	mFaceMap.put("[°®Äã]", R.drawable.f065);
	//	mFaceMap.put("[¿§·È]", R.drawable.f066);
	//	mFaceMap.put("[Ç®]", R.drawable.f067);
	//	mFaceMap.put("[ÔÂÁÁ]", R.drawable.f068);
	//	mFaceMap.put("[ÃÀÅ®]", R.drawable.f069);
	//	mFaceMap.put("[µ¶]", R.drawable.f070);
	//	mFaceMap.put("[·¢¶¶]", R.drawable.f071);
	//	mFaceMap.put("[²î¾¢]", R.drawable.f072);
	//	mFaceMap.put("[È­Í·]", R.drawable.f073);
	//	mFaceMap.put("[ÐÄËé]", R.drawable.f074);
	//	mFaceMap.put("[Ì«Ñô]", R.drawable.f075);
	//	mFaceMap.put("[ÀñÎï]", R.drawable.f076);
	//	mFaceMap.put("[×ãÇò]", R.drawable.f077);
	//	mFaceMap.put("[÷¼÷Ã]", R.drawable.f078);
	//	mFaceMap.put("[»ÓÊÖ]", R.drawable.f079);
	//	mFaceMap.put("[ÉÁµç]", R.drawable.f080);
	//	mFaceMap.put("[¼¢¶ö]", R.drawable.f081);
	//	mFaceMap.put("[À§]", R.drawable.f082);
	//	mFaceMap.put("[ÖäÂî]", R.drawable.f083);
	//
	//	mFaceMap.put("[ÕÛÄ¥]", R.drawable.f084);
	//	mFaceMap.put("[¿Ù±Ç]", R.drawable.f085);
	//	mFaceMap.put("[¹ÄÕÆ]", R.drawable.f086);
	//	mFaceMap.put("[ôÜ´óÁË]", R.drawable.f087);
	//	mFaceMap.put("[×óºßºß]", R.drawable.f088);
	//	mFaceMap.put("[¹þÇ·]", R.drawable.f089);
	//	mFaceMap.put("[¿ì¿ÞÁË]", R.drawable.f090);
	//	mFaceMap.put("[ÏÅ]", R.drawable.f091);
	//	mFaceMap.put("[ÀºÇò]", R.drawable.f092);
	//	mFaceMap.put("[Æ¹ÅÒÇò]", R.drawable.f093);
	//	mFaceMap.put("[NO]", R.drawable.f094);
	//	mFaceMap.put("[ÌøÌø]", R.drawable.f095);
	//	mFaceMap.put("[âæ»ð]", R.drawable.f096);
	//	mFaceMap.put("[×ªÈ¦]", R.drawable.f097);
	//	mFaceMap.put("[¿ÄÍ·]", R.drawable.f098);
	//	mFaceMap.put("[»ØÍ·]", R.drawable.f099);
	//	mFaceMap.put("[ÌøÉþ]", R.drawable.f100);
	//	mFaceMap.put("[¼¤¶¯]", R.drawable.f101);
	//	mFaceMap.put("[½ÖÎè]", R.drawable.f102);
	//	mFaceMap.put("[Ï×ÎÇ]", R.drawable.f103);
	//	mFaceMap.put("[×óÌ«¼«]", R.drawable.f104);
	//
	//	mFaceMap.put("[ÓÒÌ«¼«]", R.drawable.f105);
	//	mFaceMap.put("[±Õ×ì]", R.drawable.f106);
	//	

	public static final String ee_1 = "[ßÚÑÀ]";
	public static final String ee_2 = "[µ÷Æ¤]";
	public static final String ee_3 = "[Á÷º¹]";
	public static final String ee_4 = "[ÍµÐ¦]";
	public static final String ee_5 = "[ÔÙ¼û]";
	public static final String ee_6 = "[ÇÃ´ò]";
	public static final String ee_7 = "[²Áº¹]";
	public static final String ee_8 = "[ÖíÍ·]";
	public static final String ee_9 = "[Ãµ¹å]";
	public static final String ee_10 = "[Á÷Àá]";
	public static final String ee_11 = "[´ó¿Þ]";
	public static final String ee_12 = "[Ðê]";
	public static final String ee_13 = "[¿á]";
	public static final String ee_14 = "[×¥¿ñ]";
	public static final String ee_15 = "[Î¯Çü]";
	public static final String ee_16 = "[±ã±ã]";
	public static final String ee_17 = "[Õ¨µ¯]";
	public static final String ee_18 = "[²Ëµ¶]";
	public static final String ee_19 = "[¿É°®]";
	public static final String ee_20 = "[É«]";
	public static final String ee_21 = "[º¦Ðß]";
	public static final String ee_22 = "[µÃÒâ]";
	public static final String ee_23 = "[ÍÂ]";
	public static final String ee_24 = "[Î¢Ð¦]";
	public static final String ee_25 = "[·¢Å­]";
	public static final String ee_26 = "[ÞÏÞÎ]";
	public static final String ee_27 = "[¾ª¿Ö]";
	public static final String ee_28 = "[Àäº¹]";
	public static final String ee_29 = "[°®ÐÄ]";
	public static final String ee_30 = "[Ê¾°®]";
	public static final String ee_31 = "[°×ÑÛ]";
	public static final String ee_32 = "[°ÁÂý]";
	public static final String ee_33 = "[ÄÑ¹ý]";
	public static final String ee_34 = "[¾ªÑÈ]";
	public static final String ee_35 = "[ÒÉÎÊ]";

	private static final Factory spannableFactory = Spannable.Factory.getInstance();

	private static final Map<Pattern, Integer> emoticons = new HashMap<Pattern, Integer>();

	static {

		addPattern(emoticons, ee_1, R.drawable.f000);
		addPattern(emoticons, ee_2, R.drawable.f001);
		addPattern(emoticons, ee_3, R.drawable.f002);
		addPattern(emoticons, ee_4, R.drawable.f003);
		addPattern(emoticons, ee_5, R.drawable.f004);
		addPattern(emoticons, ee_6, R.drawable.f005);
		addPattern(emoticons, ee_7, R.drawable.f006);
		addPattern(emoticons, ee_8, R.drawable.f007);
		addPattern(emoticons, ee_9, R.drawable.f008);
		addPattern(emoticons, ee_10, R.drawable.f009);
		addPattern(emoticons, ee_11, R.drawable.f010);
		addPattern(emoticons, ee_12, R.drawable.f011);
		addPattern(emoticons, ee_13, R.drawable.f012);
		addPattern(emoticons, ee_14, R.drawable.f013);
		addPattern(emoticons, ee_15, R.drawable.f014);
		addPattern(emoticons, ee_16, R.drawable.f015);
		addPattern(emoticons, ee_17, R.drawable.f016);
		addPattern(emoticons, ee_18, R.drawable.f017);
		addPattern(emoticons, ee_19, R.drawable.f018);
		addPattern(emoticons, ee_20, R.drawable.f019);
		addPattern(emoticons, ee_21, R.drawable.f020);
		addPattern(emoticons, ee_22, R.drawable.f021);
		addPattern(emoticons, ee_23, R.drawable.f022);
		addPattern(emoticons, ee_24, R.drawable.f023);
		addPattern(emoticons, ee_25, R.drawable.f024);
		addPattern(emoticons, ee_26, R.drawable.f025);
		addPattern(emoticons, ee_27, R.drawable.f026);
		addPattern(emoticons, ee_28, R.drawable.f027);
		addPattern(emoticons, ee_29, R.drawable.f028);
		addPattern(emoticons, ee_30, R.drawable.f029);
		addPattern(emoticons, ee_31, R.drawable.f030);
		addPattern(emoticons, ee_32, R.drawable.f031);
		addPattern(emoticons, ee_33, R.drawable.f032);
		addPattern(emoticons, ee_34, R.drawable.f033);
		addPattern(emoticons, ee_35, R.drawable.f034);
	}

	private static void addPattern(Map<Pattern, Integer> map, String smile, int resource) {
		map.put(Pattern.compile(Pattern.quote(smile)), resource);
	}

	/**
	 * replace existing spannable with smiles
	 * @param context
	 * @param spannable
	 * @return
	 */
	public static boolean addSmiles(Context context, Spannable spannable) {
		boolean hasChanges = false;
		for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
			Matcher matcher = entry.getKey().matcher(spannable);
			while (matcher.find()) {
				boolean set = true;
				for (ImageSpan span : spannable.getSpans(matcher.start(), matcher.end(), ImageSpan.class))
					if (spannable.getSpanStart(span) >= matcher.start() && spannable.getSpanEnd(span) <= matcher.end())
						spannable.removeSpan(span);
					else {
						set = false;
						break;
					}
				if (set) {
					hasChanges = true;
					spannable.setSpan(new ImageSpan(context, entry.getValue()), matcher.start(), matcher.end(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
		}
		return hasChanges;
	}

	public static Spannable getSmiledText(Context context, CharSequence text) {
		Spannable spannable = spannableFactory.newSpannable(text);
		addSmiles(context, spannable);
		return spannable;
	}

	public static boolean containsKey(String key) {
		boolean b = false;
		for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
			Matcher matcher = entry.getKey().matcher(key);
			if (matcher.find()) {
				b = true;
				break;
			}
		}

		return b;
	}

}
