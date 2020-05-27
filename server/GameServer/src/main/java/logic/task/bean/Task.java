package logic.task.bean;

/**
 * 任务
 */
public class Task {


    /**
     * 配置id
     */
    private int cid;

    /**
     * 进展(进度)
     */
    private int progress;

    /**
     * 状态
     */
    private int status;

    /**
     * 最后重置时间
     */
    private long date;

    /**
     * 是否完成
     */
    private int finish;

    @Deprecated
    public static Task builder() {
        Task task = new Task();
        return task;
    }

    @Deprecated
    public Task() {
        super();
    }

    public Task(int cid, int progress, int status, long date, int finish) {
        super();
        this.cid = cid;
        this.progress = progress;
        this.status = status;
        this.date = date;
        this.finish = finish;
    }


    /**
     * 配置id
     */
    public int getCid() {
        return cid;
    }

    /**
     * 配置id
     */
    public Task setCid(int cid) {
        this.cid = cid;
        return this;
    }


    /**
     * 进展(进度)
     */
    public int getProgress() {
        return progress;
    }

    /**
     * 进展(进度)
     */
    public Task setProgress(int progress) {
        this.progress = progress;
        return this;
    }

    /**
     * 状态
     */
    public int getStatus() {
        return status;
    }

    /**
     * 状态
     */
    public Task setStatus(int status) {
        this.status = status;
        return this;
    }


    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getFinish() {
        return finish;
    }

    public void setFinish(int finish) {
        this.finish = finish;
    }

}
