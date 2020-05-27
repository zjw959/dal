package com.enity.forever;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@IdClass(ForeverUse.class)
@Table(name = "t_u_forever_got")
public class ForeverUse implements Serializable {

    private static final long serialVersionUID = 1174416437046692346L;
    @Id
    private String id;
    @Id
    private Integer roleId;
    private Integer packageId;
    private Integer got;
    private Date gotTime;
    private Date createTime;

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

    public Integer getGot() {
        return got;
    }

    public void setGot(Integer got) {
        this.got = got;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getIdAndRoleIdStr() {
        return id + ":" + roleId;
    }

    public void addGot() {
        got += 1;
        this.gotTime = new Date();
    }

    public void addGot(int value) {
        got += value;
        this.gotTime = new Date();
    }
}
