package cn.hn.java.summer.xmlsql;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

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
	String text;

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
}
