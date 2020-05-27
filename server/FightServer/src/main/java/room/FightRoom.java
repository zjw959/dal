package room;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import org.apache.commons.compress.utils.Lists;
import org.game.protobuf.s2c.S2CFightMsg.NetFrame;

import com.google.protobuf.GeneratedMessage;

import data.GameDataManager;
import data.bean.ChasmDungeonCfgBean;
import data.bean.DiscreteDataCfgBean;
import logic.chasm.bean.TeamInfo;
import logic.chasm.bean.TeamMember;
import logic.support.LogicScriptsUtils;
import logic.support.MessageUtils;
import message.SMessageFactory;
import net.kcp.Transfer;
import thread.FightRoomProcessor;
import thread.FightRoomProcessorManager;
import utils.ToolMap;

public class FightRoom extends AbstractRoom implements Comparable<FightRoom> {
    /** 同步间隔 */
    public static final int SYNC_INTERVAL = 66;
    /** 房间状态 */
    private Status status;
    /** 时间 */
    private long statusTime;
    /** 队伍信息 */
    private TeamInfo teamInfo;
    /** key:sessionId,value:roleId */
    public Map<String, Integer> sessionIdRoleIds = new ConcurrentHashMap<>();
    /** 当前帧 */
    private NetFrame.Builder currentFrame;
    /** 网络帧 */
    private List<NetFrame> netFrames = Lists.newArrayList();
    /** 上次同步时间 */
    private long lastSyncTime;
    /** 同步次数 */
    private long syncCount;
    /** 战斗时间 */
    private int battleTime;
    /** 记录成员的胜负 */
    private Set<Boolean> membersWin = new CopyOnWriteArraySet<>();
    /** 进入房间的时间 */
    private int enterTime;
    /** 战斗前的准备时间 */
    private int readyTime;
    
    public FightRoom(TeamInfo teamInfo) {
        this.id  = teamInfo.getTeamId();
        FightRoomProcessor roomPro = (FightRoomProcessor) FightRoomProcessorManager
                .getInstance().chooseLineBySequence();
        this.processorId = roomPro.getProcessorId();
        this.status = Status.LOADING;
        this.statusTime = createTime;
        this.teamInfo = teamInfo;
        ChasmDungeonCfgBean cfgBean =
                GameDataManager.getChasmDungeonCfgBean(teamInfo.getDungeonCid());
        battleTime = cfgBean.getBattleTime() * 1000;
        currentFrame = NetFrame.newBuilder();
        currentFrame.setIndex(1);
        DiscreteDataCfgBean enterTimeCfgBean = GameDataManager.getDiscreteDataCfgBean(21002);
        DiscreteDataCfgBean readyTimeCfgBean = GameDataManager.getDiscreteDataCfgBean(21003);
        enterTime = ToolMap.getInt("time", enterTimeCfgBean.getData(), 30);
        readyTime = ToolMap.getInt("time", readyTimeCfgBean.getData(), 20);
    }
    
    @Override
    public void tick() {
        LogicScriptsUtils.getChasmScript().roomTick(this);
    }

    /**
     * 销毁房间
     * @param campId
     */
    public void destroyRoom(boolean isWin) {
        LogicScriptsUtils.getChasmScript().destroyRoom(this, isWin);
    }
    
    public TeamInfo getTeamInfo() {
        return teamInfo;
    }
    
    public void setStatus(Status status) {
        this.status = status;
        this.statusTime = System.currentTimeMillis();
    }
    
    public Status getStatus() {
        return status;
    }
    
    public long getStatusTime() {
        return statusTime;
    }
    
    public enum Status {
        LOADING(1), READY(2), FIGHTING(3);

        private int status;

        private Status(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }
    }
    
    public NetFrame.Builder getCurrentFrame() {
        return currentFrame;
    }
    
    public void setCurrentFrame(NetFrame.Builder currentFrame) {
        this.currentFrame = currentFrame;
    }
    
    public List<NetFrame> getNetFrames() {
        return netFrames;
    }
    
    public long getLastSyncTime() {
        return lastSyncTime;
    }
    
    public void setLastSyncTime(long lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }
    
    public long getSyncCount() {
        return syncCount;
    }
    
    public void setSyncCount(long syncCount) {
        this.syncCount = syncCount;
    }
    
    public int getBattleTime() {
        return battleTime;
    }
    
    public void setBattleTime(int battleTime) {
        this.battleTime = battleTime;
    }
    
    public int getEnterTime() {
        return enterTime;
    }
    
    public int getReadyTime() {
        return readyTime;
    }
    
    /**
     * 发送消息给在线玩家
     * @param builder
     */
    public void sendToOnlinePlayer(GeneratedMessage.Builder<?> builder) {
        Map<Integer, TeamMember> teamMembers = teamInfo.getMembers();
        for(Map.Entry<Integer, TeamMember> entry : teamMembers.entrySet()) {
            TeamMember teamMember = entry.getValue();
            if(teamMember.isConnect()) {
                if(teamMember.getKcp() != null) {
                    Transfer.send(teamMember.getKcp(), getFactory(), builder);
                } else {
                    MessageUtils.send(teamMember.getCtx(), builder, getFactory());
                }
            }
        }
    }
    
    /**
     * 是否全部都掉线了
     * @return
     */
    public boolean isAllOffline() {
        boolean isAllOffline = true;
        Map<Integer, TeamMember> teamMembers = teamInfo.getMembers();
        for(Map.Entry<Integer, TeamMember> entry : teamMembers.entrySet()) {
            TeamMember teamMember = entry.getValue();
            if(teamMember.isConnect()) {
                isAllOffline = false;
            }
        }
        return isAllOffline;
    }

    @Override
    public SMessageFactory getFactory() {
        FightRoomProcessor processor =
                (FightRoomProcessor) FightRoomProcessorManager.getInstance().getRoomProcessor(this.getProcessorId());
        if (processor != null)
            return processor.getsMsgFactory();
        return null;
    }

    @Override
    public int compareTo(FightRoom o) {
        if(this.lastSyncTime > o.lastSyncTime) {
            return 1;
        } else if(this.lastSyncTime < o.lastSyncTime) {
            return -1;
        } else {
            return 0;
        }
    }
    
    
    public void addMemberWin(boolean isWin) {
        membersWin.add(isWin);
    }

    public boolean isSameMemberWin(){
        if(membersWin.size()<2)return true;
        Iterator<Boolean> it = membersWin.iterator();
        boolean isWin = it.next();
        while(it.hasNext()){
            boolean isNextWin = it.next();
            if (isWin != isNextWin)
                return false;
        }
        return true;
    }
}
