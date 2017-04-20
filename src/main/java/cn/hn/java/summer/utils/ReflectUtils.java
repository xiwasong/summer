package cn.hn.java.summer.utils;

import java.beans.BeanInfo;
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

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;

public class ReflectUtils {

	static Log logger=LogFactory.getLog(ReflectUtils.class);
	
	/**
	 * 判断一个类是否包含属性
	 * @param cls
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean hasProperty(Class cls){
		//找到有get开头、并与字段名称一致
		Field[] fields= cls.getDeclaredFields();
		Method[] mths=cls.getDeclaredMethods();
		for(Field f:fields){
			for(Method m:mths){
				if(m.getName().startsWith("get") && 
						f.getName().toUpperCase().equals(m.getName().substring(3).toUpperCase())){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 取泛型类的泛型类型
	 * @param clazz
	 * @param index 第几个类型
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public static Class getClassGenricType(final Class clazz, final int index) {  
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
	@SuppressWarnings("rawtypes")
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
	 * 取指定字段名的get方法
	 * @param cls
	 * @param name
	 * @return
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 */
	@SuppressWarnings("rawtypes")
	public static Method getGetter(Class cls,String name) throws SecurityException, NoSuchMethodException{
		name="get"+name.substring(0,1).toUpperCase()+name.substring(1);
		return getMethod(cls,name);
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
		
		Map<String,Object> rst=new HashMap<String, Object>();
		
		//取所有属性
		Field[] fields= getAllFields(obj.getClass());
		
		for(Field f : fields){
			//取属性值
			f.setAccessible(true);
			//取参数值
			Object value=null;
			try {
				value = f.get(obj);
				//取不到的为null
			} catch (IllegalArgumentException e) {				
			} catch (IllegalAccessException e) {
			}
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
        Map<String, Object> map = new HashMap<String, Object>();  
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
	 * @param obj
	 * @throws NoSuchFieldException 
	 * @throws SecurityException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws InstantiationException 
	 */
	public static <T> T copyValues(Map<String,Object> from,Class<T> cls){
		T obj=null;
		try {
			obj = cls.newInstance();
		} catch (InstantiationException e) {
			logger.error("InstantiationException", e);
		} catch (IllegalAccessException e) {
			logger.error("IllegalAccessException", e);
		}
		for(String key:from.keySet()){
			Field f=null;
			try {
				if(!cls.getSuperclass().getName().equals("java.lang.Object")){
					//有父类
					f=getAccessbleField(cls.getSuperclass(),key);
				}
				if(f==null){
					f=getAccessbleField(cls,key);
				}
			} catch (SecurityException e) {
				logger.error("SecurityException", e);
			}
			
			//设置字段值
			setFieldValue(f,obj,from.get(key));
		}
		
		return obj;
	}
	
	/**
	 * 设置字段值
	 * @param f
	 * @param obj
	 * @param val
	 */
	private static void setFieldValue(Field f,Object obj,Object val){
		try {
			if(f!=null && obj!=null && val!=null){
				//字段类型与值类型不一致时,尝试转换类型
				if(f.getType()!=val.getClass()){
					val=val==null?"0":val;
					if(f.getType()==Integer.class || f.getType()==int.class ){
						//int
						val=Integer.parseInt(val.toString());
					}else if(f.getType()==Long.class || f.getType()==long.class){
						//long
						val=Long.parseLong(val.toString());
					}else if(f.getType()==Double.class || f.getType()==double.class){
						//double
						val=Double.parseDouble(val.toString());
					}else if(f.getType()==Float.class || f.getType()==float.class){
						//float
						val=Float.parseFloat(val.toString());
					}else if(f.getType()==Boolean.class || f.getType()==boolean.class){
						//boolean
						val="true".equals(val.toString().toLowerCase());
					}
				}
				//System.out.println("set "+f.getName()+"="+val);
				f.set(obj, val);
			}
		} catch (IllegalArgumentException e) {
			logger.error("IllegalArgumentException", e);
		} catch (IllegalAccessException e) {
			logger.error("IllegalAccessException", e);
		}
	}
	
	/**
	 * 取可访问的字段
	 * @param cls
	 * @param name
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Field getAccessbleField(Class cls,String name){
		Field f=null;
		try {
			f = cls.getDeclaredField(name);
		} catch (SecurityException e) {
			return null;
		} catch (NoSuchFieldException e) {
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
	@SuppressWarnings("unchecked")
	public static <T> T getFieldValue(Object o,String name){
		if(org.apache.commons.lang3.StringUtils.isBlank(name)){
			return null;
		}
		
		//取到指定属性名的getter方法
		Method mtd;
		try {
			mtd = getGetter(o.getClass(), name);
			//取值
			return (T)(mtd.invoke(o));
		} catch (Exception e) {
			logger.error(o.getClass().getName()+"中找不到"+name+"属性\t"+e);
			return null;
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
		List<T> result=new ArrayList<T>();		
		for(Object o:data){
			//取值
			result.add((T)getFieldValue(o,name));				
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
		Map<Object,Object> result=new HashMap<Object, Object>();
		
		for(Object o:data){
			if(o==null){
				continue;
			}
			result.put(getFieldValue(o,keyField),getFieldValue(o,valueField));
		}		
		return result;
	}
}
