package cn.hn.java.summer;

import cn.hn.java.summer.context.ThreadContext;
import cn.hn.java.summer.context.ThreadContextManage;
import cn.hn.java.summer.context.WebContext;

/**
 * 配置类
 * @author sjg
 * @version 1.0.1 2014-2-12
 *
 */
public abstract class Config {

	/**
	 * 标识是否是web应用
	 */
	private static boolean isWebApplication=true;

	/**
	 * 是否是web应用
	 * @return
	 */
	public static boolean isWebApplication() {
		return isWebApplication;
	}

	/**
	 * 设置应用是否为web应用
	 * @param isWebApplication
	 */
	public static void setWebApplication(boolean isWebApplication) {
		Config.isWebApplication = isWebApplication;
		ThreadContextManage.setThreadContext(isWebApplication?new WebContext():new ThreadContext());
	}

	/**
	 * 是否支持“回退提交”即要将数据通过post方式提交返回到上次页面
	 */
	public static boolean isSupportBackPost=false;

	/**
	 * 是否支持action日志记录
	 */
	public static boolean isIsSupportActionLog=false;

	/**
	 * 是否支持为restful风格请求指定视图名
	 */
	public static boolean isIsSupportSetRestfulViewName =false;

	/**
	 * 数据库实体类扫描包过滤表达式，支持正则
	 * 如：.*entity.db.*
	 */
	public static String dbBeanScanFilter;
}
