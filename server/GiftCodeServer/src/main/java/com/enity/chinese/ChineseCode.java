package com.enity.chinese;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import util.CsvField;

@Entity
@Table(name = "t_u_chinese_code")
public class ChineseCode implements Serializable {

    private static final long serialVersionUID = -2318444135345038646L;
    @Id
    @CsvField(fileName = "id")
    private String id;
    @CsvField(fileName = "packageId")
    private Integer packageId;
    @CsvField(fileName = "createTime")
    private Date createTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }


    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "ChineseCode [id=" + id + ", packageId=" + packageId + "]";
    }

}
