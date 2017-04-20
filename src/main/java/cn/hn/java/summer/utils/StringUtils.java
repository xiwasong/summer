package cn.hn.java.summer.utils;

import java.text.DecimalFormat;
import java.util.UUID;

public class StringUtils extends org.apache.commons.lang3.StringUtils{
	
	public StringUtils(){}
	
	
	/***
	 * 
	 * @param obj
	 * @param format 
	 */
	public static String numberFormat(Object obj,String format){
		String str="0.00";
		try {
			if(null==format||"".equals(format.trim())){
				format="##########0.00";
			}
			
			DecimalFormat dataFormat = new DecimalFormat(format);
			str=dataFormat.format(obj);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return str;
	}
	
	
	/**
	 * 格式化字符串：<br/>
	 * 如：format("a{0}b{1}","xx","yy")<br/>
	 * 结果：axxbyy
	 * @param val
	 * @param args
	 * @return
	 */
	public static String format(String val,Object... args){
		for(int i=0; i< args.length; i++){
			val=val.replace("{"+i+"}","%"+(i+1)+"$s");
		}
		return String.format(val, args);
	}
	
	/**
	 * 32位无-的全局唯一ID
	 * @return
	 */
	public static String newUUID(){
		return UUID.randomUUID().toString().replace("-", "");
	}
}
