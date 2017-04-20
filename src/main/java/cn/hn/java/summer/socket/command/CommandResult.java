package cn.hn.java.summer.socket.command;

/**
 * 命令调用结果封装
 * @author sjg
 * @version 1.0.1 2014-1-22
 *
 */
public class CommandResult {
	
	/**
	 * 结果，成功/失败
	 */
	private boolean result;
	
	/**
	 * 结果数据
	 */
	private Object data;	

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		setData(data, null);
	}
	
	public void setData(Object data,String msg) {
		if(!this.result){
			this.data=new Error(data.toString(),msg);
		}else{
			this.data = data;
		}
	}
	

	/**
	 * 错误包装
	 * @author sjg
	 * @version 1.0.1 2014-1-22
	 *
	 */
	private class Error{
		private String exception;
		private String msg;
		
		public Error(String er,String msg){
			this.exception=er;
			this.msg=msg;
		}

		@SuppressWarnings("unused")
		public String getException() {
			return exception;
		}
		@SuppressWarnings("unused")
		public void setException(String exception) {
			this.exception = exception;
		}

		@SuppressWarnings("unused")
		public String getMsg() {
			return msg;
		}

		@SuppressWarnings("unused")
		public void setMsg(String msg) {
			this.msg = msg;
		}
		
	}
}
