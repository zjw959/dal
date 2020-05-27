/**
 * 
 */
package com.controller.helper;

import java.io.Serializable;

/**
 * @author wk.dai
 *
 */
public class QueryBatch implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5936733034034149637L;

    protected int id;
    protected String startTime;
    protected String endTime;
    protected String batchId;
    protected int limitStart;
    protected int limitLength = 100;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
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
