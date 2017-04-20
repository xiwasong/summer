package cn.hn.java.summer.mvc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.hn.java.summer.constants.Default;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * json映射器
 * @author sjg
 * @version 1.0.1 2013-09-27
 *
 */
public class SnJsonMaper extends ObjectMapper {
	private static final long serialVersionUID = 1L;
	protected final Log logger = LogFactory.getLog(getClass());	

	public Object getObj(Object obj){
		return obj;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	/**
	 * 包装json输出
	 */
	public void writeValue(JsonGenerator arg0, Object arg1) throws IOException,
			JsonGenerationException, JsonMappingException {
		boolean noException=true;		
		
		if(arg1 instanceof HashMap){			
			HashMap dataMap = (HashMap)arg1;			
			/**开发时输出map的键，以便参考*/			
			if(logger.isDebugEnabled()){
				Iterator exs=dataMap.keySet().iterator();
				while(exs.hasNext()){
					logger.debug(exs.next());	
				}
			}
			
			Map<String,Object> _mp=dataMap;
			Map<String,Object> resultMap=null;
			
			//异常json输出，result为false
			noException=!dataMap.containsKey("exception");
			
			if(noException){
				//是否有业务异常
				Object bizException=WebContext.getRequestAttribute(Default.ERROR_IN_REQUEST_KEY);
				if(bizException!=null){
					noException=false;
					//显示业务异常
					dataMap.put("exception", bizException);
				}
			}
			
			if(noException){
				//action返回对象键
				String resultKey=WebContext.getRequestAttribute(Default.ACTIONRESULT_IN_REQUEST_KEY);
				_mp=new HashMap<String, Object>();
				//包含了要返回对象的键
				if(dataMap.containsKey(resultKey)){
					//对于返回的原始类型，手动Result包装
					if("result".equals(resultKey)){
						super.writeValue(arg0,dataMap.get(resultKey));
						return;
					}else{
						//其它类型
						resultMap=genResultMap(dataMap.get(resultKey));
					}
				}else if("void".equals(resultKey)){
					//对于void类型，未产生异常时，认它是成功的，只是没有返回数据
					resultMap=new HashMap<String,Object>();
					resultMap.put("result", true);
				}else if(resultKey.contains(Default.MUTIPLE_RESULT_PREFIX)){
					//多值键，有多个返回值的键
					Map<String, Object> data=new HashMap<String, Object>();
					resultKey=resultKey.replaceFirst(Default.MUTIPLE_RESULT_PREFIX, "");
					//取到多值的每个键
					String[] mutipleKey=resultKey.split(",");
					for(String key : mutipleKey){
						data.put(key, WebContext.getRequestAttribute(key));
					}
					resultMap=genResultMap(data);
				}else{
					//未正常返回值，如产生异常、返回的原始类型未包装成Result，很可能是controller拦截器配置的包与实际的不一致action执行的结果未结果拦截器处理导致找不到返回值的键
					noException=false;
					_mp.put("exception", "JsonMaper找到键["+resultKey+"]的返回值为空");
				}
			}
			
			//有异常时输出异常信息
			if(!noException){
				resultMap=new HashMap<String,Object>();
				resultMap.put("result", false);
				resultMap.put("data", getObj(_mp.get("exception")));
			}
			
			super.writeValue(arg0, resultMap);
			return;
		}
		
		super.writeValue(arg0, new Result(noException, arg1));
	}
	
	/**
	 * 生成正常返回值的结果map
	 * @param data
	 * @return
	 */
	private Map<String, Object> genResultMap(Object data){
		//其它类型
		Map<String, Object> resultMap=new HashMap<String,Object>();
		resultMap.put("result", true);
		resultMap.put("data", getObj(data));
		
		//处理分页
		Integer recordTotal=ViewTools.getRecordTotal(-1);
		if(recordTotal!=-1){
			resultMap.put("recordTotal", recordTotal);
		}
		return resultMap;
	}

}
