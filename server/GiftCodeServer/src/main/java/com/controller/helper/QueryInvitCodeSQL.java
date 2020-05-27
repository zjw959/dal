/**
 * 
 */
package com.controller.helper;

import java.io.Serializable;

import org.springframework.util.StringUtils;

import com.enity.InvitationCode;

import util.TimeUtil;

/**
 * @author wk.dai
 *
 */
public class QueryInvitCodeSQL implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5936733034034149637L;

    protected String id;
    protected String startTime;
    protected String endTime;
    protected int packageId;
    protected int roleId;
    /**
     * 0：全部；1：未领取；2：已经领取
     */
    protected int gotMark;
    protected int limitStart;
    /** 批次号 */
    protected String batchId;

    protected int limitLen = 1000;



    public QueryInvitCodeSQL() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }



    public int getLimitStart() {
        return limitStart;
    }

    public void setLimitStart(int limitStart) {
        this.limitStart = limitStart;
    }

    public int getLimitLen() {
        return limitLen;
    }

    public void setLimitLen(int limitLen) {
        this.limitLen = limitLen;
    }

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getGotMark() {
        return gotMark;
    }

    public void setGotMark(int gotMark) {
        this.gotMark = gotMark;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }



    public InvitationCode toInvitationCode() {
        InvitationCode code = new InvitationCode();
        code.setCreateTime(
                !StringUtils.isEmpty(this.startTime) ? TimeUtil.parse(this.startTime) : null);
        code.setGot(this.gotMark == 0 ? null : this.gotMark == 1 ? 0 : 1);
        code.setGotTime(!StringUtils.isEmpty(this.endTime) ? TimeUtil.parse(this.endTime) : null);
        code.setId(this.id);
        code.setPackageId(this.packageId == 0 ? null : this.packageId);
        code.setRoleId(this.getRoleId() == 0 ? null : this.getRoleId());
        code.setBatchId(!StringUtils.isEmpty(this.batchId) ? this.batchId : null);
        return code;
    }

}
