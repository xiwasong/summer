package cn.hn.java.summer.mvc;

/**
 * 用于json包装结果类
 * @author sjg
 * @version 1.0.1 2013-10-10
 *
 */
public class Result {

	/**
	 * 是否得到正确结果
	 */
	boolean result;
	
	/**
	 * 保存结果数据
	 */
	Object data;

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
		this.data = data;
	}

	public Result(boolean result) {
		super();
		this.result = result;
	}

	public Result(boolean result, Object data) {
		super();
		this.result = result;
		this.data = data;
	}

	public Result(Object data) {
		super();
		this.data = data;
	}
	
}
