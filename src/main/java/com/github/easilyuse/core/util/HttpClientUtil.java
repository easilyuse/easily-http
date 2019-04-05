package com.github.easilyuse.core.util;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.easilyuse.common.util.StringUtil;

/**
 * 
 * <p>
 * Title:HttpClientUtil
 * </p>
 * <p>
 * Description: http客户端调用工具类
 * </p>
 * 
 * @author linyb
 */
public class HttpClientUtil {
	private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

	// 设置请求超时时间
	private static RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(60000).setConnectTimeout(60000)
			.setConnectionRequestTimeout(60000).build();

	public static String doGet(String url, Map<String, String> param) {

		// 创建Httpclient对象
		CloseableHttpClient httpclient = HttpClients.createDefault();

		String resultString = "";
		CloseableHttpResponse response = null;
		try {
			// 创建uri
			URIBuilder builder = new URIBuilder(url);
			if (param != null) {
				for (String key : param.keySet()) {
					builder.addParameter(key, param.get(key));
				}
			}
			URI uri = builder.build();

			// 创建http GET请求
			HttpGet httpGet = new HttpGet(uri);
			httpGet.setConfig(requestConfig);

			// 执行请求
			response = httpclient.execute(httpGet);

			resultString = EntityUtils.toString(response.getEntity(), "UTF-8");

		} catch (Exception e) {
			logger.error("call remote [" + url + "] error:" + e.getMessage(), e);
			RetryUtil.setRetryTimes(2).retry(url, param);
		} finally {
			try {
				if (response != null) {
					response.close();
				}
				httpclient.close();
			} catch (IOException e) {
				logger.error("close error:" + e.getMessage(), e);
			}
		}
		return resultString;
	}

	public static String doGet(String url) {
		return doGet(url, null);
	}

	public static String doPost(String url, Map<String, String> param) {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			httpPost.setConfig(requestConfig);
			// 创建参数列表
			if (param != null) {
				List<NameValuePair> paramList = new ArrayList<>();
				for (String key : param.keySet()) {
					paramList.add(new BasicNameValuePair(key, param.get(key)));
				}
				// 模拟表单
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, "utf-8");
				httpPost.setEntity(entity);

			}
			// 执行http请求
			response = httpClient.execute(httpPost);
			resultString = EntityUtils.toString(response.getEntity(), "utf-8");
		} catch (Exception e) {
			logger.error("call remote [" + url + "] error:" + e.getMessage(), e);
			RetryUtil.setRetryTimes(2).retry(url, param);
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				logger.error("close error:" + e.getMessage(), e);
			}
		}

		return resultString;
	}

	public static String doPost(String url) {
		return doPost(url, null);
	}

	public static String doPostJson(String url, String json) {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			httpPost.setConfig(requestConfig);
			// 创建请求内容
			if (StringUtil.isNotBlank(json)) {
				StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
				httpPost.setEntity(entity);
			}
			// 执行http请求
			response = httpClient.execute(httpPost);
			resultString = EntityUtils.toString(response.getEntity(), "utf-8");
		} catch (Exception e) {
			logger.error("call remote [" + url + "] error:" + e.getMessage(), e);
			RetryUtil.setRetryTimes(2).retry(url, json);
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				logger.error("close error:" + e.getMessage(), e);
			}
		}

		return resultString;
	}
}
