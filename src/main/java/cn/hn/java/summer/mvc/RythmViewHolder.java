package cn.hn.java.summer.mvc;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rythmengine.Rythm;
import org.rythmengine.RythmEngine;
import org.rythmengine.spring.web.RythmConfigurer;
import org.rythmengine.spring.web.RythmHolder;
import org.rythmengine.template.JavaTagBase;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.support.WebApplicationObjectSupport;

import cn.hn.java.summer.mvc.tag.DateFormatTag;
import cn.hn.java.summer.mvc.tag.EqOutTag;
import cn.hn.java.summer.mvc.tag.ErrorTag;
import cn.hn.java.summer.mvc.tag.FullUrlTag;
import cn.hn.java.summer.mvc.tag.UrlTag;
import cn.hn.java.summer.mvc.tag.ValTag;

public class RythmViewHolder extends WebApplicationObjectSupport implements RythmHolder {
	
	private String templateHome;
	private String mode;
	
	public RythmViewHolder(String templateHome,String mode){
		this.templateHome=templateHome;
		this.mode=mode;
	}
	
	public RythmEngine getRythmEngine() {
		Map<String, Object> confMap=new HashMap<String, Object>();

		confMap.put("home.template.dir", templateHome);
		//设置存放生成页面类的临时目录
		String tempDir=templateHome+"/temp";
		System.setProperty("java.io.tmpdir",tempDir);
		//每次启动时先清除原来的临时文件
		new File(tempDir).delete();
		
		//开发模式
		confMap.put("engine.mode", Rythm.Mode.valueOf(mode));
		RythmEngine engine=new RythmEngine(confMap);
		//输出请求参数
		engine.setProperty(RythmConfigurer.CONF_OUTOUT_REQ_PARAMS, true);
		
		for(URI uri : engine.conf().templateHome()){
			if(uri!=null){
				System.out.println("template home:"+uri.toString());
			}
		}
		
		//注册标签
		configBuildInTag(engine);
		
		return engine;
	}
	
	  protected void configBuildInTag(RythmEngine engine) {
	    AutowireCapableBeanFactory factory = getApplicationContext().getAutowireCapableBeanFactory();
	    for (Class<?> clazz : defaultTagClasses()) {
	      Object tag = factory.autowire(clazz, 3, false);
	      engine.registerFastTag((JavaTagBase) tag);
	    }
	  }
	  
	  protected List<Class<?>> defaultTagClasses() {
	    List<Class<?>> classes = new ArrayList<Class<?>>();
	    classes.add(UrlTag.class);
	    classes.add(FullUrlTag.class);
	    classes.add(DateFormatTag.class);
	    classes.add(ValTag.class);
	    classes.add(ErrorTag.class);
	    classes.add(EqOutTag.class);
	    return classes;
	  }
}
