package cn.hn.java.summer.socket.handler;

import java.lang.reflect.Method;

/**
 * 命令方法
 * @author sjg
 * @version 1.0.1 2014-1-19
 *
 */
public class CommandAction {
	
	private String route;

	private Object command;
	
	private Method action;
	
	/**
	 * 命令方法参数类型
	 */
	private Class<?> paramType;
	
	public Class<?> getParamType() {
		return paramType;
	}

	public void setParamType(Class<?> paramType) {
		this.paramType = paramType;
	}

	public CommandAction(){
		
	}

	public CommandAction(String route,Object command,Method action){
		this.route=route;
		this.command=command;
		this.action=action;
	}
	
	public CommandAction(String route,Object command,Method action,Class<?> paramType){
		this.route=route;
		this.command=command;
		this.action=action;
		this.paramType=paramType;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public Object getCommand() {
		return command;
	}

	public void setCommand(Object command) {
		this.command = command;
	}

	public Method getAction() {
		return action;
	}

	public void setAction(Method action) {
		this.action = action;
	}
	
	
}
