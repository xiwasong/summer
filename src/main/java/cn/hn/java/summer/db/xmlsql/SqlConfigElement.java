package cn.hn.java.summer.db.xmlsql;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * xml解析实体类，对应SqlConfig根节点
 * @author sjg
 * @version 1.0.1 2013-09-27
 *
 */
@XStreamAlias("SqlConfig")
public class SqlConfigElement {

	/**
	 * query节点
	 */
	SqlTypeElement query;
	/**
	 * update节点
	 */
	SqlTypeElement update;
	/**
	 * insert节点
	 */
	SqlTypeElement insert;
	/**
	 * delete节点
	 */
	SqlTypeElement delete;
	public SqlTypeElement getQuery() {
		return query;
	}
	public void setQuery(SqlTypeElement query) {
		this.query = query;
	}
	public SqlTypeElement getUpdate() {
		return update;
	}
	public void setUpdate(SqlTypeElement update) {
		this.update = update;
	}
	public SqlTypeElement getInsert() {
		return insert;
	}
	public void setInsert(SqlTypeElement insert) {
		this.insert = insert;
	}
	public SqlTypeElement getDelete() {
		return delete;
	}
	public void setDelete(SqlTypeElement delete) {
		this.delete = delete;
	}
}
