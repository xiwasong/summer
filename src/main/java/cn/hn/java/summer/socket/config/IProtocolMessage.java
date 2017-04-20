package cn.hn.java.summer.socket.config;

import java.nio.charset.Charset;


/**
 * 协议消息接口
 * @author sjg
 * @version 1.0.1 2014-1-19
 *
 */
public interface IProtocolMessage extends ICommandProtocol {

	/**
	 * 短连接
	 */
	public static final int CONNETTYPE_SHORT=0;
	/**
	 * 长连接
	 */
	public static final int CONNETTYPE_LONG=1;
	
	/**
	 * 连接类型长度
	 */
	public static final int CONNECTTYPE_LENGTH=1;
	
	/**
	 * 命令名长度
	 */
	public static final int COMMANDNAME_LENGTH=30;
	
	/**
	 * "数据长度"长度
	 */
	public static final int DATALENGTH_LENGTH=5;
	
	/**
	 * 数据包长度
	 */
	public static final int PACKAGE_SIZE=1800;
	
	/**
	 * 通信编码方案
	 */
	public static final String CHARSET_NAME="UTF-8";
	/**
	 * 通信编码方案
	 */
	public static final Charset CHARSET=Charset.forName(CHARSET_NAME);

		
	/**
	 * 数据长度
	 * @return
	 */
	public int getLength();
		
	/**
	 * 状态是否正确
	 * @return
	 */
	public boolean isOk();
	public void setOk(boolean f);
}
