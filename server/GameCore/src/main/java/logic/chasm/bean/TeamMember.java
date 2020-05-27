package logic.chasm.bean;

import java.util.HashMap;
import java.util.Map;
import com.alibaba.fastjson.JSONObject;
import com.jarvis.cache.to.HashRedisObject;

import io.netty.channel.ChannelHandlerContext;
import net.kcp.KcpOnUdp;

public class TeamMember implements HashRedisObject {
    private int pid;
    private String name;
    private int level;
    private EMemberStatus status = EMemberStatus.IDLE;
    private int heroCid;
    private int skinCid;
    private int serverId;
    private int reliveCount;
    private KcpOnUdp kcp;
    private ChannelHandlerContext ctx;
    /**
     * 玩家是否是连接状态
     */
    private boolean isConnect;

    public TeamMember() {}

    public TeamMember(int pid, String name, int level, int heroCid, int skinCid, int serverId) {
        super();
        this.pid = pid;
        this.name = name;
        this.level = level;
        this.heroCid = heroCid;
        this.skinCid = skinCid;
        this.serverId = serverId;
        this.reliveCount = 0;
    }

    public TeamMember(JSONObject json) {
        this.pid = json.getIntValue("pid");
        this.name = json.getString("name");
        this.level = json.getIntValue("level");
        this.status = EMemberStatus.getTeamStatus(json.getIntValue("status"));
        this.heroCid = json.getIntValue("heroCid");
        this.skinCid = json.getIntValue("skinCid");
        this.serverId = json.getIntValue("serverId");
        this.reliveCount = json.getIntValue("reliveCount");
    }

    @Override
    public Map<String, String> toHash() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("pid", String.valueOf(this.pid));
        map.put("name", String.valueOf(this.name));
        map.put("level", String.valueOf(this.level));
        map.put("status", String.valueOf(this.status.getStatus()));
        map.put("heroCid", String.valueOf(this.heroCid));
        map.put("skinCid", String.valueOf(this.skinCid));
        map.put("serverId", String.valueOf(this.serverId));
        map.put("reliveCount", String.valueOf(this.reliveCount));
        return map;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public EMemberStatus getStatus() {
        return status;
    }

    public void setStatus(EMemberStatus status) {
        this.status = status;
    }

    public int getHeroCid() {
        return heroCid;
    }

    public void setHeroCid(int heroCid) {
        this.heroCid = heroCid;
    }

    public void setSkinCid(int skinCid) {
        this.skinCid = skinCid;
    }

    public int getSkinCid() {
        return skinCid;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public void setReliveCount(int reliveCount) {
        this.reliveCount = reliveCount;
    }

    public int getReliveCount() {
        return reliveCount;
    }

    public enum EMemberStatus {
        /** 空闲 */
        IDLE(1),
        /** 准备中 */
        READY(2);

        private int status;

        private EMemberStatus(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }

        public static EMemberStatus getTeamStatus(int status) {
            for (EMemberStatus s : EMemberStatus.values()) {
                if (s.compare(status)) {
                    return s;
                }
            }
            throw new IllegalArgumentException("unknown teammember status value:" + status);
        }

        public boolean compare(int status) {
            return this.status == status;
        }
    }

    public KcpOnUdp getKcp() {
        return kcp;
    }
    
    public void setKcp(KcpOnUdp kcp) {
        this.kcp = kcp;
    }
    
    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }
    
    public ChannelHandlerContext getCtx() {
        return ctx;
    }
    
    public boolean isConnect() {
        return isConnect;
    }

    public void setConnect(boolean isConnect) {
        this.isConnect = isConnect;
    }

}