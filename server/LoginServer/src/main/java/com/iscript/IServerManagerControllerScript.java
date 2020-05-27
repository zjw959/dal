package com.iscript;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.constant.LoginErrorCode;
import com.controller.ServerManagerController;

import script.IScript;


public abstract class IServerManagerControllerScript implements IScript {

	/**
	 * 获取全部服务器信息
	 * @param serverManagerController
	 * @param request
	 * @return
	 */
	public abstract String getAllServerInfo(ServerManagerController serverManagerController, 
			HttpServletRequest request);

	/**
	 * 初始化服务器
	 * @param serverManagerController
	 * @param request
	 * @return
	 */
	public abstract String serverInit(ServerManagerController serverManagerController, 
			HttpServletRequest request);

	/**
	 * 添加修改服务器信息
	 * @param serverManagerController
	 * @param request
	 * @return
	 */
	public abstract LoginErrorCode operServer(ServerManagerController serverManagerController, 
			HttpServletRequest request);

	/**
	 * 服务器在线人数通知
	 * @param serverManagerController
	 * @param request
	 */
	public abstract void onlineNum(ServerManagerController serverManagerController, 
			HttpServletRequest request);

	/**
	 * 服务器维护设置
	 * @param serverManagerController
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public abstract LoginErrorCode serverMaintenance(ServerManagerController serverManagerController,
			HttpServletRequest request) throws IOException;
	
	
	
	

}
