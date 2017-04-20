package cn.hn.java.summer.cache;

import cn.hn.java.summer.utils.AppUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;

/**
 * 缓存配置
 * @author sjg
 * 2017年2月17日 下午5:24:03
 *
 */
@EnableCaching//标注启动缓存.
public class CacheConfiguration {
   
    /**
     *  ehcache 主要的管理器
     * @return
     */
    @Bean
    public EhCacheCacheManager ehCacheCacheManager(EhCacheManagerFactoryBean bean){
       return new EhCacheCacheManager(bean.getObject());
    }
   
    /*
       * 据shared与否的设置,
       * Spring分别通过CacheManager.create()
       * 或new CacheManager()方式来创建一个ehcache基地.
       *
       * 也说是说通过这个来设置cache的基地是这里的Spring独用,还是跟别的(如hibernate的Ehcache共享)
       *
       */
      @Bean
      public EhCacheManagerFactoryBean ehCacheManagerFactoryBean(
    		  @Value("${ehcache.path}") final String ehcachePath){
        EhCacheManagerFactoryBean cacheManagerFactoryBean = new EhCacheManagerFactoryBean ();
        cacheManagerFactoryBean.setConfigLocation (new FileSystemResource(AppUtils.getAppRoot()+ehcachePath));
        cacheManagerFactoryBean.setShared(true);
        return cacheManagerFactoryBean;
      }
   
}