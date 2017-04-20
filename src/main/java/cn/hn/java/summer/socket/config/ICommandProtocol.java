package cn.hn.java.summer.socket.config;

/**
 * 命令协议消息接口
 * @author sjg 2014-8-26 下午7:24:38
 *
 */
public interface ICommandProtocol {	
	
	/**
	 * 通信编码方案
	 */
	public static final String CHARSET="UTF-8";

	/**
	 * 连接类型
	 * 0:短连接
	 * 1:长连接
	 * @return
	 */
	public int getConnectType();
	
	public void setConnectType(int t);
	
	/**
	 * 命令名
	 * @return
	 */
	public String getCmdName();
	
	public void setCmdName(String cmdName);
	
	/**
	 * 数据长度
	 * @return
	 */
	public int getLength();
	
	/**
	 * 数据
	 * @return
	 */
	public String getData();	
	
	/**
	 * 设置消息内容
	 * @param d
	 */
	public void setData(String data);
}
