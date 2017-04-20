package cn.hn.java.summer.exception;

/**
 * 自定义异常基类
 * @author sjg
 * @version 1.0.1 2013-10-9
 *
 */
public class SnException extends Exception{
	private static final long serialVersionUID = 1L;

	public SnException(){
		
	}
	
	public SnException(String msg){
		super(msg);
	}
	
	public SnException(Throwable ex){
		super(ex);
	}
	
	public SnException(String msg,Throwable ex){
		super(msg,ex);
	}
	
}
