package logic.marquee.bean;

import gm.db.global.bean.MarqueeDBBean;

/**
 * 
 * @Description 跑马灯
 * @author LiuJiang
 * @date 2018年7月31日 下午6:19:18
 *
 */
public class MarqueeInfo {
    /** 唯一id */
    private long id;
    /** 正文 */
    private String body;
    /** 权重(决定同一时段时的显示顺序,值越大越优先) */
    private int weight;
    /** 循环次数 */
    private int loop_count;
    /** 间隔时间(秒) */
    private int interval_time;
    /** 上次执行时间 */
    private long lastDoTime;
    /** 下次执行时间 */
    private long nextDoTime;
    /** 已执行次数 */
    private int count;

    public MarqueeInfo(MarqueeDBBean bean) {
        this.id = bean.getId();
        this.body = bean.getBody();
        this.weight = bean.getWeight();
        this.loop_count = bean.getLoop_count();
        this.interval_time = bean.getInterval_time();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getLoop_count() {
        return loop_count;
    }

    public void setLoop_count(int loop_count) {
        this.loop_count = loop_count;
    }

    public int getInterval_time() {
        return interval_time;
    }

    public void setInterval_time(int interval_time) {
        this.interval_time = interval_time;
    }

    public long getLastDoTime() {
        return lastDoTime;
    }

    public void setLastDoTime(long lastDoTime) {
        this.lastDoTime = lastDoTime;
    }

    public long getNextDoTime() {
        return nextDoTime;
    }

    public void setNextDoTime(long nextDoTime) {
        this.nextDoTime = nextDoTime;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
