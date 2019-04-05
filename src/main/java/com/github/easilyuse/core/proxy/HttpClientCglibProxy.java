package com.github.easilyuse.core.proxy;

import java.lang.reflect.Method;

import com.github.easilyuse.core.enu.HttpMethod;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * 
 * <p>
 * Title:HttpClientCglibProxy
 * </p>
 * <p>
 * Description: cglib http请求代理
 * </p>
 * 
 * @author linyb
 */
public class HttpClientCglibProxy extends BaseHttpClientProxy implements MethodInterceptor {

	public HttpClientCglibProxy(HttpMethod httpMethod, String url) {
		this.httpMethod = httpMethod;
		this.url = url;
	}

	public HttpClientCglibProxy() {
	}

	public static Object newInstance(Class targetClz, HttpMethod httpMethod, String url) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(targetClz);
		enhancer.setCallback(new HttpClientCglibProxy(httpMethod, url));
		return enhancer.create();
	}

	public static Object newInstance(Class targetClz) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(targetClz);
		enhancer.setCallback(new HttpClientCglibProxy());
		return enhancer.create();
	}

	@Override
	public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
		return doService(proxy, method, args);
	}

}
