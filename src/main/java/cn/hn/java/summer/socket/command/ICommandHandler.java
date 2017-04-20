package cn.hn.java.summer.socket.command;

import cn.hn.java.summer.exception.MessageCodeException;
import cn.hn.java.summer.socket.command.codec.ICmdDataDecoder;
import cn.hn.java.summer.socket.command.codec.ICmdDataEncoder;
import cn.hn.java.summer.socket.config.ICommandProtocol;


/**
 * 命令处理接口
 * @author sjg
 * @version 1.0.1 2014-1-19
 *
 */
public interface ICommandHandler{

	Object invokeCmd(ICommandProtocol msg) throws MessageCodeException;
	
	CommandAction getCommand(String route);
	
	ICmdDataDecoder getDataDecoder();
	
	ICmdDataEncoder getDataEncoder();
	
	/**
	 * 是否暴露异常信息
	 * 为false时异常信息只显示错误码
	 * 为true时异常信息还包含异常具体描述
	 * @return
	 */
	Boolean isExposeExceptionMsg(); 
}
