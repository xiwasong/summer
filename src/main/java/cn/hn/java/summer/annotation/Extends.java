package cn.hn.java.summer.annotation;

import java.lang.annotation.*;

/**
 * 多继承注解
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Extends {
    Class<?>[] value() default {};
}
