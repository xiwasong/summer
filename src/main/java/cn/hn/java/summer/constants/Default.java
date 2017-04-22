package cn.hn.java.summer.constants;

/**
 * 存放默认值
 * @author sjg
 * @version 1.0.1 2013-10-21
 *
 */
public abstract class Default {
	/**
	 * 默认分页大小
	 */
	public static final int PAGE_SIZE =15;
	

	/**
	 * 存储在请求上下文中的错误信息键
	 */
	public static final String ERROR_IN_REQUEST_KEY="errors";
	
	
	/**
	 * 存储在请求上下文中的错误信息异常键
	 */
	//public static final String ERROR_EXCEPTION_IN_REQUEST_KEY="errors_exception";

	/**
	 * 存储在请求中的 调用swallowAndReturn方法被吞没的异常
	 */
	public static final String EXCEPTION_SWALLOWED_KEY="swallowed_exception";
	

	/**
	 * 存储在请求上下文中的action结果键
	 */
	public static final String ACTION_RESULT_IN_REQUEST_KEY ="action_result";
	

	/**
	 * 存储在请求上下文中的分页总记录数键
	 */
	public static final String PAGING_TOTAL_IN_REQUEST_KEY="paging_total";
	
	/**
	 * 提交表单成功的提示信息
	 */
	public static final String POST_FORM_SUCCESS_MSG="操作成功";
	
	/**
	 * 加密存储在cookies中的session对象键
	 */
	public static final String COOKIES_SESION_KEY="sessionstore";
	
	/**
	 * 加密存储在cookies中的当前时间键
	 */
	public static final String COOKIES_TIME_KEY="time";
	
	/**
	 * 存储在request上下文中的response对象
	 */
	public static final String RESPONSE_IN_REQUEST_KEY="response_in_request";
	
	/**
	 * session超时时间(分)
	 */
	public static final int SESSION_TIME_OUT=30;
	
	/**
	 * 标记响应已跳转
	 */
	public static final String MARK_RESPONSE_IS_REDIRECTED = "markResponseIsRedirected";

	/**
	 * action多值返回时键的前缀
	 */
	public static final String MULTIPLE_RESULT_PREFIX ="multiple:";

	/**
	 * 表单字段值汇总键
	 */
	public static final String FORM_SUMMUERY_KEY="__form";

	/**
	 * 页面提交返回键
	 */
	public static final String PAGE_POST_BACK_KEY="_isPostBack";

	/**
	 * 存储restful风格视图名的键
	 */
	public static final String RESTFUL_VIEW_NAME_KEY ="restful_view_name_key";
}
