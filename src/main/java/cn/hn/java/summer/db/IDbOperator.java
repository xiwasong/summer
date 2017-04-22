package cn.hn.java.summer.db;

import cn.hn.java.summer.exception.SummerException;

import javax.sql.DataSource;
import java.util.List;

/**
 * 自定义jdbcTemplate接口，定义常用操作
 * @author sjg
 * @version 1.0.1 2013-10-22
 *
 */
public interface IDbOperator {

	/**
	 * 获取数据源
	 * @return 数据源
	 */
	DataSource getDataSource();

	/**
	 * 设置数据源
	 * @param ds 数据源
	 */
	void setDataSource(DataSource ds);

	/**
	 * 取列表
	 * @param <T> 实体类型
	 * @param sql sql语句
	 * @param elementType 实体类
	 * 列表元素类型，对于单列可选值为:String.class、int.class、float.class、double.class和long.class<br/>
	 * 对于多列，该值为实体bean类，字段名与属性名对应
	 * @param args 查询参数
	 * 可以是包含参数的对象，也可以是单个参数列
	 * @return 实体列表
	 */
	<T> List<T> list(String sql,Class<T> elementType, Object...args);

	/**
	 * 取单表列表
	 * @param elementType 实体类
	 * @param args 查询参数
	 * @param <T> 实体类型
	 * @return 实体列表
	 */
	<T> List<T> list(Class<T> elementType, Object...args);

	/**
	 * 取单表列表
	 * @param elementType 实体类
	 * @param orderBy 排序
	 * @param args 查询参数
	 * @param <T> 实体类型
	 * @return 实体列表
	 */
	<T> List<T> list(Class<T> elementType, OrderBy orderBy, Object...args);

	/**
	 * 强制取所有数据不分页的方法
	 * @param <T> 实体类型
	 * @param sql sql语句
	 * @param elementType 实体类
	 * @param args 查询参数
	 * @return 实体列表
	 */
	<T> List<T> all(String sql,Class<T> elementType, Object...args);

	/**
	 * 单表-强制取所有数据不分页的方法
	 * @param elementType 实体类
	 * @param args 查询参数
	 * @param <T> 实体类型
	 * @return 实体列表
	 */
	<T> List<T> all(Class<T> elementType, Object...args);

	/**
	 * 查询单个对象值
	 * @param <T> 实体类型
	 * @param type 实体类
	 * @param sql sql语句
	 * @param args 查询参数
	 * @return 实体列表
	 */
	<T> T get(String sql, Class<T> type, Object... args);

	/**
	 * 单表-查询单个对象值
	 * @param type 实体类
	 * @param args 查询参数
	 * @param <T> 实体类型
	 * @return 实体列表
	 */
	<T> T get(Class<T> type, Object... args);

	//==========================================================================================

	/**
	 * 执行sql
	 */
	void execute(String sql) ;

	/**
	 * 根据sql语句执行更新、插入、删除操作
	 * @param sql sql语句
	 * @param args 参数
	 * @return 影响行数
	 */
	int update(String sql, Object... args) ;

	/**
	 * 按主键更新
	 * @param bean 实体
	 * @param <T> 实体类型
	 * @return 更新记录数
	 */
	<T> int update(T bean);

	/**
	 * 按条件更新
	 *
	 * @param bean 实体
	 * @param condition 条件
	 * @return 更新记录数
	 */
	<T> int update(T bean, ConditionEntity condition);

	/**
	 * 插入
	 * @param bean 实体
	 * @param <T> 实体类型
	 * @return 插入记录数
	 */
	<T> int insert(T bean);

	/**
	 * 按主键删除
	 * @param bean 实体
	 * @param <T> 实体类型
	 * @return 删除记录数
	 */
	<T> int delete(T bean);

	/**
	 * 按指定条件删除
	 * @param type 实体类型类
	 * @param conditionSet 条件键值
	 * @param <T> 实体类型
	 * @return 删除记录数
	 */
	<T> int delete(Class<T> type, ConditionEntity conditionSet);

	/**
	 * 批量更新、插入、删除操作
	 * @param <T> 实体类型
	 * @param sql sql语句
	 * @param batchArgs 参数列表
	 * @param mapper 参数映射
	 * @return 影响行数
	 */
	@SuppressWarnings("rawtypes")
	<T> int[] batchUpdate(String sql, List<T> batchArgs,IBatchArgMapper mapper) throws SummerException;
}
