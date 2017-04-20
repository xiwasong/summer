package cn.hn.java.summer.mvc.log;

import java.lang.reflect.Method;
import java.util.List;

import cn.hn.java.summer.exception.SnException;

/**
 * action日志记录器接口
 * @author sjg
 * @version 1.0.1 2013-11-16
 *
 */
public interface IActionLogger {

	/**
	 * 取日志规则
	 * @return
	 */
	public List<LogRule> getLogRules() throws SnException;	
		
	/**
	 * 记录日志
	 * @param msg
	 */
	public void log(LogInfo msg,String exception);	
	
	/**
	 * 取要记录方法的规则
	 * @param mtd 拦截的方法
	 * @return
	 */
	public String getRule(Method mtd);
}
