/**
 * 
 */
package com.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.ClientProtocolException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.utils.LogicScriptsUtils;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Controller
public class RechargeController {
	
    public RechargeController() {
        super();
    }
    
    @ApiOperation(value="充值", notes="充值回调")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "query",name = "sid", value = "服务器id（默认为1）", required = true, dataType = "String"),
        @ApiImplicitParam(paramType = "query",name = "uid", value = "玩家id", required = true, dataType = "String"),
        @ApiImplicitParam(paramType = "query",name = "orderno", value = "黑桃订单", required = true, dataType = "String"),
        @ApiImplicitParam(paramType = "query",name = "amount", value = "真实充值金额", required = true, dataType = "String"),
        @ApiImplicitParam(paramType = "query",name = "status", value = "状态", required = true, dataType = "String"),
        @ApiImplicitParam(paramType = "query",name = "extinfo", value = "订单id", required = true, dataType = "String"),
        @ApiImplicitParam(paramType = "query",name = "htnonce", required = true, dataType = "String"),
        @ApiImplicitParam(paramType = "query",name = "httoken", required = true, dataType = "String"),
      })
	@RequestMapping(value = {"/recharge"} , method = RequestMethod.GET)
	public @ResponseBody void rechargeCallBack(HttpServletRequest request,
			HttpServletResponse response) throws ClientProtocolException, IOException {
    	LogicScriptsUtils.getIRechargeControllerScript().rechargeCallBack(this,request,response);
	}
}
