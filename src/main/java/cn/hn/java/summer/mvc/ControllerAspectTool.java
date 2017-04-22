package cn.hn.java.summer.mvc;

import cn.hn.java.summer.Config;
import cn.hn.java.summer.annotation.ParamValid;
import cn.hn.java.summer.constants.Default;
import cn.hn.java.summer.exception.BusinessException;
import cn.hn.java.summer.exception.ForwardException;
import cn.hn.java.summer.exception.LoginException;
import cn.hn.java.summer.mvc.log.ActionLogger;
import cn.hn.java.summer.utils.Func;
import cn.hn.java.summer.utils.StringUtils;
import cn.hn.java.summer.validate.BeanValidator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.rythmengine.utils.F.T2;
import org.rythmengine.utils.F.T3;
import org.rythmengine.utils.F.T4;
import org.rythmengine.utils.F.T5;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;


/**
 * 拦截以Controller结尾控制器的所有方法
 * 记录日志、保存错误信息
 * @author sjg
 * @version 1.0.1 2013-10-20
 *
 */
public abstract class ControllerAspectTool{
	static Log logger=LogFactory.getLog(ControllerAspectTool.class);	
    
	/**
	 * 用around拦截controller所有action方法，进行异常处理和日志记录
	 * @param pjp
	 * @param context
	 * @return
	 * @throws Throwable
	 */
    public static Object around(ProceedingJoinPoint pjp,ApplicationContext context) throws Throwable{

    	//如果请求已跳转，则不调用action方法
		String redirectUrl=WebContext.getRequestAttribute(Default.MARK_RESPONSE_IS_REDIRECTED);
		if(redirectUrl!=null){
			return null;
		}

    	Method mtd=getMethod(pjp);

        //支持“回退提交”且是“回退提交”时不执行业务方法
        if(Config.isSupportBackPost && isBackPost(mtd)){
        	return null;
		}

	    try{
	        //验证切入点方法的参数
            validParams(pjp,mtd);
	    	
	    	//调用目标对象方法
	    	Object rst=pjp.proceed();
	    	
	    	//保存返回值的键
	    	Type type=mtd.getReturnType();
	    	ArrayList<Object> allTupleValues=new ArrayList<>();
	    	
	    	boolean isTupleResult=false;
	    	//元组类型,将元组中的值分离出来
	    	if(type == T2.class){
	    		isTupleResult=true;
	    		T2<?, ?> t2=(T2<?, ?>)rst;
	    		allTupleValues.add(t2._1);
	    		allTupleValues.add(t2._2);
	    	}else if(type == T3.class){
	    		isTupleResult=true;
	    		T3<?, ?, ?> t3=(T3<?, ?, ?>)rst;
	    		allTupleValues.add(t3._1);
	    		allTupleValues.add(t3._2);
	    		allTupleValues.add(t3._3);
	    	}else if(type == T4.class){
	    		isTupleResult=true;
	    		T4<?, ?, ?, ?> t4=(T4<?, ?, ?, ?>)rst;
	    		allTupleValues.add(t4._1);
	    		allTupleValues.add(t4._2);
	    		allTupleValues.add(t4._3);
	    		allTupleValues.add(t4._4);
	    	}else if(type == T5.class){
	    		isTupleResult=true;
	    		T5<?, ?, ?, ?, ?> t5=(T5<?, ?, ?, ?, ?>)rst;
	    		allTupleValues.add(t5._1);
	    		allTupleValues.add(t5._2);
	    		allTupleValues.add(t5._3);
	    		allTupleValues.add(t5._4);
	    		allTupleValues.add(t5._5);
	    	}
	    	
	    	//如果是元组类型返回值
	    	if(isTupleResult){
	    		ParameterizedType tupleReturnType =(ParameterizedType) mtd.getGenericReturnType();
	    		Type[] tupleTypes=tupleReturnType.getActualTypeArguments();
	    		String[] resultKey=new String[tupleTypes.length];
	    		for(int i=0;i<tupleTypes.length;i++){
		    		//将元组中的值添加到上下文中
		    		resultKey[i]=getGenericString(tupleTypes[i].toString());
		    		WebContext.setRequestAttribute(resultKey[i],allTupleValues.get(i));
		    		logger.debug("resultKey=>"+resultKey[i]);
	    		}
	    		//将键添加到请求中，待SnJsonMapper取
	    		WebContext.setRequestAttribute(Default.ACTION_RESULT_IN_REQUEST_KEY,
	    				Default.MULTIPLE_RESULT_PREFIX +StringUtils.join(resultKey, ","));
	    	}else{
	    		//其它类型
	    		//取返回值类型标识
	    		String resultKey=mtd.getGenericReturnType().toString();
	    		resultKey=getGenericString(resultKey);
	    		//添加到请求中，待SnJsonMapper取
	    		WebContext.setRequestAttribute(Default.ACTION_RESULT_IN_REQUEST_KEY, resultKey);
	    		logger.debug("resultKey=>"+resultKey);
	    	}

	    	//处理调用swallowAndReturn时被吞没的异常
            Exception swallowException= WebContext.getRequestAttribute(Default.EXCEPTION_SWALLOWED_KEY);
            if(swallowException!=null){
                processException(swallowException,pjp);
            }

            //处理restful风格请求时指定的视图名
            if(Config.isIsSupportSetRestfulViewName){
				processRestfulViewName(mtd);
			}

			//记录操作日志
			if(Config.isIsSupportActionLog) {
				ActionLogger.log(context, mtd, pjp.getArgs(),
						swallowException==null?"":swallowException.getMessage());
			}
	    	return rst;
	    }catch(Exception ex){
            //处理异常
            processException(ex,pjp);
			//处理restful风格请求时指定的视图名
			if(Config.isIsSupportSetRestfulViewName){
				processRestfulViewName(mtd);
			}
            //记录操作日志
			if(Config.isIsSupportActionLog) {
				ActionLogger.log(context, mtd, pjp.getArgs(), ex.getMessage());
			}
	    	return null;
	    }
    }

