package javascript.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import utils.ExceptionEx;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.constant.EScriptIdDefine;
import com.constant.LoginErrorCode;
import com.controller.ServerGroupController;
import com.enity.ServerGroup;
import com.iscript.IServerGroupControllerScript;
import com.service.ServerListManager;
import com.utils.HttpBaseRequest;

public class ServerGroupControllerScript extends IServerGroupControllerScript {
    private static final Logger log = Logger.getLogger(ServerGroupControllerScript.class);
    
    @Override
    public int getScriptId() {
        return EScriptIdDefine.Server_Group_Controller_SCRIPTID.Value();
    }

    private String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."+
			"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."+
			"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."+
			"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
    
	@Override
	public String getAllServerGroup(ServerGroupController serverGroupController, HttpServletRequest request) {
		ServerListManager serverListManager =serverGroupController.getServerListManager();
		LoginErrorCode errCode = LoginErrorCode.SUCCESS;
		String allGroupJson = JSON.toJSONString(serverListManager.getAllServerGroup(), true);
		JSONObject object = new JSONObject();
		object.put("status", errCode.getCode());
        object.put("msg", errCode.getMsg());
        JSONObject dataObject = new JSONObject();
        dataObject.put("all_group", allGroupJson);
        String dataResult = JSON.toJSONString(dataObject,true);
        object.put("data", dataResult);
		String result = JSON.toJSONString(object,true);
		return result;
	}

	@Override
	public LoginErrorCode saveServerGroup(ServerGroupController serverGroupController, HttpServletRequest request) {
		log.info("后台添加修改分组信息 ");
		ServerListManager serverListManager =serverGroupController.getServerListManager();
		LoginErrorCode errCode = LoginErrorCode.SUCCESS;
		String text = null;
		try {
			text = HttpBaseRequest.getDefault().getRequestJson(request);
		} catch (IOException e) {
            log.error(ExceptionEx.e2s(e));
		}
		JSONObject jsonObject = JSONObject.parseObject(text);
		ServerGroup serverGroup = JSON.toJavaObject(jsonObject.getJSONObject("ServerGroup"), ServerGroup.class);
		if(serverGroup.getName()==null||serverGroup.getName().isEmpty()
				||serverGroup.getInfo()==null||serverGroup.getInfo().isEmpty()){
			errCode = LoginErrorCode.SERVER_NAME_MISS;
			return errCode;
		}
        if (!serverGroup.getGlobalDatabasesIp().matches(regex)
                || !serverGroup.getMailDatabasesIp().matches(regex)
                || !serverGroup.getPayDatabasesIp().matches(regex)
                || !serverGroup.getGameDatabasesIp().matches(regex)) {
			errCode = LoginErrorCode.SERVER_IP_MISS;
			return errCode;
		}
		boolean status = serverListManager.saveServerGroup(serverGroup);
		if(!status){
			errCode = LoginErrorCode.UNKNOWN_EXCEPTION;
		}
		return errCode;
	}

	@Override
	public LoginErrorCode groupMaintenance(ServerGroupController serverGroupController, HttpServletRequest request) throws IOException {
		log.info("后台设置分组下所有服务器维护状态 ");
		ServerListManager serverListManager =serverGroupController.getServerListManager();
		LoginErrorCode errCode = LoginErrorCode.SUCCESS;
		String text = HttpBaseRequest.getDefault().getRequestJson(request);
		Integer groupId = null;
		String notice = null;
		Integer mark = null;
		if(text.isEmpty()){
			String groupIdStr = request.getParameter("groupId");
			if(groupIdStr==null||groupIdStr.isEmpty()){
				errCode = LoginErrorCode.MISS_PARAM;
				return errCode;
			}
			groupId = Integer.parseInt(groupIdStr);
			notice = request.getParameter("notice");
			String markStr = request.getParameter("mark");
			mark = Integer.parseInt(markStr);
		}else{
			JSONObject jsonObject = JSONObject.parseObject(text);
			groupId = jsonObject.getInteger("groupId");
			mark = jsonObject.getInteger("mark");
			notice = jsonObject.getString("notice");
		}
		ServerGroup group = serverListManager.getServerGroupById(groupId);
		if(group==null){
			errCode = LoginErrorCode.SERVER_GROUP_MISS;
			return errCode;
		}
		group.setMark(mark);
		group.setMaintenanceNotice(notice);
		boolean status = serverListManager.saveServerGroup(group);
        log.info("后台设置分组下所有服务器维护状态  groupId:" + groupId + " mark:" + mark + " succ:" + status);
		if(!status){
			errCode = LoginErrorCode.UNKNOWN_EXCEPTION;
		}
		return errCode;
	}
}
