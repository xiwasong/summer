package cn.hn.java.summer.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xw2sy on 2017-04-14.
 */
public class RegExpUtils {

    /**
     * 获取正则匹配的结果
     * @param content 内容
     * @param regExp 正则
     * @return 匹配内容列表
     */
    public static List<String[]> findAll(String content, String regExp){
        Pattern pattern=Pattern.compile(regExp,Pattern.CASE_INSENSITIVE);
        Matcher matcher=pattern.matcher(content);
        List<String[]> result=new ArrayList<>();
        while(matcher.find()){
            String[] row=new String[matcher.groupCount()];
            for(int i=1;i<=matcher.groupCount();i++){
                row[i-1]=matcher.group(i);
            }
            result.add(row);
        }
        return result;
    }

    /**
     * 获取正则匹配的结果
     * @param content 内容
     * @param regExp 正则
     * @return 匹配的单个内容
     */
    public static String[] findOne(String content, String regExp){
        List<String[]> result=findAll(content,regExp);
        if(!result.isEmpty()){
            return result.get(0);
        }
        return  null;
    }
}
