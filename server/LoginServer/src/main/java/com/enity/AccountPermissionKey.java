package com.enity;

import java.io.Serializable;

public class AccountPermissionKey implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int playerId;//玩家id
	private int type;//1:封号2：禁言
	
	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
}
