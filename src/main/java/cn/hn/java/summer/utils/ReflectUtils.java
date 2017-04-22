package cn.hn.java.summer.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReflectUtils extends ReflectionUtils{

	static Log logger=LogFactory.getLog(ReflectUtils.class);
	
	/**
	 * 判断一个类是否包含属性
	 * @param cls
	 * @return
	 */
	public static boolean hasProperty(Class cls){
		Field[] fields= getAllFields(cls);
		for(Field f : fields){
			PropertyDescriptor prop;
			try {
				prop = new PropertyDescriptor(f.getName(),cls);
			} catch (IntrospectionException e) {
				logger.error("hasProperty error!",e);
				continue;
			}
			return prop.getReadMethod()!=null || prop.getWriteMethod()!=null;
		}
		return false;
	}
	
	/**
	 * 取泛型类的泛型类型
	 * @param clazz
	 * @param index 第几个类型
	 * @return
	 */
	public static Class getClassGenericType(final Class clazz, final int index) {
        Type genType = clazz.getGenericSuperclass();  
        if (!(genType instanceof ParameterizedType)) {  
            return Object.class;  
        }  
        Type[] params = ((ParameterizedType)genType).getActualTypeArguments();  
        if (index >= params.length || index < 0) {  
            return Object.class;  
        }  
        if (!(params[index] instanceof Class)) {  
            return Object.class;  
        }  
        return (Class) params[index];  
    }  
	
	/**
	 * 取class的所有属性
	 * @param cls
	 * @return
	 */
	public static Field[] getAllFields(Class cls){
		Field[] fs=cls.getDeclaredFields();
		if(cls.getSuperclass()!=Object.class){
			fs=ArrayUtils.addAll(fs,getAllFields(cls.getSuperclass()));
		}
		return fs;
	}
	
	/**
	 * 取类的方法，递归基类
	 * @param cls
	 * @param name
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	public static Method getMethod(Class cls,String name){
		Method mtd=null;
		try {
			mtd = cls.getDeclaredMethod(name);
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
		
		if(mtd==null){
			cls= cls.getSuperclass();
			if(cls!=Object.class){
				mtd=getMethod(cls,name);
			}
		}
		return mtd;
	}

	/**
	 * 获取类中指定名称的声明的字段，包括父类
	 * @param cls
	 * @param fieldName
	 * @return
	 */
	public static Field getField(Class cls, String fieldName){
		Field field;
		try {
			field = cls.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			logger.error("getField error!",e);
			return  null;
		}
		if(field==null){
			cls= cls.getSuperclass();
			if(cls!=Object.class){
				field=getField(cls,fieldName);
			}
		}
		return field;
	}

	/**
	 * 获取类型的class
	 * @param type
	 * @return
	 */
	public static Class getTypeClass(Type type){
		if(type instanceof Class){
			return (Class)type;
		}
		return (Class)((ParameterizedType)type).getRawType();
	}

	/**
	 * 取类型的实际类型参数列表
	 * @param type
	 * @return
	 */
	public static Class[] getActualTypeArguments(Type type){
		Type[] types =((ParameterizedType)type).getActualTypeArguments();
		Class[] classes=new Class[types.length];
		for(int i=0; i<types.length; i++){
			classes[i]=(Class)types[i];
		}
		return classes;
	}
	
	/**
	 * 取对象所有属性的值
	 * @param obj
	 * @return
	 */
	public static Map<String,Object> getAllFieldValues(Object obj){
		if(obj==null || obj.getClass()==Object.class){
			return null;
		}
		
		Map<String,Object> rst=new HashMap<>();
		
		//取所有属性
		Field[] fields= getAllFields(obj.getClass());
		
		for(Field f : fields){
			Object value=getFieldValue(obj,f.getName());
			rst.put(f.getName(),value);
		}
		
		return rst;
	}
	
	/**
	 * 将bean转换为map
	 * @param obj
	 * @return
	 */
	public static Map<String, Object> beanToMap(Object obj) {  
		  
        if(obj == null){  
            return null;  
        }          
        Map<String, Object> map = new HashMap<>();
        try {  
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());  
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();  
            for (PropertyDescriptor property : propertyDescriptors) {  
                String key = property.getName();  
                // 过滤class属性  
                if (!key.equals("class")) {  
                    // 得到property对应的getter方法  
                    Method getter = property.getReadMethod();  
                    Object value = getter.invoke(obj);  
  
                    map.put(key, value);  
                }  
            }  
        } catch (Exception e) {  
        	logger.error("beanToMap error",e);
        }  
  
        return map;  
  
    }  
	
	
	/** 
     * 获取spring动态代理的目标对象 
     * @param proxy 代理对象 
     * @return  
     * @throws Exception 
     */  
    public static Object getProxyTarget(Object proxy) throws Exception {  
          
        if(!AopUtils.isAopProxy(proxy)) {  
            return proxy;//不是代理对象  
        }  
          
        if(AopUtils.isJdkDynamicProxy(proxy)) {  
            return getJdkDynamicProxyTargetObject(proxy);  
        } else { //cglib  
            return getCglibProxyTargetObject(proxy);  
        }  
    }
  
    private static Object getCglibProxyTargetObject(Object proxy) throws Exception {  
        Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");  
        h.setAccessible(true);  
        Object dynamicAdvisedInterceptor = h.get(proxy);  
          
        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");  
        advised.setAccessible(true);  
          
        Object target = ((AdvisedSupport)advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();  
          
        return target;  
    }  
  
  
    private static Object getJdkDynamicProxyTargetObject(Object proxy) throws Exception {  
        Field h = proxy.getClass().getSuperclass().getDeclaredField("h");  
        h.setAccessible(true);  
        AopProxy aopProxy = (AopProxy) h.get(proxy);  
          
        Field advised = aopProxy.getClass().getDeclaredField("advised");  
        advised.setAccessible(true);  
          
        Object target = ((AdvisedSupport)advised.get(aopProxy)).getTargetSource().getTarget();  
          
        return target;  
    }  
    

	/**
	 * 复制map中的值到对象
	 * @param from
	 * @param cls
	 */
	public static <T> T copyValues(Map<String,Object> from,Class<T> cls){
		T obj=null;
		try {
			obj = cls.newInstance();
		} catch (InstantiationException e) {
			logger.error("copyValues failed!", e);
		} catch (IllegalAccessException e) {
			logger.error("copyValues failed!", e);
		}
		for(String key:from.keySet()){
			setFieldValue(obj,key,from.get(key));
		}
		
		return obj;
	}

	/**
	 * 取可访问的字段
	 * @param cls
	 * @param name
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Field getAccessibleField(Class cls, String name){
		Field f;
		try {
			f = cls.getDeclaredField(name);
		} catch (SecurityException | NoSuchFieldException e) {
			return null;
		}
		f.setAccessible(true);
		return f;
	}
	
	/**
	 * 取对象指定getter字段的值
	 * @param o
	 * @param name
	 * @return
	 */
	public static <T> T getFieldValue(Object o,String name){
		if(StringUtils.isBlank(name)){
			return null;
		}
		try {
			PropertyDescriptor pd = new PropertyDescriptor(name, o.getClass());
			//获得get方法
			Method getter = pd.getReadMethod();
			return (T) getter.invoke(o);
		}catch (Exception e){
			logger.error("invoke getter method failed!",e);
			return null;
		}
	}


	/**
	 * 设置对象指定setter字段值
	 * @param o
	 * @param name
	 * @param value
	 */
	public static void setFieldValue(Object o,String name,Object value){
		if(StringUtils.isBlank(name)){
			return;
		}
		try {
			PropertyDescriptor pd = new PropertyDescriptor(name, o.getClass());
			//获得set方法
			Method setter = pd.getWriteMethod();
			setter.invoke(o,value);
		}catch (Exception e){
			logger.error("invoke setter method failed!",e);
		}
	}

	/**
	 * 取对象列表中的属性值列表
	 * @param data	对象列表
	 * @param name 指定属性名称
	 * @return 指定属性值列表
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> getProValueList(List<?> data,String name){
		List<T> result=new ArrayList<>();
		for(Object o:data){
			//取值
			result.add(getFieldValue(o,name));
		}
		return result;
	}

	/**
	 * 将对象列表转换成字典
	 * @param data 对象列表
	 * @param keyField 键字段
	 * @param valueField 值字段
	 * @return
	 */
	public static Map<Object, Object> toMap(List<?> data,String keyField,String valueField){
		Map<Object,Object> result=new HashMap<>();
		
		for(Object o:data){
			if(o==null){
				continue;
			}
			result.put(getFieldValue(o,keyField),getFieldValue(o,valueField));
		}		
		return result;
	}
}
