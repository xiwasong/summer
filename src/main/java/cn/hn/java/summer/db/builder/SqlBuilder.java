package cn.hn.java.summer.db.builder;

import cn.hn.java.summer.annotation.Mosaic;
import cn.hn.java.summer.db.ConditionEntity;
import cn.hn.java.summer.utils.AppUtils;
import cn.hn.java.summer.utils.ArrayUtils;
import cn.hn.java.summer.utils.ReflectUtils;
import cn.hn.java.summer.db.xmlsql.SqlConfigElement;
import cn.hn.java.summer.db.xmlsql.SqlConverter;
import cn.hn.java.summer.db.xmlsql.SqlElement;
import cn.hn.java.summer.db.xmlsql.SqlTypeElement;
import cn.hn.java.summer.utils.StringUtils;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * sql构建器
 * @author sjg
 * @version 1.0.1 2013-10-22
 *
 */
public abstract class SqlBuilder {
	private static final Log logger = LogFactory.getLog(SqlBuilder.class);
	
	/**
	 * 注释正则
	 * 1.以"//"开头的注释
	 * 2.以<!--->包围的注释
	 * 3.以"/** /"包围的注释
	 */
	private static final String Regexp_Comment="((//[^\\n]*)|(<!--[^\\n]*-->)\\s*\\n)|(/\\*.*\\*/)";

	/**
	 * 去除order by.. 正则
	 */
	private static final String Regexp_Orderby="order by .*";
	
	/**
	 * 命名参数名正则
	 */
	private static final String Regexp_NamedParamName="(?<!:) *: *(\\w+)";

	/**
	 * 硬编码正则
	 */
	private static final String REGEXP_HARDCODE="#([^#\\s]+)#";

	/**
	 * 取空参数正则
	 */
	//static final String Regexp_NullParam="where?(?<=\\?[%\\_]{0,10}|where)[^?]+\\?[%\\_]*";
	//static final String Regexp_NullParam="where?[^?]+\\?[%\\_]*";

	private static final String SQL_CONFIG_XML="sql*.xml";

	/**
	 * 级联查询父子sqlId的连接符
	 */
	public static  final String CASCADE_SYMBOL="<-";

	/**
	 * sql映射-query
	 */
	private static final Map<String,String> SQL_MAPPING_QUERY=new HashMap<>();

	/**
	 * sql映射-update
	 */
	private static final Map<String,String> SQL_MAPPING_UPDATE=new HashMap<>();

	/**
	 * sql映射-insert
	 */
	private static final Map<String,String> SQL_MAPPING_INSERT=new HashMap<>();
	
	/**
	 * sql映射-delete
	 */
	private static final Map<String,String> SQL_MAPPING_DELETE=new HashMap<>();
	

	static{
		//初始化sql配置文件
		String path=scanSqlXml();

		//监控sql文件改动
		new Thread(new SqlFileWatcher(path)).start();		
	}
	
	/**
	 * 扫描sql配置文件
	 * @return 返回sql
	 */
	public static String scanSqlXml(){
		try {
			//查当前程序目录中的sql配置文件目录
			String configPath=AppUtils.getAppRoot()+"/config";
			logger.info("sql config dir found:"+configPath);
			//取下面所有以sql开头以.xml结尾的配置文件
			Collection<File> sqlXmlFiles= FileUtils.listFiles(new File(configPath),new PrefixFileFilter("sql"),new SuffixFileFilter(".xml"));
			
			//读取sql配置
			for(File file:sqlXmlFiles){
				ReadSqlXml(file);
			}
			return configPath;
			
		} catch (Exception e) {
			logger.error("scan sql*.xml config file failed",e);
		}
		return null;
	}	
	
