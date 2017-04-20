package cn.hn.java.summer.mvc.tag;

import org.apache.commons.lang3.StringEscapeUtils;
import org.rythmengine.template.ITag;
import org.rythmengine.template.JavaTagBase;

import cn.hn.java.summer.mvc.WebContext;

public class ValTag extends JavaTagBase
{
  public String __getName()
  {
    return "val";
  }

  protected void call(ITag.__ParameterList params, ITag.__Body body)
  {
    Object name = params.getDefault();
    Object defVal= params.size()>1?params.get(1):null;
    Object val=null;
    if ((name != null) && ((name instanceof String))) {
      val=WebContext.getRequestObject((String) name);
      val=val==null?defVal:val;
      if(val instanceof String && val!=null){
    	  p(StringEscapeUtils.escapeHtml4(val.toString()));
      }else{
    	  p(val);
      }
    }
    else {
      p("");
    }
  }
}