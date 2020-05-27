package com.enity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.alibaba.fastjson.JSONObject;

@Entity
@Table(name="t_u_account_permission")
@IdClass(AccountPermissionKey.class)
public class AccountPermissionInfo {
	@Id
	private int playerId;//玩家id
	@Id
	private int type;//1:封号2：禁言
	private Date startTime;//开始封禁时间
	private Date endTime;//结束时间
	private String reason;//封号原因
	private String operator;//操作者
	private Date updateTime;//操作时间
	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "AccountPermissionInfo [playerId=" + playerId + ", type=" + type + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", reason=" + reason + ", operator=" + operator + ", updateTime="
				+ updateTime + "]";
	}
	
	public JSONObject toJsonObject() {
		JSONObject object = new JSONObject();
		object.put("playerId", playerId);
		object.put("type", type);
		object.put("startTime", startTime);
		object.put("endTime", endTime);
		object.put("reason", reason);
		object.put("operator", operator);
		object.put("updateTime", updateTime);
		return object;
	}
	
	public boolean isEnable(){
		long now =  System.currentTimeMillis();
		if(now<startTime.getTime()){
			return false;
		}
		if(now>endTime.getTime()){
			return false;
		}
		return true;
	}
}