	/**
	 * 从配置文件中解析sql配置
	 * @param xmlFile 配置文件
	 */
	private static void ReadSqlXml(File xmlFile){
		//解析器
		XStream xs=new XStream(new StaxDriver());
		xs.registerConverter(new SqlConverter());
		xs.processAnnotations(SqlConfigElement.class);	
				
		if(xmlFile!=null){
			SqlConfigElement config =(SqlConfigElement)xs.fromXML(xmlFile);
			//读取 查询、修改、插入、删除 sql配置
			ReadSql(config.getQuery(),SQL_MAPPING_QUERY);		
			ReadSql(config.getUpdate(),SQL_MAPPING_UPDATE);
			ReadSql(config.getInsert(),SQL_MAPPING_INSERT);
			ReadSql(config.getDelete(),SQL_MAPPING_DELETE);
		}else{
			logger.error("InputStream from "+SQL_CONFIG_XML+" is null");
		}
	}
	
	/**
	 * 读取sql存储
	 * @param el sql类型
	 * @param map sql map
	 */
	private static void ReadSql(SqlTypeElement el, Map<String,String> map){
		if(el==null || el.getSql()==null){
			return;
		}
		List<SqlElement> list= el.getSql();
		for(SqlElement se : list){
			//父级sql也可以作为有效的sql
			map.put(se.getId(),processSqlScripts(se.getText()));
			if(se.getSubSql().isEmpty()){
				continue;
			}
			//处理有子级sql脚本的
			for(SqlElement subSql : se.getSubSql()){
				if(StringUtils.isNotBlank(subSql.getProp())){
					//父子sql级联使用
					map.put(
							//级联关系作为id
							se.getId()+CASCADE_SYMBOL+subSql.getProp(),
							//子脚本作为sql
							processSqlScripts(subSql.getText())
					);
				}
				//父子sql连接使用
				map.put(
						//将id和子脚本名称连接作为id
						se.getId()+"+"+subSql.getName(),
						//与子脚本连接
						processSqlScripts(se.getText()+subSql.getText())
				);
			}
		}
	}

	/**
	 * 处理和缓存脚本
	 * @param content 脚本内容
	 * @return 处理过的脚本内容
	 */
	private static String processSqlScripts(String content){
		return lowerCaseKeyWords(//小写关键字
					clearFragment(//清理sql碎片
							processComment(//处理注释、空格
									content
							)
					)
			);
	}
	
	/**
	 * 处理注释、空格
	 * @return 返回处理后的sql
	 */
	private static String processComment(String sql){
		if(sql!=null){
			return sql.replaceAll(Regexp_Comment, " ");
		}
		return "";
	}
	
	/**
	 * 清理sql碎片
	 * @return 返回sql
	 */
	private static String clearFragment(String sql){
		if(sql!=null){
			//去掉2个及以上的空白字符
			return sql.replaceAll("[\\s]{2,}", " ")
						.trim();
		}
		return "";
	}
	
	/**
	 * 小写关键字
	 * @param sql sql
	 * @return sql
	 */
	private static String lowerCaseKeyWords(String sql){
		sql=" "+sql;
		//将select insert update delete or and where转换成小写
		sql=Pattern.compile("\\sselect\\s",Pattern.CASE_INSENSITIVE).matcher(sql).replaceAll(" select ");
		sql=Pattern.compile("\\sinsert\\s",Pattern.CASE_INSENSITIVE).matcher(sql).replaceAll(" insert ");
		sql=Pattern.compile("\\supdate\\s",Pattern.CASE_INSENSITIVE).matcher(sql).replaceAll(" update ");
		sql=Pattern.compile("\\sdelete\\s",Pattern.CASE_INSENSITIVE).matcher(sql).replaceAll(" delete ");
		sql=Pattern.compile("\\sset\\s",Pattern.CASE_INSENSITIVE).matcher(sql).replaceAll(" set ");
		sql=Pattern.compile("\\sor\\s",Pattern.CASE_INSENSITIVE).matcher(sql).replaceAll(" or ");
		sql=Pattern.compile("\\sand\\s",Pattern.CASE_INSENSITIVE).matcher(sql).replaceAll(" and ");
		sql=Pattern.compile("\\swhere\\s",Pattern.CASE_INSENSITIVE).matcher(sql).replaceAll(" where ");
		sql=Pattern.compile("\\sorder by\\s",Pattern.CASE_INSENSITIVE).matcher(sql).replaceAll(" order by ");
		sql=Pattern.compile("\\sgroup by\\s",Pattern.CASE_INSENSITIVE).matcher(sql).replaceAll(" group by ");
		return sql;
	}
		
