package cn.hn.java.summer.mvc.tag;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.rythmengine.template.ITag;
import org.rythmengine.template.JavaTagBase;

public class DateFormatTag extends JavaTagBase
{
  public String __getName()
  {
    return "dateFmt";
  }

  protected void call(ITag.__ParameterList params, ITag.__Body body)
  {
    Date date = (Date)(params.getByName("date") == null ? params.getDefault() : params.getByName("date"));
    String format = (String)(params.getByName("format") == null ? "dd-MM-yyyy" : params.getByName("format"));

    if (date != null)
      p(createDateFormat(format).format(date));
  }

  protected SimpleDateFormat createDateFormat(String format)
  {
    return new SimpleDateFormat(format);
  }
}