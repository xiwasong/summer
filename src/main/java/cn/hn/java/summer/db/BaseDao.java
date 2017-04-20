package cn.hn.java.summer.db;


import cn.hn.java.summer.context.ThreadContextManage;
import cn.hn.java.summer.db.multiple.IDataSourceMark;
import cn.hn.java.summer.db.multiple.MultipleDataSource;
import cn.hn.java.summer.db.paging.IPaging;
import cn.hn.java.summer.db.paging.Page;
import cn.hn.java.summer.exception.SnException;
import cn.hn.java.summer.utils.ReflectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.List;


/**
 * 其中用实现IMultipleIndex接口的Index来标识使用哪个数据源
 * @author sjg
 * @version 1.0.1 2013-10-27
 * 
 * @param <Index>
 */
@Component
public class BaseDao<Index extends IDataSourceMark> implements IDbOperator {
	protected final Log logger = LogFactory.getLog(getClass());
	
	/**
	 * 多数据源
	 */
	@Autowired
	private MultipleDataSource multipleDataSource;
	
	/**
	 * 当前使用的JdbcTemplate
	 */
	protected DbOperator jdbcTemplate;
	
	/**
	 * 取jdbcTemplate，调用getDataSource获取数据源
	 * @return
	 */
	protected DbOperator getDbOperator(){
		//由子类指定数据源
		DataSource ds=this.getDataSource();
		if(ds!=null){
			jdbcTemplate.setDataSource(this.getDataSource());
		}
		
		return jdbcTemplate;
	}
	
	/**
	 * 初始化
	 * @throws SnException 
	 */
	@PostConstruct
	private void init() throws SnException{
		//取Index的类型
		Class<?> cls= ReflectUtils.getClassGenricType(getClass(), 0);
		if(jdbcTemplate==null){
			try {
				//根据Index的类型取对应的jdbcTemplate
				jdbcTemplate=multipleDataSource.get(cls.newInstance());
			} catch (Exception ex) {
				logger.error("设置jdbcTemplate出错",ex);
				throw new SnException("初始化数据源失败",ex);
			}
		}
	}
	
	/**
	 * 动态取数据源，每次执行数据库操作时调用。
	 * 请谨慎实现该方法，确保在需要创建DataSource时才创建新的
	 * @throws SnException 
	 */
	public DataSource getDataSource(){
		return null;
	}
	
	public void setDataSource(DataSource ds){	
	}
	
	/**
	 * 执行任意sql语句或id
	 */
	public void execute(String sql) {
		getDbOperator().execute(sql);
	}

	/**
	 * 取分页参数信息
	 * @return 分页参数
	 */
	private Page getPageInfo(Object... args){
		//取分页信息
		Page page= ThreadContextManage.getPagingInfo();
		//上下文中未取到
		if(page==null){
			//从参数中取
			for(Object arg : args){
				if(arg instanceof IPaging){
					IPaging ipaging=(IPaging)arg;
					//分页设置正确
					if(ipaging.getPageSize()>0 && ipaging.getPage()>0){
						page=new Page(ipaging.getPageSize(),ipaging.getPage());
						break;
					}
				}
			}
		}
		return page;
	}

	/**
	 * 取单个值
	 * @param type 返回值类型
	 * @param sql sql语句或id
	 * @param args 传入参数
	 */
	public <T> T get(String sql, Class<T> type, Object... args) {
		return getDbOperator().get(sql, type, args);
	}

	/**
	 * 取列表
	 * @param elementType 返回值类型
	 * @param sql sql语句或id
	 * @param args 传入参数
	 */
	public <T> List<T> list(String sql, Class<T> elementType, Object... args){
		Page page=getPageInfo(args);
		if(page==null){
			return getDbOperator().list(sql, elementType, args);
		}else{
			return getDbOperator().list(sql, elementType,page.getPageSize(),page.getPage(),args);
		}		
	}

	/**
	 * 分页取列表,建议只在非web程序中使用,web程序中会自动进行分页
	 * @param <T>
	 * @param sql
	 * @param elementType
	 * @param pageSize
	 * @param page
	 * @param args
	 * @return
	 * @throws SnException
	 */
	public <T> List<T> page(String sql, Class<T> elementType, int pageSize, int page, Object... args) throws SnException {
		return getDbOperator().list(sql, elementType,pageSize,page,args);
	}