	/**
	 * 针对Sql的格式化字符串<br/> 
	 * @param sqlId sqlid
	 * @param args 参数,
     *             1:原始类型参数=>[1,2,3],
     *             2:实体类型参数=>
	 *             		[bean,bean],[bean,bean,Object[]{key,value}],
	 *             		[bean,bean,Object[][]{{key,value},{key,value}}]
	 *             		[bean,bean,Object[][]{{key,value},{key,value}},ConditionEntity]
	 * @return 格式化后的结果
	 */
	public static FormatResult format(String sqlId,Object... args){
		//取到配置的sql
		String sql=getSqlById(sqlId);
		
		//取不到，表示传入的为完整sql
		//if(sql==null){
		//    logger.warn("sqlId:"+sqlId+" not found!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		//	return new FormatResult(sqlId,args);
		//}
		if(sql==null){
			sql=sqlId;
		}

		FormatResult rst=new FormatResult(sql,args);
		
		//处理强制使用所有
		boolean isForce= processForceUseAllParams(rst);
		
		//处理硬编码参数
		//processHardCode(rst);暂时屏蔽对硬编码的支持
		
		//有传参数
		if(rst.getArgs()!=null && rst.getArgs().length>0 && rst.getArgs()[0]!=null){

		    Package aPackage=rst.getArgs()[0].getClass().getPackage();
		    if(aPackage!=null && aPackage.getName().equals("java.lang")){
		        //基本类型参数列表，不用处理
            }else{
		        //其它类型转换为map
                Map argsMap=translateArgsToMap(rst.getArgs());
                //生成顺序化参数
                rst.setArgs(getOrderedParams(rst.getSql(),argsMap));
            }
		}else{
			//无参数,设置每个参数位置的参数为null,后面删除参数时会将所有为null的参数删除
			rst.setArgs(getEmptyParams(rst.getSql()));
		}
		
		//将命名参数替换成"?"
		sql=rst.getSql().replaceAll(Regexp_NamedParamName, "?");		
		
		//恢复转义字符
		//sql=restoreEscapeChars(sql);//不能用转义，有问题	
		
		//保存
		rst.setSql(sql);
		
		//不强制使用所有参数
		if(!isForce){
			//删除空参数
			deleteNullParam(rst);
		}

		//处理in查询参数
		processInCondition(rst);

		logger.debug("format sql ["+sqlId+"]=>"+rst.getSql());
		//调试参数输出
		if(logger.isDebugEnabled() && rst.getArgs()!=null){
			StringBuilder sbArgs=new StringBuilder("params=>");
			for(int i=0; i<rst.getArgs().length; i++){
				sbArgs.append("p").
				append(i+1).
				append(":").
				append(rst.getArgs()[i]).
				append(",");
			}
			logger.info(sbArgs.toString());
		}
		return rst;
	}

	/**
	 * 处理in查询参数
	 * 将in(?)处的问号替换为对应参数数量的问号
	 * 同时将in的数组参数展开到参数列表
	 * @param rst 格式化结果
	 */
	private static void processInCondition(FormatResult rst){
		StringBuilder sbSql=new StringBuilder(rst.getSql());
		List<Object> args=new ArrayList<>();
		int index,lastIndex=0,i=0;
		while ((index=sbSql.indexOf("?",lastIndex+1))!=-1){
			Object arg=rst.getArgs()[i];
			//若是数组参数，则视为in(?)的参数
			if(arg.getClass().isArray()){
				//生成?,?,?串
				String statement=StringUtils.repeat(",?",Array.getLength(arg)).substring(1);
				//将原来的?替换成?,?,?
				sbSql.replace(index,index+1,statement);
				//插入了字符，索引位置后移
				index=index+statement.length();
				//展开in参数
				for(int j=0;j<Array.getLength(arg);j++) {
					args.add(Array.get(arg, j));
				}
			}else{
				args.add(rst.getArgs()[i]);
			}
			lastIndex=index;
			i++;
		}

		rst.setSql(sbSql.toString());
		rst.setArgs(args.toArray());
	}

