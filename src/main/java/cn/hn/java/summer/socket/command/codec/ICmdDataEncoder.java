package cn.hn.java.summer.socket.command.codec;

import cn.hn.java.summer.exception.MessageCodeException;


/**
 * 命令数据编码接口:将对象编码成字符串
 * @author sjg
 * @version 1.0.1 2014-1-19
 *
 */
public interface ICmdDataEncoder {

	/**
	 * 将对象编码成字符串
	 * @param obj
	 * @return
	 */
	public String encode(Object obj) throws MessageCodeException;
}
