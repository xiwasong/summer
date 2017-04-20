package cn.hn.java.summer.context;

import cn.hn.java.summer.Config;
import cn.hn.java.summer.constants.Default;
import cn.hn.java.summer.db.paging.Page;
import cn.hn.java.summer.utils.IntUtils;


/**
 * 上下文管理器，根据不同的运行环境使用不同的上下文
 * @author sjg
 * @version 1.0.1 2014-6-16
 *
 */
public class ThreadContextManage {

	private static IThreadContext THREADCONTEXT=null;
	
	public static void setThreadContext(IThreadContext threadContext){
		THREADCONTEXT=threadContext;
	}
	
	public static IThreadContext getThreadContext(){
		if(THREADCONTEXT==null){
			if(Config.isWebApplication()){
				THREADCONTEXT=new WebContext();
			}else{
				THREADCONTEXT=new ThreadContext();
			}
		}
		return THREADCONTEXT;
	}
	
	/**
	 * 获取值
	 * @param key
	 * @return
	 */
	public static Object getAttribute(String key) {
		return getThreadContext().getAttribute(key);
	}

	/**
	 * 设置值
	 * @param key
	 * @param value
	 */
	public static void setAttribute(String key, Object value) {
		getThreadContext().setAttribute(key, value);
	}
	
	public static String getParameter(String key){
		return getThreadContext().getParameter(key);
	}

	/**
	 * 从上下文中取对象
	 * @param key
	 * @return
	 */
	public static Object getRequestObject(String key){
		if(key==null){
			return null;
		}
		Object obj=null;
		//web应用
		if(Config.isWebApplication()){
				obj=getParameter(key);
			if(obj==null){
				obj=getAttribute(key);
			}
			//else{
			//	//或从__form参数中获取
			//	String formFields=getParameter(Default.FORM_SUMMUERY_KEY);
			//	if(formFields==null){
			//		return null;
			//	}
			//	Matcher mc= Pattern.compile("(\\w+)=([^&]+)").matcher(formFields);
			//	while(mc.find() && mc.groupCount()==2 && key.equals(mc.group(0))){
			//		return mc.group(1);
			//	}
			//}
			return obj;
		}
		return getAttribute(key);		
	}
	
	/**
	 * 获取其它信息
	 * @param key
	 * @return
	 */
	public static String getOtherInfo(String key){
		return getThreadContext().getOtherInfo(key);
	}

	/**
	 * 获取分页信息
	 * @return
	 */
	public static Page getPagingInfo(){
		Page page=new Page();		

		boolean isPrint=getRequestObject("print")!=null;
		
		//打印不分页
		if(isPrint){
			return null;
		}
		
		Object ps=getRequestObject("pageSize");
		Object p=getRequestObject("page");
		
		
		if(ps==null && p==null){
			return null;			
		}
		
		if(ps!=null){
			page.setPageSize(IntUtils.parseInt(ps.toString(), Default.PAGESIZE));
		}
		
		if(p!=null){
			page.setPage(IntUtils.parseInt(p.toString(), 1));
		}
		return page;
	}
}