    /**
     * 将参数转换为map
     * @param args 参数
     * @return map
     */
	private static Map translateArgsToMap(Object[] args){
	    Map map=new HashMap();
	    for(Object arg : args){
	    	if(arg instanceof ConditionEntity) {
	    		//ConditionEntity对象,直接取条件map
				map.putAll(((ConditionEntity) arg).getConditionMap());
			}else if(arg instanceof Object[][]){
                //map格式数组
                Map mp=ArrayUtils.toMap((Object[][])arg);
                map.putAll(mp);
            }else if(arg instanceof Object[] ){
	            //数组类型map
                Object[] arr=(Object[])arg;
                if(arr.length==2) {
                    map.put(arr[0].toString(),arr[1]);
                }
            }else{
                //bean转换为map
                Map mp=ReflectUtils.beanToMap(arg);
                map.putAll(mp);
            }
        }
        return map;
    }

	/**
	 * 处理强制使用所有参数
	 * @param fr 格式化对象
	 * @return 是否强制参数
	 */
	private static boolean processForceUseAllParams(FormatResult fr){
		Object[] args= fr.getArgs();
		//只能是第一个参数为强制参数
		if(args.length>0 && args[0] instanceof ForceUseAllParams){
			args=ArrayUtils.remove(args,0);
			fr.setArgs(args);
			return true;
		}
		return false;
	}
	
	/**
	 * 处理硬编码参数
	 * @param fr 格式化结果
	 */
	private static void processHardCode(FormatResult fr){
		//如果没有硬编码不处理
		if(!fr.getSql().matches(REGEXP_HARDCODE)){
			return;
		}

		Object[] args= fr.getArgs();
		List<Object> newArg=new ArrayList<>();
		
		for(Object obj: args){
			if(obj instanceof HardCode){
				HardCode hc=(HardCode)obj;
				//替换硬编码参数
				fr.setSql(fr.getSql().replace("#"+hc.getKey()+"#", 
							//过滤'
							hc.isEscape()? hc.getParam().replace("'", "''"):hc.getParam()
						)
				);
			}else{
				//使用注解硬编码参数(拼接sql)的
				//找出拼接的占位符
				Matcher matcher= Pattern.compile(REGEXP_HARDCODE).matcher(fr.getSql());
				while(matcher.find()){
					String pos=matcher.group(1);
					//找到拼接的字段
					Field mosField=null;
					try {
						mosField = ReflectUtils.getAccessibleField(obj.getClass(),pos);
					} catch (Exception e) {
						//忽略
					}
					if(mosField==null){
						continue;
					}
					//是否有Mosaic注解
					if(mosField.getAnnotation(Mosaic.class)==null){
						continue;
					}
					//取拼接字段的值
					Object posValue=ReflectUtils.getFieldValue(obj, pos);
					if(posValue==null){
						posValue="";
					}
					//转义'
					posValue=posValue.toString().replace("'", "''");
					//替换占位符为值
					fr.setSql(fr.getSql().replaceAll("#"+pos+"#", posValue.toString()));
				}
				newArg.add(obj);
			}
		}
		fr.setArgs(newArg.toArray());
	}
	
	/**
	 * 针对Sql的格式化字符串<br/> 无参数
	 * @param sqlId sqlid
	 * @return sql
	 */
	public static String format(String sqlId){
		return format(sqlId,new Object[0]).getSql();
	}
	
