package cn.hn.java.summer.socket.command.codec;

import org.apache.commons.lang3.StringUtils;

import cn.hn.java.summer.exception.MessageCodeException;
import cn.hn.java.summer.utils.codec.Coder;

public abstract class LoginIdTools {
	
	public static final int LOGINID_LENGTH=32;
	
	public static final String DEFAULT_LOGINID="0-0-0-0-0-0-0-0-0-0-0-0-0-0-0-0-";

	/**
	 * 从loginId生成密码
	 * @param loginId
	 * @return
	 * @throws MessageCodeException
	 */
	public static String getPwdFromLoginId(String loginId) throws MessageCodeException{
		//取密码
		String pwd=loginId.substring(0,LOGINID_LENGTH);
		try {
			//取loginId中的数字
			String code=pwd.replaceAll("[^\\d]", "");
			//不足12位，补充0
			if(code.length()<12){
				code=code+"0000000000000";
			}
			//截取12位
			code=code.substring(0,12);
			
			return Coder.encryptBASE64(code.getBytes());
		} catch (Exception e) {
			throw new MessageCodeException("生成密码失败,错误码:0006", "0006", e);			
		}
	}
	
	/**
	 * 修正loginId
	 * @param loginId
	 * @return
	 */
	public static String fixLoginId(String loginId){
		return StringUtils.isBlank(loginId) || loginId.length()!=LOGINID_LENGTH ? DEFAULT_LOGINID:loginId;
	}
}
