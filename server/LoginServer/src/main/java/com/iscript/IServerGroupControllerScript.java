package com.iscript;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.constant.LoginErrorCode;
import com.controller.ServerGroupController;

import script.IScript;

public abstract class IServerGroupControllerScript implements IScript {

	/**
	 * 获取所有分组信息
	 * @param serverGroupController
	 * @param request
	 * @return
	 */
	public abstract String getAllServerGroup(ServerGroupController serverGroupController, 
			HttpServletRequest request);

	/**
	 * 添加修改分组信息
	 * @param serverGroupController
	 * @param request
	 * @return
	 */
	public abstract LoginErrorCode saveServerGroup(ServerGroupController serverGroupController, 
			HttpServletRequest request);

	/**
	 * 分组进行维护设置
	 * @param serverGroupController
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public abstract LoginErrorCode groupMaintenance(ServerGroupController serverGroupController, 
			HttpServletRequest request) throws IOException;
	
}
