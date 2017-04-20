package cn.hn.java.summer.exception;

/**
 * 业务异常(带错误号)，抛出此异常日志不记录
 * @author sjg
 * @version 1.0.1 2014-1-22
 *
 */
public class CodeBusinessException extends BusinessException {
	private static final long serialVersionUID = 2L;
	
	private String code;

	public CodeBusinessException(){
		
	}	

	public CodeBusinessException(String msg,String code){
		super(msg);		
		this.code=code;
	}
	
	public CodeBusinessException(String msg,String code,Throwable ex){
		super(msg,ex);
		this.code=code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		if(super.getMessage()!=null && getCode()!=null){
			return super.getMessage()+",error code:"+getCode();
		}
		return super.getMessage();
	}
}
