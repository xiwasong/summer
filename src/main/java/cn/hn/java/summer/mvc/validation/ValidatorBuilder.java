package cn.hn.java.summer.mvc.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.hn.java.summer.utils.ReflectUtils;

/**
 * 自动验证器
 * @author sjg
 * @version 1.0.1 2013-11-14
 *
 */
public class ValidatorBuilder {

	protected static final Log logger = LogFactory.getLog(ValidatorBuilder.class);	
	
	/**
	 * 缓存验证规则
	 */
	private static Map<String,List<ValidDescription>> validations=new HashMap<String, List<ValidDescription>>();
	
	/**
	 * 生成页面JS验证描述
	 * @param cls
	 * @param groupName 分组名称
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static List<ValidDescription> generateValidationDescription(Class cls,Class group){
		if(cls==null){
			return null;
		}

		//验证规则描述列表
		List<ValidDescription> list=null;
		
		//从缓存中取
		list=validations.get(cls.getName());
		if(list!=null){
			return list;
		}
		
		list=new ArrayList<ValidDescription>();
		
		//分组类名
		String groupName=group==null?null:group.getName();
		
		//@org.hibernate.validator.constraints.NotBlank(message=帐号不能为空, payload=[], groups=[interface com.sino.manage.user.check.ManageUpdateCheck, interface com.sino.manage.user.check.PersonalUpdateCheck])
		Pattern p= Pattern.compile("@(\\w+\\.)+(\\w+)\\((.*)\\)");
			
		//取定义的属性
		Field[] fields=ReflectUtils.getAllFields(cls);
		
		for(Field f: fields){
			String fname=f.getName();
			//取属性的注解
			Annotation[] anos= f.getAnnotations();
			for(Annotation a: anos){
				//如果是验证注释
				//@org.hibernate.validator.constraints.NotBlank(message=帐号不能为空, payload=[], groups=[interface com.sino.manage.user.check.ManageUpdateCheck, interface com.sino.manage.user.check.PersonalUpdateCheck])
				//@javax.validation.constraints.Min(message=最多终端位置数不能为0, payload=[], groups=[], value=1)
				String anStr=a.toString();
				
				Matcher mc=p.matcher(anStr);
				if(mc.find()){
					//取验证类名
					String validType=mc.group(2);
					
					//取描述
					ValidDescription desc=getDescription(mc.group(3));
					desc.setFieldName(fname);
					desc.setGroup(groupName);
					desc.setValidType(validType);
					
					//未指定分组或指定的分组存在
					if(groupName==null || 
								(groupName!=null && desc.getGroup().indexOf(groupName)>=0 &&
								//消息不是常量
								!desc.getMessage().startsWith("{")
								)
							){
						//添加
						list.add(desc);				
					}
				}
			}
		}
		
		//缓存
		validations.put(cls.getName(), list);
		
		return list;
	}
	
	
	/**
	 * 取描述
	 * @param str
	 * @return
	 */
	private static ValidDescription getDescription(String str){
		ValidDescription vd=new ValidDescription();
		
		//message=帐号不能为空, payload=[], groups=[interface com.sino.manage.user.check.ManageUpdateCheck, interface com.sino.manage.user.check.PersonalUpdateCheck]
		//message=最多终端位置数不能为0, payload=[], groups=[], value=1
		//取分组
		String groups="";
		Matcher mc=Pattern.compile("groups=\\[[^\\]]*\\]").matcher(str);
		if(mc.find()){
			groups=mc.group();
		}
		vd.setGroup(groups);
		
		str=str.replace(groups, "");
		
		//message=帐号不能为空, payload=[], groups=[interface com.sino.manage.user.check.ManageUpdateCheck, interface com.sino.manage.user.check.PersonalUpdateCheck]
		//message=最多终端位置数不能为0, payload=[], groups=[], value=1
		//取其它属性
		mc= Pattern.compile("(\\w+)=([^◎]+)").matcher(str.replace(", ", "◎"));
		Map<String,String> attrs=new HashMap<String, String>();
		while(mc.find()){
			attrs.put(mc.group(1), mc.group(2));
		}
		vd.setAttrs(attrs);
		//提示消息
		vd.setMessage(attrs.get("message"));
		attrs.remove("message");
		
		return vd;
	}
	
}
