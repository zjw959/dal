/**
 * 
 */
package com.controller.helper;

import java.io.Serializable;

public class QueryChineseUse implements Serializable {

    private static final long serialVersionUID = 5936733034034149637L;

    protected String id;
    protected int roleId;
    /** 领取时间范围 */
    protected String starTime;
    /** 领取时间范围 */
    protected String endTime;
    protected int packageId;

    protected int limitStart;
    protected int limitLength = 1000;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getStarTime() {
        return starTime;
    }

    public void setStarTime(String starTime) {
        this.starTime = starTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }


    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public int getLimitStart() {
        return limitStart;
    }

    public void setLimitStart(int limitStart) {
        this.limitStart = limitStart;
    }

    public int getLimitLength() {
        return limitLength;
    }

    public void setLimitLength(int limitLength) {
        this.limitLength = limitLength;
    }



}
