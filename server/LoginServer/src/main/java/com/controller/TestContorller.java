/**
 * 
 */
package com.controller;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.constant.ChannelType;    
import com.service.AccountPermissionService;
import com.service.AccountService;
import com.service.ServerListManager;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@Controller
public class TestContorller {
	@SuppressWarnings("unused")
	@Autowired
    private ServerListManager serverListManager;
    @Autowired
    private AccountService accountService;
    @SuppressWarnings("unused")
	@Autowired
    private AccountPermissionService accountPermissionService;
    private static final Logger log = Logger.getLogger(TestContorller.class);
    
	public TestContorller() {
		super();
	}

	@ApiOperation(value="测试通信", notes="测试服务器连接")
	@RequestMapping(value = {"/hi"} , method = RequestMethod.GET)
	public @ResponseBody String sayHi(HttpServletRequest request,
			HttpServletResponse response) throws ClientProtocolException,IOException{
		log.info("hello world！");
		return "hello world！";
	}
	
	@ApiOperation(value="创建N个测试账号", notes="测试工具")
	@ApiImplicitParam(paramType = "query",name = "num", value = "要创建的数量", required = true, dataType = "int")
	@RequestMapping(value = {"/addTestUser"} , method = RequestMethod.GET)
	public @ResponseBody String addTestUser(HttpServletRequest request,
			HttpServletResponse response) throws ClientProtocolException, IOException {
		String num = request.getParameter("num");
		int number = 0;
		if(num!=null&&!num.isEmpty()){
			number = Integer.parseInt(num);
		}
		ExecutorService pool = Executors.newFixedThreadPool(16);
		for(int i=0;i<=number;i++){
			pool.execute(new Runnable() {
				@Override
				public void run() {
					accountService.findByAccountIdAndChannel(UUID.randomUUID().toString().substring(0, 10), ChannelType.LOCAL_TEST);
				}
			});
		}
		return "hello world！";
	}
}