	/**
	 * 取按照sql参数排好序的参数
	 * @param sql sql
	 * @param arg map
	 * @return 按照sql参数排好序的参数
	 */
	private static Object[] getOrderedParams(String sql, Map arg){
		if(arg==null || arg.size()==0){
			return new Object[0];
		}

		//排序好的参数
		List<Object> orderedArgs=new ArrayList<>();

		Pattern p=Pattern.compile(Regexp_NamedParamName);
		Matcher mch= p.matcher(sql);

		String fieldName="";
		try {
			while(mch.find()){
				fieldName=mch.group(1);
				if(arg.containsKey(fieldName)){
					//保存参数值
					Object getValue=arg.get(fieldName);
					orderedArgs.add(getValue);
				}else{
					orderedArgs.add(null);
				}
			}
		} catch (Exception e) {
			logger.error("Gets the parameters error:"+fieldName,e);
			//throw new SummerException(StrUtil.format("获取参数出错:{0},{1}",fieldName,e.getMessage()),e);
		}
		return orderedArgs.toArray();
	}
	
	/**
	 * 取空的参数列表
	 * @param sql sql
	 * @return 返回空的参数
	 */
	private static Object[] getEmptyParams(String sql){
		//排序好的参数
		List<Object> orderedArgs=new ArrayList<>();
		
		Pattern p=Pattern.compile(Regexp_NamedParamName);		
		Matcher mch= p.matcher(sql);		
				
		while(mch.find()){
			//为每个参数位置设置空参数
			orderedArgs.add(null);
		}
		
		return orderedArgs.toArray();
	}
	
	/**
	 * 删除空参数
	 * @param rst 格式化结果
	 */
	private static void deleteNullParam(FormatResult rst){
		if(rst.getArgs()==null || rst.getArgs().length==0){
			return;
		}

		String startWith=rst.getSql().trim();
		if(startWith.startsWith("update")){
			//删除空的更新字段
			deleteUpdateNullFields(rst);
			return;
		}else if(
			//对于插入、删除操作不能删除为空的参数
				startWith.startsWith("insert") ||
				startWith.startsWith("delete")){
			return;
		}

		//删除空的查询条件
		deleteQueryNullParams(rst);
	}

	/**
	 * 删除查询sql中的空条件
	 * @param rst 结果
	 */
	private static void deleteQueryNullParams(FormatResult rst){
		List<Object> args=new ArrayList<>();
		int argLen=rst.getArgs().length;
		int i=argLen-1;

		//匹配查询条件参数
		String sql=rst.getSql();
		StringBuilder sbSql=new StringBuilder(sql);

		//最后一个问号位置
		int lastIndex=-1;
		while(i>=0){
			lastIndex=sbSql.lastIndexOf("?",lastIndex==-1?sbSql.length()-lastIndex:lastIndex-1);
			//判断?前后是否有like用的字符:%、_
			if(lastIndex>=0 && lastIndex<sbSql.length()-1){
				char cAfter= sbSql.charAt(lastIndex+1);
				char cBefore=sbSql.charAt(lastIndex-1);
				//增加一个长度
				if(cAfter=='%' || cAfter=='_'){
					//加上后面的通配符
					if(rst.getArgs()[i]!=null){
						rst.getArgs()[i]=rst.getArgs()[i].toString()+cAfter;
					}
					//去掉sql中的通配符
					sbSql.replace(lastIndex+1, lastIndex+2, "");
				}
				if(cBefore=='%' || cBefore=='_'){
					//加上前面的匹配符
					if(rst.getArgs()[i]!=null){
						rst.getArgs()[i]=cBefore+rst.getArgs()[i].toString();
					}

					//去掉sql中的通配符
					sbSql.replace(lastIndex-1, lastIndex, "");
					lastIndex--;
				}
			}

			boolean delArg=false;
			//参数为空
			if(rst.getArgs()[i]==null ||
					StringUtils.isBlank(rst.getArgs()[i].toString()) ||
					rst.getArgs()[i].toString().equals("%%") ||
					rst.getArgs()[i].toString().equals("%")
					){
				//往前找and/or/where
				int startIndex=sbSql.lastIndexOf("and ", lastIndex);
				int startIndex1=sbSql.lastIndexOf("or ", lastIndex);
				int startIndex2=sbSql.lastIndexOf("where ", lastIndex);
				//找到最近一个
				startIndex=Math.max(Math.max(startIndex, startIndex1),startIndex2);
				if(startIndex!=-1 && startIndex<lastIndex){
					int count1=sbSql.length();
					//找到，删除或替换成where 1=1
					sbSql.replace(startIndex, lastIndex+1, startIndex==startIndex2?"where 1=1":"");
					//减去减少的数量
					lastIndex-=count1-sbSql.length();
					delArg=true;
				}
			}

			//删除空参数
			if(!delArg){
				args.add(rst.getArgs()[i]);
			}
			i--;
		}

		//保存
		Object[] arr=args.toArray();
		org.apache.commons.lang3.ArrayUtils.reverse(arr);
		rst.setArgs(arr);
		rst.setSql(sbSql.toString().replaceAll(" {2,}", " "));
	}

