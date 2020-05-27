package logic.task.bean;

import org.game.protobuf.s2c.S2CTaskMsg.TaskInfo;

import logic.character.bean.Player;

/**
 * @author : DengYing
 * @CreateDate : 2018年3月6日 下午9:06:04
 * @Description ：事件数据载体
 */
public interface GameEventVector {
    int getProgress();

    void setProgress(int progress);

    void trigger(int maxProcess);

    TaskInfo notify(Player player);

    Task getTask();
}
