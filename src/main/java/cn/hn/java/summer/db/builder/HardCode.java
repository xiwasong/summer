package cn.hn.java.summer.db.builder;

/**
 * 拼sql的参数
 * @author sjg
 * @version 1.0.1 2013-10-23
 *
 */
public class HardCode {

	/**
	 * 键
	 */
	private String key;
	
	/**
	 * 键对应的参数值
	 */
	private String param;
	
	public HardCode(){
		
	}
	
	public HardCode(String key, Object param, boolean escape) {
		super();
		this.key = key;
		this.param = param==null?"":param.toString();
		this.escape = escape;
	}

	/**
	 * 是否要转义'号
	 */
	private boolean escape;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public boolean isEscape() {
		return escape;
	}

	public void setEscape(boolean escape) {
		this.escape = escape;
	}
}
