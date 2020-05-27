/**
 * 
 */
package com.enity.batch;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import util.CsvField;

/**
 * @author zjw
 *
 */
@Entity
@Table(name = "t_u_batch")
public class BatchInfo implements Serializable {

    private static final long serialVersionUID = -1717268181721812501L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @CsvField(fileName = "id")
    private Integer id; // 自增ID
    @CsvField(fileName = "createTime")
    private Date createTime;
    @CsvField(fileName = "batchId")
    private String batchId;


    public Integer getId() {
        return id;
    }


    public Date getCreateTime() {
        return createTime;
    }


    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }


    public void setId(Integer id) {
        this.id = id;
    }


    public BatchInfo(String batchId, Date createTime) {
        super();
        this.batchId = batchId;
        this.createTime = createTime;
    }


    public String getBatchId() {
        return batchId;
    }

    public BatchInfo() {}

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }


}
