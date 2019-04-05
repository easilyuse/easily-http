package com.github.easilyuse.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.easilyuse.core.enu.HttpMethod;
import com.github.easilyuse.core.enu.MimeType;

/**
 * 
 * <p>
 * Title:HttpReqMethod
 * </p>
 * <p>
 * Description: 通过该注解，可以配置远程http请求的相对路径，GET或POST请求，指定请求类型为表单或者是JSON，并可以指定你期待的响应参数对象类型
 * </p>
 * 
 * @author linyb
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpReqMethod {
	// 远程http调用方法名
	String path();

	HttpMethod HTTP_METHOD() default HttpMethod.GET;

	// 是否请求参数首字母大写
	boolean capwordsRequired() default false;

	// 请求的内容类型。目前只支持表单和JSON
	MimeType contentType() default MimeType.APPLICATION_FORM;

	// 期待响应返回的对象类型参数，通常用于返回值是复杂对象集合，诸如List<User>，则expectReturnType可以配置为User.class
	Class<?>[] expectReturnType() default {};

}
