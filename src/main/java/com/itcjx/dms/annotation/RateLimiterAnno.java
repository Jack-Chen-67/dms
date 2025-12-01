package com.itcjx.dms.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimiterAnno {
    /**
     * 限流key
     */
    String key() default "";

    /**
     * 限流时间单位秒
     */
    int time() default 60;

    /**
     * 单位时间内限制访问次数
     */
    int count() default 100;

    /**
     * 限流提示语
     */
    String limitTip() default "访问过于频繁，请稍后再试";
}
