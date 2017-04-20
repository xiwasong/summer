package cn.hn.java.summer.socket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;

import java.io.File;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

import javax.annotation.PreDestroy;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import cn.hn.java.summer.BaseApp;
import cn.hn.java.summer.socket.command.CommandBuilder;
import cn.hn.java.summer.utils.AppUtils;

public class BootstrapTCPServer extends BaseApp implements CommandLineRunner{
	static Log logger=LogFactory.getLog(BootstrapTCPServer.class);
	
	@Autowired
	private CommandBuilder commandBuilder;
	@Autowired
	private ServerBootstrap serverBootstrap;
	@Autowired
	private InetSocketAddress tcpPort;
	
	private Channel serverChannel;
	
	static{
		//扫描加载扩展jar包
		scanExtLib();
	}
	
	public void run(String... args) throws Exception {
		//初始化命令
		commandBuilder.setUp();
		//启动tcp服务器
		serverChannel =  serverBootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(64, 5120, 65536))
				.bind(tcpPort).sync().channel().closeFuture().sync().channel();
	}

	@PreDestroy
	public void stop() throws Exception {
	    serverChannel.close();
	    serverChannel.parent().close();
	}
	
	/**
	 * 扫描扩展库jar包
	 * @throws MalformedURLException 
	 */
	private static void scanExtLib(){
		//扩展包路径
		String jarDir;
		Method method=null;
		boolean accessible =false;
		try {
			String libPath=AppUtils.getAppLibPath();
			//获取扩展包路径,.toURI().getPath()处理路径中包含中文和空格的问题
			URL url=ClassLoader.getSystemResource(libPath+"/ext");
			if(url==null){
				logger.info("libPath:"+libPath+"/ext not exist!");
				return;
			}
			jarDir = url.toURI().getPath();
			//查找目录中的jar文件
			Collection<?> files= FileUtils.listFiles(new File(jarDir),new String[]{"jar"},true);
			if(files.size()==0){
				return;
			}
			// 对于jar文件，可以理解为一个存放class文件的文件夹  
		    method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);  
		    accessible = method.isAccessible();     // 获取方法的访问权限  
	        if (accessible == false) {  
	            method.setAccessible(true);     // 设置方法的访问权限  
	        }  
	        // 获取系统类加载器  
	        URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();  
			for(Object obj:files){
				url=((File)obj).toURI().toURL();
				method.invoke(classLoader, url);    
				System.out.println(url.getFile());
			}
	    }catch (URISyntaxException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}finally {  
	    	if(method!=null){
	    		method.setAccessible(accessible);
	    	}
	    }  
	}


}
