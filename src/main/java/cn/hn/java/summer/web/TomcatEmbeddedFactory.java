package cn.hn.java.summer.web;

import org.apache.catalina.Context;
import org.springframework.boot.context.embedded.EmbeddedServletContainer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;

import java.io.File;

public class TomcatEmbeddedFactory extends TomcatEmbeddedServletContainerFactory{

	@Override
	protected void postProcessContext(Context context) {
		context.setDocBase(WebApp.webRootDir);
	}
	
	@Override
	public EmbeddedServletContainer getEmbeddedServletContainer(
			ServletContextInitializer... initializers) {
		
		logger.info("tomcat temp dir:"+WebApp.webRootDir);
		
		//tomcat临时目录
		this.setBaseDirectory(new File(WebApp.webRootDir, "temp"));
		return super.getEmbeddedServletContainer(initializers);
	}
	
}
