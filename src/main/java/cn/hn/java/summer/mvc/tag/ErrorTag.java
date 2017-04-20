package cn.hn.java.summer.mvc.tag;

import org.rythmengine.template.ITag;
import org.rythmengine.template.JavaTagBase;

import cn.hn.java.summer.constants.Default;
import cn.hn.java.summer.mvc.WebContext;

public class ErrorTag extends JavaTagBase
{
  public String __getName()
  {
    return "errors";
  }

  protected void call(ITag.__ParameterList params, ITag.__Body body)
  {
	  //包装串
	  Object obj = params.getDefault();
	  //错误内容
	  String errors=WebContext.getRequestAttribute(Default.ERROR_IN_REQUEST_KEY);
	  StringBuffer msg=new StringBuffer();
	  if(obj!=null && errors!=null){
		  String[] arr=errors.split(";");
		  for(String str:arr){
			  msg.append(obj.toString().replace("{0}", str));
		  }
	  }else if(errors!=null){
		  msg.append(errors);
	  }
	  p(msg.toString());
  }
}