package cn.hn.java.summer.db;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xw2sy on 2017-04-14.
 */
public class OrderBy {

    private List<OrderByInfo> orderByList=new ArrayList<>();

    public List<OrderByInfo> getOrderByList() {
        return orderByList;
    }

    public OrderBy asc(String name){
        orderByList.add(new OrderByInfo(name,"asc"));
        return this;
    }

    public OrderBy desc(String name){
        orderByList.add(new OrderByInfo(name,"desc"));
        return this;
    }

    public class OrderByInfo{
        public String name;
        public String direction;

        public OrderByInfo(String name, String direction) {
            this.name = name;
            this.direction = direction;
        }
    }
}
