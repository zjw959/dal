package com.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/*
 游戏服返回的
 {"result":"1","desc":"",other}
 * 
 * */
/***
 *** @author: King
 *** @date : 2018年6月27日 上午10:46:50
 ***/
public class ResponseData {
	// 成员变量
	private String content;

	private int errorCode;

	private JSONObject requestJson = null;
	
	// 调用接口失败
	public static final String RESULT = "result";
	// 调用接口失败
	public static final String DESC   = "desc";

	public ResponseData(String content, int errorCode) {
		super();
		this.content = content;
		this.errorCode = errorCode;
	}

	public ResponseData() {
		this.content = "";
		this.errorCode = 0;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;

		if (errorCode == 200){
			requestJson = JSON.parseObject(content);
//			requestJson = JSON.parseObject(content, JSONObject.class);
		}
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

//	public RemoteApiResult getApiResult() {
//		// 调用网络过程报错 , java 本身报错
//		if (errorCode < 0) {
//			return new RemoteApiResult(RemoteApiResult.REMOTE_ERROR_SYSTEM);
//		}
//
//		// http200  记录下gameserver的返回值
//		else if (errorCode == 200) {
//			
//			if (requestJson == null) {
//				return new RemoteApiResult(RemoteApiResult.REMOTE_ERROR_API,
//						-1);
//			}
//			
//			Integer status = requestJson.getInteger(RESULT);
//			// 调用成功，调用结果失败，错误码为游戏服返回
//			if (status != 0) {
//				return new RemoteApiResult(RemoteApiResult.REMOTE_ERROR_API,
//						status, content);
//			}
//
//			// 调用成功，调用结果成功，将服务器返回的内容返回出去
//			else{
//				
//				RemoteApiResult apiRet = new RemoteApiResult(RemoteApiResult.REMOTE_API_SUCCESS,
//						status, content);
//				
//				apiRet.setResponseJson(requestJson);
//				
//				return apiRet;
//			}
//				
//		}
//
//		// http错误 , 记录下http返回码
//		else {
//			// 调用成功，调用结果失败, 错误码为http返回码
//			return new RemoteApiResult(RemoteApiResult.REMOTE_ERROR_HTTP,
//					errorCode);
//
//		}
//
////		return new RemoteApiResult(RemoteApiResult.REMOTE_API_SUCCESS);
//	}
	
	public int getAPIReturnCode() {
		if (requestJson != null) {
			return requestJson.getInteger(RESULT);
		}
		
		return -1;
	}
	
	public String getAPIReturnDesc() {
		if (requestJson != null) {
			return requestJson.getString(DESC);
		}
		
		return "";
	}

}
