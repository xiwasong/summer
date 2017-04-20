package cn.hn.java.summer.mvc.validation;

import java.io.Serializable;
import java.util.Map;

/**
 * 验证描述
 * @author sjg
 * @version 1.0.1 2013-11-13
 *
 */
public class ValidDescription implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 5106130162769624171L;

	/**
	 * 属性名
	 */
	private String fieldName;
	
	/**
	 * 验证类型
	 */
	private String validType;
	
	/**
	 * 验证提示信息
	 */
	private String message;
	
	/**
	 * 所属分组
	 */
	private String group;
	
	/**
	 * 其它属性
	 */
	private Map<String,String> attrs;


	public ValidDescription() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public ValidDescription(String fieldName, String validType, String message,
			String group) {
		super();
		this.fieldName = fieldName;
		this.validType = validType;
		this.message = message;
		this.group = group;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getValidType() {
		return validType;
	}

	public void setValidType(String validType) {
		this.validType = validType;
	}

	public Map<String, String> getAttrs() {
		return attrs;
	}

	public void setAttrs(Map<String, String> attrs) {
		this.attrs = attrs;
	}
	
	
}
