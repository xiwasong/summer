package cn.hn.java.summer.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
public class PropertiesUtils {
	static final Log logger = LogFactory.getLog(PropertiesUtils.class);
	
	/**
	 * 取属性文件键值
	 * @param file 文件名 systemParameter.properties
	 * @param names 要取的属性
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String,String> getPros(String file,String... names){
		Map<String,String> pros=new HashMap<String,String>();
		InputStream isPros=null;
		Properties pro=null;
		try {
			//在当前路径下的文件
			if(file!=null && file.startsWith("classpath:")){
				file=file.replaceFirst("classpath:", "");
				isPros=PropertiesUtils.class.getClassLoader().getResourceAsStream(file);
			}else{
				isPros=new FileInputStream(file);
			}
			
			pro=new Properties();
			pro.load(isPros);
		} catch (IOException e) {			
			logger.error("读取属性文件出错:"+file,e);
		}finally{
			if(isPros!=null){
				try {
					isPros.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}
		//取需要的属性
		if(names!=null && names.length>0){
			for(String p : names){
				pros.put(p, pro.getProperty(p));
			}
		}else{
			//取全部
			Enumeration all= pro.keys();
			String key;
			while(all.hasMoreElements()){
				key=all.nextElement().toString();
				pros.put(key, pro.getProperty(key));
			}
		}
		
		return pros;
	}
}
