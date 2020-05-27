/**
 * 
 */
package com.enity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import util.CsvField;

/**
 * @author zjw
 *
 */
@Entity
@Table(name = "t_u_package")
public class PackageInfo implements Serializable, Comparable<PackageInfo> {

    private static final long serialVersionUID = -1717268181721812501L;
    @Id
    @CsvField(fileName = "id")
    private Integer id;
    @CsvField(fileName = "name")
    private String name;
    @CsvField(fileName = "channel")
    private String channel;
    @CsvField(fileName = "serverId")
    private String serverId;
    @CsvField(fileName = "beginTime")
    private Date beginTime;
    @CsvField(fileName = "endTime")
    private Date endTime;
    @CsvField(fileName = "expression")
    private String expression;
    @CsvField(fileName = "count")
    private Integer count;
    @CsvField(fileName = "dayInterval")
    private Integer dayInterval;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void setDayInterval(Integer dayInterval) {
        this.dayInterval = dayInterval;
    }

    @Override
    public String toString() {
        return "CreatePackageStruct [id=" + id + ", " + ("count=" + count + ", ")
                + ("dayInterval=" + dayInterval + ", ")
                + (name != null ? "name=" + name + ", " : "")
                + (channel != null ? "channel=" + channel + ", " : "")
                + (serverId != null ? "serverId=" + serverId + ", " : "")
                + (beginTime != null ? "beginTime=" + beginTime + ", " : "")
                + (endTime != null ? "endTime=" + endTime + ", " : "")
                + (expression != null ? "expression=" + expression : "") + "]";
    }

    public void swap(PackageInfo info) {
        this.id = info.id;
        this.name = info.name;
        this.expression = info.expression;
        this.channel = info.channel;
        this.serverId = info.serverId;
        this.beginTime = info.beginTime;
        this.endTime = info.endTime;
        this.count = info.count;
        this.dayInterval = info.dayInterval;
    }

    @Override
    public int compareTo(PackageInfo o) {
        return id - o.id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PackageInfo other = (PackageInfo) obj;
        if (id != other.id)
            return false;
        return true;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getDayInterval() {
        return dayInterval;
    }

    public void setDayInterval(int dayInterval) {
        this.dayInterval = dayInterval;
    }

    public PackageInfo() {}

    public PackageInfo(Integer id, String name, String channel, String serverId, Date beginTime,
            Date endTime, String expression, Integer count, Integer dayInterval) {
        super();
        this.id = id;
        this.name = name;
        this.channel = channel;
        this.serverId = serverId;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.expression = expression;
        this.count = count;
        this.dayInterval = dayInterval;
    }

}
