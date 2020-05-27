package logic.task.bean;

import java.util.Map;

import com.google.common.collect.Maps;

public class GameEventStack {
    /** 事件ID */
    private int eventId;
    /** 条件ID */
    private int conditionType;
    /** 条件参数 */
    @SuppressWarnings("rawtypes")
    private Map params;
    /** 当前进度 */
    private int progress;
    /** 条件进度 */
    private int maxProgress;
    /** 条件是否触发 */
    private boolean trigger;

    @SuppressWarnings("rawtypes")
    public GameEventStack(int eventId, int conditionType, Map params, int progress, int maxProgress,
            boolean trigger) {
        super();
        this.eventId = eventId;
        this.conditionType = conditionType;
        this.params = params;
        if (this.params == null) {
            this.params = Maps.newHashMap();
        }
        this.progress = progress;
        this.maxProgress = maxProgress;
        this.trigger = trigger;
    }


    public int getEventId() {
        return eventId;
    }


    public void setEventId(int eventId) {
        this.eventId = eventId;
    }


    public int getConditionType() {
        return conditionType;
    }

    public void setConditionType(int conditionType) {
        this.conditionType = conditionType;
    }

    @SuppressWarnings("rawtypes")
    public Map getParams() {
        return params;
    }

    @SuppressWarnings("rawtypes")
    public void setParams(Map params) {
        this.params = params;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        this.trigger = true;
    }

    public void changeProgress(int progress) {
        this.progress += progress;
        this.trigger = true;
    }

    public boolean isTrigger(GameEventVector vector, int process) {
        if (trigger) {
            // 进度继承向下传递
            // int progress = Math.min(process, getMaxProgress());
            // 修改进度
            vector.setProgress(progress);
        }
        return trigger;
    }

    public boolean isTrigger() {
        return trigger;
    }

    public void setTrigger(boolean trigger) {
        this.trigger = trigger;
    }


    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }
}
