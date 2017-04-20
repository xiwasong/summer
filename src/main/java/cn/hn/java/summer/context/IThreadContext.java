package cn.hn.java.summer.context;

public interface IThreadContext {

	/**
	 * 获取上下文中的对应键的值
	 * @param key
	 * @return
	 */
	Object getAttribute(String key);

	/**
	 * 存储键值对到上下文中
	 * @param key
	 * @param value
	 */
	void setAttribute(String key,Object value);
	
	/**
	 * 取上下文(请求)中的参数
	 * @param key
	 * @return
	 */
	String getParameter(String key);
	
	/**
	 * 取上下文中其它信息
	 * @param key
	 * @return
	 */
	String getOtherInfo(String key);
}
