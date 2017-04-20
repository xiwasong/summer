package cn.hn.java.summer.db.builder;

/**
 * 格式化sql的结果类
 * @author sjg
 *
 */
public class FormatResult{
	private String sql;
	private Object[] args;
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public Object[] getArgs() {
		return args;
	}
	public void setArgs(Object[] args) {
		this.args = args;
	}
	
	public FormatResult(){
		
	}
	
	public FormatResult(String sql, Object[] args){
		this.sql=sql;
		this.args=args;
	}
}