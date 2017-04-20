package cn.hn.java.summer.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * json工具类
 * @author sjg
 * 2017年2月25日 下午7:56:47
 *
 */
public class JsonUtils {
	static Log logger=LogFactory.getLog(JsonUtils.class);
	
	/**
	 * 将对象转换成json字符串
	 * @param data
	 * @return
	 */
	public static String toJson(Object data){
		try {
			return new ObjectMapper().writeValueAsString(data);
		} catch (JsonProcessingException e) {
			logger.error("toJson error!", e);
			return "{}";
		}
	}
}
