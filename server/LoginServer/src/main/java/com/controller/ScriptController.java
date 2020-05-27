/**
 * 
 */
package com.controller;

import io.swagger.annotations.ApiOperation;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import script.ScriptManager;
import utils.ExceptionEx;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.constant.LoginErrorCode;


@Controller
@RequestMapping("script")
public class ScriptController extends AbstractHandler {
    
    private static final Logger log = Logger.getLogger(ScriptController.class);
    
    public ScriptController() {
        super();
    }

    @ApiOperation(value = "重载脚本", notes = "重载脚本")
    @RequestMapping(value = {"/reloadAllScripts"}, method = {RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody String reloadAllScripts(HttpServletRequest request,
            HttpServletResponse response)
            throws ClientProtocolException, IOException {
        String result = null;
        LoginErrorCode code = LoginErrorCode.SUCCESS;
        JSONObject obj = new JSONObject();
        try {
            ScriptManager.getInstance().loadAllScript();
        } catch (Exception e) {
            log.error(ExceptionEx.e2s(e));
            code = LoginErrorCode.UNKNOWN_EXCEPTION;
        }
        obj.put("errno", code.getCode());
        obj.put("errmsg", code.getMsg());
        result = JSON.toJSONString(obj, true);
        return result;
    }
}
