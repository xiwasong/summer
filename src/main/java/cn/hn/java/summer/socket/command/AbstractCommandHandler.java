package cn.hn.java.summer.socket.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cn.hn.java.summer.exception.CodeBusinessException;
import cn.hn.java.summer.exception.IExceptionData;
import cn.hn.java.summer.exception.MessageCodeException;
import cn.hn.java.summer.socket.config.ICommandProtocol;
import cn.hn.java.summer.utils.ExceptionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public abstract class AbstractCommandHandler implements ICommandHandler{
	protected Log logger=LogFactory.getLog(getClass());
		
	@Autowired
	CommandBuilder commandBuilder;
	
	/**
	 * 是否暴露异常信息
	 */
	private Boolean exposeExceptionMsg;
	
	public CommandAction getCommand(String route) {
		return commandBuilder.getCommand(route);
	}
	
	public Object invokeCmd(ICommandProtocol msg) throws MessageCodeException {	
		//取可执行命令对象
		CommandAction action= commandBuilder.getCommand(msg.getCmdName().trim());
		if(action==null){
			logger.info(msg.getCmdName().trim());
			throw new MessageCodeException("命令方法调用失败,错误码：0001","0001");
		}
		
		Object[] args=new Object[0];
		Object dataObj= args=new Object[0];
		Class<?> type= action.getParamType();
		//有参数
		if(type!=null && msg.getData()!=null){
			dataObj= this.getDataDecoder().decode(msg.getData(), action.getParamType());
			if(dataObj!=null){
				args=new Object[]{dataObj};	
			}			
		}
		
		String objJson="see above";
		//debug模式输出参数值json
		if(logger.isDebugEnabled()){
			try {
				objJson=new ObjectMapper().writeValueAsString(dataObj);
			} catch (JsonProcessingException e1) {
				logger.error("start invoke",e1);
			}
		}
		
		logger.info("start invoke command===\n route:"+action.getRoute()+", arg type:"+(type==null?"void":type.getName())+", arg value:"+objJson);
		
		Method m=action.getAction();
		//调用命令方法
		try {
			return m.invoke(action.getCommand(), args);
		}catch(InvocationTargetException ite){
			Throwable targetException=ite.getTargetException();
			//业务异常
			if(targetException instanceof CodeBusinessException){
				//普通业务异常
				CodeBusinessException cbe=(CodeBusinessException)ite.getTargetException();
				MessageCodeException mce=new MessageCodeException(cbe.getMessage(),cbe.getCode(),true,ite);
				//包含异常数据的异常
				if(targetException instanceof IExceptionData){
					//取出数据
					IExceptionData ed=(IExceptionData)targetException;
					mce.setData(ed.getData());
				}
				throw mce;
			}
			//错误码0001:命令方法调用失败
			throw new MessageCodeException("命令方法调用失败,错误码：0001. "+ExceptionUtils.getExceptionMessage(ite),"0001",ite);
		}catch (Exception e) {	
			if(e instanceof MessageCodeException){
				throw (MessageCodeException)e;
			}
			//错误码0001:命令方法调用失败
			throw new MessageCodeException("命令方法调用失败,错误码：0001. "+ExceptionUtils.getExceptionMessage(e),"0001",e);
		}
	}

	@Override
	public Boolean isExposeExceptionMsg() {
		if(exposeExceptionMsg==null){
			return false;
		}
		return this.exposeExceptionMsg;
	}

	public void setExposeExceptionMsg(Boolean exposeExceptionMsg) {
		this.exposeExceptionMsg = exposeExceptionMsg;
	}
	
	
}
