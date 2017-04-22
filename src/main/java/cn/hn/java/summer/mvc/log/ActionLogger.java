package cn.hn.java.summer.mvc.log;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import cn.hn.java.summer.exception.SummerException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.ui.Model;

import cn.hn.java.summer.mvc.WebContext;
import cn.hn.java.summer.utils.ReflectUtils;


/**
 * Action日志记录器
 * @author sjg
 * @version 1.0.1 2013-11-16
 *
 */
public class ActionLogger {	

	private static final Log logger = LogFactory.getLog(ActionLogger.class);	

	/**
	 * 日志记录规则
	 */
	private static Map<String,LogRule> LoggingRules=null;
	
	/**
	 * 日志读写
	 */
	private static IActionLogger actionLogger;
	
	private static boolean noLogger=false;
	
	/**
	 * 记录指定方法名的操作日志
	 * @param applicationContext 应用上下文
	 * @param method
	 * @param args 目标方法参数
	 * @param exception 异常信息
	 * @throws SummerException
	 */
	public static void log(ApplicationContext applicationContext,Method mtd,Object[] args, String exception) throws SummerException {
		//未安装完系统，不能记录日志
		if(!WebContext.installed){
			return ;
		}
		
		
		//初始化日志规则格式
		initRules(applicationContext);
		
		if(noLogger){
			return;
		}
		
		String target="";
		
		//取方法规则
		target=actionLogger.getRule(mtd);
		
		//记录对应方法日志
		if(LoggingRules.containsKey(target)){			
			try{
				actionLogger.log(getArgs(target,args),exception);
			}catch (Exception e) {
				//忽略错误
				logger.error("记录操作日志出错",e);
			}
		}
	}
	
	/**
	 * 初始化规则
	 * @throws SummerException
	 */
	private synchronized static void initRules(ApplicationContext context) throws SummerException {
		if(LoggingRules==null){
			try{
				//查找加载的包中实现了IActionLogger类的实例
				actionLogger=context.getBean(IActionLogger.class);
			}catch (Exception e) {
				noLogger=true;
				return;
			}
			//取日志规则
			List<LogRule> rules= actionLogger.getLogRules();
			LoggingRules=new HashMap<String, LogRule>();
			for(LogRule lr : rules){
				LoggingRules.put(lr.getLogRule(), lr);
			}
		}
	}
	
	/**
	 * 取操作方法参数
	 * @param method
	 * @param args
	 * @return
	 */
	private static LogInfo getArgs(String rule,Object[] args){
		//取日志规则
		LogRule format= LoggingRules.get(rule);
		
		LogInfo info= new LogInfo();		
		info.setLogType(format.getLogType());
		info.setLogRule(rule);
		info.setLogFormat(format.getLogFormat());
		
		Map<String,Object> kvs=new HashMap<String, Object>();
		
		//取参数键和值
		for(int i=0; i<args.length; i++){
			//忽略常用非自定义对象
			if(
				args[i] instanceof Model ||
				args[i] instanceof ServletRequest ||
				args[i] instanceof ServletResponse
				){
				continue;
			}
			//单值对象
			if(args[i].getClass().getName().startsWith("java.lang")){
				kvs.put(i+"", args[i]);
			}else{
				//自定义对象键值
				kvs.putAll(ReflectUtils.getAllFieldValues(args[i]));
			}
		}
		
		info.setParams(kvs);
		
		return info;
	}
	
}
