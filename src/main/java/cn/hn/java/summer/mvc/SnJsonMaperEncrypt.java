package cn.hn.java.summer.mvc;

import cn.hn.java.summer.utils.codec.AESCoder;

import com.fasterxml.jackson.core.JsonProcessingException;

public class SnJsonMaperEncrypt extends SnJsonMaper{
	private static final long serialVersionUID = 1L;
	@Override
	public Object getObj(Object obj){
		try {
			//将对象转换成json字符串
			String strjson = super.writeValueAsString(obj); 
			//从上下文中取出秘钥
			String sign = WebContext.getRequestAttribute("encryptKey");
			if(sign == null) return obj;//如果没有找到秘钥就直接返回
			//加密并返回
			return AESCoder.encryptBase64(strjson,sign);
		} catch (JsonProcessingException e) {
			logger.error("返回结果进行加密时出错",e);
			return "obj转换成json格式时出错";
		}
	}
}
