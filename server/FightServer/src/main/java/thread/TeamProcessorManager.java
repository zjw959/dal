package thread;

import java.util.Collection;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import logic.chasm.bean.TeamInfo;
import logic.chasm.bean.TeamMember;

public class TeamProcessorManager extends AbstractRoomProcessorManager {

    protected TeamProcessorManager() {
        super(TeamProcessor.class);
    }

    protected TeamProcessorManager(int processorCount) {
        super(processorCount, TeamProcessor.class);
    }

    public static TeamProcessorManager getInstance() {
        return Singleton.INSTANCE.getProcessor();
    }

    private enum Singleton {
        INSTANCE;
        TeamProcessorManager processor;

        Singleton() {
            int proNum = Runtime.getRuntime().availableProcessors();
            proNum = (proNum / 2);
            if (proNum < 1) {
                proNum = 1;
            }
            this.processor = new TeamProcessorManager(proNum);
        }

        TeamProcessorManager getProcessor() {
            return processor;
        }
    }

    public void stop() {
        super.stop();
    }

    public TeamInfo getTeamInfo(long key){
        Collection<AbstractRoomProcessor> processors = getAllProcessor();
        for (AbstractRoomProcessor processor : processors) {
            TeamInfo teamInfo = (TeamInfo) processor.getRoom(key);
            if (teamInfo != null)
                return teamInfo;
        }
        return null;
    }

    public JSONArray getAllTeamInfoForJson() {
        JSONArray array = new JSONArray();
        Collection<AbstractRoomProcessor> processors = getAllProcessor();
        for (AbstractRoomProcessor processor : processors) {
            Collection<TeamInfo> roomList = processor.getRooms();
            for (TeamInfo teamInfo : roomList) {
                JSONObject teamJson = new JSONObject();
                teamJson.put("teamId", teamInfo.getTeamId());
                teamJson.put("processorId", teamInfo.getProcessorId());
                teamJson.put("status", teamInfo.getStatus().getStatus());
                teamJson.put("leaderId", teamInfo.getLeaderId());
                teamJson.put("members", getTeamMemberId(teamInfo));
                teamJson.put("dungeonCid", teamInfo.getDungeonCid());
                array.add(teamJson);
            }
        }
        return array;
    }

    private String getTeamMemberId(TeamInfo teamInfo) {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (TeamMember teamMember : teamInfo.getMembers().values()) {
            if (!isFirst) {
                sb.append(",");
            } else {
                isFirst = false;
            }
            sb.append(teamMember.getPid());
        }
        return sb.toString();
    }

    public int getTeamNum() {
        int teamNum = 0;
        Collection<AbstractRoomProcessor> processors = getAllProcessor();
        for (AbstractRoomProcessor processor : processors) {
            Collection<TeamInfo> teamInfos = processor.getRooms();
            teamNum = teamNum + teamInfos.size();
        }
        return teamNum;
    }

    public void shutdown() {
        Collection<AbstractRoomProcessor> processors = getAllProcessor();
        for (AbstractRoomProcessor processor : processors) {
            processor.shutdown();
        }
    }

}
