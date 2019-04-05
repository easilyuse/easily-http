package com.github.easilyuse.common.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * <p>
 * Title:MapUtil
 * </p>
 * <p>
 * Description: map相关工具类
 * </p>
 * 
 * @author linyb
 */
public class MapUtil {
	private static Logger logger = LoggerFactory.getLogger(MapUtil.class);

	/**
	 * key首字母转大写
	 * 
	 * @param map
	 * @return 返回key首字母大写，value不为空的map集合
	 */
	public static Map<String, String> getCapwordsKeyMap(Map<String, String> map) {
		Map<String, String> capWordsKeyMap = new HashMap<>();
		if (!map.isEmpty()) {
			map.forEach((key, value) -> {
				String capwordsKey = StringUtil.capitalize(key);
				if (StringUtil.isNotBlank(value)) {
					capWordsKeyMap.put(capwordsKey, value);
				}

			});
		}

		return capWordsKeyMap;

	}

	/**
	 * 对象映射成key为首字母大写的map
	 * 
	 * @param t
	 * @return 返回key首字母大写，value不为空的map集合
	 */
	public static <T> Map<String, String> getCapwordsKeyMap(T t) {
		Map<String, String> params = new HashMap<>();

		try {
			params = BeanUtils.describe(t);
			params.remove("class");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return getCapwordsKeyMap(params);

	}

	/**
	 * 对象映射成map
	 * 
	 * @param t
	 * @return 返回value不为空的map集合
	 */
	public static <T> Map<String, String> convertBean2Map(T t) {
		Map<String, String> newParams = new HashMap<>();

		try {
			Map<String, String> params = BeanUtils.describe(t);
			params.remove("class");
			params.forEach((key, value) -> {
				if (StringUtil.isNotBlank(value)) {
					newParams.put(key, value);
				}
			});

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return newParams;

	}
}
