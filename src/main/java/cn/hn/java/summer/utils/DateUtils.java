package cn.hn.java.summer.utils;


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

public class DateUtils {

	public static final String YMD1="yyyy-MM-dd";
	public static final String YMD2="yyyyMMdd";
	public static final String YMDHMS1="yyyy-MM-dd HH:mm:ss";
	public static final String YMDHMS2="yyyyMMddHHmmss";



	/**
	 * @see 按指定格式取得当前时间字符串
	 * @return String
	 */
	public static String now(String pattern) {
		return DateTime.now().toString(DateTimeFormat.forPattern(pattern));
	}

	/**
	 * yyyy-MM-dd HH:mm:ss 字符串转为DateTime
	 * @param d
	 * @return
	 */
	public static DateTime parse(String d){
		return parse(d,"yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 字符串转为DateTime
	 * @param d
	 * @param pattern
	 * @return
	 */
	public static DateTime parse(String d,String pattern){
		return DateTimeFormat.forPattern(pattern).parseDateTime(d);
	}
	
	/**
	 * 将字符串时间从一种格式转换为另一种格式
	 * @param d
	 * @param fromPattern
	 * @param toPattern
	 * @return
	 */
	public static String strTranslate(String d,String fromPattern,String toPattern){
		return parse(d,fromPattern).toString(toPattern);
	}
	
}
