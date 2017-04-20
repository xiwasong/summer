package cn.hn.java.summer.cache;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 需要实现缓存操作的基类
 * @author sjg
 * @version 1.0.1 2013-9-27
 *
 */
public abstract class CachedBase implements ApplicationContextAware {

	protected final Log logger = LogFactory.getLog(getClass());
	private ApplicationContext applicationContext;

	@Autowired
	CacheManager cacheManager;
	
	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		this.applicationContext=context;
	}
	
	/**
	 * 取缓存的对象
	 * @param <T>
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> Map<String,T> getCachedMap(String key){
		ValueWrapper vw= cacheManager.getCache("default").get(key);
		if(vw!=null){
			return (Map<String,T>)vw;
		}
		logger.warn("未找到缓存 getCachedValue key:"+key);
		return new HashMap<String, T>();
	}	
	
	/**
	 * 获取指定类型的bean对象
	 * @param cls
	 * @return
	 */
	public <T> T getBean(Class<T> cls){
		/**
		String beanName=cls.getSimpleName();
		beanName=beanName.toLowerCase().substring(0, 1)+beanName.substring(1);
		return this.applicationContext.getBean(beanName);
		*/
		
		return applicationContext.getBean(cls);
	}
	
	/**
	 * 取自身的bean对象,用于执行自身类中的其它需要缓存的方法
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getSelf(){
		return (T)getBean(getClass());
	}
	
	/**
	 * 取自身接口类型的bean对象,用于执行自身类中的其它需要缓存的方法
	 * @param icls
	 * @return
	 */
	public <T> T getSelf(Class<T> icls){
		return getBean(icls);
	}
}
