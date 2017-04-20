package cn.hn.java.summer.socket.command.codec;

import cn.hn.java.summer.exception.MessageCodeException;


/**
 * 命令数据解码接口:将字符串解码成对象
 * @author sjg
 * @version 1.0.1 2014-1-19
 *
 */
public interface ICmdDataDecoder {

	/**
	 * 将字符串解码成对象
	 * @param data
	 * @param dest 目标对象类
	 * @return
	 */
	public <T> T decode(String data,Class<T> dest) throws MessageCodeException;
}
