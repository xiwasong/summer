package cn.hn.java.summer.socket.config;

import org.apache.commons.lang3.StringUtils;

import cn.hn.java.summer.exception.CodeBusinessException;


public class ProtocolMessage implements IProtocolMessage {
	
	private int readIndex=0;

	private int connectType;
	
	private String cmdName;
	
	private String data;
	
	public ProtocolMessage(){
		
	}
	
	public ProtocolMessage(String data){
		this.data=data;
	}

	public ProtocolMessage(String data,String cmdName){
		this.data=data;
		this.cmdName=cmdName;
	}
	
	@Override
	public String getCmdName() {
		return this.cmdName;
	}

	@Override
	public int getConnectType() {
		return this.connectType;
	}

	@Override
	public String getData() {
		return this.data;
	}

	@Override
	public int getLength() {
		if(this.data==null){
			return 0;
		}
		return this.data.toString().length();
	}


	public void setConnectType(int connectType) {
		this.connectType = connectType;
	}

	public void setCmdName(String cmdName) {
		this.cmdName = cmdName;
	}

	private boolean ok=false;

	@Override
	public boolean isOk() {
		return this.ok;
	}

	@Override
	public void setOk(boolean f) {
		this.ok=f;
	}

	@Override
	public void setData(String data) {
		this.data=data;
	}
	
	/**
	 * 将消息字节数组转换为消息对象
	 * @param bytes
	 * @return
	 * @throws CodeBusinessException
	 */
	public static ProtocolMessage decodeFromBytes(byte[] bytes) throws CodeBusinessException{
		return new ProtocolMessage().decode(bytes);
	}
	
	/**
	 * 将消息字节数组转换为消息对象
	 * @param msg
	 * @return
	 * @throws CodeBusinessException
	 */
	private ProtocolMessage decode(byte[] msg) throws CodeBusinessException{
		String dataStr=new String(msg,IProtocolMessage.CHARSET);
		//连接类型
		int connectType=0;
		try{
			String strType=readField(dataStr,IProtocolMessage.CONNECTTYPE_LENGTH);
			connectType=Integer.parseInt(strType);
		}catch (NumberFormatException nfe) {
			throw new CodeBusinessException("error connectType, expect 0 or 1 !error code:0001.001","0001.001",nfe);
		}
		if(connectType!=IProtocolMessage.CONNETTYPE_SHORT && connectType!=IProtocolMessage.CONNETTYPE_LONG){
			throw new CodeBusinessException("error connectType, expect 0 or 1 !error code:0001.002","0001.002");
		}

		//命令名
		String cmdName=readField(dataStr,ProtocolMessage.COMMANDNAME_LENGTH);
		if(cmdName.length()!=IProtocolMessage.COMMANDNAME_LENGTH){
			throw new CodeBusinessException("error cmdName length! expect "+IProtocolMessage.COMMANDNAME_LENGTH+",error code:0001.003","0001.003");
		}

		//数据长度
		String dataLen=readField(dataStr,IProtocolMessage.DATALENGTH_LENGTH);
		if(dataLen.length()!=IProtocolMessage.DATALENGTH_LENGTH){
			throw new CodeBusinessException("error dataLen length! expect "+IProtocolMessage.DATALENGTH_LENGTH+",error code:0001.004","0001.004");
		}
		int dlen=0;
		try{
			dlen=Integer.parseInt(dataLen.trim());
		}catch (NumberFormatException nfe) {
			throw new CodeBusinessException("the dataLen field is not a number!error code:0001.001","0001.005",nfe);
		}
		
		//数据
		String data=readField(dataStr, dlen);
		if(data.length()!=dlen){
			throw new CodeBusinessException("the data length is not equal to dataLen field !error code:0001.006","0001.006");
		}
		
		ProtocolMessage pm=new ProtocolMessage();
		pm.setConnectType(connectType);
		pm.setCmdName(cmdName);
		pm.setData(data);
		return pm;
	}
	
	/**
	 * 读取字段值
	 * @param data
	 * @param len
	 * @return
	 */
	private String readField(String data,int len){
		String field=data.substring(readIndex,readIndex+len);
		readIndex+=len;
		return field;
	}
	
	/**
	 * 将消息对象转换为字节数组
	 * @param msg
	 * @return
	 */
	public static byte[] encodeFromMsg(ProtocolMessage msg){
		StringBuffer sb=new StringBuffer();
		//连接类型
		sb.append(msg.getConnectType())
		//命令名(不足30补空格)
		.append(StringUtils.rightPad(msg.getCmdName(),IProtocolMessage.COMMANDNAME_LENGTH," "))
		//数据长度(不足5补0)
		.append(StringUtils.leftPad(String.valueOf(msg.getLength()),IProtocolMessage.DATALENGTH_LENGTH,'0'))
		//数据内容
		.append(msg.getData());
		return sb.toString().getBytes(IProtocolMessage.CHARSET);
	}
}
