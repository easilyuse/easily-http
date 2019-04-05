package com.github.easilyuse.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.github.easilyuse.core.spring.registrar.HttpClientScannerRegistrar;

/**
 * 
 * <p>
 * Title:EnableHttpClients
 * </p>
 * <p>
 * Description: 通过该注解，可以开启spring扫描HttpClient注解
 * </p>
 * 
 * @author linyb
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(HttpClientScannerRegistrar.class)
public @interface EnableHttpClients {

	// 配置扫描有用到httpclient注解的父级包
	String[] basePackages() default {};

	// 配置扫描有用到httpclient注解的父级类
	Class<?>[] basePackageClasses() default {};

}
