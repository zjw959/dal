package com.enity.forever;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_u_invit_code_forever")
public class ForeverCode implements Serializable {

    private static final long serialVersionUID = 2631040946253045662L;
    @Id
    private String id;
    private Integer packageId;
    private Integer got;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void addGot() {
        got += 1;
    }

    public void addGot(int value) {
        got += value;
    }

}
