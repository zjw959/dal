package logic.dating.bean.dto;

/**
 * 约会对话数据传输对象
 * 
 * @author Alan
 *
 */
public class DatingDialogDTO {
    int branchNodeId;
    int selectedNodeId;
    int datingType;
    int roleId;
    boolean isLastNode;
    long datingId;

    public DatingDialogDTO(int branchNodeId, int selectedNodeId, int datingType, int roleId,
            boolean isLastNode,long datingId) {
        super();
        this.branchNodeId = branchNodeId;
        this.selectedNodeId = selectedNodeId;
        this.datingType = datingType;
        this.roleId = roleId;
        this.isLastNode = isLastNode;
        this.datingId=datingId;
    }

    public int getBranchNodeId() {
        return branchNodeId;
    }

    public DatingDialogDTO setBranchNodeId(int branchNodeId) {
        this.branchNodeId = branchNodeId;
        return this;
    }

    public int getSelectedNodeId() {
        return selectedNodeId;
    }

    public DatingDialogDTO setSelectedNodeId(int selectedNodeId) {
        this.selectedNodeId = selectedNodeId;
        return this;
    }

    public int getDatingType() {
        return datingType;
    }

    public DatingDialogDTO setDatingType(int datingType) {
        this.datingType = datingType;
        return this;
    }

    public int getRoleId() {
        return roleId;
    }

    public DatingDialogDTO setRoleId(int roleId) {
        this.roleId = roleId;
        return this;
    }

    public boolean isLastNode() {
        return isLastNode;
    }

    public DatingDialogDTO setLastNode(boolean isLastNode) {
        this.isLastNode = isLastNode;
        return this;
    }

    public long getDatingId() {
        return datingId;
    }

    public void setDatingId(long datingId) {
        this.datingId = datingId;
    }

}
