package cn.hn.java.summer.exception;

/**
 * 消息码异常包装
 * @author sjg
 * @version 1.0.1 2014-1-22
 *
 */
@SuppressWarnings("serial")
public class MessageCodeException extends Exception {


	private String msg;
	
	private String code;
	
	private Object data;
	
	/**
	 * 是否为业务异常
	 */
	private boolean business;
	
	public boolean isBusiness() {
		return business;
	}

	public void setBusiness(boolean business) {
		this.business = business;
	}

	public MessageCodeException(){
		
	}
	
	public MessageCodeException(String msg,String code){
		this.msg=msg;
		this.code=code;
	}
	
	public MessageCodeException(String msg,String code,Throwable ex){
		super(msg,ex);
		this.msg=msg;
		this.code=code;
	}
	
	public MessageCodeException(String msg,String code,boolean business,Throwable ex){
		super(msg,ex);
		this.msg=msg;
		this.code=code;
		this.business=business;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
}
