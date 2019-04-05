package com.github.easilyuse.core.spring.bean;

import org.springframework.beans.factory.FactoryBean;

import com.github.easilyuse.core.util.HttpClientServiceUtil;

/**
 * 
 * <p>
 * Title:HttpClientFactroyBean
 * </p>
 * <p>
 * Description: 创建动态代理Bean
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 */
public class HttpClientFactroyBean<T> implements FactoryBean<T> {

	public static final String field_httpClientServiceBeanClz = "httpClientServiceBeanClz";

	private Class<T> httpClientServiceBeanClz;

	public Class<T> getHttpClientServiceBeanClz() {
		return httpClientServiceBeanClz;
	}

	public void setHttpClientServiceBeanClz(Class<T> httpClientServiceBeanClz) {
		this.httpClientServiceBeanClz = httpClientServiceBeanClz;
	}

	@Override
	public T getObject() throws Exception {

		T t = HttpClientServiceUtil.getInstance().getService(httpClientServiceBeanClz);

		return t;
	}

	@Override
	public Class<?> getObjectType() {
		return httpClientServiceBeanClz;
	}

}
