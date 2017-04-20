package cn.hn.java.summer.validate;

import java.util.Iterator;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import cn.hn.java.summer.exception.BusinessException;

/**
 * bean验证器
 * @author sjg
 * @version 1.0.1 2013-10-23
 *
 */
public class BeanValidator {

	static Validator  validator;

	 
	static
	{
		//获取验证器
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
	    validator = factory.getValidator();
		
	}
	
	/**
	 * 验证bean是否符合规则
	 * @param <T>
	 * @param bean
	 * @param throwDirect 验证不通过时是否直接抛出异常
	 * @return
	 * @throws BusinessException 
	 */
	@SuppressWarnings("rawtypes")
	public static <T> boolean valid(T bean,boolean throwDirect,Class...group) throws BusinessException{
		  Set<ConstraintViolation<T>> constraintViolations =
	            validator.validate(bean,group);
		  
		  if(constraintViolations.size()>0){
			  StringBuilder sbError=new StringBuilder();
			  //遍历所有验证错误取出信息
			  Iterator<ConstraintViolation<T>> itErrors= constraintViolations.iterator();
			  while(itErrors.hasNext()){
				  sbError.append(itErrors.next().getMessage()).append(";");
			  }
			  //直接抛出异常
			  if(throwDirect){
				  throw new BusinessException(sbError.toString());
			  }
			  return false;
		  }
		  return true;
	}
	

	/**
	 * 验证bean是否符合规则，并抛出不符合验证的异常
	 * @param <T>
	 * @param bean
	 * @return
	 * @throws BusinessException 
	 */
	@SuppressWarnings("rawtypes")
	public static <T> boolean valid(T bean,Class...group) throws BusinessException{
		  return valid(bean,true,group);
	}
}
