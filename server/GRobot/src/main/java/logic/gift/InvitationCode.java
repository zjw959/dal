/**
 * 
 */
package logic.gift;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zjw
 *
 */
public class InvitationCode implements Serializable {

    private static final long serialVersionUID = 6847514695297712206L;
    private String id;
    private Integer packageId;
    private Integer got;
    private Integer roleId;
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

    public void addGot() {
        got += 1;
        this.gotTime = new Date();
    }

    public void addGot(int value) {
        got += value;
        this.gotTime = new Date();
    }

    @Override
    public String toString() {
        return "InvitationCode [" + (id != null ? "id=" + id + ", " : "") + "packageId=" + packageId
                + ", got=" + got + ", roleId=" + roleId + ", "
                + (gotTime != null ? "gotTime=" + gotTime : "")
                + (createTime != null ? "createTime=" + createTime : "") + "]";
    }

    public void swap(InvitationCode code) {
        if (this == code) {
            return;
        }
        id = code.id;
        roleId = code.roleId;
        got = code.got;
        gotTime = code.gotTime;
        packageId = code.packageId;
        createTime = code.createTime;
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

    public String getRoleIdAndPackIdStr() {
        return roleId + ":" + packageId;
    }

}
