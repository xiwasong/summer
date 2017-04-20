package cn.hn.java.summer.mvc.tag;

import javax.servlet.http.HttpServletRequest;

import org.rythmengine.template.ITag;
import org.rythmengine.template.JavaTagBase;

import cn.hn.java.summer.mvc.WebContext;



public class FullUrlTag extends JavaTagBase
{
  public String __getName()
  {
    return "fullUrl";
  }

  protected void call(ITag.__ParameterList params, ITag.__Body body)
  {
    Object obj = params.getDefault();
    HttpServletRequest request = WebContext.getRequest();
    String contextPath = request.getContextPath();
    StringBuilder builder = new StringBuilder();

    builder.append(request.getScheme());
    builder.append("://");
    builder.append(request.getServerName());

    if (request.getServerPort() != 80) {
      builder.append(":");
      builder.append(request.getServerPort());
    }

    builder.append(contextPath);

    if ((obj != null) && ((obj instanceof String)))
    {
      builder.append(obj.toString());
    }

    p(builder.toString());
  }
}