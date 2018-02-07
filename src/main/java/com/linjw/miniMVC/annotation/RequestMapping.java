package com.linjw.miniMVC.annotation;


import java.lang.annotation.*;

/**
 * @RequestMapping提供URL地址处理映射 持久化层注解
 */

@Documented
@Target({ElementType.TYPE,ElementType.METHOD}) //作用在类以及方法上
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    public String value();
}
