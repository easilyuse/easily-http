package com.github.easilyuse.core.util;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * <p>
 * Title:RetryUtil
 * </p>
 * <p>
 * Description: 重试工具类
 * </p>
 * 
 * @author linyb
 */
public class RetryUtil {
	private static final Logger logger = LoggerFactory.getLogger(RetryUtil.class);
	private static ThreadLocal<Integer> retryTimesInThread = new ThreadLocal<>();
	private static Integer totalRetryTimes;

	private TimeUnit timeUnit = TimeUnit.SECONDS;

	private Integer fixedWait = 60;

	/**
	 * 设置当前方法重试次数
	 *
	 * @param retryTimes
	 * @return
	 */
	public static RetryUtil setRetryTimes(Integer retryTimes) {
		if (retryTimesInThread.get() == null)
			retryTimesInThread.set(retryTimes);
		totalRetryTimes = retryTimes;
		return new RetryUtil();
	}

	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}

	public void setFixedWait(Integer fixedWait) {
		this.fixedWait = fixedWait;
	}

	/**
	 * 重试当前方法
	 * <p>
	 * 按顺序传入调用者方法的所有参数 ,args[0]传的是调用者对象
	 * </p>
	 *
	 * @param args
	 * @return
	 */
	public Object retry(Object... args) {
		try {
			Integer retryTimes = retryTimesInThread.get();
			if (retryTimes <= 0) {
				retryTimesInThread.remove();
				return null;
			}
			retryTimesInThread.set(--retryTimes);
			Integer alreadyRetryTimes = totalRetryTimes - retryTimes;
			String upperClassName = Thread.currentThread().getStackTrace()[2].getClassName();
			String upperMethodName = Thread.currentThread().getStackTrace()[2].getMethodName();

			Object targetObject = args[0];
			// Class clazz = Class.forName(upperClassName);
			// Object targetObject = clazz.newInstance();
			Object[] targetArgs = new Object[args.length - 1];
			System.arraycopy(args, 1, targetArgs, 0, targetArgs.length);
			Method targetMethod = null;
			for (Method method : targetObject.getClass().getDeclaredMethods()) {
				if (method.getName().equals(upperMethodName)) {
					targetMethod = method;
					break;
				}
			}
			if (targetMethod == null)
				return null;
			targetMethod.setAccessible(true);
			Thread.sleep(timeUnit.toMillis(fixedWait));
			logger.info("调用类:{},调用方法：{},调用参数：{}正在进行第{}次重试", upperClassName, upperMethodName, targetArgs,
					alreadyRetryTimes);
			return targetMethod.invoke(targetObject, targetArgs);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return null;
		}
	}

}
