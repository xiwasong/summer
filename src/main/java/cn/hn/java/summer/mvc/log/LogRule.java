package cn.hn.java.summer.mvc.log;

/**
 * 日志规则类
 * @author sjg
 * @version 1.0.1 2013-11-16
 *
 */
public class LogRule {

	/**
	 * 日志类别
	 */
	private String logType;
	
	/**
	 * 记录日志规则(类名+方法)
	 */
	private String logRule;
	
	/**
	 * 日志内容格式
	 */
	private String logFormat;
	
	 
	public LogRule() {
		super();
		// TODO Auto-generated constructor stub
	}


	public LogRule(String logRule, String logFormat) {
		super();
		this.logRule = logRule;
		this.logFormat = logFormat;
	}


	public LogRule(String logType, String logRule, String logFormat) {
		super();
		this.logType = logType;
		this.logRule = logRule;
		this.logFormat = logFormat;
	}


	public String getLogFormat() {
		return logFormat;
	}

	public void setLogFormat(String logFormat) {
		this.logFormat = logFormat;
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
