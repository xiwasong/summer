package cn.hn.java.summer.web;

import cn.hn.java.summer.BaseApp;
import cn.hn.java.summer.Config;
import cn.hn.java.summer.mvc.JsonViewResolver;
import cn.hn.java.summer.mvc.RythmViewHolder;
import cn.hn.java.summer.utils.AppUtils;
import org.apache.commons.lang3.StringUtils;
import org.rythmengine.spring.web.RythmHolder;
import org.rythmengine.spring.web.RythmViewResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.web.ServerProperties.Tomcat;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.handler.MappedInterceptor;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;

import javax.servlet.Servlet;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@EnableAspectJAutoProxy(proxyTargetClass=true)
@SpringBootApplication(scanBasePackages={"cn.hn.java.summer"})
@EnableWebMvc
public class WebApp extends BaseApp implements WebMvcConfigurer
{
	static {
		Config.setWebApplication(true);
	}

	/**
	 * 网站根目录
	 */
	public static String webRootDir;

	@Bean
	public ServletRegistrationBean dispatcherRegistration(DispatcherServlet dispatcherServlet) {
		ServletRegistrationBean registration = new ServletRegistrationBean(
				dispatcherServlet);
		dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
		return registration;
	}

	/**
	 * 初始化视图解析器
	 * @param manager
	 * @return
	 */
	@Bean
	public ViewResolver contentNegotiatingViewResolver(ContentNegotiationManager manager){

		List<ViewResolver> resolvers = new ArrayList<>();

		//页面视图解析器
		resolvers.add(rythmViewResolver());
		//json视图解析器
		resolvers.add(jsonViewResolver());

		ContentNegotiatingViewResolver viewResolver=new ContentNegotiatingViewResolver();
		viewResolver.setViewResolvers(resolvers);
		viewResolver.setContentNegotiationManager(manager);
		return viewResolver;
	}

	@Bean
	public ViewResolver rythmViewResolver(){
		//设置页面视图解析器
		RythmViewResolver razorResolver=new RythmViewResolver();
		//视图模板存放目录
		razorResolver.setPrefix("/");
		razorResolver.setSuffix(".html");
		razorResolver.setContentType("text/html;charset=UTF-8");
		razorResolver.setExposeContextBeansAsAttributes(true);
		razorResolver.setExposePathVariables(true);
		razorResolver.setExposeRequestAttributes(true);
		razorResolver.setExposeSessionAttributes(true);
		razorResolver.setExposeSpringMacroHelpers(true);
		return razorResolver;
	}

	@Bean
	public ViewResolver jsonViewResolver() {
		return new JsonViewResolver();
	}

    @Bean
    public RythmHolder rythmHolder(
		@Value("${template.home}") final String templateHome,
		@Value("${template.mode}") final String mode
    ){
    	configWebRoot(templateHome);
    	return new RythmViewHolder(webRootDir, mode);
    }

    /**
     * 覆盖spring boot内置tomcat的docbase设置
     * @author songjiangang
     * @time 2016年5月10日 上午11:13:42
     */

	@ConditionalOnClass({ Servlet.class, Tomcat.class })
	public static class EmbeddedTomcat {

		@Bean
		public TomcatEmbeddedServletContainerFactory tomcatEmbeddedServletContainerFactory(
				@Value("${server.tomcat.docbase}") final String docBase
		) {
			configWebRoot(docBase);
			return new TomcatEmbeddedFactory();
		}

	}

	/**
	 * 配置网站根目录
	 * @param templateHome
	 */
	private static void configWebRoot(String templateHome){
		if(webRootDir!=null){
			return;
		}
		
		String templateHome1=templateHome;
		//配置模板文件存放根目录
		String templateDir=templateHome;
		//如果未指定目录则取程序根目录
		if(StringUtils.isBlank(templateHome)){
			templateHome1="./";
		}
		//在项目当前目录
		if(templateHome1.startsWith("./")){
			String appRoot=AppUtils.getAppRoot();
			templateDir=Paths.get(appRoot, templateHome1.substring(1)).toString();
		}
		
		//设置网站根目录
    	webRootDir=templateDir;
	}

	/**
	 * 静态资源映射
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/static/**")
				.addResourceLocations("/static/");
	}

	@Override
	public void configureContentNegotiation(
			ContentNegotiationConfigurer configurer) {
		configurer.ignoreAcceptHeader(true).defaultContentType(
				MediaType.TEXT_HTML);
	}



	//=====================================implements WebMvcConfigurer======================
	/**
	 * registered by default.
	 *
	 * @param registry
	 */
	@Override
	public void addFormatters(FormatterRegistry registry) {

	}

