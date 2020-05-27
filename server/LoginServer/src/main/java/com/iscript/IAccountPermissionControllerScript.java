package com.iscript;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.constant.LoginErrorCode;
import com.controller.AccountPermissionController;

import script.IScript;

public abstract class IAccountPermissionControllerScript implements IScript {

	/**
	 * 用户权限操作
	 * @param accountPermissionController
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public abstract LoginErrorCode operatorPermission(AccountPermissionController accountPermissionController,
			HttpServletRequest request) throws IOException;

	/**
	 * 解除玩家限制
	 * @param accountPermissionController
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public abstract LoginErrorCode reOperatorPermission(AccountPermissionController accountPermissionController,
			HttpServletRequest request) throws IOException;

	/**
	 * 按条件查询受限玩家
	 * @param accountPermissionController
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public abstract String queryOPList(AccountPermissionController accountPermissionController,
			HttpServletRequest request) throws IOException;
	
}
