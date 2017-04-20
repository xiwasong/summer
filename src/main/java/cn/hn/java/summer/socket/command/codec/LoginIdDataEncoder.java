package cn.hn.java.summer.socket.command.codec;

import org.springframework.stereotype.Component;

import cn.hn.java.summer.exception.MessageCodeException;

/**
 * json数据编码
 * @author sjg
 * @version 1.0.1 2014-1-20
 *
 */
@Component
public class LoginIdDataEncoder extends JsonDataEncoder {

	private String loginId;
	

	public String getLoginId() {
		return loginId;
	}


	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	
	@Override
	public String encode(Object obj) throws MessageCodeException {
		//this.setAesPwd(LoginIdTools.getPwdFromLoginId(this.loginId));
		String encodeStr=super.encode(obj);
		//无数据不返回null
		return LoginIdTools.fixLoginId(this.loginId)+(encodeStr==null?"":encodeStr);
	}

}
