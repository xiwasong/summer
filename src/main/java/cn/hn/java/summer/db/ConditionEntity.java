package cn.hn.java.summer.db;

import java.util.HashMap;
import java.util.Map;

/**
 * 条件项
 * Created by xw2sy on 2017-04-06.
 */
public class ConditionEntity {

    private Map conditionMap=new HashMap();

    /**
     * 添加条件项
     * @param key
     * @param value
     * @return
     */
    public ConditionEntity set(String key, Object value){
        conditionMap.put(key,value);
        return this;
    }

    public Map getConditionMap(){
        return conditionMap;
    }
}
