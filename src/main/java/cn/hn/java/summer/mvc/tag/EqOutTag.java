package cn.hn.java.summer.mvc.tag;

import org.apache.commons.lang3.StringEscapeUtils;
import org.rythmengine.template.ITag;
import org.rythmengine.template.JavaTagBase;

/**
 * 条件等于输出标签
 * @author sjg
 * 2017年2月26日 下午12:24:42
 *
 */
public class EqOutTag extends JavaTagBase
{
  public String __getName()
  {
    return "eqOut";
  }

  protected void call(ITag.__ParameterList params, ITag.__Body body)
  {
	  if(params.size()!=3){
		  return;
	  }
	  //期望值
	  Object expectVal=params.get(0).value;
	  //实际值
	  Object actVal=params.get(1).value;
	  //输出值
	  Object outVal=params.get(2).value;
	  
	  if(expectVal==null || actVal==null){
		  p("");
		  return;
	  }
	  
	  if(expectVal.equals(actVal)){
		  if(outVal instanceof String && outVal!=null){
	    	  p(StringEscapeUtils.escapeHtml4(outVal.toString()));
	      }else{
	    	  p(outVal);
	      }
	  }else{
		  p("");
	  }
  }
}