package cn.hn.java.summer.db.xmlsql;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

/**
 * xml解析实体类，对应一个操作类型
 * @author sjg
 * @version 1.0.1 2013-10-27
 *
 */
public class SqlTypeElement {
	
	@XStreamImplicit(itemFieldName="sql")
	List<SqlElement> sql;

	public List<SqlElement> getSql() {
		return sql;
	}

	public void setSql(List<SqlElement> sql) {
		this.sql = sql;
	} 
}
