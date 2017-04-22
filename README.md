summer
=======

此项目目的在于提供一个简化、简洁、迅速的开发架构。

它是基于spring boot的RESTfull风格web框架、socket服务框架，数据库操作同样使用的是spring的jdbc template在它的基础上进行封装简化，视图层采用的是Rythm模板引擎(http://rythmengine.org)。
可以用它来做web项目、微服务、socket服务，且同一套代码同时兼容这三种方式。

它的优点如下：
--------------
  1. 基本建立在spring一套组件之上采用注解方式，方便快捷无需学习其它框架。
  2. 数据库操作简单，只需写好sql即可，自动与指定实体bean进行绑定。
  3. 无须编写DAO，无须动态拼sql，查询条件智能组装。
  4. 支持单表ORM，零配置无需注解，实体类生成。
  5. 支持多数据源，不同数据库。
  6. 自动分页支持，无须编写任何与分页相关的代码。
  7. 统一的异常捕获，无须编写异常处理代码。
  8. 统一日志记录，无须编写代码，只须配置需要记录日志的controller方法和要记录的信息即可。
  9. 采用hibernate的注解数据验证，同时应用于前台验证无须编写JS代码。
  10. 支持json和页面输出自动转换，异常包装确保输出的都是可用的json。
  11. 页面使用Rythm提供的Razor语法，基本与java语法一致无须学习，没有标签。
  12. 表单自动JS验证，列表自动分页。
  13. action支持多值返回，彻底告别Model。
  14. 默认使用cookies代替session，开发时重启程序session不会丢失。
  15. action支持使用RequestMapping的name指定视图名。

### 群：233391281 
### github:[https://github.com/xiwasong/summer](https://github.com/xiwasong/summer)
### 在线文档：[http://http://xiwa.oschina.io/summer-sample](http://http://xiwa.oschina.io/summer-sample)  
### 功能演示：[https://github.com/xiwasong/summer-sample](https://github.com/xiwasong/summer-sample)  
### 项目示例：[https://git.oschina.net/mgfireworks/mgplatform.git](https://git.oschina.net/mgfireworks/mgplatform.git)      
