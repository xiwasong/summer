package cn.hn.java.summer.db.mapper.rule;

/**
 * 数据类型映射规则
 * Created by xw2sy on 2017-04-15.
 */
public interface IDataTypeMapperRule {

    /**
     * 取列类型对应的java类型
     * @param columnType 列类型
     * @return java类型
     */
    Class<?> getJavaType(String columnType);

    /**
     * 取java类型对应的列类型
     * @param javaType java类型
     * @return 列类型
     */
    String getColumnType(Class<?> javaType);

}
