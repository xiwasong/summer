package cn.hn.java.summer.mvc;

import cn.hn.java.summer.Config;
import cn.hn.java.summer.constants.Default;
import cn.hn.java.summer.context.ThreadContextManage;
import cn.hn.java.summer.db.paging.Page;
import cn.hn.java.summer.utils.ReflectUtils;
import cn.hn.java.summer.utils.codec.AESCoder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;



/**
 * web上下文操作
 * @author sjg
 * @version 1.0.1 2013-10-24
 *
 */
public class WebContext {
	static final Log logger = LogFactory.getLog(WebContext.class);

	//标识系统是否已经安装
	public static boolean installed=false;	
	
	/**
	 * cookies aes加密的密码
	 */	
	private static String COOKIES_PWD=null;
	private static long COOKIES_PWD_TIME=0;
	
	/**
	 * 标识当前请求已更新cookies时间
	 */
	private static String SESSIONCOOKIETIME_UPDATEDKEY="sessionCookieTimeUpdated";
	
	private static String REQUESTATTR_KEYS="requestattr_keys";

	/**
	 * 设置上下文路径(同一路径下cookie共享)
	 */
	public static String contextPath="/";
	
	/**
	 * 为web上下文准备response对象
	 * @param response
	 */
	public static void preperedResponse(HttpServletResponse response){
		setRequestAttribute(Default.RESPONSE_IN_REQUEST_KEY, response);
	}
	
	/**
	 * 取response
	 * @return
	 */
	public static HttpServletResponse getResponse(){
		if(!Config.isWebApplication()){
			return null;
		}
		return getRequestAttribute(Default.RESPONSE_IN_REQUEST_KEY);
	}
	
	/**
	 * 取当前HttpServletRequest对象
	 * @return
	 */
	public static HttpServletRequest getRequest(){
		if(!Config.isWebApplication()){
			return null;
		}
		ServletRequestAttributes attr =null;
		try{
			attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		}catch (IllegalStateException e) {
			logger.warn("webcontext no request bind");
			return null;
		}
		if(attr!=null){
		    return attr.getRequest();
		}
		
		 return null;
	}
	
	private static String getCookiesPwd(){
		if(COOKIES_PWD==null){
			//生成密码时的毫秒
			COOKIES_PWD_TIME=Calendar.getInstance().getTimeInMillis();
			//生成密码
			COOKIES_PWD=genaratePwdFromTime(COOKIES_PWD_TIME);
		}
		return COOKIES_PWD;
	}
	
	private static String genaratePwdFromTime(long ms){
		//密码=当前总毫秒+上下文路径+当前总毫秒%上下文路径长度
		return ms+contextPath+ms%(contextPath.length()*201411);
	}
		
	private static String seperateCookiesPwd(String cookie){
		//从cookies中分离出密码
		if(!org.apache.commons.lang3.StringUtils.isBlank(cookie) && cookie.length()>13){
			try{
				//密码生成时间
				long pwdTime=Long.parseLong(cookie.substring(0, 13));
				COOKIES_PWD_TIME=pwdTime;
			
				//生成密码
				COOKIES_PWD=genaratePwdFromTime(pwdTime);
				cookie=cookie.substring(13);
			}catch (NumberFormatException e) {	
				COOKIES_PWD=null;
			}
		}
		return cookie;
	}
	
	@SuppressWarnings("unchecked")
	private static Map<String,Object> getCookiesDataMap(){
		//取当前请求中的session加密cookies
		String cookie=getCookie(Default.COOKIES_SESION_KEY);
		//分离出密码
		cookie=seperateCookiesPwd(cookie);
		
		//解密
		String data=null;
		//取存储的键值对
		Map<String,Object> kvs=new HashMap<String, Object>();;
		if(cookie!=null){
			data=AESCoder.decryptBase64(cookie,getCookiesPwd());	
			if(!org.apache.commons.lang3.StringUtils.isBlank(data)){
				//读取原有的字符串转换成对象
				try {
					kvs= new ObjectMapper().readValue(data, Map.class);
				} catch (Exception e) {
					logger.warn("parse string to key/value failed!!!!!!",e);
				}					
			}
		}
		if(kvs!=null && kvs.size()>0){
			//查看上下文缓存中是否存在该键值
			for(String key:kvs.keySet()){
				Object val=getRequestAttribute(Default.COOKIES_SESION_KEY+"_"+key);
				if(val!=null){
					//替换成最新的(因为cookies里新加的值要下个请求时才能取到)
					kvs.put(key, val);
				}
			}
		}else{
			//如果从cookies中未取到,从上下文缓存中取
			Object requestKeys=getRequestAttribute(REQUESTATTR_KEYS);
			if(requestKeys!=null){
				for(String key:((Map<String,String>)requestKeys).keySet()){
					Object val=getRequestAttribute(Default.COOKIES_SESION_KEY+"_"+key);
					if(val!=null){
						kvs.put(key, val);
					}
				}
			}
		}
		
		return kvs;
	}
	
