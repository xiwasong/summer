package cn.hn.java.summer.utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 和应用程序相关的工具类
 * @author sjg
 * 2016年12月30日 上午9:38:48
 *
 */
public class AppUtils {
	static Log logger=LogFactory.getLog(AppUtils.class);
	
	public static final String APP_README_FILE="README.txt";


	private static String rootPath=null;
	/**
	 * 取程序运行根目录，异常时返回null
	 * @return
	 */
	public static String getAppRoot(){
		if(rootPath!=null){
			return rootPath;
		}
		try {
			//如果是tomcat容器,取下WEB-INF目录作为程序的根目录
			URL rootURL=FileUtils.class.getClassLoader().getResource("/");
			if(rootURL!=null){
				rootPath = new File(new URI(rootURL.toString())).getParent();
			}
		} catch (Exception e) {
			logger.error(e);
		}
		//否则以程序启动的目录作为程序的根目录
		if(rootPath==null){
			File directory	= new File(".");
			try {
				rootPath = directory.getCanonicalPath();
			} catch (IOException e) {
				logger.error(e);
			}
		}
		
		return rootPath;
	}

	/**
	 * 取应用配置目录路径
	 * @return
	 */
	public static String getAppConfigPath(){
		return getAppRoot()+"/config";
	}
	
	/**
	 * 取应用lib库目录路径
	 * @return
	 */
	public static String getAppLibPath(){
		return getAppRoot()+"/lib";
	}
	
	/**
	 * 取应用doc目录路径
	 * @return
	 */
	public static String getAppDocPath(){
		return getAppRoot()+"/doc";
	}
	
	/**
	 * 获取应用目录下doc/README.txt的内容
	 * 包括程序版本、日期等相关信息
	 * @return
	 */
	public static String getReadmeInfo(){
		String docPath=getAppDocPath();
		//应用目录/doc/README.txt
		docPath=docPath+"/"+APP_README_FILE;
		File docFile=new File(docPath);
		if(docFile==null || !docFile.exists()){
			logger.error("no readme file in /doc!");
			return "";
		}
		
		try {
			return FileUtils.readFileToString(docFile,"utf-8");
		} catch (IOException e) {
			logger.error("getReadmeInfo error!", e);
			return "";
		}
	}
}
