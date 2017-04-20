package cn.hn.java.summer.context;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class WebContext implements IThreadContext {

	@Override
	public Object getAttribute(String key) {
		return getRequest().getAttribute(key);
	}

	@Override
	public void setAttribute(String key, Object value) {
		getRequest().setAttribute(key, value);
	}

	/**
	 * 取当前HttpServletRequest对象
	 * @return
	 */
	public static HttpServletRequest getRequest(){	
		ServletRequestAttributes attr =null;
		try{
			attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		}catch (IllegalStateException e) {
			return null;
		}
		if(attr!=null){
		    return attr.getRequest();
		}
		 return null;
	}

	/**
	 * 取上下文(请求)中的参数
	 * @param key
	 * @return
	 */
	public String getParameter(String key) {
		HttpServletRequest request=getRequest();
		if(request!=null){
			return request.getParameter(key);
		}
		return "";
	}

	/**
	 * 取上下文中其它信息
	 * @param key
	 * @return
	 */
	public String getOtherInfo(String key) {
		HttpServletRequest request=getRequest();
		return request.getHeader(key);
	}
}
