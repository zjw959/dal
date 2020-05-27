package com.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.controller.ExceptionController;

import utils.ExceptionEx;



/***
 *** @author: King
 *** @date : 2018年6月27日 上午10:46:50
 ***/
public class HttpBaseRequest {
    private static final Logger LOGGER = Logger.getLogger(HttpBaseRequest.class);


	private static final HttpBaseRequest DEFAULT = new HttpBaseRequest();

	public static HttpBaseRequest getDefault() {
		return DEFAULT;
	}

	/**
	 * @return
	 */
	protected RequestConfig getRequestConfig() {
		return RequestConfig.custom().setSocketTimeout(30000)
				.setConnectTimeout(10000).build();
	}

	public ResponseData requestRemoteWithGet(String url, String data)
			throws Throwable {
		url = url + "?" + data;

		return requestRemoteWithGet(url);
	}

	public ResponseData requestRemoteWithGet(String url) throws Throwable {
		ResponseData resposeData = new ResponseData();

		CloseableHttpClient httpClient = HttpClients.createDefault();
		RequestConfig requestConfig = getRequestConfig();
		HttpGet httpGet = new HttpGet(url);
		httpGet.setConfig(requestConfig);
		CloseableHttpResponse response = httpClient.execute(httpGet);
		try {
			int httpCode = response.getStatusLine().getStatusCode();
			resposeData.setErrorCode(httpCode);
			if (httpCode != HttpStatus.SC_OK) {
				// logger.error("登陆验证失败，StatusCode is not 200");
				return resposeData;
			}
			HttpEntity responseEntity = response.getEntity();
			String text = EntityUtils.toString(responseEntity, "UTF-8");

			resposeData.setContent(text);

		} finally {
			response.close();
		}

		return resposeData;
	}

	public ResponseData requestRemoteWithPost(String url, String data)
			throws Throwable {
		ResponseData resposeData = new ResponseData();

		ContentType contentType = ContentType.create("application/json",
				Consts.UTF_8);
		StringEntity entity = new StringEntity(data, contentType);
		entity.setChunked(true);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		// String url = getUrl() + "/server/switch/update.do";
		RequestConfig requestConfig = getRequestConfig();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(entity);
		httpPost.setConfig(requestConfig);
		// logger.info("request remote http url: {}", url);
		CloseableHttpResponse response = httpClient.execute(httpPost);
		try {

			int httpCode = response.getStatusLine().getStatusCode();
			resposeData.setErrorCode(httpCode);
			if (httpCode != HttpStatus.SC_OK) {
				// logger.error("更新服务器开关失败，StatusCode is not 200");

				return resposeData;
			}

			HttpEntity responseEntity = response.getEntity();
			String text = EntityUtils.toString(responseEntity, "UTF-8");

			resposeData.setContent(text);

		} finally {
			response.close();
		}

		return resposeData;
	}

	public ResponseData requestWithGet(String url) {
		ResponseData resposeData = new ResponseData("java request http error", -1);
		try {
			resposeData = requestRemoteWithGet(url);
		} catch (Throwable e) {
            LOGGER.error(ExceptionEx.t2s(e));
		}
		return resposeData;

	}

	public ResponseData requestWithPost(String url, String data) {
		ResponseData resposeData = new ResponseData("java request http error", -1);
		try {
			resposeData = requestRemoteWithPost(url, data);
		} catch (Throwable e) {
            LOGGER.error(ExceptionEx.t2s(e));
		}

		return resposeData;
	}
	
	public String getRequestJson(HttpServletRequest request)
			throws IOException {
		InputStream in = request.getInputStream();
		int length = 0;
		ByteArrayOutputStream bos = null;
		DataOutputStream output = null;
		try {
			bos = new ByteArrayOutputStream();
			output = new DataOutputStream(bos);
			byte[] data = new byte[4096];
			// int totalLen = 0;
			while ((length = in.read(data, 0, data.length)) != -1) {
				output.write(data, 0, length);
				// totalLen +=length;
			}
			byte[] bytes = bos.toByteArray();
			String text = new String(bytes, "UTF-8");
			request.setAttribute(ExceptionController.INPUTSTREAM_PARAMETER, text);
			return text;
		} finally {
			if (output != null) {
				output.close();
			}
		}
	}
	
	public String getIpAddr(HttpServletRequest request) { 
        String ip = request.getHeader("x-forwarded-for"); 
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getHeader("Proxy-Client-IP"); 
        } 
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getHeader("WL-Proxy-Client-IP"); 
        } 
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getRemoteAddr(); 
        } 
        return ip; 
    }
}
