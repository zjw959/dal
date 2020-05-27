/**
 * 
 */
package com.enity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author zjw
 *
 */
public class UsedList implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 8299342983312862434L;
    private int roleId;
    private Map<String, InvitationCode> codeMap;

    public UsedList(int roleId, Map<String, InvitationCode> codeMap) {
        super();
        this.roleId = roleId;
        this.codeMap = codeMap;
    }

    public UsedList(int roleId) {
        this(roleId, new ConcurrentHashMap<String, InvitationCode>());
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }


    public Map<String, InvitationCode> getCodeMap() {
        return codeMap;
    }

    public void setCodeMap(Map<String, InvitationCode> codeMap) {
        this.codeMap = codeMap;
    }

    /**
     * @param id
     * @return
     */
    public InvitationCode findById(String id) {
        if (codeMap == null || codeMap.isEmpty()) {
            return null;
        }
        return codeMap.get(id);
    }

    /**
     * @param code
     */
    public void add(InvitationCode code) {
        codeMap.put(code.getId(), code);
    }

    /**
     * @param code
     * @return
     */
    public boolean remove(InvitationCode code) {
        if (codeMap.containsKey(code.getId())) {
            codeMap.remove(code.getId());
            return true;
        }
        return false;
    }

    /**
     * @param packageId
     * @return
     */
    public boolean removeAll(int packageId) {
        if (codeMap == null || codeMap.isEmpty())
            return false;

        List<InvitationCode> removeList = new ArrayList<InvitationCode>(4);
        for (InvitationCode code : codeMap.values()) {
            if (code.getPackageId() != packageId) {
                continue;
            }
            removeList.add(code);
        }

        if (!removeList.isEmpty()) {
            for (InvitationCode code : removeList) {
                codeMap.remove(code.getId());
            }
            return true;
        }
        return false;
    }

    /**
     * @param packageId
     * @return
     */
    public int count(int packageId) {
        if (codeMap == null || codeMap.isEmpty())
            return 0;

        int count = 0;
        for (InvitationCode code : codeMap.values()) {
            if (code.getPackageId() != packageId) {
                continue;
            }
            count++;
        }
        return count;

    }

    @Override
    public String toString() {
        return "UsedList [roleId=" + roleId + ", " + (codeMap != null ? "codeList=" + codeMap : "")
                + "]";
    }

}
