package cn.hn.java.summer.socket.command.codec;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.hn.java.summer.exception.MessageCodeException;
import cn.hn.java.summer.utils.ReflectUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * json数据解码
 * @author sjg
 * @version 1.0.1 2014-1-20
 *
 */
public class JsonDataDecoder implements ICmdDataDecoder {
	protected Log logger=LogFactory.getLog(getClass());

	/**
	 * 将字符串解码成对象
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T decode(String data,Class<T> dest) throws MessageCodeException{
		//无数据不解析json
		if(data==null || data.equals("")){
			return null;
		}
		
		Map<String,Object> map=null;
		try {			
			//原始类型,直接返回解析结果
			if(dest.getName().indexOf(".")==-1 || dest.getName().indexOf("java.lang.")==0){
				return new ObjectMapper().readValue(data,dest);
			}
			//其它类型
			map= new ObjectMapper().readValue(data, Map.class);
		} catch (Exception e) {
			throw new MessageCodeException("json解析失败,错误码:0002","0002", e);
		}
		if(map!=null && map.size()>0){
			//复制值到对象
			T obj=ReflectUtils.copyValues(map,dest);
			if(obj!=null){
				return obj;
			}else{
				throw new MessageCodeException("json解析失败,错误码:0003","0003");
			}
		}
		logger.warn("json解析结果为空");
		return null;
	}

}
