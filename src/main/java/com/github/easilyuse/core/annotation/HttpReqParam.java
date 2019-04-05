package com.github.easilyuse.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * <p>
 * Title:HttpReqParam
 * </p>
 * <p>
 * Description: 该注解用于当方法请求参数为简单参数，比如string,int等基本类型参数，该参数不能与HttpReqListBean共存
 * </p>
 * 
 * @author linyb
 */
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpReqParam {
	// 参数必须和远程http请求参数名称一致
	String value();

}
