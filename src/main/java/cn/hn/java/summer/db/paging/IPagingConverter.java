package cn.hn.java.summer.db.paging;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import cn.hn.java.summer.db.builder.FormatResult;

/**
 * 数据库分页转换接口
 * @author sjg
 * @version 1.0.1 2013-10-26
 *
 */
public interface IPagingConverter {

	/**
	 * 分页
	 * @param <T>
	 * @param jdbcTemplate JdbcTemplate
	 * @param sql 不带分页的sql
	 * @param pageSize 分页大小
	 * @param page 当前页
	 * @param rowMapper 行映射器
	 * @param args sql中的参数
	 * @return
	 */
	public <T> List<T> paging(JdbcTemplate jdbcTemplate,String sql,int pageSize,int page,RowMapper<T> rowMapper,Object...args);
	
	/**
	 * 格式化sql
	 * @param sql
	 * @param args 传入sql中的参数
	 * @return
	 */
	public FormatResult formatSql(String sql,Object...args);
}
