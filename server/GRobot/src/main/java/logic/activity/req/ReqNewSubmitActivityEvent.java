package logic.activity.req;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.game.protobuf.c2s.C2SActivityMsg;
import org.game.protobuf.s2c.S2CActivityMsg;
import org.game.protobuf.s2c.S2CActivityMsg.ActivityConfigMsg;
import org.game.protobuf.s2c.S2CActivityMsg.ActivityItemMsg;
import org.game.protobuf.s2c.S2CActivityMsg.ActivityProgressMsg;
import com.alibaba.fastjson.JSON;
import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.activity.ReqActivityOrder;
import logic.robot.entity.RobotPlayer;

/***
 * 
 * 提交活动
 * 
 * @author lihongji
 *
 */

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.ACTIVITY,
        order = ReqActivityOrder.REQ_GET_AWARD)
public class ReqNewSubmitActivityEvent extends AbstractEvent {

    /**
     * 活动商店
     */
    int activity_store = 1;

    /**
     * 活动任务
     */
    int activity_task = 2;

    public ReqNewSubmitActivityEvent(RobotThread robot) {
        super(robot, S2CActivityMsg.NewResultSubmitActivity.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {

        Log4jManager.getInstance().debug(robot.getWindow(),
                "robot:" + robot.getName() + "请求条目奖励~~~~~~~~~");

        ActivityProgressMsg award = awardList(robot.getPlayer());
        Map<Integer, Integer> props = propList(robot.getPlayer());
        
        if (award != null) {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "请求任务条目奖励");
            C2SActivityMsg.NewSubmitActivity.Builder builder =
                    C2SActivityMsg.NewSubmitActivity.newBuilder();
            builder.setActivitId(award.getId());
            builder.setActivitEntryId(award.getItemId());
            SMessage msg = new SMessage(C2SActivityMsg.NewSubmitActivity.MsgID.eMsgID_VALUE,
                    builder.build().toByteArray(), resOrder);
            sendMsg(msg);
        }
        else {
            if (props != null && props.size() > 0) {
                Log4jManager.getInstance().debug(robot.getWindow(),
                        "robot:" + robot.getName() + "请求商品条目奖励");
                C2SActivityMsg.NewSubmitActivity.Builder builder =
                        C2SActivityMsg.NewSubmitActivity.newBuilder();
                int id = 0;
                int itemId = 0;
                for (Map.Entry<Integer, Integer> entry : props.entrySet()) {
                    id = entry.getKey();
                    itemId = entry.getValue();
                }
                builder.setActivitId(id);
                builder.setActivitEntryId(itemId);
                builder.setExtendData(1 + "");
                SMessage msg = new SMessage(C2SActivityMsg.NewSubmitActivity.MsgID.eMsgID_VALUE,
                        builder.build().toByteArray(), resOrder);
                sendMsg(msg);
            } else
                robotSkipRun();
            
        }
    }

    public ArrayList<Integer> getRecord(RobotPlayer player) {
        /** 判断有没有活动 有没有条目 **/
        if (player.getActivitys() == null || player.getActivitys().size() <= 0)
            return null;
        if (player.getActivityItems() == null || player.getActivityItems().size() <= 0)
            return null;
        return null;
    }

    /** 获取活动信息 **/
    public List<ActivityConfigMsg> getActivityByType(RobotPlayer player, int type) {
        List<ActivityConfigMsg> list = new ArrayList<>();
        for (ActivityConfigMsg config : player.getActivitys()) {
            if (config.getActivityType() == type)
                list.add(config);
        }
        return list;
    }


    /** 根据类型获取 **/
    public List<ActivityItemMsg> getActivityItemMsgByType(RobotPlayer player, int type) {
        List<ActivityItemMsg> list = new ArrayList<>();
        for (ActivityItemMsg items : player.getActivityItems()) {
            if (items.getType() == type) {
                list.add(items);
            }
        }
        return list;
    }

    /** 检测是否有奖励可以领取 **/
    public ActivityProgressMsg awardList(RobotPlayer player) {
        for (ActivityProgressMsg progress : player.getActivityRecords()) {
            if (progress.getStatus() == 1)
                return progress;
        }
        return null;
    }


    /** 根据id获取当前条目 **/
    public ActivityItemMsg getActivityItemMsgById(List<ActivityItemMsg> itemsList, int id) {
        for (ActivityItemMsg msg : itemsList) {

            if (id == msg.getId())
                return msg;
        }
        return null;

    }


    /** 检测是否有奖励可以领取 **/
    public Map<Integer, Integer> propList(RobotPlayer player) {
        List<ActivityConfigMsg> list = getActivityByType(player, activity_store);
        if (list == null || list.size() <= 0)
            return null;
        List<ActivityItemMsg> itemsList = getActivityItemMsgByType(player, activity_store);
        Map<Integer, Integer> awardMap = new HashMap<Integer, Integer>();

        for (ActivityConfigMsg msg : list) {
            for (ActivityItemMsg items : itemsList) {
                if (msg.getItemsList().contains(items.getId())) {
                    ActivityProgressMsg progress =
                            getProgressMsg(player, msg.getId(), items.getId());
                    if (progress == null || progress.getProgress() < 10) {
                        if(progress==null) 
                        {
                            awardMap.put(msg.getId(), items.getId());
                            return awardMap;
                        }
                        if(items.getExtendData()!=null && items.getExtendData().length()>0)
                        {
                            Map<String, Integer> condition=getAwardMap(items.getExtendData());
                            int limitVal=condition.get("limitVal");
                            if(condition.get("limitType")==0)
                            {
                                awardMap.put(msg.getId(), items.getId());
                                return awardMap;
                            }
                            // 限购（0.不限购。1.每日限购。2.总数限购。3.全服每日限购。5.全服总数限购）
                            if(condition.get("limitType")==1)
                            {
                               if(progress.getProgress()<limitVal)
                               {
                                   String ex=progress.getExtend();
                                   if(ex!=null && ex.length()>0)
                                   {
                                       int ext=Integer.parseInt(ex);
                                       if(ext>=limitVal) continue;
                                   }
                                   awardMap.put(msg.getId(), items.getId());
                                   return awardMap;
                               }
                               else
                                   continue;
                            }
                            if(condition.get("limitType")==2)
                            {
                                if(progress.getProgress()<limitVal)
                                {
                                    String ex=progress.getExtend();
                                    if(ex!=null && ex.length()>0)
                                    {
                                        int ext=Integer.parseInt(ex);
                                        if(ext>=limitVal) continue;
                                    }
                                    awardMap.put(msg.getId(), items.getId());
                                    return awardMap;
                                } 
                                else
                                    continue;
                            }
                            if(condition.get("limitType")==3)
                            {
                                if(progress.getProgress()<limitVal)
                                {
                                    String ex=progress.getExtend();
                                    if(ex!=null && ex.length()>0)
                                    {
                                        int ext=Integer.parseInt(ex);
                                        if(ext>=limitVal) continue;
                                    }
                                    awardMap.put(msg.getId(), items.getId());
                                    return awardMap;
                                } 
                                else
                                    continue;
                            }
                            if(condition.get("limitType")==4)
                            {
                                if(progress.getProgress()<limitVal)
                                {
                                    String ex=progress.getExtend();
                                    if(ex!=null && ex.length()>0)
                                    {
                                        int ext=Integer.parseInt(ex);
                                        if(ext>=limitVal) continue;
                                    }
                                    awardMap.put(msg.getId(), items.getId());
                                    return awardMap;
                                } 
                                else
                                    continue;
                            }
                            if(condition.get("limitType")==5)
                            {
                                if(progress.getProgress()<limitVal)
                                {
                                    String ex=progress.getExtend();
                                    if(ex!=null && ex.length()>0)
                                    {
                                        int ext=Integer.parseInt(ex);
                                        if(ext>=limitVal) continue;
                                    }
                                    awardMap.put(msg.getId(), items.getId());
                                    return awardMap;
                                } 
                                else
                                    continue;
                            }
                        }
//                        else
//                        {
//                            awardMap.put(msg.getId(), items.getId());
//                            return awardMap;
//                        }
                    }
                }
            }
        }
        return null;
    }

    public ActivityProgressMsg getProgressMsg(RobotPlayer player, int id, int itemsId) {
        if (player.getActivityRecords() == null)
            return null;
        for (ActivityProgressMsg msg : player.getActivityRecords()) {
            if (msg.getId() == id && msg.getItemId() == itemsId)
                return msg;
        }
        return null;
    }
    
    /** 获取当前Map奖励 **/
    @SuppressWarnings("unchecked")
    public Map<String, Integer> getAwardMap(String awardInfo) {
        return (Map<String, Integer>) JSON.parseObject(awardInfo, Map.class);
    }
    
}