	/**
	 * 判断是否为“返回提交”即要将数据通过post方式提交返回到上次页面
	 * @return
	 */
	private static Boolean isBackPost(Method mtd){
		//有提交返回
		Object backPost=WebContext.getRequestObject(Default.PAGE_POST_BACK_KEY);

		//如果页面返回提交参数,则不调用业务方法
		if(backPost!=null && StringUtils.isNoneBlank(backPost.toString())){
			//如果是post请求
			if(mtd.getAnnotation(PostMapping.class)!=null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 处理restful风格的视图名
	 * 如：xx/{id}需要指定使用xx.html视图
	 * 此时可以在RequestMapping上指定name="xx.html"，无需在action中返回字符串
	 */
	private static void  processRestfulViewName(Method method){
		RequestMapping rm=method.getAnnotation(RequestMapping.class);
		if(rm!=null && StringUtils.isNoneBlank(rm.name())){
			WebContext.setRequestAttribute(Default.RESTFUL_VIEW_NAME_KEY,rm.name());
		}
	}

    /**
     * 处理异常
     * @param ex
     * @return
     */
    private static void processException(Exception ex,ProceedingJoinPoint pjp) throws Exception {

        //用于跳转
        if(ex instanceof ForwardException || ex instanceof LoginException){
            throw ex;
        }

        //取栈中异常
        Object stackException= WebContext.getRequestAttribute(Default.ERROR_IN_REQUEST_KEY);

        if(stackException==null){
            //保存异常
            Throwable exThrow=ex.getCause();
            //如果Cause为空则输出最终的包装错误信息，否则输出包装信息和原始信息
            String msg=exThrow==null?ex.getMessage():(ex.getMessage()+":"+exThrow.getMessage());
            WebContext.setRequestAttribute(Default.ERROR_IN_REQUEST_KEY, msg==null?"null":msg);

            //取目标方法
            Method mth=getMethod(pjp);
            String errorMsg=StringUtils.format("在 {0}.{1}: ",pjp.getTarget().getClass(), mth.getName());
            //非业务异常
            if(!(ex instanceof BusinessException)){
                logger.error("执行方法出错"+errorMsg,ex);
            }else{
                //业务异常信息
                logger.warn("业务异常"+errorMsg+ex.getMessage());
            }
        }
    }

    /**
     * 验证切入方法的参数
     * @param pjp
     * @param mtd
     */
    private static void validParams(ProceedingJoinPoint pjp, Method mtd) throws BusinessException {
		//取到方法参数所有注解
		Annotation[][] anns= mtd.getParameterAnnotations();
		//验证输入参数
		Object[] args = pjp.getArgs();
		for(int i=0;i<args.length;i++){
			//忽略为空的参数
			if(args[i]==null){
				continue;
			}
			//只对有valid注解的参数进行验证
			for(int j=0;j<anns[i].length;j++){
				if(anns[i][j] instanceof ParamValid){
                    ParamValid pv=(ParamValid)anns[i][j];
					//参数注解验证
					BeanValidator.valid(args[i],pv.value());
					break;
				}
			}
		}
	}
    
    /**
     * 取方法的注解
     * @param pjp
     * @return
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private static Method getMethod(ProceedingJoinPoint pjp) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{

		//获取调用方法信息
		Field methodInvocationField = pjp.getClass().getDeclaredField("methodInvocation");
		//反射获取被调用的方法信息
		methodInvocationField.setAccessible(true);
		ProxyMethodInvocation method= (ProxyMethodInvocation)(methodInvocationField.get(pjp));
		Method targetMethod=method.getMethod();
		
		return targetMethod;
    }


    /**
     * 获取泛型的类型名
     * @param typeString
     * @return
     */
    private static String getGenericString(String typeString){
    	typeString=typeString.replaceAll(".*\\.(\\w+)(<.*\\.(\\w+)>)|.*\\.(\\w+)", "$4$3$1");
    	typeString=typeString.substring(0, 1).toLowerCase()+typeString.substring(1);	
    	return typeString;
    }

	/**
	 * 吞没源方法异常并返回目标方法的返回值
	 * @param from
	 * @param to
	 * @param <R>
	 * @return
	 */
    public static <R> R swallowAndReturn(Func.Fun from, Func.FunR<R> to) throws BusinessException{
        try {
            from.run();
        }catch (Exception e){
            WebContext.setRequestAttribute(Default.EXCEPTION_SWALLOWED_KEY,e);
        }
        return to.run();
    }

}
