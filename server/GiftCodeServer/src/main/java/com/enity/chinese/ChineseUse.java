package com.enity.chinese;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import util.CsvField;

@Entity
@IdClass(ChineseUse.class)
@Table(name = "t_u_invit_code_chinese")
public class ChineseUse implements Serializable {

    private static final long serialVersionUID = 8269545597346816348L;
    @CsvField(fileName = "id")
    private String id;
    @Id
    @CsvField(fileName = "packageId")
    private Integer packageId;
    @Id
    @CsvField(fileName = "roleId")
    private Integer roleId;
    @CsvField(fileName = "gotTime")
    private Date gotTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getPackageId() {
        return packageId;
    }

    public void setPackageId(Integer packageId) {
        this.packageId = packageId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Date getGotTime() {
        return gotTime;
    }

    public void setGotTime(Date gotTime) {
        this.gotTime = gotTime;
    }

    public String getIdAndRoleIdStr() {
        return id + ":" + roleId;
    }

}
