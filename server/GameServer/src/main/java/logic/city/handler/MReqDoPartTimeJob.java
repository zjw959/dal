package logic.city.handler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SNewBuildingMsg;
import org.game.protobuf.c2s.C2SNewBuildingMsg.ReqDoPartTimeJob;
import org.game.protobuf.s2c.S2CNewBuildingMsg;
import logic.city.PartTimeManager;
import logic.city.build.bean.JobRecord;
import logic.msgBuilder.NewBuildingMsgBuilder;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

/***
 * 
 * 请求兼职任务 code=2077
 * 
 * @author lihongji
 *
 */
@MHandler(messageClazz = C2SNewBuildingMsg.ReqDoPartTimeJob.class)
public class MReqDoPartTimeJob extends MessageHandler {

    private static final Logger LOGGER = Logger.getLogger(MReqDoPartTimeJob.class);

    @Override
    public void action() throws Exception {
        logic.character.bean.Player player = (logic.character.bean.Player) getGameData();
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        C2SNewBuildingMsg.ReqDoPartTimeJob req = (ReqDoPartTimeJob) getMessage().getData();
        int buildingId = req.getBuildingId();
        int jobId = req.getJobId();
        PartTimeManager manager = player.getPartTimeManager();
        JobRecord record = manager.createJobEvent(buildingId, jobId, player);
        S2CNewBuildingMsg.RespDoPartTimeJob.Builder builder =
                S2CNewBuildingMsg.RespDoPartTimeJob.newBuilder();
        S2CNewBuildingMsg.JobInfo.Builder jobInfo =
                NewBuildingMsgBuilder.packageIngJobInfo(record, buildingId);
        jobInfo.setEtime((int) (manager.getJobEvent().getEtime() / 1000));
        MessageUtils.send(player, builder.setJobInfo(jobInfo));
    }

}
