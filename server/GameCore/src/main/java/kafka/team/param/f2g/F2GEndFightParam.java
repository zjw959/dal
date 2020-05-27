package kafka.team.param.f2g;

import java.util.ArrayList;
import java.util.List;

public class F2GEndFightParam {
    private boolean win;
    private int fightTime;
    private int dungeonCid;
    private long teamId;
    private int playerId;
    private List<MemberData> memberDatas = new ArrayList<>();
    
    public boolean isWin() {
        return win;
    }

    public void setWin(boolean win) {
        this.win = win;
    }

    public int getFightTime() {
        return fightTime;
    }
    
    public void setFightTime(int fightTime) {
        this.fightTime = fightTime;
    }
    
    public int getDungeonCid() {
        return dungeonCid;
    }

    public void setDungeonCid(int dungeonCid) {
        this.dungeonCid = dungeonCid;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public void setMemberDatas(List<MemberData> memberDatas) {
        this.memberDatas = memberDatas;
    }
    
    public List<MemberData> getMemberDatas() {
        return memberDatas;
    }
    
    public static class MemberData {
        private int pid;
        private int hurt;
        private boolean isMvp;

        public int getPid() {
            return pid;
        }

        public void setPid(int pid) {
            this.pid = pid;
        }

        public int getHurt() {
            return hurt;
        }

        public void setHurt(int hurt) {
            this.hurt = hurt;
        }

        public boolean isMvp() {
            return isMvp;
        }

        public void setMvp(boolean isMvp) {
            this.isMvp = isMvp;
        }
    }
}
