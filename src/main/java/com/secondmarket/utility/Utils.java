package com.secondmarket.utility;

public class Utils {

	/**
	 * Delete the no meaning characters in a string. For example: $sea son& ->
	 * season
	 * 
	 * @param str
	 * @return
	 */
	public static String deleteNonMeaningChars(String str) {
		StringBuilder sb = new StringBuilder();
		str = str.toLowerCase();
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) >= 'a' && str.charAt(i) <= 'z') {
				sb.append(str.charAt(i));
			}
		}
		return sb.toString();
	}

	/***
	 * Compare two strings
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static boolean compareTwoStrings(String str1, String str2) {
		String newStr1 = deleteNonMeaningChars(str1);
		String newStr2 = deleteNonMeaningChars(str2);
		if (newStr1.contains(newStr2) || newStr2.contains(newStr1)) {
			return true;
		}
		return false;
	}

}
