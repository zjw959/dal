package com.iscript;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.controller.RechargeController;

import script.IScript;


public abstract class IRechargeControllerScript implements IScript {

	/**
	 * 充值回调
	 * @param rechargeController
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	public abstract void rechargeCallBack(RechargeController rechargeController,
			HttpServletRequest request, HttpServletResponse response) throws IOException;
	
	
}
