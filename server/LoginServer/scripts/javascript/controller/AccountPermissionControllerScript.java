package javascript.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import utils.ExceptionEx;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.constant.EScriptIdDefine;
import com.constant.LoginErrorCode;
import com.controller.AccountPermissionController;
import com.enity.AccountPermissionInfo;
import com.enity.ServerInfo;
import com.iscript.IAccountPermissionControllerScript;
import com.service.AccountPermissionService;
import com.service.AccountService;
import com.service.ServerListManager;
import com.utils.HttpBaseRequest;

public class AccountPermissionControllerScript extends IAccountPermissionControllerScript {
    private static final Logger log = Logger.getLogger(AccountPermissionControllerScript.class);

    @Override
    public int getScriptId() {
        return EScriptIdDefine.Account_Permission_Controller_SCRIPTID.Value();
    }

	@Override
	public LoginErrorCode operatorPermission(AccountPermissionController accountPermissionController,
			HttpServletRequest request) throws IOException {
		AccountPermissionService acountPermissionService = accountPermissionController.getAcountPermissionService();
		AccountService accountService = accountPermissionController.getAccountService();
		ServerListManager serverListManager = accountPermissionController.getServerListManager();
		LoginErrorCode errCode = LoginErrorCode.SUCCESS;
		
		String text = HttpBaseRequest.getDefault().getRequestJson(request);
		Integer playerId = null;
		Integer type = null;
		Date startTime = null;
		Date endTime = null;
		String reason = null;
		String operator = null;
		if(text.isEmpty()){
			String playerIdStr = request.getParameter("playerId");//玩家id
			if(playerIdStr==null||playerIdStr.isEmpty()){
				errCode = LoginErrorCode.MISS_PARAM;
				return errCode;
			}
			playerId = Integer.parseInt(playerIdStr);
			String typeStr = request.getParameter("type");//操作类型
			if(typeStr==null||typeStr.isEmpty()){
				errCode = LoginErrorCode.MISS_PARAM;
				return errCode;
			}
			type = Integer.parseInt(typeStr);
			String startTimeStr = request.getParameter("startTime");//开始时间
			String endTimeStr = request.getParameter("endTime");//结束时间
			try {
				 startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTimeStr);
				 endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTimeStr);
			} catch (ParseException e) {
                log.error(ExceptionEx.e2s(e));
				errCode = LoginErrorCode.MISS_PARAM;
				return errCode;
			}
			reason = request.getParameter("reason");//原因
			operator = request.getParameter("operator");//操作者
		}else{
			JSONObject jsonObject = JSONObject.parseObject(text);
			playerId = jsonObject.getInteger("playerId");
			type = jsonObject.getInteger("type");
			startTime = jsonObject.getDate("startTime");
			endTime = jsonObject.getDate("endTime");
			reason = jsonObject.getString("reason");
			operator = jsonObject.getString("operator");
		}
		
		if(playerId==null||type==null||startTime==null||endTime==null){
			errCode = LoginErrorCode.MISS_PARAM;
			return errCode;
		}
		
		AccountPermissionInfo accountPermissionInfo = acountPermissionService.findByPlayerIdAndType(playerId,type);
		if(accountPermissionInfo==null){
			accountPermissionInfo = new AccountPermissionInfo();
			accountPermissionInfo.setPlayerId(playerId);
			accountPermissionInfo.setType(type);
		}
		accountPermissionInfo.setStartTime(startTime);
		accountPermissionInfo.setEndTime(endTime);
		accountPermissionInfo.setReason(reason);
		accountPermissionInfo.setOperator(operator);
		accountPermissionInfo.setUpdateTime(new Date());
		acountPermissionService.putAccountPermission(accountPermissionInfo);
		boolean state = accountPermissionInfo.isEnable();
		//通知游戏服务器  获取该玩家所在服务器
		Integer sid = accountService.getLocServerId(playerId);
		if(sid!=null&&sid!=-1){
			ServerInfo serverInfo = serverListManager.getServerById(sid);
			if(type==AccountPermissionService.ban){
				CloseableHttpClient httpClient = HttpClients.createDefault();
				String url = serverInfo.getHttpUrl()+"/gm/kickoff";
				JSONObject object = new JSONObject();
				object.put("playerId", playerId);
				String content = object.toString();
				ContentType contentType = ContentType.create("application/json",
						Consts.UTF_8);
				StringEntity entity = new StringEntity(content, contentType);
				entity.setChunked(true);
				HttpPost httpPost = new HttpPost(url);
				httpPost.setEntity(entity);
				CloseableHttpResponse response = httpClient.execute(httpPost);
				try {
					if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
		                return LoginErrorCode.UNKNOWN_EXCEPTION;
					}
					HttpEntity responseEntity = response.getEntity();
					String responseString = EntityUtils.toString(responseEntity, "UTF-8");
					JSONObject resultJO = JSONObject.parseObject(responseString);
					Integer status = resultJO.getInteger("result");
					if(status!=0){
		                return LoginErrorCode.UNKNOWN_EXCEPTION;
					}
				} finally {
					if(response!=null){
						response.close();
					}
				}
			}else if(type==AccountPermissionService.gag){
				CloseableHttpClient httpClient = HttpClients.createDefault();
				String url = serverInfo.getHttpUrl()+"/gm/chat";
				JSONObject object = new JSONObject();
				object.put("playerId", playerId);
				object.put("state", state);
				String content = object.toString();
				ContentType contentType = ContentType.create("application/json",
						Consts.UTF_8);
				StringEntity entity = new StringEntity(content, contentType);
				entity.setChunked(true);
				HttpPost httpPost = new HttpPost(url);
				httpPost.setEntity(entity);
				CloseableHttpResponse response = httpClient.execute(httpPost);
				try {
					if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
		                return LoginErrorCode.UNKNOWN_EXCEPTION;
					}
					HttpEntity responseEntity = response.getEntity();
					String responseString = EntityUtils.toString(responseEntity, "UTF-8");
					JSONObject resultJO = JSONObject.parseObject(responseString);
					Integer status = resultJO.getInteger("result");
					if(status!=0){
		                return LoginErrorCode.UNKNOWN_EXCEPTION;
					}
				} finally {
					if(response!=null){
						response.close();
					}
				}
			}
		}
		return errCode;
	}

	@Override
	public LoginErrorCode reOperatorPermission(AccountPermissionController accountPermissionController,
			HttpServletRequest request) throws IOException {
		AccountPermissionService acountPermissionService = accountPermissionController.getAcountPermissionService();
		AccountService accountService = accountPermissionController.getAccountService();
		ServerListManager serverListManager = accountPermissionController.getServerListManager();
		LoginErrorCode errCode = LoginErrorCode.SUCCESS;
		String text = HttpBaseRequest.getDefault().getRequestJson(request);
		Integer playerId = null;
		Integer type = null;
		if(text.isEmpty()){
			String playerIdStr = request.getParameter("playerId");//玩家id
			if(playerIdStr==null||playerIdStr.isEmpty()){
				errCode = LoginErrorCode.MISS_PARAM;
				return errCode;
			}
			playerId = Integer.parseInt(playerIdStr);
			String typeStr = request.getParameter("type");//操作类型
			if(typeStr==null||typeStr.isEmpty()){
				errCode = LoginErrorCode.MISS_PARAM;
				return errCode;
			}
			type = Integer.parseInt(typeStr);
		}else{
			JSONObject jsonObject = JSONObject.parseObject(text);
			playerId = jsonObject.getInteger("playerId");
			type = jsonObject.getInteger("type");
		}
		if(playerId==null||type==null){
			errCode = LoginErrorCode.MISS_PARAM;
			return errCode;
		}
		acountPermissionService.removeAccountPermission(playerId, type);
		//通知游戏服务器  获取该玩家所在服务器
		if(type==AccountPermissionService.gag){
			Integer sid = accountService.getLocServerId(playerId);
			if(sid!=-1){
				ServerInfo serverInfo = serverListManager.getServerById(sid);
				CloseableHttpClient httpClient = HttpClients.createDefault();
				String url = serverInfo.getHttpUrl()+"/gm/chat";
				JSONObject object = new JSONObject();
				object.put("playerId", playerId);
				object.put("state", false);
				String content = object.toString();
				ContentType contentType = ContentType.create("application/json",
						Consts.UTF_8);
				StringEntity entity = new StringEntity(content, contentType);
				entity.setChunked(true);
				HttpPost httpPost = new HttpPost(url);
				httpPost.setEntity(entity);
				CloseableHttpResponse Gameresponse = httpClient.execute(httpPost);
				try {
					if (Gameresponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
						errCode =  LoginErrorCode.UNKNOWN_EXCEPTION;
				        return errCode;
					}
					HttpEntity responseEntity = Gameresponse.getEntity();
					String responseString = EntityUtils.toString(responseEntity, "UTF-8");
					JSONObject resultJO = JSONObject.parseObject(responseString);
					Integer status = resultJO.getInteger("result");
					if(status!=0){
						errCode =  LoginErrorCode.UNKNOWN_EXCEPTION;
				        return errCode;
					}
				} finally {
					if(Gameresponse!=null){
						Gameresponse.close();
					}
				}
			}
		}
		return errCode;
	}

	@Override
	public String queryOPList(AccountPermissionController accountPermissionController,
			HttpServletRequest request) throws IOException {
		AccountPermissionService acountPermissionService = accountPermissionController.getAcountPermissionService();
		LoginErrorCode errCode = LoginErrorCode.SUCCESS;
		
		String text = HttpBaseRequest.getDefault().getRequestJson(request);
		String playerId = null;
		String type = null;
		String startTime = null;
		String endTime = null;
		String reason = null;
		String operator = null;
		String size = null;
		String page = null;
		if(text.isEmpty()){
			playerId = request.getParameter("playerId");//玩家id
			type = request.getParameter("type");//操作类型
			startTime = request.getParameter("startTime");//开始时间
			endTime = request.getParameter("endTime");//结束时间
			reason = request.getParameter("reason");//原因
			operator = request.getParameter("operator");//操作者
			size = request.getParameter("size");
			page = request.getParameter("page");
		}else{
			JSONObject jsonObject = JSONObject.parseObject(text);
			playerId = jsonObject.getString("playerId");
			type = jsonObject.getString("type");
			startTime = jsonObject.getString("startTime");
			endTime = jsonObject.getString("endTime");
			reason = jsonObject.getString("reason");
			operator = jsonObject.getString("operator");
			size = jsonObject.getString("size");
			page = jsonObject.getString("page");
		}
		List<AccountPermissionInfo> list =  acountPermissionService.queryList(size,page,playerId,type,startTime,endTime,reason,operator);
		JSONArray jsonArray = new JSONArray();
		for(AccountPermissionInfo info:list){
			JSONObject dataObject = info.toJsonObject();
			jsonArray.add(dataObject);
		}
		JSONObject object = new JSONObject();
		object.put("status", errCode.getCode());
        object.put("msg", errCode.getMsg());
        object.put("data", jsonArray);
		String result = JSON.toJSONString(object,true);
        return result;
	}
}