	/**
	 * Configure the {@link HttpMessageConverter}s to use for reading or writing
	 * to the body of the request or response. If no converters are added, a
	 * default list of converters is registered.
	 * <p><strong>Note</strong> that adding converters to the list, turns off
	 * default converter registration. To simply add a converter without impacting
	 * default registration, consider using the method
	 * {@link #extendMessageConverters(List)} instead.
	 *
	 * @param converters initially an empty list of converters
	 */
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

	}

	/**
	 * A hook for extending or modifying the list of converters after it has been
	 * configured. This may be useful for example to allow default converters to
	 * be registered and then insert a custom converter through this method.
	 *
	 * @param converters the list of configured converters to extend.
	 * @since 4.1.3
	 */
	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

	}

	/**
	 * Provide a custom {@link Validator} instead of the one created by default.
	 * The default implementation, assuming JSR-303 is on the classpath, is:
	 * {@link OptionalValidatorFactoryBean}.
	 * Leave the return value as {@code null} to keep the default.
	 */
	@Override
	public Validator getValidator() {
		return null;
	}

	/**
	 * Configure asynchronous request handling options.
	 *
	 * @param configurer
	 */
	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {

	}

	/**
	 * Helps with configuring HandlerMappings path matching options such as trailing slash match,
	 * suffix registration, path matcher and path helper.
	 * Configured path matcher and path helper instances are shared for:
	 * <ul>
	 * <li>RequestMappings</li>
	 * <li>ViewControllerMappings</li>
	 * <li>ResourcesMappings</li>
	 * </ul>
	 *
	 * @param configurer
	 * @since 4.0.3
	 */
	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {

	}

	/**
	 * Add resolvers to support custom controller method argument types.
	 * <p>This does not override the built-in support for resolving handler
	 * method arguments. To customize the built-in support for argument
	 * resolution, configure {@link } directly.
	 *
	 * @param argumentResolvers initially an empty list
	 */
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {

	}

	/**
	 * Add handlers to support custom controller method return value types.
	 * <p>Using this option does not override the built-in support for handling
	 * return values. To customize the built-in support for handling return
	 * values, configure RequestMappingHandlerAdapter directly.
	 *
	 * @param returnValueHandlers initially an empty list
	 */
	@Override
	public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {

	}

	/**
	 * Configure the {@link HandlerExceptionResolver}s to handle unresolved
	 * controller exceptions. If no resolvers are added to the list, default
	 * exception resolvers are added instead.
	 *
	 * @param exceptionResolvers initially an empty list
	 */
	@Override
	public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {

	}

	/**
	 * A hook for extending or modifying the list of
	 * {@link HandlerExceptionResolver}s after it has been configured. This may
	 * be useful for example to allow default resolvers to be registered and then
	 * insert a custom one through this method.
	 *
	 * @param exceptionResolvers the list of configured resolvers to extend.
	 * @since 4.3
	 */
	@Override
	public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {

	}

	/**
	 * Add Spring MVC lifecycle interceptors for pre- and post-processing of
	 * controller method invocations. Interceptors can be registered to apply
	 * to all requests or be limited to a subset of URL patterns.
	 * <p><strong>Note</strong> that interceptors registered here only apply to
	 * controllers and not to resource handler requests. To intercept requests for
	 * static resources either declare a
	 * {@link MappedInterceptor MappedInterceptor}
	 * bean or switch to advanced configuration mode by extending
	 * {@link WebMvcConfigurationSupport
	 * WebMvcConfigurationSupport} and then override {@code resourceHandlerMapping}.
	 *
	 * @param registry
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {

	}

	/**
	 * Provide a custom {@link MessageCodesResolver} for building message codes
	 * from data binding and validation error codes. Leave the return value as
	 * {@code null} to keep the default.
	 */
	@Override
	public MessageCodesResolver getMessageCodesResolver() {
		return null;
	}

	/**
	 * Configure simple automated controllers pre-configured with the response
	 * status code and/or a view to render the response body. This is useful in
	 * cases where there is no need for custom controller logic -- e.g. render a
	 * home page, perform simple site URL redirects, return a 404 status with
	 * HTML content, a 204 with no content, and more.
	 *
	 * @param registry
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {

	}

	/**
	 * Configure view resolvers to translate String-based view names returned from
	 * controllers into concrete {@link View}
	 * implementations to perform rendering with.
	 *
	 * @param registry
	 */
	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {

	}

	/**
	 * Configure a handler to delegate unhandled requests by forwarding to the
	 * Servlet container's "default" servlet. A common use case for this is when
	 * the {@link DispatcherServlet} is mapped to "/" thus overriding the
	 * Servlet container's default handling of static resources.
	 *
	 * @param configurer
	 */
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {

	}

	/**
	 * Configure cross origin requests processing.
	 *
	 * @param registry
	 * @since 4.2
	 */
	@Override
	public void addCorsMappings(CorsRegistry registry) {

	}
}