	private static void writeCookies(Map<String,Object> kvs){
		if(kvs!=null && kvs.size()>0){
			//存储到上下文缓存中
			for(String key:kvs.keySet()){
				setRequestAttribute(Default.COOKIES_SESION_KEY+"_"+key,kvs.get(key));
			}
		}
		
		long now=Calendar.getInstance().getTimeInMillis();
		//保存当前时间
		kvs.put(Default.COOKIES_TIME_KEY, now);
		
		//转换成字符串
		String data="";
		try {
			data = new ObjectMapper().writeValueAsString(kvs);
		} catch (JsonProcessingException e) {
			logger.warn("parse key/value to string failed!!!!!!",e);
		}
		//logger.debug("==================保存的Session="+data);
		//加密
		data=AESCoder.encryptBase64(data, getCookiesPwd());
		//存储到cookies中
		addCookie(Default.COOKIES_SESION_KEY, COOKIES_PWD_TIME+data);
	}
	
	/**
	 * 以cookies的形式保存session并加密
	 * @param key 要保存值的键
	 * @param obj 保存的值
	 */
	public static void addSessionCookie(String key, Object obj){
		//取存储cookies中的键值对
		Map<String,Object> kvs=getCookiesDataMap();
		if(key!=null){
			//保存数据
			kvs.put(key, obj);
		}
		writeCookies(kvs);
		
		//保存所有的key
		saveRequestKeys(key);
	}
	
	/**
	 * 以cookies的形式保存session并加密,批量保存
	 * @param map 要保存的键值对
	 */
	public static void addSessionCookie(Map<String,Object> map){
		//取存储cookies中的键值对
		Map<String,Object> kvs=getCookiesDataMap();
		for(String key : map.keySet()){
			kvs.put(key, map.get(key));
			//保存所有的key
			saveRequestKeys(key);
		}
		writeCookies(kvs);
	}
	
	@SuppressWarnings("unchecked")
	private static void saveRequestKeys(String key){
		//保存所有的key
		Object requestKeys=getRequestAttribute(REQUESTATTR_KEYS);
		if(requestKeys==null){
			requestKeys=new HashMap<String,String>();
		}
		((Map<String,String>)requestKeys).put(key, key);
		
		setRequestAttribute(REQUESTATTR_KEYS, requestKeys);
	}
	
	/**
	 * 更新cookies时间
	 */
	private static void updateSessionCookieTime(){
		addSessionCookie(null,null);
		//标识已经更新时间
		setRequestAttribute(SESSIONCOOKIETIME_UPDATEDKEY, true);
	}
	
	/**
	 * 删除指定键的session加密cookie
	 * @param key
	 */
	public static void removeSessionCookie(String key){
		addSessionCookie(key,null);
	}
	
	/**
	 * 清空session加密cookie
	 */
	public static void invalidateSessionCookie(){
		addCookie(Default.COOKIES_SESION_KEY, null);
	}
	
