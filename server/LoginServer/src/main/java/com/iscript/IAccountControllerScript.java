package com.iscript;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.ClientProtocolException;

import script.IScript;

import com.controller.AccountController;

public abstract class IAccountControllerScript implements IScript {

    /** 客户端获取登录信息 */
    public abstract String getServerInfo(AccountController accountContoller,
            HttpServletRequest request) throws ClientProtocolException,
            IOException;

    /** 系统托管登录 */
    public abstract String adminLogin(AccountController accountContoller, HttpServletRequest request)
            throws Exception;

    /** 登录验证 */
    public abstract String loginVerify(AccountController accountContoller,
            HttpServletRequest request) throws Exception;

	/**
	 * 通过渠道账号获得玩家信息
	 * @param accountController
	 * @param request
	 * @return
	 */
	public abstract String queryPlayerInfoByAccount(AccountController accountController, 
			HttpServletRequest request);

	/**
	 * 通过渠道账号获得玩家ID
	 * @param accountController
	 * @param request
	 * @return
	 */
	public abstract String queryPlayerIdByAccount(AccountController accountController, 
			HttpServletRequest request);

	public abstract String queryPlayerInfoByPlayerId(AccountController accountController, 
			HttpServletRequest request);

	public abstract String queryPlayerInfoByMobile(AccountController accountController,
			HttpServletRequest request);
}
