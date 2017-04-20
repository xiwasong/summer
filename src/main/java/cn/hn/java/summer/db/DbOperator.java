package cn.hn.java.summer.db;

import cn.hn.java.summer.constants.Default;
import cn.hn.java.summer.context.ThreadContextManage;
import cn.hn.java.summer.db.builder.FormatResult;
import cn.hn.java.summer.db.builder.SqlBuilder;
import cn.hn.java.summer.db.mapper.SqlGenerator;
import cn.hn.java.summer.db.paging.IPagingConverter;
import cn.hn.java.summer.exception.SnException;
import cn.hn.java.summer.utils.ReflectUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 数据库操作类
 */
public class DbOperator extends JdbcTemplate implements IDbOperator {

	/**
	 * 无属性类型缓存
	 */
	private static final ConcurrentHashMap<Type,Boolean> NoPropertyTypeCache=new ConcurrentHashMap<>();

	/**
	 * 判断类型是否是包含属性的bean
	 * @param type 类型
	 * @return boolean
	 */
	public static <T> boolean hasProperty(Class<T> type){
		if(type==null){
			return false;
		}
		Boolean result=NoPropertyTypeCache.get(type);
		if(result!=null){
			return result;
		}
		if(type.getPackage().getName().contains("java.")){
			return false;
		}
		result= ReflectUtils.hasProperty(type);
		NoPropertyTypeCache.put(type,result);
		return result;
	}

	/**
	 * 分页转换器
	 */
	private IPagingConverter pagingConverter;
	public void setPagingConverter(IPagingConverter pagingConverter) {
		this.pagingConverter = pagingConverter;
	}

	/**
	 * 调用存储过程执行分页查询
	 * @param <T>
	 * @param sql 查询语句
	 * @param rowMapper 行映射器
	 * @param pageSize 分页大小 不设置默认为Default.PAGESIZE
	 * @param page 当前页 从1开始
	 * @param args sql参数
	 * @return 实体列表
	 * @throws DataAccessException
	 */
	private <T> List<T> page(String sql,RowMapper<T> rowMapper,Integer pageSize,final Integer page,Object...args){
		
		//取格式化后的sql
		FormatResult rst=pagingConverter.formatSql(sql, args);
		
		//取总记录数
		count(rst.getSql(),rst.getArgs());
		
		//分页
		return pagingConverter.paging(this, rst.getSql(), pageSize, page, rowMapper, rst.getArgs());
	}
	
	/**
	 * 计算查询记录数
	 * @param sql  select xxx from yyy
	 * @param args 参数
	 * @return 记录数
	 */
	private int count(String sql,Object...args){
		//组织sql语句
		sql=SqlBuilder.formCountSql(sql);
		Integer total = getNoPropertyObject(Integer.class,sql,args);
		//保存总记录数
		ThreadContextManage.setAttribute(Default.PAGING_TOTAL_IN_REQUEST_KEY,total);
		return total;
	}	
	
	/**
	 * 调用存储过程执行分页查询,以args为查询条件
	 * @param <T>
	 * @param sql
	 * @param cls
	 * @param pageSize
	 * @param page
	 * @param args
	 * @return
	 */
	public <T> List<T> list(final String sql, Class<T> cls,Integer pageSize,final Integer page,Object...args) {
		return this.page(sql,getListRowMapper(cls),pageSize,page,args);
	}
	
	/**
	 * 取列表
	 * @param <T>
	 * @param sql
	 * @param elementType  
	 * 该值为实体bean类，字段名与属性名对应
	 * @param args
	 * 可以是包含参数的对象，也可以是单个参数列
	 * @return
	 */
	public <T> List<T> list(String sql,Class<T> elementType, Object...args){
		FormatResult rst=SqlBuilder.format(sql, args);
		return this.query(rst.getSql(),rst.getArgs(),getListRowMapper(elementType));
	}
	
	/**
	 * 取列表行映射器
	 * @param <T>
	 * @param elementType
	 * @return
	 */
	private <T> RowMapper<T> getListRowMapper(Class<T> elementType){
		//单列，String和一些原始类型
		if(
				elementType==String.class ||
				elementType==Integer.class ||
				elementType==Float.class ||
				elementType==Double.class ||
				elementType==Long.class ||
				elementType== Date.class
		){
			//取第一列值
			return (ResultSet rs, int rowNum)->(T)rs.getObject(1);
		}
		//多列，其它实体类
		return new BeanPropertyRowMapper<>(elementType);
	}

	/**
	 * 强制取所有数据不分页的方法
	 * @param <T>
	 * @param sql
	 * @param elementType
	 * @param args
	 * @return
	 * @throws SnException
	 */
	public <T> List<T> all(String sql,Class<T> elementType, Object...args){
		return this.list(sql, elementType, args);
	}
	