	/**
	 * 删除更新语句中的空字段
	 * 方便只更新传了值的字段
	 * @param rst 结果
	 */
	private static void deleteUpdateNullFields(FormatResult rst){
		String sql=rst.getSql();
		StringBuilder sbSql=new StringBuilder();
		Object[] args= rst.getArgs();
		List<Object> targetArgs=new ArrayList<>();

		int setIndex=sql.indexOf("set ")+4;
		if(setIndex<10){//至少有update xx
			return;
		}
		//存储set前部分
		sbSql.append(sql.substring(0,setIndex));

		//where位置
		int whereIndex=sql.indexOf(" where");
		if(whereIndex==-1){
			whereIndex=sql.length();
		}
		//截取set和where之间的字段设置
		String[] fieldSets=sql.substring(setIndex,whereIndex).split(",");

		 //更新的字段sql片断：set a = ? , b= ?, c =?
		for(int i=0;i<args.length;i++){
			if(args[i]==null){
				//略过为空的字段
				continue;
			}
			targetArgs.add(args[i]);
			//索引超过字段个数为where中的条件参数
			if(i<=fieldSets.length-1) {
				sbSql.append(fieldSets[i]).append(",");
			}
		}
		//去除多余的","
		sbSql.delete(sbSql.length()-1,sbSql.length());
		//连上where部分
		sbSql.append(sql.substring(whereIndex));

		rst.setSql(sbSql.toString());
		rst.setArgs(targetArgs.toArray());
	}
	
	/**
	 * 组织成count语句
	 * @param sql sql
	 * @return count sql
	 */
	public static String formCountSql(String sql){
		
		//生成count语句
		if(sql!=null){
			
			//去掉order by...
			sql=Pattern.compile(Regexp_Orderby,Pattern.CASE_INSENSITIVE).
			matcher(sql).replaceFirst("");
			
			//生成select count(0) from xx
			if(!sql.contains("group by")){
				//无group by的sql可以去掉查询字段
				//找到第一个select与第一个from之间的字符替换
				sql="select count(0) "+sql.substring(sql.toLowerCase().indexOf("from"));				
			}else{
				//有group by的在外面包一层count
				sql="select count(0) from(#innerTable#) _inner_table".replace("#innerTable#",sql);
			}
		}
		return sql;
	}
	
	/**
	 * 取配置的sql，找不到返回原sqlId
	 * @param sqlId sqlid
	 * @return sql
	 */
	public static String getSqlById(String sqlId){
		String sql=null;
		//取对应sql
		if(SQL_MAPPING_QUERY.containsKey(sqlId)){
			sql=SQL_MAPPING_QUERY.get(sqlId);
		}else if(SQL_MAPPING_DELETE.containsKey(sqlId)){
			sql=SQL_MAPPING_DELETE.get(sqlId);
		}else if(SQL_MAPPING_INSERT.containsKey(sqlId)){
			sql=SQL_MAPPING_INSERT.get(sqlId);
		}else if(SQL_MAPPING_UPDATE.containsKey(sqlId)){
			sql=SQL_MAPPING_UPDATE.get(sqlId);
		}
		return sql;
	}
	
	/**
	 * 强制使用所有参数,当查询参数中包含此项时,不去掉为空参数的查询条件
	 * @author sjg
	 * @version 1.0.1 2014-3-17
	 *
	 */
	private interface ForceUseAllParams{
		
	}
}
