package cn.hn.java.summer.db.paging;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import cn.hn.java.summer.db.builder.FormatResult;
import cn.hn.java.summer.db.builder.SqlBuilder;

/**
 * mysql分页转换器
 * @author sjg
 * @version 1.0.1 2013-10-26
 *
 */
public class MysqlPagingConverter implements IPagingConverter{
	static final Log logger = LogFactory.getLog(MysqlPagingConverter.class);
	
	/** 
	 * 取格式化好的sql
	 */
	public FormatResult formatSql(String sql, Object... args) {
		//格式化成可执行sql
		FormatResult rst=SqlBuilder.format(sql,args);
		return rst;
	}

	/**
	 * 分页方法
	 */
	public <T> List<T> paging(JdbcTemplate jdbcTemplate, String sql, int pageSize,
			int page, RowMapper<T> rowMapper, Object... args) {
		//起始索引
		int start=(page-1)*pageSize;
		//limit 1,10
		sql+=" limit "+start+","+pageSize;
		
		logger.debug("paging sql=>"+sql);
		return jdbcTemplate.query(sql,rowMapper,args);
	}
 
}
