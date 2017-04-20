package cn.hn.java.summer.db.mapper;

import cn.hn.java.summer.db.ConditionEntity;
import cn.hn.java.summer.db.OrderBy;
import cn.hn.java.summer.utils.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * sql生成器
 * Created by xw2sy on 2017-04-16.
 */
public class SqlGenerator {

    //查询语句模板
    private static final String SQL_SELECT_TEMPLATE="select {0} from {1} where {2} {3}";
    //更新语句模板
    private static final String SQL_UPDATE_TEMPLATE="update {0} set {1} where {2}";
    //插入语句模板
    private static final String SQL_INSERT_TEMPLATE="insert into {0} ({1}) values({2})";
    //删除语句模板
    private static final String SQL_DELETE_TEMPLATE="delete from {0} where ({1})";

    /**
     * 为实体类生成select语句
     * @param beanType 实体类型
     * @param orderBy 排序
     * @param <T> 实体类类型
     * @return
     */
    public static <T> String genSelectStatement(Class<T> beanType, OrderBy orderBy){
        SqlMapper.BeanMap beanMap= SqlMapper.getBeanMap(beanType);
        if(beanMap!=null){
            StringBuilder sbFields=new StringBuilder();
            StringBuilder sbConditions=new StringBuilder();
            StringBuilder sbOrderBy=new StringBuilder();
            //生成字段和条件列表
            for(SqlMapper.ColField f : beanMap.fields){
                if(sbFields.length()!=0){
                    sbFields.append(",");
                    sbConditions.append(" and ");
                }
                //字段
                sbFields.append(f.colName)
                        .append(" as ")
                        .append(f.propName);
                //条件
                sbConditions.append(f.colName)
                        .append("=:")
                        .append(f.propName);
                //默认按主键倒序排序
                if(orderBy==null && f.isPrimaryKey){
                    if(sbOrderBy.length()!=0){
                        sbOrderBy.append(",");
                    }
                    sbOrderBy.append(f.colName).append(" desc");
                }
            }

            if(orderBy!=null) {
                //生成排序
                List<OrderBy.OrderByInfo> orders = orderBy.getOrderByList();
                for (OrderBy.OrderByInfo orderByInfo : orders) {
                    String colName = beanMap.propKeyMap.get(orderByInfo.name).colName;
                    sbOrderBy.append(colName).append(" ").append(orderByInfo.direction);
                }
            }

            //连接为完整的查询语句
            //select {0} from {1} where {2}
            return StringUtils.format(
                    SQL_SELECT_TEMPLATE,
                    sbFields.toString(),
                    beanMap.tableName,
                    sbConditions.toString(),
                    sbOrderBy.length()==0?"":" order by "+sbOrderBy.toString()
            );
        }
        return "";
    }

    /**
     * 生成update语句
     * 若有指定多个条件，则生成的update语句条件都是以and连接
     * @param beanType 实体类型
     * @param <T> 实体类型
     * @return update语句
     */
    public static <T> String genUpdateStatement(Class<T> beanType, ConditionEntity condition){
        SqlMapper.BeanMap beanMap= SqlMapper.getBeanMap(beanType);
        if(beanMap!=null){
            StringBuilder sbSets=new StringBuilder();
            for(SqlMapper.ColField cf : beanMap.fields){
                if(sbSets.length()!=0){
                    sbSets.append(",");
                }
                //set xx=:xx
                sbSets.append(cf.colName)
                        .append("=:")
                        .append(cf.propName);
            }

            //返回组装的update语句
            return StringUtils.format(
                    SQL_UPDATE_TEMPLATE,
                    beanMap.tableName,
                    sbSets.toString(),
                    //条件
                    genUpdateOrDeleteWhere(beanMap,condition)
            );

        }
        return "";
    }

    /**
     * 生成insert语句
     * @param beanType 实体类型
     * @param <T> 实体类型
     * @return insert语句
     */
    public static <T> String genInsertStatement(Class<T> beanType){
        SqlMapper.BeanMap beanMap=SqlMapper.getBeanMap(beanType);
        if(beanMap!=null){
            StringBuilder sbColumns=new StringBuilder();
            StringBuilder sbValues=new StringBuilder();
            for(SqlMapper.ColField cf : beanMap.fields){
                if(sbColumns.length()!=0){
                    sbColumns.append(",");
                    sbValues.append(",");
                }
                //a,b,c
                sbColumns.append(cf.colName);
                //:a,:b,:c
                sbValues.append(":").append(cf.propName);
            }

            return StringUtils.format(
                    SQL_INSERT_TEMPLATE,
                    beanMap.tableName,
                    sbColumns.toString(),
                    sbValues.toString()
            );
        }

        return "";
    }

    /**
     * 生成删除语句
     * @param beanType 实体类型
     * @param <T> 实体类型
     * @return 删除语句
     */
    public static <T> String genDeleteStatement(Class<T> beanType, ConditionEntity condition){
        SqlMapper.BeanMap beanMap=SqlMapper.getBeanMap(beanType);
        if(beanMap!=null){
            return StringUtils.format(
                    SQL_DELETE_TEMPLATE,
                    beanMap.tableName,
                    //条件
                    genUpdateOrDeleteWhere(beanMap,condition)
            );
        }
        return "";
    }

    /**
     * 生成更新或删除语句的where条件
     * @param beanMap 实体映射
     * @param condition 条件
     * @param <T> 实体类型
     * @return where条件
     */
    private static <T> String genUpdateOrDeleteWhere(SqlMapper.BeanMap beanMap, ConditionEntity condition){
        StringBuilder sbCondition=new StringBuilder();
        Map<String,Object> conditionMap=new HashMap<>();
        for(SqlMapper.ColField cf : beanMap.fields){
            if(
                //无指定条件且为主键，则设置为更新的条件
                condition==null && cf.isPrimaryKey ||
                //有指定条件，找到对应的字段作为条件
                condition!=null && condition.getConditionMap().get(cf.propName)!=null
            ){
                conditionMap.put(cf.colName,cf.propName);
            }

        }

        //生成where xx=:xx
        for(String key : conditionMap.keySet()){
            if(sbCondition.length()!=0) {
                sbCondition.append(" and ");
            }
            sbCondition.append(key)
                    .append("=:")
                    .append(conditionMap.get(key));
        }

        return sbCondition.toString();
    }

}
