package cn.hn.java.summer.xmlsql;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

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
    	sql.setText(reader.getValue());
        return sql;
    }

    @SuppressWarnings("rawtypes")
	public boolean canConvert(Class clazz) {
        return clazz.equals(SqlElement.class);
    }

}
