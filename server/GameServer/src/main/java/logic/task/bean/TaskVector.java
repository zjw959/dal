package logic.task.bean;

import org.game.protobuf.s2c.S2CShareMsg.ChangeType;
import org.game.protobuf.s2c.S2CTaskMsg.TaskInfo;

import logic.character.bean.Player;
import logic.constant.TaskConstant;
import logic.msgBuilder.TaskMsgBuilder;

public class TaskVector implements GameEventVector {
    private Task task;

    public TaskVector(Task task) {
        super();
        this.task = task;
    }

    @Override
    public int getProgress() {
        return task.getProgress();
    }

    @Override
    public void setProgress(int progress) {
        task.setProgress(progress);
    }

    @Override
    public void trigger(int maxProcess) {
        if (task.getProgress() >= maxProcess && task.getStatus() != TaskConstant.STATUS_FINISH) {
            task.setStatus(TaskConstant.STATUS_FINISH);
        }
        // task.update();
    }

    @Override
    public TaskInfo notify(Player player) {
        // List<TaskInfo> taskInfos = Lists.newArrayList();
        // taskInfos.add(TaskMsgBuilder.createTaskInfo(player, ChangeType.UPDATE, task));
        // TaskMsgBuilder.notifyTaskInfos(player, taskInfos);
        TaskInfo info = TaskMsgBuilder.createTaskInfo(player, ChangeType.UPDATE, task);
        return info;
    }

    @Override
    public Task getTask() {
        return task;
    }
}
