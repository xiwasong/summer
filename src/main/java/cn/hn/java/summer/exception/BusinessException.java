package cn.hn.java.summer.exception;

/**
 * 业务异常，抛出此异常日志不记录
 * @author sjg
 * @version 1.0.1 2013-10-23
 *
 */
public class BusinessException extends SnException {
	private static final long serialVersionUID = 1L;

	public BusinessException(){
		
	}
	
	public BusinessException(String msg){
		super(msg);
	}
	
	public BusinessException(Throwable ex){
		super(ex);
	}
	
	public BusinessException(String msg,Throwable ex){
		super(msg,ex);
	}
}