	/**
	 * 查询单个对象值
	 * @param <T>
	 * @param type
	 * @param sql
	 * @param args
	 * @return
	 */
	public <T> T get(String sql, Class<T> type, Object... args){
		FormatResult rst=SqlBuilder.format(sql,args);
		//取无声明属性的对象
		if(!hasProperty(type)){
			return getNoPropertyObject(type,rst.getSql(),rst.getArgs());
		}else{
			//取有属性的对象
			return getObject(type,rst.getSql(),rst.getArgs());
		}
	}	
	
	
	/**
	 * 取无声明属性的对象，如String,int...
	 * @param <T>
	 * @param type
	 * @param sql
	 * @param args
	 * @return
	 */
	private <T> T getNoPropertyObject(Class<T> type, final String sql, Object... args){
		try{
			return queryForObject(
				sql, 
				args, 
				getSingleColumnRowMapper(type)
			 );
		}catch(EmptyResultDataAccessException ex){
			return null;
		}
	}
	
	/**
	 * 将单行结果转换为实体bean，字段别名与属性名对应
	 * @param <T>
	 * @param sql
	 * @param cls
	 * @param args
	 * @return
	 */
	private <T> T getObject(Class<T> cls,final String sql,Object... args){
		List<T> list= this.query(sql, new BeanPropertyRowMapper<>(cls),args);
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}		
	
	
	//==========================================================================================
	
	
	/**
	 * 执行sql语句
	 */
	public void execute(String sql) {
		super.execute(SqlBuilder.format(sql));
	}

	/**
	 * 执行添加、修改、删除sql
	 */
	public int update(String sql, Object... args) {
		FormatResult rst=SqlBuilder.format(sql,args);
		return super.update(rst.getSql(), 
				rst.getArgs());
	}

	/**
	 * 执行批量添加、修改、删除操作
	 * @throws SnException 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> int[] batchUpdate(String sql, List<T> batchArgs,
			IBatchArgMapper mapper) throws SnException {
		FormatResult rst=SqlBuilder.format(sql,batchArgs.get(0));
		List<Object[]> args=new ArrayList<>();
		for(T t:batchArgs){
			args.add(mapper.getArgs(t));
		}
		return super.batchUpdate(rst.getSql(), args);
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
		return list(SqlGenerator.genSelectStatement(elementType,null),elementType,args);
	}

	/**
	 * 取单表列表
	 * @param elementType 实体类
	 * @param orderBy 排序
	 * @param args 查询参数
	 * @param <T> 实体类型
	 * @return 实体列表
	 */
	public <T> List<T> list(Class<T> elementType, OrderBy orderBy, Object...args){
		return list(SqlGenerator.genSelectStatement(elementType,orderBy),elementType,args);
	}

	/**
	 * 分页取单表列表
	 *
	 * @param elementType 实体类
	 * @param page        第几页（从1开始）
	 * @param pageSize    分页大小
	 * @param orderBy     排序
	 * @param args        查询参数
	 * @return 实体列表
	 */
	public <T> List<T> list(Class<T> elementType, Integer pageSize, Integer page, OrderBy orderBy, Object... args) {
		return list(SqlGenerator.genSelectStatement(elementType, orderBy),elementType,pageSize,page,args);
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
		return all(SqlGenerator.genSelectStatement(elementType,null), elementType, args);
	}

	/**
	 * 单表-查询单个对象值
	 * @param type 实体类
	 * @param args 查询参数
	 * @return 实体列表
	 */
	@Override
	public <T> T get(Class<T> type, Object... args) {
		return get(SqlGenerator.genSelectStatement(type,null),type,args);
	}

	/**
	 * 按主键更新
	 *
	 * @param bean 实体
	 * @return 更新记录数
	 */
	@Override
	public <T> int update(T bean) {
		return update(SqlGenerator.genUpdateStatement(bean.getClass(),null),bean);
	}

	/**
	 * 按条件更新
	 *
	 * @param bean 实体
	 * @param condition 条件
	 * @return 更新记录数
	 */
	public <T> int update(T bean, ConditionEntity condition) {
		return update(SqlGenerator.genUpdateStatement(bean.getClass(),condition));
	}

	/**
	 * 插入
	 *
	 * @param bean 实体
	 * @return 插入记录数
	 */
	@Override
	public <T> int insert(T bean) {
		return update(SqlGenerator.genInsertStatement(bean.getClass()),bean);
	}

	/**
	 * 按主键删除
	 *
	 * @param bean 实体
	 * @return 删除记录数
	 */
	@Override
	public <T> int delete(T bean) {
		return update(SqlGenerator.genDeleteStatement(bean.getClass(),null), bean);
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
		return update(SqlGenerator.genDeleteStatement(type,conditionSet), conditionSet);
	}
}
