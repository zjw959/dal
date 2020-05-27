package logic.chasm.handler;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CFightMsg.NetFrame;
import org.game.protobuf.s2c.S2CFightMsg.RespPullNetFrame;

import kafka.service.F2GProductService;
import kafka.team.param.f2g.F2GEndFightParam;
import kafka.team.param.f2g.F2GPrintAttributeHandlerParam;
import logic.chasm.InnerHandler.LDismissTeamHandler;
import logic.chasm.bean.TeamInfo;
import logic.chasm.bean.TeamMember;
import room.FightRoom;
import room.FightRoomManager;
import thread.BaseHandler;
import thread.FightRoomProcessor;
import thread.FightRoomProcessorManager;
import thread.TeamProcessor;
import thread.TeamProcessorManager;
import utils.ExceptionEx;
import utils.FileEx;

public class DestroyFightRoomHandler extends BaseHandler {
    private final static Logger LOGGER = Logger.getLogger(DestroyFightRoomHandler.class);
    private FightRoom room;
    private boolean isWin;

    public DestroyFightRoomHandler(FightRoom room, boolean isWin) {
        this.room = room;
        this.isWin = isWin;
    }

    @Override
    public void action() throws Exception {
        TeamInfo teamInfo = room.getTeamInfo();
        TeamProcessor teamProcessor = (TeamProcessor) TeamProcessorManager.getInstance()
                .getRoomProcessor(teamInfo.getProcessorId());
        teamProcessor.executeHandler(new LDismissTeamHandler(teamInfo));

        FightRoomManager.removeRoom(room);
        FightRoomProcessor roomProcessor = (FightRoomProcessor) FightRoomProcessorManager
                .getInstance().getRoomProcessor(room.getProcessorId());
        roomProcessor.removeRoom(room);

        if (!isWin) {
            long fightTime = (long) (room.getSyncCount() * 66.67d);
            Map<Integer, TeamMember> members = teamInfo.getMembers();
            for (Map.Entry<Integer, TeamMember> entry : members.entrySet()) {
                TeamMember teamMember = entry.getValue();
                if(teamMember.isConnect()) {
                    F2GEndFightParam param = new F2GEndFightParam();
                    param.setPlayerId(teamMember.getPid());
                    param.setWin(false);
                    param.setFightTime((int) (fightTime / 1000));
                    try {
                        F2GProductService.getDefault().sendMsg(teamMember.getServerId(), param);
                    } catch (Exception e) {
                        LOGGER.error(ExceptionEx.e2s(e));
                    }
                }
            }
        }
        
        if (!room.isSameMemberWin()) {
            StringBuilder fileName = new StringBuilder();
            fileName.append("./logs/win" + room.getId());
            for (TeamMember member : room.getTeamInfo().getMembers().values()) {
                fileName.append("_" + member.getPid() + "_" + member.getServerId());
                F2GPrintAttributeHandlerParam param = new F2GPrintAttributeHandlerParam();
                param.setPlayerId(member.getPid());
                param.setHeroCid(member.getHeroCid());
                param.setTeamId(room.getId());
                try {
                    F2GProductService.getDefault().sendMsg(member.getServerId(), param);
                } catch (Exception e) {
                    LOGGER.error(ExceptionEx.e2s(e));
                }
            }
            List<NetFrame> netFrames = room.getNetFrames();
            int toIndex = netFrames.size();
            List<NetFrame> netFrameList = netFrames.subList(0, toIndex);;
            RespPullNetFrame.Builder respPullNetFrameBuilder = RespPullNetFrame.newBuilder();
            for (NetFrame frame : netFrameList) {
                respPullNetFrameBuilder.addNetFrames(frame);
            }
            try {
                FileEx.write(fileName.toString(), respPullNetFrameBuilder.build().toByteArray());
            } catch (IOException e) {
                LOGGER.error("isSameMemberWin=false print error!fileName=" + fileName.toString());
            }
        }
    }

}
