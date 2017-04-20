package cn.hn.java.summer.db.builder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.nio.file.*;

/**
 * 监控sql配置文件改动
 * @author sjg
 * @date 2014-08-22 10:07
 *
 */
public class SqlFileWatcher implements Runnable {
	static final Log logger = LogFactory.getLog(SqlBuilder.class);
	//windows系统
	static Boolean isWindows=false;
	
	private String watchPath;
	
	static{
		//标识是不是windows系统
		isWindows=(""+System.getProperty( "os.name" )).contains("indows");
	}
	
	public SqlFileWatcher(String path){
		if(path!=null){
			//去掉/E:/xx中的第一个字符
			if(isWindows && path.startsWith("/")){
				path=path.substring(1);
			}
			watchPath=path;
		}
	}

	@Override
	public void run() {
		if(watchPath==null){
			logger.error("monitor file dir is null");
			return;
		}
		
		try {
			WatchService watchService = FileSystems.getDefault().newWatchService();
	        Paths.get(watchPath).register(watchService,   
	                StandardWatchEventKinds.ENTRY_CREATE,  
	                StandardWatchEventKinds.ENTRY_DELETE,  
	                StandardWatchEventKinds.ENTRY_MODIFY);  
	        while(true)  
	        {  
	            WatchKey key=watchService.take();  
	            for(WatchEvent<?> event:key.pollEvents())  
	            {  
	            	logger.info("sql xml file monitor:"+event.context()+" has been "+event.kind());  
	            }  
	            logger.info("sql xml files may have been modified,reloading...");
	            //重新扫描sql并更新
	            SqlBuilder.scanSqlXml();
	            
	            if(!key.reset())  
	            {  
	                break;  
	            }  
	        }  
			
		} catch (IOException e) {
			logger.error("sql xml file monitor error",e);
		} catch (InterruptedException e) {
			logger.error("sql xml file monitor error",e);
		}
	}

}
