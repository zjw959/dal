package thread;

import logic.chasm.bean.TeamInfo;
import logic.support.LogicScriptsUtils;

public class TeamProcessor extends AbstractRoomProcessor<TeamInfo> {

    public TeamProcessor(int lineId) {
        super(TeamProcessor.class.getSimpleName() + "_" + lineId, lineId);
    }

    @Override
    public Long getId(TeamInfo t) {
        return Long.valueOf(t.getTeamId());
    }

    @Override
    public void addRoom(TeamInfo room) {
        room.setCreateTime(System.currentTimeMillis());
        this.rooms.put(getId(room), room);
    }
    
    @Override
    protected void tick() {
        LogicScriptsUtils.getChasmScript().teamMatchTick(rooms, this);
    }

    public void executeHandler(BaseHandler handler) {
        super.executeHandler(handler);
    }

    @Override
    public void shutdown() {
        for (TeamInfo teamInfo : rooms.values()) {
            LogicScriptsUtils.getChasmScript().removeTeamRedis(teamInfo.getTeamId());
        }
    }
}
