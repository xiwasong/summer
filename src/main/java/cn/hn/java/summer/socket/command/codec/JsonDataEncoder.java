package cn.hn.java.summer.socket.command.codec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.hn.java.summer.exception.MessageCodeException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * json数据编码
 * @author sjg
 * @version 1.0.1 2014-1-20
 *
 */
public class JsonDataEncoder implements ICmdDataEncoder {
	protected Log logger=LogFactory.getLog(getClass());

	@Override
	public String encode(Object obj) throws MessageCodeException {
		try {
			if(obj==null){
				return null;
			}
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new MessageCodeException("json编码失败,错误码:0005", "0005", e);
		}
	}

}
