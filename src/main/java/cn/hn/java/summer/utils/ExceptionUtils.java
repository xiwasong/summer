package cn.hn.java.summer.utils;

public class ExceptionUtils {

	/**
	 * 取异常信息
	 * @param e
	 * @return
	 */
	public static String getExceptionMessage(Exception e){
		if(e.getMessage()==null && e.getCause()!=null){
			return e.getCause().getMessage();
		}
		return e.getMessage();
	}
}
