package db;

import cn.hn.java.summer.db.mapper.EntityGenerator;
import cn.hn.java.summer.db.mapper.SqlMapper;
import cn.hn.java.summer.utils.ClassUtils;
import cn.hn.java.summer.utils.ReflectUtils;
import cn.hn.java.summer.utils.RegExpUtils;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xw2sy on 2017-04-14.
 */
public class SqlMaperTest {

    @Test
    public void testCreateScripteRegExpress(){
        String script="CREATE TABLE `mg_area` (\n" +
                "  `areaId` varchar(12) NOT NULL COMMENT '区域编号(省码/省码+市码/省码+市码+县码...)',\n" +
                "  `areaName` varchar(255) NOT NULL COMMENT '区域名称',\n" +
                "  `areaLevel` int(11) DEFAULT NULL COMMENT '区域级别(0:省,1:市,2:县,3:区/镇,4:街道/村,5:组)',\n" +
                "  `parentId` varchar(20) NOT NULL COMMENT '父级编号',\n" +
                "  PRIMARY KEY  (      `areaId`, parentId)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='区域(省/市/县/区/街道)';\n";


        List<String[]> result=new ArrayList<>();
        result.add(RegExpUtils.findOne(script, SqlMapper.REG_CREATE_TABLE_NAME));
        result.addAll(RegExpUtils.findAll(script, SqlMapper.REG_CREATE_FIELDS));
        result.addAll(RegExpUtils.findAll(script, SqlMapper.REG_CREATE_TABLE_KEYS));
        for(String[] s : result){
            System.out.println(String.join(",",s));
        }

    }

    @Test
    public void testGetFields(){
        Field[] fields= ReflectUtils.getAllFields(Bean.class);
        for(Field f : fields){
            System.out.println(f.getName());
        }
    }

    @Test
    public void testGetClasses(){
        List<Class<?>> allClz= ClassUtils.getClasses("", ".*utils.*");
        for (Class c: allClz){
            System.out.println(c.getName());
        }
    }

    @Test
    public void testGenEntity() throws URISyntaxException, IOException {
        new EntityGenerator().genAllEntity("/src/main/java","src/main/resources",false);
    }

}
