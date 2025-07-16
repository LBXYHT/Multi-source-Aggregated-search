package com.hutao.aggregatedsearch.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验
 *
 */
// 只能标注在方法上
@Target(ElementType.METHOD)
// 运行时可以通过反射获取
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {

    /**
     * 必须有某个角色
     *
     * @return
     */
    // 可选参数，默认为“”
    String mustRole() default "";

}

