package cn.hn.java.summer.exception;

/**
 * 用于请求跳转
 * @author sjg
 * @version 1.0.1 2013-10-22
 *
 */
public class ForwardException extends SummerException {
	private static final long serialVersionUID = 1L;
	/**
	 * 转发路径
	 */
	private String forwardUrl;
	
	public ForwardException(){
	}
	
	public ForwardException(String url){
		this.forwardUrl=url;
	}

	public String getForwardUrl() {
		return forwardUrl;
	}

	public void setForwardUrl(String forwardUrl) {
		this.forwardUrl = forwardUrl;
	}
	
}