	/**
	 * 获取指定键的解密后的session cookie值
	 * @param key
	 * @param cls
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getSessionCookie(String key,Class<T> cls){
		String data=getCookie(Default.COOKIES_SESION_KEY);
		if(data==null){
			//如果cookies里没有,从上下文中取(用于一次请求未完时保存的情况)
			return getRequestAttribute(Default.COOKIES_SESION_KEY+"_"+key);
		}
		//分离出密码
		data=seperateCookiesPwd(data);
		
		try {
			//解密
			data=AESCoder.decryptBase64(data, getCookiesPwd());
			if(org.apache.commons.lang3.StringUtils.isBlank(data)){
				//解密失败,清空cookie
				removeSessionCookie(key);
				return null;
			}
			
			//转换成对象
			Map<String,Object> kvs= new ObjectMapper().readValue(data,Map.class);
			
			//取时间
			long cookiesTime= Long.parseLong(""+kvs.get(Default.COOKIES_TIME_KEY));
			long now=Calendar.getInstance().getTimeInMillis();
			
			//如果cookies时间与当前时间相差SESSION_TIME_OUT,则超时
			if(now-cookiesTime>Default.SESSION_TIME_OUT*60*1000){
				return null;
			}

			//如果未更新时间
			if(getRequestAttribute(SESSIONCOOKIETIME_UPDATEDKEY)==null){
				//更新cookies当前时间
				updateSessionCookieTime();
			}
			
			//先从上下文缓存中取
			Object rst= getRequestAttribute(Default.COOKIES_SESION_KEY+"_"+key);
			if(rst!=null){
				if(rst instanceof Map){
					//转换成目标类型对象
					return ReflectUtils.copyValues((Map<String,Object>)rst,cls);
				}
				return (T)rst;
			}
			
			//从cookies中取需要的值
			rst=kvs.get(key);
			if(rst==null){
				return null;
			}
			
			//原始类型,直接返回解析结果
			if(cls.getName().indexOf(".")==-1 || cls.getName().indexOf("java.lang.")==0){
				return (T)rst;
			}
			
			//其它对象类型,组装成对象
			return ReflectUtils.copyValues((Map<String,Object>)rst,cls);
		} catch (Exception e) {
			logger.warn("cookies aes decrypt or json parse failed", e);
		}
		return null;
	}
	
	/**
	 * 取对应键的cookie值
	 * @param key
	 * @return
	 */
	public static String getCookie(String key){
		Cookie[] allCookies= getRequest().getCookies();
		if(allCookies==null || allCookies.length==0 || key==null){
			return null;
		}
		
		for(Cookie ck:allCookies){
			if(key.equals(ck.getName())){
				return ck.getValue();
			}
		}
		return null;
	}	
	
	/**
	 * 添加cookie到响应中
	 * @param key
	 * @param data
	 */
	public static void addCookie(String key,String data){
		//存储到cookies中
		Cookie ck=new Cookie(key, data);
		//网站所有路径可用
		ck.setPath(contextPath);
		//关闭浏览器失效
		ck.setMaxAge(-1);
		getResponse().addCookie(ck);
	}
	
	/**
	 * 添加对象到请求上下文中
	 * @param key
	 * @param value
	 */
	public static void setRequestAttribute(String key, Object value){
		ThreadContextManage.setAttribute(key, value);
	}	

	/**
	 * 取请求中的对象
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getRequestAttribute(String key){
		return (T)ThreadContextManage.getAttribute(key);
	}
	
	/**
	 * 从请求中取对象
	 * @param key
	 * @return
	 */
	public static Object getRequestObject(String key){
		return ThreadContextManage.getRequestObject(key);
	}
	
	/**
	 * 获取请求头信息
	 * @param key
	 * @return
	 */
	public static String getRequestHead(String key){
		return ThreadContextManage.getOtherInfo(key);
	}
	
	/**
	 * 取分页对象
	 * @return
	 */
	public static Page getPage(){
		return ThreadContextManage.getPagingInfo();
	}
	
	/**
	 * 跳转到指定路径
	 * @param url
	 */
	public static void redirectTo(String url){
		try {
			if(url==null){
				url="";
			}
			url=url.toLowerCase();
			HttpServletRequest request=getRequest();
			//不以http开头的路径补全上下文路径
			if(!Pattern.matches("^https?://.*", url)){
				url=request.getContextPath()+url;
			}
			//标记本次响应已跳转，通知视图模板略过本次请求的内容处理
			request.setAttribute(Default.MARK_RESPONSE_IS_REDIRECTED, "");
			getResponse().sendRedirect(url);
			
		} catch (IOException e) {
			logger.error("redirect error!",e);
		}
	}
	
}
