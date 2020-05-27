package com.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import utils.ExceptionEx;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.utils.HttpBaseRequest;

/**
 * @Description 全局异常捕获 
 */
@ControllerAdvice
public class ExceptionController{
	private static final Logger log = Logger.getLogger(ExceptionController.class);
	
	public static String INPUTSTREAM_PARAMETER = "InputStream_Parameter";
	
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public String defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
    	JSONObject object = new JSONObject();
    	object.put("status", 99999);
        JSONObject objectException = new JSONObject();
        objectException.put("Exception_type", e.getClass());
        objectException.put("Exception_StackTrace", e.getStackTrace());
        objectException.put("Exception_CharacterEncoding",req.getCharacterEncoding());
        objectException.put("Exception_url",HttpBaseRequest.getDefault().getIpAddr(req));
        objectException.put("Exception_Method", req.getMethod());
        objectException.put("Exception_ParameterMap",req.getParameterMap());
        objectException.put("Exception_data",req.getAttribute(INPUTSTREAM_PARAMETER));
        object.put("msg", objectException);
        log.error("Exception_type="+e.getClass()
        +"\n ,Exception_CharacterEncoding="+req.getCharacterEncoding()
        +"\n ,Exception_url="+req.getRequestURL()
        +"\n ,Exception_Method="+req.getMethod()
        +"\n ,Exception_ParameterMap="+JSON.toJSONString(req.getParameterMap(),true)
 + "\n ,Exception_data="
                + req.getAttribute(INPUTSTREAM_PARAMETER) + "\n ,异常:" + ExceptionEx.e2s(e));
        String result = JSON.toJSONString(object,true);
        return result;
    }
}
