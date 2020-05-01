package com.nowcoder.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 该注解表示是否需要登录
 */
@Target(ElementType.METHOD)          //作用在方法上
@Retention(RetentionPolicy.RUNTIME)  //运行时生效
public @interface LoginRequired {

}
