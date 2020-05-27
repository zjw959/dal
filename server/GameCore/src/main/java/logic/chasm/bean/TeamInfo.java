package logic.chasm.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jarvis.cache.to.HashRedisObject;
import logic.chasm.bean.TeamMember.EMemberStatus;

public class TeamInfo implements HashRedisObject {
    /** 队伍id */
    private long teamId;
    /** 线程id */
    private int processorId;
    /** 队伍状态 */
    private volatile ETeamStatus status = ETeamStatus.WAITING;
    /** 队长id */
    private int leaderId;
    /** 成员 */
    private Map<Integer, TeamMember> members = new HashMap<>();
    /** 队伍类型 */
    private ETeamType teamType;
    /** 关卡id */
    private int dungeonCid;
    /** 战斗服务器id */
    private int serverId;
    /** 战斗服务器ip */
    private String serverIp;
    /** 战斗服务器端口 */
    private int serverPort;
    /** 随机值 */
    private int randomSeed;
    /** 队伍创建时间 */
    private long createTime;
    
    public TeamInfo() {}

    public TeamInfo(long teamId, int leaderId, Map<Integer, TeamMember> members,
            ETeamType teamType, int dungeonCid, int serverId, String serverIp, int serverPort, int randomSeed) {
        super();
        this.teamId = teamId;
        this.leaderId = leaderId;
        this.members = members;
        this.teamType = teamType;
        this.dungeonCid = dungeonCid;
        this.serverId = serverId;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.randomSeed = randomSeed;
    }

    public TeamInfo(Map<String, String> map) {
        teamId = Long.parseLong(map.get("teamId"));
        processorId = Integer.parseInt(map.get("processorId"));
        status = ETeamStatus.getTeamStatus(Integer.parseInt(map.get("status")));
        leaderId = Integer.parseInt(map.get("leaderId"));
        JSONArray jsonArray = JSON.parseArray(map.get("members"));
        for(int i = 0; i < jsonArray.size(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            TeamMember member = new TeamMember(json);
            members.put(member.getPid(), member);
        }
        teamType = ETeamType.getTeamType(Integer.parseInt(map.get("teamType")));
        dungeonCid = Integer.parseInt(map.get("dungeonCid"));
        serverId = Integer.parseInt(map.get("serverId"));
        serverIp = map.get("serverIp");
        serverPort = Integer.parseInt(map.get("serverPort"));
        randomSeed = Integer.parseInt(map.get("randomSeed"));
    }
    
    @Override
    public Map<String, String> toHash() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("teamId", String.valueOf(this.teamId));
        map.put("processorId", String.valueOf(this.processorId));
        map.put("status", String.valueOf(this.status.getStatus()));
        map.put("leaderId", String.valueOf(this.leaderId));
        Collection<JSONObject> membersList = new ArrayList<>();
        for (TeamMember member : this.members.values()) {
            JSONObject itemJSONObj = JSONObject.parseObject(JSON.toJSONString(member.toHash()));
            membersList.add(itemJSONObj);
        }
        map.put("members", JSON.toJSONString(membersList));
        map.put("teamType", String.valueOf(this.teamType.getType()));
        map.put("dungeonCid", String.valueOf(this.dungeonCid));
        map.put("serverId", String.valueOf(this.serverId));
        map.put("serverIp", String.valueOf(this.serverIp));
        map.put("serverPort", String.valueOf(this.serverPort));
        map.put("randomSeed", String.valueOf(this.randomSeed));
        return map;
    }

    public long getTeamId() {
        return teamId;
    }
    
    public int getProcessorId() {
        return processorId;
    }

    public void setProcessorId(int processorId) {
        this.processorId = processorId;
    }
    
    public ETeamStatus getStatus() {
        return status;
    }
    
    public void setStatus(ETeamStatus status) {
        this.status = status;
    } 
    
    public int getLeaderId() {
        return leaderId;
    }
    
    public void setLeaderId(int leaderId) {
        this.leaderId = leaderId;
    }
    
    public Map<Integer, TeamMember> getMembers() {
        return members;
    }
    
    public int getMemberNum() {
        return members.size();
    }

    public ETeamType getTeamType() {
        return teamType;
    }
    
    public int getDungeonCid() {
        return dungeonCid;
    }
    
    public int getServerId() {
        return serverId;
    }
    
    public String getServerIp() {
        return serverIp;
    }
    
    public int getServerPort() {
        return serverPort;
    }
    
    public int getRandomSeed() {
        return randomSeed;
    }
    
    public enum ETeamStatus {
        /** 等待 关闭自动匹配 */
        WAITING(1),
        /** 开启自动匹配 */
        OPEN_AUTO_MATCH(2),
        /** 战斗 */
        FIGHTING(3);
        
        private int status;
        
        private ETeamStatus(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }
        
        public static ETeamStatus getTeamStatus(int status) {
            for (ETeamStatus s : ETeamStatus.values()) {
                if (s.compare(status)) {
                    return s;
                }
            }
            throw new IllegalArgumentException("unknown team status value:" + status);
        }

        public boolean compare(int status) {
            return this.status == status;
        }
    }
    
    public static enum ETeamType {
        CHASM(1);

        private int type;
        
        private ETeamType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
        
        public static ETeamType getTeamType(int type) {
            for (ETeamType t : ETeamType.values()) {
                if (t.compare(type)) {
                    return t;
                }
            }
            throw new IllegalArgumentException("unknown team type value:" + type);
        }

        public boolean compare(int type) {
            return this.type == type;
        }
    }
    
    /** 队伍中是否存在改成员 */
    public TeamMember getMember(int pid) {
        return members.get(pid);
    }
    
    public TeamMember removeMember(int pid) {
        return members.remove(pid);
    }

    public boolean changeLeader() {
        if (members.size() >= 0) {
            Collection<TeamMember> teamMembers = members.values();
            Iterator<TeamMember> itr = teamMembers.iterator();
            TeamMember teamMember = itr.next();
            leaderId = teamMember.getPid();
            return true;
        }
        return false;
    }

    public boolean changeLeader(int leaderId) {
        TeamMember member = members.get(leaderId);
        if (member == null)
            return false;
        this.leaderId = leaderId;
        return true;
    }

    /** 检查队长权限 */
    public boolean checkLeaderAuthority(int targetPid) {
        if(leaderId != targetPid) {
            return false;
        }
        return true;
    }
    
    /** 是否全部准备 */
    public boolean allReady() {
        boolean allReady = true;
        for (Map.Entry<Integer, TeamMember> entry : members.entrySet()) {
            TeamMember menber = entry.getValue();
            if (getLeaderId() == menber.getPid()) {
                continue;
            }
            
            if(menber.getStatus() != EMemberStatus.READY) {
                allReady = false;
                break;
            }
        }
        return allReady;
    }
    
    public long getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
