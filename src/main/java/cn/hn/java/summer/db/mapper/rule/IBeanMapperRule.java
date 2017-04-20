package cn.hn.java.summer.db.mapper.rule;

/**
 * Created by xw2sy on 2017-04-15.
 */
public interface IBeanMapperRule {

    /**
     * 通过类名取表名
     * @param className 类名
     * @return 表名
     */
    String getTableName(String className);

    /**
     * 通过表名取类名
     * @param tableName 表名
     * @return 类名
     */
    String getClassName(String tableName);

    /**
     * 通过属性名取列名
     * @param fieldName 属性名
     * @return 列名
     */
    String getColName(String fieldName);

    /**
     * 通过列名取属性名
     * @param colName
     * @return 属性名
     */
    String getFieldName(String colName);
}
