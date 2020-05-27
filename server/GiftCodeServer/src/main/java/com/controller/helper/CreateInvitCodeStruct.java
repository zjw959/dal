/**
 * 
 */
package com.controller.helper;

import java.io.Serializable;

/**
 * @author wk.dai
 *
 */
public class CreateInvitCodeStruct implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1175610778543172554L;
	/**
	 * 礼包配置ID
	 */
	private int packageId;
	/**
	 * 创建的数量
	 */
	private int count;
	/**
	 * 类型。1：一次性；2：永久
	 */
	private int type;

	public int getPackageId() {
		return packageId;
	}

	public void setPackageId(int packageId) {
		this.packageId = packageId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "CreateInvitCodeStruct [packageId=" + packageId + ", count="
				+ count + ", type=" + type + "]";
	}

}
