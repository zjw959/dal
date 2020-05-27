/**
 * 
 */
package logic.gift;


import java.io.Serializable;



/**
 * @author wk.dai
 *
 */
public class QueryInvitCodeSQL implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5936733034034149637L;

    protected String id;
    protected String startTime;
    protected String endTime;
    protected int packageId;
    protected int roleId;
    /**
     * 0：全部；1：未领取；2：已经领取
     */
    protected int gotMark;
    protected String tableName = "t_u_invit_code";
    protected String where;
    protected int limitStart;
    protected int limitLen = 1000;

    protected static final String SELECT_FIRST = "SELECT * FROM ";
    protected String executeSQL;

    public QueryInvitCodeSQL() {
        super();
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

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getGotMark() {
        return gotMark;
    }

    public void setGotMark(int gotMark) {
        this.gotMark = gotMark;
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

        if (packageId != 0) {
            if (!appendWhere) {
                buffer.append(" WHERE ");
                appendWhere = true;
            } else {
                buffer.append(" and ");
            }
            buffer.append("package_id=").append(packageId);
        }

        if (startTime != null && startTime.length() > 0
                && (startTime = startTime.trim()).length() > 0) {
            if (!appendWhere) {
                buffer.append(" WHERE ");
                appendWhere = true;
            } else {
                buffer.append(" and ");
            }
            buffer.append("(got_time !=null and got_time>=DATE_FORMAT('").append(startTime)
                    .append("','%Y-%m-%d %H:%i:%s'))");
        }

        if (endTime != null && endTime.length() > 0 && (endTime = endTime.trim()).length() > 0) {
            if (!appendWhere) {
                buffer.append(" WHERE ");
                appendWhere = true;
            } else {
                buffer.append(" and ");
            }
            buffer.append("(got_time !=null or got_time<=DATE_FORMAT('").append(endTime)
                    .append("','%Y-%m-%d %H:%i:%s'))");
        }

        if (roleId != 0) {
            if (!appendWhere) {
                buffer.append(" WHERE ");
                appendWhere = true;
            } else {
                buffer.append(" and ");
            }
            buffer.append("role_id=").append(roleId);
        }

        if (gotMark != 0) {
            if (!appendWhere) {
                buffer.append(" WHERE ");
                appendWhere = true;
            } else {
                buffer.append(" and ");
            }
            if (gotMark == 1) {
                buffer.append("got=").append(0);
            } else {
                buffer.append("got>").append(0);
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
        return "QueryInvitCodeSQL [" + (id != null ? "id=" + id + ", " : "")
                + (startTime != null ? "startTime=" + startTime + ", " : "")
                + (endTime != null ? "endTime=" + endTime + ", " : "") + "packageId=" + packageId
                + ", roleId=" + roleId + ", gotMark=" + gotMark + ", "
                + (tableName != null ? "tableName=" + tableName + ", " : "")
                + (where != null ? "where=" + where + ", " : "") + "limitStart=" + limitStart
                + ", limitLen=" + limitLen + ", "
                + (executeSQL != null ? "executeSQL=" + executeSQL : "") + "]";
    }

}
