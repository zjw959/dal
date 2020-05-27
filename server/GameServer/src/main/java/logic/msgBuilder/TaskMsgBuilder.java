package logic.msgBuilder;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.game.protobuf.s2c.S2CShareMsg.ChangeType;
import org.game.protobuf.s2c.S2CTaskMsg;
import org.game.protobuf.s2c.S2CTaskMsg.RespTasks;
import org.game.protobuf.s2c.S2CTaskMsg.TaskInfo;

import com.google.common.collect.Lists;

import cn.hutool.core.util.RandomUtil;
import data.GameDataManager;
import data.bean.TaskCfgBean;
import logic.character.bean.Player;
import logic.constant.TaskConstant;
import logic.support.MessageUtils;
import logic.task.bean.Task;

/**
 * @author : DengYing
 * @CreateDate : 2017年8月15日 下午4:21:17
 * @Description ：任务消息封装
 */
public class TaskMsgBuilder {

    public static TaskInfo createTaskInfo(Player player, ChangeType changeType, Task task) {
        TaskInfo.Builder b = TaskInfo.newBuilder();
        b.setCt(changeType);
        b.setId(String.valueOf(0L));
        b.setCid(task.getCid());
        b.setProgress(task.getProgress());
        b.setStatus(task.getStatus());
        return b.build();
    }

    public static TaskInfo createFinishTaskInfo(Player player, ChangeType changeType,
            TaskCfgBean cfg) {
        TaskInfo.Builder b = TaskInfo.newBuilder();
        b.setCt(changeType);
        b.setId(RandomUtil.randomUUID());
        b.setCid(cfg.getId());
        b.setProgress(cfg.getProgress());
        b.setStatus(TaskConstant.STATUS_RECEIVE);
        return b.build();
    }



    public static List<TaskInfo> createTaskInfoList(Player player, ChangeType type,
            Map<Integer, Task> tasks) {
        List<TaskInfo> taskInfos = Lists.newArrayList();
        for (Task task : tasks.values()) {
            taskInfos.add(createTaskInfo(player, type, task));
        }
        return taskInfos;
    }

    public static List<TaskInfo> createTaskInfoList(Player player, ChangeType type,
            List<Task> tasks) {
        List<TaskInfo> taskInfos = Lists.newArrayList();
        for (Task task : tasks) {
            taskInfos.add(createTaskInfo(player, type, task));
        }
        return taskInfos;
    }

    public static S2CTaskMsg.RespTasks.Builder createRespTasks(Player player,
            Map<Integer, Task> allMap, ChangeType type) {
        S2CTaskMsg.RespTasks.Builder builder = S2CTaskMsg.RespTasks.newBuilder();
        builder.addAllTaks(createTaskInfoList(player, type, allMap));
        // builder.addAllTaks(createTaskInfoList(player, type,
        // player.getTaskManager().getDailyMap()));
        // 完成任务封装
        Map<Integer, Integer> finishTasks = player.getTaskManager().getFinishMap();
        for (Entry<Integer, Integer> e : finishTasks.entrySet()) {

            TaskCfgBean cfg = GameDataManager.getTaskCfgBean(e.getKey());
            if (cfg != null && cfg.getResetType() == TaskConstant.RESET_NO) {
                builder.addTaks(createFinishTaskInfo(player, type, cfg));
            }
        }
        return builder;
    }

    /**
     * 通知任务更新
     * 
     * @param player
     * @param tasks
     */
    public static void notifyTaskInfos(Player player, List<TaskInfo> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return;
        }
        RespTasks.Builder builder = RespTasks.newBuilder();
        builder.addAllTaks(tasks);
        MessageUtils.send(player, builder);
    }
}
