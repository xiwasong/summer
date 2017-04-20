package cn.hn.java.summer.socket.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.hn.java.summer.exception.MessageCodeException;
import cn.hn.java.summer.socket.command.codec.ICmdDataDecoder;
import cn.hn.java.summer.socket.command.codec.ICmdDataEncoder;
import cn.hn.java.summer.socket.command.codec.LoginIdDataDecoder;
import cn.hn.java.summer.socket.command.codec.LoginIdDataEncoder;
import cn.hn.java.summer.socket.command.codec.LoginIdTools;
import cn.hn.java.summer.socket.config.ICommandProtocol;


@Component
public class LoginIdCommandHandler extends AbstractCommandHandler {
	
	@Autowired
	LoginIdDataDecoder cmdDataDecoder;
	
	@Autowired
	LoginIdDataEncoder cmdDataEncoder;
	
	private String loginId;

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		cmdDataEncoder.setLoginId(loginId);
		this.loginId = loginId;
	}

	@Override
	public ICmdDataDecoder getDataDecoder() {
		return cmdDataDecoder;
	}

	@Override
	public ICmdDataEncoder getDataEncoder() {
		return cmdDataEncoder;
	}
	
	@Override
	public Object invokeCmd(ICommandProtocol msg) throws MessageCodeException {
		
		String loginId="";
		if(msg.getData()!=null){
			loginId=msg.getData().toString().substring(0, LoginIdTools.LOGINID_LENGTH);
		}
		
		//设置loginId
		this.setLoginId(loginId);
		Object rst= super.invokeCmd(msg);
		//重新设置生成的loginId
		if(rst!=null && rst instanceof ILoginId){
			this.setLoginId(((ILoginId)rst).getLoginId());
		}
		return rst;
	}

}
