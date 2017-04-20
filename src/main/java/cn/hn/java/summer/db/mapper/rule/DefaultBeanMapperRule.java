package cn.hn.java.summer.db.mapper.rule;

/**
 * Created by xw2sy on 2017-04-15.
 */
public class DefaultBeanMapperRule implements IBeanMapperRule {

    /**
     * 取表名
     *
     * @param className 类名
     * @return 表名
     */
    @Override
    public String getTableName(String className) {
        return className;
    }

    /**
     * 取列名
     *
     * @param fieldName 属性名
     * @return 列名
     */
    @Override
    public String getColName(String fieldName) {
        return fieldName;
    }

    /**
     * 通过表名取类名
     *
     * @param tableName 表名
     * @return 类名
     */
    @Override
    public String getClassName(String tableName) {
        return tableName;
    }

    /**
     * 通过列名取属性名
     *
     * @param colName
     * @return 属性名
     */
    @Override
    public String getFieldName(String colName) {
        return colName;
    }
}
