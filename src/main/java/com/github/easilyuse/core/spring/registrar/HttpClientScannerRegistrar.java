package com.github.easilyuse.core.spring.registrar;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.github.easilyuse.core.annotation.EnableHttpClients;
import com.github.easilyuse.core.annotation.HttpClient;
import com.github.easilyuse.core.spring.bean.HttpClientFactroyBean;

/**
 * 
 * <p>
 * Title:HttpClientScannerRegistrar
 * </p>
 * <p>
 * Description: httpclient注解扫描实现，并把有加httpclient注解的类注册到spring容器中
 * </p>
 * 
 * @author linyb
 */
public class HttpClientScannerRegistrar
		implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, BeanClassLoaderAware, EnvironmentAware {
	private static Logger logger = LoggerFactory.getLogger(HttpClientScannerRegistrar.class);

	private ResourceLoader resourceLoader;

	private ClassLoader classLoader;

	private Environment environment;

	public HttpClientScannerRegistrar() {
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	/**
	 * httpclient扫描注入bean
	 */
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

		Set<String> basePackages = getBasePackages(importingClassMetadata);
		ClassPathScanningCandidateComponentProvider scanner = getScanner();
		scanner.setResourceLoader(this.resourceLoader);
		scanner.addIncludeFilter(new AnnotationTypeFilter(HttpClient.class));

		for (String basePackage : basePackages) {
			Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
			for (BeanDefinition candidateComponent : candidateComponents) {
				if (candidateComponent instanceof AnnotatedBeanDefinition) {
					AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
					AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
					registerHttpClient(registry, annotationMetadata);
				}
			}
		}

	}

	/**
	 * 获取有配置httpclient注解类的父级包
	 * 
	 * @param importingClassMetadata
	 * @return
	 */
	protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
		Map<String, Object> attributes = importingClassMetadata
				.getAnnotationAttributes(EnableHttpClients.class.getCanonicalName());

		Set<String> basePackages = new HashSet<>();

		for (String pkg : (String[]) attributes.get("basePackages")) {
			if (StringUtils.hasText(pkg)) {
				basePackages.add(pkg);
			}
		}
		for (Class<?> clazz : (Class[]) attributes.get("basePackageClasses")) {
			basePackages.add(ClassUtils.getPackageName(clazz));
		}

		if (basePackages.isEmpty()) {
			basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
		}
		return basePackages;
	}

	/**
	 * 包扫描 ClassPathScanningCandidateComponentProvider是ClassPathBeanDefinitionScanner的基类,其本身主要作用是包扫描,
	 * ClassPathBeanDefinitionScanner在其基础上做了注册功能,所以ClassPathBeanDefinitionScanner需要传入一个BeanDefinitionRegistry对象. 而ClassPathScanningCandidateComponentProvider扫描的对象是并不需要注册到BeanDefinitionRegistry中去的.
	 * 
	 * @return
	 */
	protected ClassPathScanningCandidateComponentProvider getScanner() {
		return new ClassPathScanningCandidateComponentProvider(false, this.environment) {

			@Override
			protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
				if (beanDefinition.getMetadata().isIndependent()) {
					if (beanDefinition.getMetadata().isInterface()
							&& beanDefinition.getMetadata().getInterfaceNames().length == 1
							&& Annotation.class.getName().equals(beanDefinition.getMetadata().getInterfaceNames()[0])) {
						try {
							Class<?> target = ClassUtils.forName(beanDefinition.getMetadata().getClassName(),
									HttpClientScannerRegistrar.this.classLoader);
							return !target.isAnnotation();
						} catch (Exception ex) {
							this.logger.error(
									"Could not load target class: " + beanDefinition.getMetadata().getClassName(), ex);

						}
					}
					return true;
				}
				return false;

			}
		};
	}

	/*
	 * bean注册
	 */
	private void registerHttpClient(BeanDefinitionRegistry registry, AnnotationMetadata annotationMetadata) {
		try {
			String className = annotationMetadata.getClassName();
			Class<?> beanClazz = Class.forName(className);
			BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanClazz);
			GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
			definition.getPropertyValues().add(HttpClientFactroyBean.field_httpClientServiceBeanClz, beanClazz);
			definition.setBeanClass(HttpClientFactroyBean.class);
			definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
			registry.registerBeanDefinition(beanClazz.getSimpleName(), definition);
			;
		} catch (ClassNotFoundException e) {
			logger.error("Could not register target class: " + annotationMetadata.getClassName(), e);
		}
	}

}
