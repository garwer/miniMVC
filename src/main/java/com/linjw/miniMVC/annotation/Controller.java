package com.linjw.miniMVC.annotation;

import java.lang.annotation.*;

/**
 * 自定义Controller注解(模拟springmvc)
 */

@Target(ElementType.TYPE) //作用于类上
@Documented //JAVADOCzuoweu作为文档
@Retention(RetentionPolicy.RUNTIME) //限制注解生命周期 运行时保留
public @interface Controller {
    /**
     * 作用该类上的注解有个value属性 Controller名称
     */
    public String value();
}
