package cn.hn.java.summer.db.xmlsql;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * 读取配置文件sql节点转换类
 * @author sjg
 * @version 1.0.1 2013-10-27
 *
 */
public class SqlConverter  implements Converter {
	
	/**
	 * 将SqlElement对象信息写到xml流中
	 */
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        SqlElement sql = (SqlElement) value;
        writer.addAttribute("id", sql.getId());
        writer.setValue(sql.getText());
    }

	/**
	 * 从xml流中取出信息构建SqlElement对象
	 */
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
    	SqlElement sql = new SqlElement();
    	sql.setId(reader.getAttribute("id"));
    	sql.setName(reader.getAttribute("name"));
    	sql.setProp(reader.getAttribute("prop"));
    	sql.setText(reader.getValue());
		List<SqlElement> subSqlList=new ArrayList<>();
    	while (reader.hasMoreChildren()){
    		reader.moveDown();
			SqlElement subSql= (SqlElement) unmarshal(reader,context);
			subSqlList.add(subSql);
			reader.moveUp();
		}
		sql.setSubSql(subSqlList);
		return sql;
    }

	public boolean canConvert(Class clazz) {
        return clazz.equals(SqlElement.class);
    }

}