	/**
	 * 批量取列表(暂不支持分页)
	 * 如：要同时跨多个表查询时，sql除表名外其它都一样，这样就可以把多个查询合并到一起查询，提供批量的查询参数args
	 * @param sql sql语句或id
	 * @param elementType 返回值类型
	 * @param args 批量参数
	 */
	public <T> List<T> list(String sql, Class<T> elementType, List<Object[]> args){
		return getDbOperator().list(sql, elementType, args);
	}

	/**
	 * 强制取所有数据不分页的方法
	 * @param <T>
	 * @param sql
	 * @param elementType
	 * @param args
	 * @return
	 */
	public <T> List<T> all(String sql,Class<T> elementType, Object...args){
		return getDbOperator().list(sql, elementType, args);
	}

	/**
	 * 修改、添加、删除操作
	 * @param sql sql语句或id
	 * @param args 传入参数
	 */
	public int update(String sql, Object... args) {
		return getDbOperator().update(sql, args);
	}

	/**
	 * 批量更新操作
	 * @param sql
	 * @param batchArgs
	 * @return
	 * @throws SnException 
	 */
	@SuppressWarnings("rawtypes")
	@Transactional(rollbackFor=Exception.class)
	public <T> int[] batchUpdate(String sql, List<T> batchArgs,IBatchArgMapper mapper) throws SnException{
		return getDbOperator().batchUpdate(sql, batchArgs,mapper);
	}

	/**
	 * 生成一个条件项
	 * @return
	 */
	public ConditionEntity ce(){
		return new ConditionEntity();
	}


	//===================================不用传sql的部分==================================
	/**
	 * 取单表列表
	 *
	 * @param elementType 实体类
	 * @param args        查询参数
	 * @return 实体列表
	 */
	@Override
	public <T> List<T> list(Class<T> elementType, Object... args) {
		Page page=getPageInfo(args);
		if(page==null){
			return getDbOperator().list(elementType, args);
		}else{
			return getDbOperator().list(elementType,page.getPageSize(),page.getPage(),null,args);
		}
	}


	/**
	 * 取单表列表
	 *
	 * @param elementType 实体类
	 * @param orderBy     排序
	 * @param args        查询参数
	 * @return 实体列表
	 */
	@Override
	public <T> List<T> list(Class<T> elementType, OrderBy orderBy, Object... args) {
		Page page=getPageInfo(args);
		if(page==null){
			return getDbOperator().list(elementType, args);
		}else{
			return getDbOperator().list(elementType,page.getPageSize(),page.getPage(),orderBy,args);
		}
	}

	/**
	 * 单表-强制取所有数据不分页的方法
	 *
	 * @param elementType 实体类
	 * @param args        查询参数
	 * @return 实体列表
	 */
	@Override
	public <T> List<T> all(Class<T> elementType, Object... args) {
		return getDbOperator().all(elementType,args);
	}

	/**
	 * 单表-查询单个对象值
	 *
	 * @param type 实体类
	 * @param args 查询参数
	 * @return 实体列表
	 */
	@Override
	public <T> T get(Class<T> type, Object... args) {
		return getDbOperator().get(type,args);
	}

	/**
	 * 按主键更新
	 *
	 * @param bean 实体
	 * @return 更新记录数
	 */
	@Override
	public <T> int update(T bean) {
		return getDbOperator().update(bean);
	}

	/**
	 * 按条件更新
	 *
	 * @param bean      实体
	 * @param condition 条件
	 * @return 更新记录数
	 */
	@Override
	public <T> int update(T bean, ConditionEntity condition) {
		return getDbOperator().update(bean,condition);
	}

	/**
	 * 插入
	 *
	 * @param bean 实体
	 * @return 插入记录数
	 */
	@Override
	public <T> int insert(T bean) {
		return getDbOperator().insert(bean);
	}

	/**
	 * 按主键删除
	 *
	 * @param bean 实体
	 * @return 删除记录数
	 */
	@Override
	public <T> int delete(T bean) {
		return getDbOperator().delete(bean);
	}

	/**
	 * 按指定条件删除
	 *
	 * @param type         实体类型类
	 * @param conditionSet 条件键值
	 * @return 删除记录数
	 */
	@Override
	public <T> int delete(Class<T> type, ConditionEntity conditionSet) {
		return getDbOperator().delete(type,conditionSet);
	}
}
