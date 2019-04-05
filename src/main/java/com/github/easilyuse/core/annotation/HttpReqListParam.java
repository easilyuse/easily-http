package com.github.easilyuse.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * <p>
 * Title:HttpReqListParam
 * </p>
 * <p>
 * Description: 方法请求参数为集合时，可以用该注解，同时该注解不能和HttpReqBean和HttpReqParam共存
 * </p>
 * 
 * @author linyb
 */
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpReqListParam {
}
