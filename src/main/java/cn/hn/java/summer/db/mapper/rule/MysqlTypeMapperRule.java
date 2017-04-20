package cn.hn.java.summer.db.mapper.rule;

import java.math.BigDecimal;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * mysql 数据类型映射规则
 * Created by xw2sy on 2017-04-15.
 */
public class MysqlTypeMapperRule implements IDataTypeMapperRule {

    private static final Map<String,Class<?>> dbToJavaTypeMap=new HashMap<>();
    private static final Map<Class<?>,String> javaToDbTypeMap=new HashMap<>();
    static{
        dbToJavaTypeMap.put("CHAR",String.class);
        dbToJavaTypeMap.put("VARCHAR",String.class);
        dbToJavaTypeMap.put("LONGVARCHAR",String.class);
        dbToJavaTypeMap.put("NUMERIC",BigDecimal.class);
        dbToJavaTypeMap.put("DECIMAL",BigDecimal.class);
        dbToJavaTypeMap.put("BIT",Boolean.class);
        dbToJavaTypeMap.put("BOOLEAN",Boolean.class);
        dbToJavaTypeMap.put("TINYINT",Byte.class);
        dbToJavaTypeMap.put("SMALLINT",Short.class);
        dbToJavaTypeMap.put("INTEGER",Integer.class);
        dbToJavaTypeMap.put("BIGINT",Long.class);
        dbToJavaTypeMap.put("REAL",Float.class);
        dbToJavaTypeMap.put("FLOAT",Double.class);
        dbToJavaTypeMap.put("DOUBLE",Double.class);
        dbToJavaTypeMap.put("BINARY",Byte[].class);
        dbToJavaTypeMap.put("VARBINARY",Byte[].class);
        dbToJavaTypeMap.put("LONGVARBINARY",Byte[].class);
        dbToJavaTypeMap.put("DATE",Date.class);
        dbToJavaTypeMap.put("TIME",Time.class);
        dbToJavaTypeMap.put("TIMESTAMP",Timestamp.class);
        dbToJavaTypeMap.put("CLOB",Clob.class);
        dbToJavaTypeMap.put("BLOB",Blob.class);
        dbToJavaTypeMap.put("ARRAY",Array.class);
        dbToJavaTypeMap.put("GEOMETRY",Byte[].class);


        javaToDbTypeMap.put(String.class,"VARCHAR");
        javaToDbTypeMap.put(Float.class,"FLOAT");
        javaToDbTypeMap.put(Double.class,"DOUBLE");
        javaToDbTypeMap.put(BigDecimal.class,"DECIMAL");
        javaToDbTypeMap.put(Boolean.class,"BIT");
        javaToDbTypeMap.put(Byte.class,"TINYINT");
        javaToDbTypeMap.put(Long.class,"BIGINT");
        javaToDbTypeMap.put(Short.class,"SMALLINT");
        javaToDbTypeMap.put(Date.class,"DATE");
        javaToDbTypeMap.put(Timestamp.class,"TIMESTAMP");
    }

    /**
     * 取列类型对应的java类型
     *
     * @param columnType 列类型
     * @return java类型
     */
    @Override
    public Class<?> getJavaType(String columnType) {
        return dbToJavaTypeMap.get(columnType.toUpperCase());
    }

    /**
     * 取java类型对应的列类型
     *
     * @param javaType java类型
     * @return 列类型
     */
    @Override
    public String getColumnType(Class<?> javaType) {
        return javaToDbTypeMap.get(javaType);
    }
}
