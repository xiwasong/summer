package cn.hn.java.summer.db;

import cn.hn.java.summer.exception.SnException;

/**
 * 批量操作参数映射接口
 * @author sjg
 * @version 1.0.1 2013-10-29
 *
 */
public interface IBatchArgMapper<T> {

	/**
	 * 取参数 
	 * @param <T>
	 * @param t
	 * @return
	 */
	public Object[] getArgs(T t) throws SnException;
}
