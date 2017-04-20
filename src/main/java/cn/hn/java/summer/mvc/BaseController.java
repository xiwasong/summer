package cn.hn.java.summer.mvc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import cn.hn.java.summer.exception.ForwardException;

/**
 * controller 基类
 * 2014-11-10 更新:使用加密cookies替代session
 * @author sjg
 * @version 1.0.1 2013-09-21
 *
 */
public abstract class BaseController {

	protected final Log logger = LogFactory.getLog(getClass());	
	
	/**
	 * 从Session中取对象
	 * @param <T>
	 * @param key
	 * @return
	 */
	public <T> T getSessionAttribute(String key,Class<T> cls){
		return WebContext.getSessionCookie(key,cls);
	}
	
	/**
	 * 添加对象到session
	 * @param key
	 * @param obj
	 */
	public void addSessionAttribute(String key, Object obj){
		WebContext.addSessionCookie(key, obj);
	}
		
	/**
	 *	添加对象到request 
	 * @param key
	 * @param obj
	 */
	public void addRequestAttribute(String key, Object obj){
		WebContext.setRequestAttribute(key, obj);
	}
	
	/**
	 * 清除session
	 */
	public void clearSession(){
		WebContext.invalidateSessionCookie();
	}
	
	/**
	 * 跳转请求
	 * @param url
	 * @throws ForwardException
	 */
	public void redirectTo(String url){
		WebContext.redirectTo(url);
	}
	
	@InitBinder  
    private void initBinder(WebDataBinder binder) throws Exception {  
        binder.registerCustomEditor(Integer.class, new CustomIntegerEditor(Integer.class, true));
        binder.registerCustomEditor(Double.class, new CustomDoubleEditor(Double.class, true));
    } 
}

class CustomIntegerEditor extends CustomNumberEditor{

	public CustomIntegerEditor(Class<? extends Number> numberClass,
			boolean allowEmpty) throws IllegalArgumentException {
		super(numberClass, allowEmpty);
	}
	
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		try{
			Integer.parseInt(text);
		}catch(Exception e){
			text=null;
		}
		super.setAsText(text);
	}
}

class CustomDoubleEditor extends CustomNumberEditor{

	public CustomDoubleEditor(Class<? extends Number> numberClass,
			boolean allowEmpty) throws IllegalArgumentException {
		super(numberClass, allowEmpty);
	}
	
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		try{
			Double.parseDouble(text);
		}catch(Exception e){
			text=null;
		}
		super.setAsText(text);
	}
}