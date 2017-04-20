package cn.hn.java.summer.exception;

/**
 * 带数据的带错误码的业务异常
 * @author sjg
 * 2017年1月11日 下午3:18:18
 *
 */
public class CodeBusinessDataException extends CodeBusinessException implements IExceptionData{
	private static final long serialVersionUID = 1L;
	
	private Object data;
	

	public CodeBusinessDataException(Object data,String msg,String code){
		super(msg,code);		
		this.data=data;
	}
	
	public CodeBusinessDataException(Object data,String msg,String code,Throwable ex){
		super(msg,code,ex);
		this.data=data;
	}

	public void setData(Object data) {
		this.data=data;
	}

	public Object getData() {
		return this.data;
	}

	
}
