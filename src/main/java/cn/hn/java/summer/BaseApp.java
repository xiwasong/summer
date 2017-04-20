package cn.hn.java.summer;

import cn.hn.java.summer.db.DbOperator;
import cn.hn.java.summer.db.multiple.MultipleDataSource;
import cn.hn.java.summer.db.paging.MysqlPagingConverter;
import cn.hn.java.summer.utils.AppUtils;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;


/**
 * 基础App类
 * @author sjg
 * 2016年12月30日 上午9:56:35
 *
 */
public class BaseApp {
	static{
		String appName="";
		appName="      ___           ___           ___           ___           ___           ___     \n" +
				"     /  /\\         /__/\\         /__/\\         /__/\\         /  /\\         /  /\\    \n" +
				"    /  /:/_        \\  \\:\\       |  |::\\       |  |::\\       /  /:/_       /  /::\\   \n" +
				"   /  /:/ /\\        \\  \\:\\      |  |:|:\\      |  |:|:\\     /  /:/ /\\     /  /:/\\:\\  \n" +
				"  /  /:/ /::\\   ___  \\  \\:\\   __|__|:|\\:\\   __|__|:|\\:\\   /  /:/ /:/_   /  /:/~/:/  \n" +
				" /__/:/ /:/\\:\\ /__/\\  \\__\\:\\ /__/::::| \\:\\ /__/::::| \\:\\ /__/:/ /:/ /\\ /__/:/ /:/___\n" +
				" \\  \\:\\/:/~/:/ \\  \\:\\ /  /:/ \\  \\:\\~~\\__\\/ \\  \\:\\~~\\__\\/ \\  \\:\\/:/ /:/ \\  \\:\\/:::::/\n" +
				"  \\  \\::/ /:/   \\  \\:\\  /:/   \\  \\:\\        \\  \\:\\        \\  \\::/ /:/   \\  \\::/~~~~ \n" +
				"   \\__\\/ /:/     \\  \\:\\/:/     \\  \\:\\        \\  \\:\\        \\  \\:\\/:/     \\  \\:\\     \n" +
				"     /__/:/       \\  \\::/       \\  \\:\\        \\  \\:\\        \\  \\::/       \\  \\:\\    \n" +
				"     \\__\\/         \\__\\/         \\__\\/         \\__\\/         \\__\\/         \\__\\/    \n";

		System.out.println(appName+"\n");
		System.out.println("==================================\n");
		System.out.println(AppUtils.getReadmeInfo());
		System.out.println("==================================\n");
	}


	@Bean
	@ConfigurationProperties(prefix = "datasource")
	public DataSource dataSource() {
		return new BasicDataSource();
	}

	@Bean
	public PlatformTransactionManager txManager() {
		return new DataSourceTransactionManager(dataSource());
	}

	/**
	 * 多数据源
	 * @return
	 */
	@Bean
	public MultipleDataSource multipleDataSource(){
		MultipleDataSource dataSource=new MultipleDataSource();
		//为每个数据库源设置数据库操作实例
		DbOperator dbOperator=new DbOperator();
		dbOperator.setDataSource(dataSource());
		//设置分页方案
		dbOperator.setPagingConverter(new MysqlPagingConverter());
		dataSource.addDbOperator(dbOperator);
		return dataSource;
	}
}
