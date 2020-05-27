package logic.task;

import java.util.List;
import java.util.Map;

import org.game.protobuf.s2c.S2CShareMsg.ChangeType;

import data.bean.TaskCfgBean;
import event.Event;
import logic.character.bean.Player;
import logic.task.bean.Task;
import script.IScript;

public abstract class ITaskScript implements IScript {

    protected abstract void reqTasks(Player player, ChangeType type);

    protected abstract void submitTasks(Player player, int taskCid);

    protected abstract Task acceptTask(Player player, TaskCfgBean taskCfg);

    protected abstract Map<Integer, Task> getTask(Player player, boolean isInit);

    protected abstract void eventPerformed(Player player, Event event);

    protected abstract List<Task> checkTimeOutTasks(TaskManager manager);

    protected abstract Map<ChangeType, List<Task>> resetTasks(Player player);
}
