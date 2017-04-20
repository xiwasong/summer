package cn.hn.java.summer.mvc;

import java.util.Locale;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import cn.hn.java.summer.mvc.SnJsonMaper;

public class JsonViewResolver implements ViewResolver{

    @Override
    public View resolveViewName(String viewName, Locale locale)
            throws Exception {
        MappingJackson2JsonView view = new MappingJackson2JsonView();
        view.setPrettyPrint(true);
        view.setObjectMapper(new SnJsonMaper());
        return view;
    }

}