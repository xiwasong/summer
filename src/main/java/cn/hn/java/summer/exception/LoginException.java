package cn.hn.java.summer.exception;

/**
 * 未登录异常
 * @author sjg
 *
 */
public class LoginException extends SnException {
	private static final long serialVersionUID = 1L;

	public LoginException(){
	}
	
	public LoginException(String msg){
		super(msg);
	}
	
	public LoginException(Throwable ex){
		super(ex);
	}
	
	public LoginException(String msg,Throwable ex){
		super(msg,ex);
	}
	
}
