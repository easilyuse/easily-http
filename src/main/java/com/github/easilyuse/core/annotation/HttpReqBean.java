package com.github.easilyuse.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * <p>
 * Title:HttpReqBean
 * </p>
 * <p>
 * Description:当方法请求参数为一个对象时，可以使用该注解，该注解不能和HttpReqListParam共存
 * </p>
 * 
 * @author linyb
 */
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpReqBean {

	String value() default "";

}
