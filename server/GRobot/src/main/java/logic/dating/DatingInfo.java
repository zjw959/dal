package logic.dating;

import java.util.ArrayList;
import java.util.List;
import org.game.protobuf.s2c.S2CDatingMsg;

public class DatingInfo {
    
    /**1542**/
    List<S2CDatingMsg.BranchNode> branchNodesList=new ArrayList<S2CDatingMsg.BranchNode>();
    int datingRuleCid;
    /**1542**/
    public List<S2CDatingMsg.BranchNode> getBranchNodesList() {
        return branchNodesList;
    }
    public void setBranchNodesList(List<S2CDatingMsg.BranchNode> branchNodesList) {
        this.branchNodesList = branchNodesList;
    }
    public int getDatingRuleCid() {
        return datingRuleCid;
    }
    public void setDatingRuleCid(int datingRuleCid) {
        this.datingRuleCid = datingRuleCid;
    }
    
}
