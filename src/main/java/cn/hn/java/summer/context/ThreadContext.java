package cn.hn.java.summer.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * c/s线程上下文
 * @author sjg
 * @version 1.0.1 2014-6-16
 *
 */
public class ThreadContext implements IThreadContext {

	private static Map<String, Object> contextObjects=new ConcurrentHashMap<>();
		
	/**
	 * 存储键值对到上下文中
	 * @param key
	 * @param value
	 */
	public static void put(String key,Object value){
		contextObjects.put(key, value);
	}

	/**
	 * 获取上下文中的对应键的值
	 * @param key
	 * @return
	 */
	public Object getAttribute(String key) {		
		return contextObjects.get(key);
	}

	/**
	 * 存储键值对到上下文中
	 * @param key
	 * @param value
	 */
	public void setAttribute(String key, Object value) {
		contextObjects.put(key, value);
	}

	public String getParameter(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOtherInfo(String key) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
