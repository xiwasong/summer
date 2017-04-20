package cn.hn.java.summer.db.mapper;

import cn.hn.java.summer.db.mapper.rule.IBeanMapperRule;
import cn.hn.java.summer.db.mapper.rule.IDataTypeMapperRule;
import cn.hn.java.summer.utils.AppUtils;
import cn.hn.java.summer.utils.FileUtils;
import cn.hn.java.summer.utils.RegExpUtils;
import cn.hn.java.summer.utils.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 从建表脚本生成实体类
 * Created by xw2sy on 2017-04-16.
 */
@Component
public class EntityGenerator{
    static Log logger= LogFactory.getLog(EntityGenerator.class);


    //bean mapper规则
    private IBeanMapperRule beanMapperRule;

    //数据库类型映射规则
    private IDataTypeMapperRule dataTypeMapperRule;

    public EntityGenerator(){
    }

    public EntityGenerator(IBeanMapperRule beanMapperRule, IDataTypeMapperRule dataTypeMapperRule){
        this.beanMapperRule=beanMapperRule;
        this.dataTypeMapperRule=dataTypeMapperRule;
    }

    /**
     * 实体类模板
     */
    private static final String ENTITY_TEMPLATE=
            "package {0};\n" +
            "\n" +
            "import lombok.Data;\n" +
            "import java.sql.*;\n" +
            "import java.math.*;\n" +
            "\n" +
            "/**\n" +
            " * Created by summer.\n" +
            " */\n" +
            "@Data\n" +
            "public class {1} {\n" +
            "{2}\n" +
            "}";

    /**
     * 生成所有实体类
     * @param srcDir 源码目录
     * @param scriptDir 脚本目录
     * @param replace 是否替换已存在的类
     */
    public void genAllEntity(String srcDir, String scriptDir, boolean replace) throws URISyntaxException, IOException {
        //取脚本目录绝对路径
        String scriptRoot= getScriptPath(scriptDir);
        //取脚本文件列表
        Collection<File> files=getAllScriptFiles(scriptDir);
        for(File f :files){
            //生成实体类文件
            genOneEntity(
                    FileUtils.readFileToString(f,"utf-8"),
                    AppUtils.getAppRoot()+"/"+srcDir,
                    f.getParentFile().getPath().replace("\\","/").replace(scriptRoot,""),
                    replace
            );
        }
    }

    /**
     * 当sql脚本发生变化时自动生成实体类
     * @param srcDir 源码目录
     * @param scriptDir 脚本目录
     */
    public void autoGenEntity(String srcDir, String scriptDir){
        //取脚本目录绝对路径
        String scriptRoot= getScriptPath(scriptDir);
        new Thread(() -> {
            Map<String,Long> lastModifyMap=new HashMap<>();
            while (true){
                //取脚本文件列表
                Collection<File> files=getAllScriptFiles(scriptDir);
                for(File f :files){
                    //取上次更新时间
                    Long lastModify=lastModifyMap.get(f.getAbsolutePath());
                    //取不上次更新的时间且创建时间在间隔时间内，则是刚新建的文件
                    boolean isCreate=(
                        lastModify==null && System.currentTimeMillis() - f.lastModified()<=500 ||
                        lastModify!=null && lastModify==0
                    );
                    //或取到的更新时间大于上次更新的时间，则是刚修改过的文件
                    boolean isModify=lastModify!=null && f.lastModified()>lastModify;
                    if(isCreate || isModify){
                        lastModifyMap.put(f.getAbsolutePath(),f.lastModified());
                        logger.info(f.getAbsolutePath()+(isCreate?" created":" modified"));
                        logger.info("start to generate entity...");
                        try {
                            genOneEntity(
                                    FileUtils.readFileToString(f,"utf-8"),
                                    AppUtils.getAppRoot()+"/"+srcDir,
                                    f.getParentFile().getPath().replace("\\","/").replace(scriptRoot,""),
                                    //lastModify为0表示有新的文件但不确定是否生成了实体类
                                    (lastModify!=null && lastModify==0)?false:true
                            );
                        } catch (IOException e) {
                            logger.error("autoGenEntity read file error!",e);
                        }
                    }else if(lastModify==null){
                        lastModifyMap.put(f.getAbsolutePath(),new Long(0));
                    }else{
                        lastModifyMap.put(f.getAbsolutePath(),f.lastModified());
                    }
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    logger.error("autoGenEntity error!",e);
                }
            }
        }).start();
        logger.info("start watching script ["+scriptRoot+"] ...");
    }

    /**
     * 取脚本绝对路径
     * @param scriptDir 脚本相对目录
     * @return 脚本绝对路径
     */
    private String getScriptPath(String scriptDir){
        return (AppUtils.getAppRoot()+"/"+scriptDir).replace("\\","/");
    }

    /**
     * 目录下获取所有sql脚本文件
     * @param scriptDir 目录
     * @return 文件列表
     */
    private Collection<File> getAllScriptFiles(String scriptDir){
        Collection<File> files=FileUtils.listFiles(
                new File(
                        getScriptPath(scriptDir)
                ),
                new String[]{"sql"},
                true
        );
        return files;
    }

    /**
     * 生成一个实体类并写入对应目录
     * @param createScript 建表脚本
     * @param srcDir 根目录
     * @param packagePath 包目录
     * @param replace 是否替换存在的类
     * @throws IOException
     */
    public void genOneEntity(String createScript, String srcDir, String packagePath, boolean replace) throws IOException {
        //匹配到表名
        String[] tableName= RegExpUtils.findOne(createScript,SqlMapper.REG_CREATE_TABLE_NAME);
        if(tableName==null || tableName.length==0){
            logger.error("no table name can be found in create script.");
            return;
        }

        //根据表名取到实体类文件名
        String javaFileName=beanMapperRule.getClassName(tableName[0]);
        File javaFile=Paths.get(srcDir,packagePath,javaFileName+".java").toFile();
        //如果文件存在且不替换，不重新生成
        if(javaFile.exists() && !replace){
            return;
        }

        //匹配到字段列表
        List<String[]> columns=RegExpUtils.findAll(createScript,SqlMapper.REG_CREATE_FIELDS);
        if(columns.size()==0){
            logger.error("no column found in create script.");
            return;
        }

        //全包名
        String fullPackage=packagePath.replace("/",".");
        if(fullPackage.startsWith(".")){
            fullPackage=fullPackage.substring(1);
        }

        StringBuilder sbFields=new StringBuilder();
        for(String[] column : columns){
            Class<?> columnType=dataTypeMapperRule.getJavaType(column[1].toUpperCase());
            if(columnType==null){
                continue;
            }
            sbFields.append("   private ")
                    .append(columnType.getName())
                    .append(" ")
                    .append(beanMapperRule.getFieldName(column[0]))
                    .append(";\n");
        }

        //创建目录
        javaFile.getParentFile().mkdirs();

        //生成java文件
        FileUtils.writeStringToFile(
                javaFile,
                StringUtils.format(
                        ENTITY_TEMPLATE,
                        fullPackage,
                        javaFileName,
                        sbFields.toString()
                )
        );

    }

}
