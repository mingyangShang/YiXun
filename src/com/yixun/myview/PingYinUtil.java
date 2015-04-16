package com.yixun.myview;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PingYinUtil {
	public static String getPingYin(String inputString) {
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		format.setVCharType(HanyuPinyinVCharType.WITH_V);
		if (inputString == null) {
			return "@";
		}
		char[] input = inputString.trim().toCharArray();
		String output = "";
		if (input != null) {
			if (((int) input[0] > 97 && (int) input[0] < 122)
					|| ((int) input[0] > 65 && (int) input[0] < 90)) {
				output = String.valueOf(input[0]).toLowerCase();
			} else {
				try {
					for (int i = 0; i < input.length; i++) {
						if (java.lang.Character.toString(input[i]).matches(
								"[\\u4E00-\\u9FA5]+")) {
							String[] temp = PinyinHelper
									.toHanyuPinyinStringArray(input[i], format);
							output += temp[0];
						} else
							output += java.lang.Character.toString(input[i]);
					}
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			}

		}
		return output;
	}

	/**
	 * 姹夊瓧杞崲浣嶆眽璇嫾闊抽瀛楁瘝锛岃嫳鏂囧瓧绗︿笉鍙�
	 * 
	 * @param chines姹夊瓧
	 * @return 鎷奸煶
	 */
	public static String converterToFirstSpell(String chines) {
		String pinyinName = "";
		if (chines == null) {
			return "@";
		}
		char[] nameChar = chines.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < nameChar.length; i++) {
			if (nameChar[i] > 128) {
				try {
					pinyinName += PinyinHelper.toHanyuPinyinStringArray(
							nameChar[i], defaultFormat)[0].charAt(0);
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			} else {
				pinyinName += nameChar[i];
			}
		}
		return pinyinName;
	}

}
