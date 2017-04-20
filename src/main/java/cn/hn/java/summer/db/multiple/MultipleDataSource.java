package cn.hn.java.summer.db.multiple;

import cn.hn.java.summer.db.DbOperator;

import java.util.ArrayList;
import java.util.List;

/**
 * 多个数据源类
 * @author sjg
 * @version 1.0.1 2013-10-26
 *
 */
public class MultipleDataSource {

	/**
	 * 存放多个JdbcTemplate
	 */
	private List<DbOperator> dbOperators=new ArrayList<>();

	public List<DbOperator> getDbOperators() {
		return dbOperators;
	}

	/**
	 * 添加数据库操作实例
	 * @param dbOperator 数据库操作类实例
	 */
	public void addDbOperator(DbOperator dbOperator){
		dbOperators.add(dbOperator);
	}

	/**
	 * 添加数据库操作实例集合
	 * @param dbOperators 数据库操作实例集合
	 */
	public void addAllDbOperator(List<DbOperator> dbOperators){
		dbOperators.addAll(dbOperators);
	}

	/**
	 * 取对应序号数据源的dbOperators
	 * @param mc
	 * @return
	 */
	public DbOperator get(Object mc){
		if(mc instanceof DataSource2){
			return dbOperators.get(1);
		}else if(mc instanceof DataSource3){
			return dbOperators.get(2);
		}
		return dbOperators.get(0);
	}
	
}
