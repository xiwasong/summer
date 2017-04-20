package cn.hn.java.summer.socket.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.hn.java.summer.exception.MessageCodeException;
import cn.hn.java.summer.socket.command.ICommandHandler;
import cn.hn.java.summer.socket.config.ICommandProtocol;


public class HandlerProcess {
	static final Log logger=LogFactory.getLog(HandlerProcess.class);

	/**
	 * 调用协议中的命令方法
	 * @param session
	 * @param commandHandler
	 * @param msg
	 */
	public static ICommandProtocol invockCommand(ICommandHandler commandHandler,ICommandProtocol msg){
		CommandResult rst=new CommandResult();
		try {
			Object objRst=commandHandler.invokeCmd(msg);
			//调用成功，包装结果
			rst.setResult(true);
			rst.setData(objRst);
		}catch(MessageCodeException e) {
			rst.setResult(false);
			//非业务异常才记录错误日志
			if(!e.isBusiness()){
				logger.error("命令方法调用失败,错误码："+e.getCode(), e);	
			}else{
				logger.error("业务异常："+e.getMessage());	
			}
			
			//如果允许暴露异常信息则设置信息
			String errorMsg=commandHandler.isExposeExceptionMsg()==true?(e.getMsg()==null?e.getMessage():e.getMsg()):null;
			
			//包含数据的异常
			if(e.getData()!=null){
				rst.setExceptionData(e.getData(), e.getCode(), errorMsg);
			}else{
				//普通异常
				rst.setData(e.getCode(),errorMsg);
			}
		}
				
		//编码结果，包装成返回消息
		try {
			msg.setData(commandHandler.getDataEncoder().encode(rst));			
			return msg;
		} catch (Exception e) {
			logger.error("包装返回消息发生异常",e);
			return null;
		}
	}
}
