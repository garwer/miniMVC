package com.linjw.miniMVC.annotation;
import java.lang.annotation.*;

/**
 * 地址映射处理地址 依赖注入
 */


@Documented
@Target(ElementType.FIELD) //作用在字段上不是类上
@Retention(RetentionPolicy.RUNTIME)
public @interface Qualifier {
    public String value();
}
