package cn.hn.java.summer.exception;

/**
 * 自定义异常基类
 * @author sjg
 * @version 1.0.1 2013-10-9
 *
 */
public class SummerException extends Exception{
	private static final long serialVersionUID = 1L;

	public SummerException(){
		
	}
	
	public SummerException(String msg){
		super(msg);
	}
	
	public SummerException(Throwable ex){
		super(ex);
	}
	
	public SummerException(String msg, Throwable ex){
		super(msg,ex);
	}
	
}
