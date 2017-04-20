package cn.hn.java.summer.socket.command.codec;

import org.springframework.stereotype.Component;

import cn.hn.java.summer.exception.MessageCodeException;
import cn.hn.java.summer.socket.command.ILoginId;


/**
 * 前有loginId的数据解析
 * @author sjg
 * @version 1.0.1 2014-1-20
 *
 */
@Component
public class LoginIdDataDecoder extends JsonDataDecoder {
	
	private String loginId;
	
	@Override
	public <T> T decode(String data,Class<T> dest) throws MessageCodeException{
		if(data.length()<LoginIdTools.LOGINID_LENGTH){
			throw new MessageCodeException("数据格式不正确,无loginId,错误码:0007", "0007");
		}
		this.loginId=data.substring(0, LoginIdTools.LOGINID_LENGTH);
		
		//this.setAesPwd(LoginIdTools.getPwdFromLoginId(this.loginId));
		
		data=data.substring(LoginIdTools.LOGINID_LENGTH);
		//取解码后生成的对象
		T result= super.decode(data, dest);
		for(Class<?> itf: dest.getInterfaces()){
			if(itf==ILoginId.class){
				//如果是ILoginId对象，则需要设置loginId
				if(result==null){
					try {
						result=dest.newInstance();
					} catch (Exception e) {
						logger.error(dest.getName()+":生成参数实例失败",e);
					}
				}
				if(result!=null){
					((ILoginId) result).setLoginId(this.loginId);
				}
			}
		}
		
		
		return result;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
}
