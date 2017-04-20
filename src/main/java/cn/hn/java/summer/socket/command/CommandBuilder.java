package cn.hn.java.summer.socket.command;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Component
public class CommandBuilder implements ApplicationContextAware {
	Log logger=LogFactory.getLog(getClass());

	private ApplicationContext applicationContext;
	
	private Map<String,CommandAction> commands;

	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		this.applicationContext=context;
	}
	
	public void setUp(){
		//初始化供外部调用的命令
		Map<String,Object> beans= this.applicationContext.getBeansWithAnnotation(Controller.class);
		logger.info("getBeansWithAnnotation"+beans.size());
		initAllCommand(beans);
	}
	
	/**
	 * 初始化所有命令
	 * @param cmdBeans
	 */
	private void initAllCommand(Map<String,Object> cmdBeans){
		commands=new HashMap<String, CommandAction>();
		for(String key:cmdBeans.keySet()){
			Object cmd=cmdBeans.get(key);
			commands.putAll(initCommandAction(cmd,cmd.getClass().getAnnotation(RequestMapping.class).value()[0]));
		}
	}
	
	/**
	 * 初始化命令action
	 */
	private Map<String,CommandAction> initCommandAction(Object cmd,String route){
		Map<String,CommandAction> actions=new HashMap<String, CommandAction>();
		
		//取出所有command的action
		Method[] mtds= cmd.getClass().getMethods();
		for(Method m:mtds){
			RequestMapping c= m.getAnnotation(RequestMapping.class);
			if(c!=null){
				String fullRoute=route+c.value()[0];
				//将多个连续的/替换为一个
				fullRoute=fullRoute.replaceAll("/+", "/");
				Class<?>[] types=m.getParameterTypes();
				if(types.length>1){
					logger.error(m.getName()+"命令方法参数多于1个");
					continue;
				}
				logger.debug("found command:"+fullRoute);
				actions.put(fullRoute, new CommandAction(fullRoute,cmd,m,types.length==0?null:types[0]));
			}
		}
		return actions;
	}
	
	/**
	 * 取路由对应的命令对象
	 * @param route
	 * @return
	 */
	public CommandAction getCommand(String route){
		return commands.get(route);
	}
	
}
