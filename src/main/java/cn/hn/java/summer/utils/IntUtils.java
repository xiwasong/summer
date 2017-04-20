package cn.hn.java.summer.utils;

public class IntUtils {

	/**
	 * 转换成int
	 * @param obj
	 * @param def 失败返回默认值
	 * @return
	 */
	public static int parseInt(Object obj,int def){
		return parseInt(obj,10,def);
	}
	
	/**
	 * 转换成int
	 * @param obj
	 * @param radio 进制
	 * @param def 失败返回默认值
	 * @return
	 */
	public static int parseInt(Object obj,int radio,int def){
		try{
			String str=obj==null?"":obj.toString().trim();
			str=StringUtils.stripStart(str,"0");
			return Integer.parseInt(str);
		}catch(NumberFormatException nfe){
			return def;
		}
	}
}
