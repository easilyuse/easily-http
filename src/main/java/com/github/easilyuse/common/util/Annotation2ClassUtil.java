package com.github.easilyuse.common.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * <p>
 * Title:Annotation2ClassUtil
 * </p>
 * <p>
 * Description: 查找指定包下有用到指定注解的类的工具类
 * </p>
 * 
 * @author linyb
 */
public class Annotation2ClassUtil {

	private static Logger logger = LoggerFactory.getLogger(Annotation2ClassUtil.class);

	/**
	 * 查找指定包下有用到指定注解的类
	 * 
	 * @param packageName
	 *            包名
	 * @param annotation
	 *            注解类
	 * @return
	 */
	public static List<Class<?>> getClassesWithAnnotationFromPackage(String packageName,
			Class<? extends Annotation> annotation) {
		List<Class<?>> classList = new ArrayList<Class<?>>();
		String packageDirName = packageName.replace('.', '/');
		Enumeration<URL> dirs = null;

		try {
			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
		} catch (IOException e) {
			logger.error("Failed to get resource", e);
			return null;
		}

		while (dirs.hasMoreElements()) {
			URL url = dirs.nextElement();
			String protocol = url.getProtocol();// file

			// http, https, ftp, file, and jar
			if ("file".equals(protocol)) {
				String filePath = null;
				try {
					filePath = URLDecoder.decode(url.getFile(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					logger.error("Failed to decode class file", e);
				}

				filePath = filePath.substring(1);
				getClassesWithAnnotationFromFilePath(packageName, filePath, classList, annotation);
			} else if ("jar".equals(protocol)) {
				JarFile jar = null;
				try {
					jar = ((JarURLConnection) url.openConnection()).getJarFile();
					// 扫描jar包文件 并添加到集合中
				} catch (Exception e) {
					logger.error("Failed to decode class jar", e);
				}

				List<Class<?>> alClassList = new ArrayList<Class<?>>();
				findClassesByJar(packageName, jar, alClassList);
				getClassesWithAnnotationFromAllClasses(alClassList, annotation, classList);
			} else {
				logger.warn("can't process the protocol={}", protocol);
			}
		}

		return classList;
	}

	/**
	 * 查找jar包下指定包名的类集合
	 * 
	 * @param pkgName
	 *            包名
	 * @param jar
	 *            jar包
	 * @param classes
	 *            类集合
	 */
	private static void findClassesByJar(String pkgName, JarFile jar, List<Class<?>> classes) {
		String pkgDir = pkgName.replace(".", "/");
		Enumeration<JarEntry> entry = jar.entries();

		while (entry.hasMoreElements()) {
			// 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文
			JarEntry jarEntry = entry.nextElement();
			String name = jarEntry.getName();
			// 如果是以/开头的
			if (name.charAt(0) == '/') {
				// 获取后面的字符串
				name = name.substring(1);
			}

			if (jarEntry.isDirectory() || !name.startsWith(pkgDir) || !name.endsWith(".class")) {
				continue;
			}
			// 如果是一个.class文件 而且不是目录
			// 去掉后面的".class" 获取真正的类名
			String className = name.substring(0, name.length() - 6);
			Class<?> tempClass = loadClass(className.replace("/", "."));
			// 添加到集合中去
			if (tempClass != null) {
				classes.add(tempClass);
			}
		}
	}

	/**
	 * 加载类
	 * 
	 * @param fullClsName
	 *            类全名
	 * @return
	 */
	private static Class<?> loadClass(String fullClsName) {
		try {
			return Thread.currentThread().getContextClassLoader().loadClass(fullClsName);
		} catch (ClassNotFoundException e) {
			logger.error("PkgClsPath loadClass", e);
		}
		return null;
	}

	/**
	 * 从指定文件路径查找指定包下的有用到指定注解的类集合
	 * 
	 * @param packageName
	 * @param filePath
	 * @param classList
	 * @param annotation
	 */
	private static void getClassesWithAnnotationFromFilePath(String packageName, String filePath,
			List<Class<?>> classList, Class<? extends Annotation> annotation) {
		Path dir = Paths.get(filePath);

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
			for (Path path : stream) {
				String fileName = String.valueOf(path.getFileName()); // for current dir , it is 'helloworld'
				// 如果path是目录的话， 此处需要递归，
				boolean isDir = Files.isDirectory(path);
				if (isDir) {
					getClassesWithAnnotationFromFilePath(packageName + "." + fileName, path.toString(), classList,
							annotation);
				} else {
					String className = fileName.substring(0, fileName.length() - 6);

					Class<?> classes = null;
					String fullClassPath = packageName + "." + className;
					try {
						logger.info("fullClassPath={}", fullClassPath);
						classes = Thread.currentThread().getContextClassLoader().loadClass(fullClassPath);
					} catch (ClassNotFoundException e) {
						logger.error("Failed to find class={}", fullClassPath, e);
					}

					if (null != classes && null != classes.getAnnotation(annotation)) {
						classList.add(classes);
					}
				}
			}
		} catch (IOException e) {
			logger.error("Failed to read class file", e);
		}
	}

	/**
	 * 从类集合中查找有用到指定注解的类集合
	 * 
	 * @param inClassList
	 * @param annotation
	 * @param outClassList
	 */
	private static void getClassesWithAnnotationFromAllClasses(List<Class<?>> inClassList,
			Class<? extends Annotation> annotation, List<Class<?>> outClassList) {
		for (Class<?> myClasss : inClassList) {
			if (null != myClasss && null != myClasss.getAnnotation(annotation)) {
				outClassList.add(myClasss);
			}
		}
	}

}
