package cn.hn.java.summer.mvc;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.hn.java.summer.constants.Default;
import cn.hn.java.summer.mvc.validation.ValidDescription;
import cn.hn.java.summer.mvc.validation.ValidatorBuilder;
import cn.hn.java.summer.utils.DateUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 页面帮助类
 * @author sjg
 * @version 1.0.1 2013-10-24
 *
 */
public class ViewTools {
	protected static final Log logger = LogFactory.getLog(ViewTools.class);	

	/**
	 * 取总记录数
	 * @return
	 */
	public static Integer getRecordTotal(int dft){
		Integer total =WebContext.getRequestAttribute(Default.PAGING_TOTAL_IN_REQUEST_KEY);
		return total==null?dft:total;
	}
	
	/**
	 * 从请求、session中取信息
	 * @param <T>
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T get(String key,Object def){
		//从请求中取
		Object obj=WebContext.getRequestObject(key);		
		if(obj!=null){
			return (T)obj;
		}
		return (T)def;
	}

	
	/**
	 * 从请求、session中取信息
	 * @param <T>
	 * @param key
	 * @return
	 */
	public static <T> T get(String key){
		return get(key,null);
	}
	
	/**
	 * 取数字
	 * @param key
	 * @param dft 默认值
	 * @return
	 */
	public static int getInt(String key,int dft){
		return get(key,dft);
	}
	
	/**
	 * 取数字
	 * @param key
	 * @return
	 */
	public static int getInt(String key){
		return getInt(key,0);
	}

	/**
	 * 取字符串
	 * @param key
	 * @param dft 默认值
	 * @return
	 */
	public static String getStr(String key,String dft){
		if(key==null){
			return dft;
		}
		if(key.startsWith("head_")){
			return WebContext.getRequestHead(key.substring(5));
		}
		return get(key,dft);
	}
	
	/**
	 * 取字符串
	 * @param key
	 * @return
	 */
	public static String getStr(String key){
		return getStr(key,"");
	}
	
	/**
	 * 返回请求是否为POST请求
	 * @return
	 */
	public static boolean isPost(){
		HttpServletRequest request=WebContext.getRequest();
		if(request!=null){
			return request.getMethod().toLowerCase().equals("post");
		}
		return false;
	}
	
	/**
	 * 比较两个对象大小
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean gt(Object a,Object b){
		if(a==null || b==null){
			return a!=null || b!=null;
		}
		
		return a.toString().compareTo(b.toString())>0;
	}
	
	/**
	 * 取长时间yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String getLongTime(){
		return DateUtils.now(DateUtils.YMDHMS1);
	}

	/**
	 * 取短时间yyyy-MM-dd
	 * @return
	 */
	public static String getShortTime(){
		return DateUtils.now(DateUtils.YMD1);
	}
	
	/**
	 * 生成页面JS验证规则
	 * @param cls
	 * @param group
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String getValidations(Class cls,Class group){
		List<ValidDescription> list= ValidatorBuilder.generateValidationDescription(cls, group);
		if(list==null){
			return "[]";
		}
		try {
			//序列化成json
			return new ObjectMapper().writeValueAsString(list);
		} catch (JsonProcessingException e) {
			logger.error("验证描述转换成json失败", e);
			return "[]";
		}
	}
}
