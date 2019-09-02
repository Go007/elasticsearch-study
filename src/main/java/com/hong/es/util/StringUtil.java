package com.hong.es.util;

public class StringUtil {

	public static boolean isEmpty(String s) {
		return s == null || s.trim().length() == 0;
	}

	/**
	 * 去除字符串中所包含的空格（包括:空格(全角，半角)、制表符、换页符等）
	 * 
	 * @param s
	 * @return
	 */
	public static String removeAllBlank(String s) {
		String result = "";
		if (null != s && !"".equals(s)) {
			result = s.replaceAll("[　*| *| *|//s*]*", "");
		}
		return result;
	}

	/**
	 * 去除字符串中头部和尾部所包含的空格（包括:空格(全角，半角)、制表符、换页符等）
	 * 
	 * @param s
	 * @return
	 */
	public static String trim(String s) {
		String result = "";
		if (null != s && !"".equals(s)) {
			result = s.replaceAll("^[　*| *| *|//s*]*", "").replaceAll("[　*| *| *|//s*]*$", "");
		}
		return result;
	}

}