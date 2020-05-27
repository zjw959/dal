package gm.db.global.bean;

import java.util.Date;

/**
 * 
 * @Description 跑马灯
 * @author LiuJiang
 * @date 2018年7月30日 下午5:33:59
 *
 */
public class MarqueeDBBean {
    /** 唯一id */
    private long id;
    /** 正文 */
    private String body;
    /** 权重(决定同一时段时的显示顺序,值越大越优先) */
    private int weight;
    /** 循环次数限制(0-不限制) */
    private int loop_count;
    /** 循环间隔时间(秒) */
    private int interval_time;
    /** 创建时间 */
    private Date create_date;
    /** 开始时间 */
    private Date begin_date;
    /** 结束时间 */
    private Date end_date;

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

    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    public Date getBegin_date() {
        return begin_date;
    }

    public void setBegin_date(Date begin_date) {
        this.begin_date = begin_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

}
