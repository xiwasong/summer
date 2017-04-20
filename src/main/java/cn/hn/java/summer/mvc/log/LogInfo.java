package cn.hn.java.summer.mvc.log;

import java.util.Map;

/**
 * 日志信息
 * @author sjg
 * @version 1.0.1 2013-11-16
 *
 */
public class LogInfo {

	/**
	 * 日志类型
	 */
	private String logType;
	
	/**
	 * 记录日志规则(类名+方法)
	 */
	private String logRule;
	
	/**
	 * 日志格式
	 */
	private String logFormat;
	
	/**
	 * 参数
	 */
	private Map<String,Object> params;


	public String getLogFormat() {
		return logFormat;
	}

	public void setLogFormat(String logFormat) {
		this.logFormat = logFormat;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public String getLogRule() {
		return logRule;
	}

	public void setLogRule(String logRule) {
		this.logRule = logRule;
	}

	public String getLogType() {
		return logType;
	}

	public void setLogType(String logType) {
		this.logType = logType;
	}

}
