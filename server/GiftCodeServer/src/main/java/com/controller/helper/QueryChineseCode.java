package com.controller.helper;

import java.io.Serializable;

public class QueryChineseCode implements Serializable {

    private static final long serialVersionUID = 5936733034034149637L;
    // 中文礼包码ID
    private String id;

    private int packId;

    private int limitStart;

    private int limitLength;

    public int getPackId() {
        return packId;
    }

    public void setPackId(int packId) {
        this.packId = packId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "packId :" + packId + "---" + "id:" + id + "---" + "limitStart:" + limitStart + "---"
                + "limitLength:" + limitLength;
    }

}
