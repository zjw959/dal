package logic.summon.resp;

import java.util.List;

import org.game.protobuf.s2c.S2CSummonMsg;
import org.game.protobuf.s2c.S2CSummonMsg.HistoryRecord;
import org.game.protobuf.s2c.S2CSummonMsg.Record;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CSummonMsg.ResHistoryRecord.MsgID.eMsgID_VALUE)
public class ResHistoryRecordEvent extends AbstractEvent {

    public ResHistoryRecordEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len > 0) {
            S2CSummonMsg.ResHistoryRecord resHistoryRecord = S2CSummonMsg.ResHistoryRecord.parseFrom(data);
            List<HistoryRecord> historyRecords = resHistoryRecord.getHistoryRecordsList();
            for(HistoryRecord historyRecord : historyRecords) {
                List<Record> records = historyRecord.getRecordsList();
                Log4jManager.getInstance().debug(robot.getWindow(),
                        "robot:" + robot.getName() + "抽卡[" + historyRecord.getType() + "]历史记录数" + records.size()); 
            }
        }
    }
    
}
