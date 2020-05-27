/**
 * 
 */
package com.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.Application;

import springfox.documentation.annotations.ApiIgnore;



@ApiIgnore
@Controller
@RequestMapping("dal")
public class CloseServerController {
	public CloseServerController() {
		super();
	}
	private static final Logger log = Logger.getLogger(CloseServerController.class);
	
	private static final String VALIDATE_CODE = "abf0d19cbb424f4cb0ef812a814c335d";
	
	@RequestMapping(value = {"/closeGame"} , method = RequestMethod.GET)
	public @ResponseBody void getServerInfo(HttpServletRequest request,
			HttpServletResponse response) throws ClientProtocolException, IOException {
		String validateCode = request.getParameter("validateCode");
		boolean success = validateCode != null
				&& validateCode.equals(VALIDATE_CODE);
		if (success) {
			response.getOutputStream().write("OK".getBytes());
			response.flushBuffer();
			log.warn("Safe close game server Done!");
			Application.context.close();
			return;
		} else {
			log.warn("invalidate code");
			response.getOutputStream().write("invalidate code".getBytes());
			return;
		}
		
	}
}
