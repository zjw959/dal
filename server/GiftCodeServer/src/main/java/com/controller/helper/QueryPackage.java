/**
 * 
 */
package com.controller.helper;

import java.io.Serializable;

import org.springframework.util.StringUtils;

import com.enity.PackageInfo;

import util.TimeUtil;

/**
 * @author wk.dai
 *
 */
public class QueryPackage implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5936733034034149637L;

    protected String id;
    protected String startTime;
    protected String endTime;
    protected String tableName = "t_u_package";
    protected String where;
    protected int limitStart;
    protected int limitLen = 100;
    protected String name;
    protected String serverId;
    protected String channel;

    protected static final String SELECT_FIRST = "SELECT * FROM ";
    protected String executeSQL;

    public QueryPackage() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
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

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    protected boolean appendExtend(StringBuffer buffer, boolean whereExist) {
        return whereExist;
    }

    /**
     * @return
     */
    public String toSQL() {
        if (executeSQL != null)
            return executeSQL;

        StringBuffer buffer = new StringBuffer();
        buffer.append(SELECT_FIRST).append(tableName);
        boolean appendWhere = false;

        if (id != null && id.length() > 0 && (id = id.trim()).length() > 0) {
            if (!appendWhere) {
                buffer.append(" WHERE ");
                appendWhere = true;
            } else {
                buffer.append(" and ");
            }
            if (id.contains("%")) {
                buffer.append("id like '").append(id).append("'");
            } else {
                buffer.append("id='").append(id).append("'");
            }
        }

        if (startTime != null && startTime.length() > 0
                && (startTime = startTime.trim()).length() > 0) {
            if (!appendWhere) {
                buffer.append(" WHERE ");
                appendWhere = true;
            } else {
                buffer.append(" and ");
            }
            buffer.append("(begin_time =null or begin_time>=DATE_FORMAT('").append(startTime)
                    .append("','%Y-%m-%d %H:%i:%s'))");
        }

        if (endTime != null && endTime.length() > 0 && (endTime = endTime.trim()).length() > 0) {
            if (!appendWhere) {
                buffer.append(" WHERE ");
                appendWhere = true;
            } else {
                buffer.append(" and ");
            }
            buffer.append("(end_time=null or end_time<=DATE_FORMAT('").append(endTime)
                    .append("','%Y-%m-%d %H:%i:%s'))");
        }

        if (name != null && name.length() > 0 && (name = name.trim()).length() > 0) {
            if (!appendWhere) {
                buffer.append(" WHERE ");
                appendWhere = true;
            } else {
                buffer.append(" and ");
            }
            if (name.contains("%")) {
                buffer.append("name like '").append(name).append("'");
            } else {
                buffer.append("name='").append(name).append("'");
            }
        }

        if (serverId != null && serverId.length() > 0
                && (serverId = serverId.trim()).length() > 0) {
            if (!appendWhere) {
                buffer.append(" WHERE ");
                appendWhere = true;
            } else {
                buffer.append(" and ");
            }
            if (serverId.contains("%")) {
                buffer.append("server_id like '").append(serverId).append("'");
            } else {
                buffer.append("server_id='").append(serverId).append("'");
            }
        }

        if (channel != null && channel.length() > 0 && (channel = channel.trim()).length() > 0) {
            if (!appendWhere) {
                buffer.append(" WHERE ");
                appendWhere = true;
            } else {
                buffer.append(" and ");
            }
            if (channel.contains("%")) {
                buffer.append("channel like '").append(channel).append("'");
            } else {
                buffer.append("channel='").append(channel).append("'");
            }
        }

        appendWhere = appendExtend(buffer, appendWhere);

        if (where != null && where.length() > 0 && (where = where.trim()).length() > 0) {
            String tmp = where.toLowerCase();
            if (!tmp.startsWith("where")) {
                if (tmp.startsWith("order")) {
                    buffer.append(" ");
                } else {
                    if (!appendWhere) {
                        buffer.append(" WHERE ");
                        appendWhere = true;
                    } else {
                        buffer.append(" and ");
                    }
                }
            } else {
                buffer.append(" ");
            }
            buffer.append(where);
        }

        if (limitStart < 0) {
            limitStart = 0;
        }

        buffer.append(" limit ").append(limitStart).append(",").append(limitLen);
        executeSQL = buffer.toString();
        return executeSQL;
    }

    @Override
    public String toString() {
        return "QueryPackage [" + (id != null ? "id=" + id + ", " : "")
                + (startTime != null ? "startTime=" + startTime + ", " : "")
                + (endTime != null ? "endTime=" + endTime + ", " : "")
                + (tableName != null ? "tableName=" + tableName + ", " : "")
                + (where != null ? "where=" + where + ", " : "") + "limitStart=" + limitStart
                + ", limitLen=" + limitLen + ", " + (name != null ? "name=" + name + ", " : "")
                + (serverId != null ? "serverId=" + serverId + ", " : "")
                + (channel != null ? "channel=" + channel + ", " : "")
                + (executeSQL != null ? "executeSQL=" + executeSQL : "") + "]";
    }

    public PackageInfo toPackageInfo() {
        PackageInfo info = new PackageInfo();
        if (!StringUtils.isEmpty(id)) {
            info.setId(Integer.parseInt(id));
        }
        if (!StringUtils.isEmpty(startTime)) {
            info.setBeginTime(TimeUtil.parse(startTime));
        }
        if (!StringUtils.isEmpty(endTime)) {
            info.setEndTime(TimeUtil.parse(endTime));
        }
        if (!StringUtils.isEmpty(name)) {
            info.setName(name);
        }
        if (!StringUtils.isEmpty(serverId)) {
            info.setServerId(serverId);
        }
        if (!StringUtils.isEmpty(channel)) {
            info.setChannel(channel);
        }
        return info;
    }
}
