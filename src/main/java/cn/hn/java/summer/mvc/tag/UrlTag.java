package cn.hn.java.summer.mvc.tag;

import javax.servlet.http.HttpServletRequest;

import org.rythmengine.template.ITag;
import org.rythmengine.template.JavaTagBase;

import cn.hn.java.summer.mvc.WebContext;

public class UrlTag extends JavaTagBase
{
  public String __getName()
  {
    return "url";
  }

  protected void call(ITag.__ParameterList params, ITag.__Body body)
  {
    Object obj = params.getDefault();
    HttpServletRequest request = WebContext.getRequest();
    String contextPath = request.getContextPath();

    if ((obj != null) && ((obj instanceof String))) {
      String url = (String)obj;
      if ((url.startsWith("http")) || (url.startsWith("//")))
        p(url);
      else
        p(contextPath + url);
    }
    else {
      p(contextPath);
    }
  }
}