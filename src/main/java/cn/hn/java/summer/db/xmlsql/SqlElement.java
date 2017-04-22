package cn.hn.java.summer.db.xmlsql;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.util.ArrayList;
import java.util.List;

/**
 * xml解析实体类，对应sql节点
 * @author sjg
 * @version 1.0.1 2013-09-27
 *
 */
public class SqlElement {
	
	@XStreamAsAttribute
	String id;

	@XStreamAsAttribute
	String name;

	@XStreamAsAttribute
	String prop;

	@XStreamAsAttribute
	String text;

	List<SqlElement> subSql=new ArrayList<>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProp() {
		return prop;
	}

	public void setProp(String prop) {
		this.prop = prop;
	}

	public List<SqlElement> getSubSql() {
		return subSql;
	}

	public void setSubSql(List<SqlElement> subSql) {
		this.subSql = subSql;
	}
}
