package gm.db.mail.bean;

import java.util.Date;

/**
 * 
 * @Description 邮件基类
 * @author LiuJiang
 * @date 2018年7月2日 下午5:33:59
 *
 */
public class BaseMailDBBean {
    /** 唯一id */
    private long id;
    /** 发送者id */
    private long sender_id;
    /** 接收者id */
    private long receiver_id;
    /** 邮件状态 */
    private int status;
    /** 标题 */
    private String title;
    /** 正文 */
    private String body;
    /** 附件 */
    private String items;
    /** 附加信息 */
    private String info;
    /** 创建时间 */
    private Date create_date;
    /** 最近修改时间 */
    private Date modify_date;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSender_id() {
        return sender_id;
    }

    public void setSender_id(long sender_id) {
        this.sender_id = sender_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(long receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    public Date getModify_date() {
        return modify_date;
    }

    public void setModify_date(Date modify_date) {
        this.modify_date = modify_date;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    protected BaseMailDBBean copy(BaseMailDBBean bean) {
        bean.setId(id);
        bean.setSender_id(sender_id);
        bean.setReceiver_id(receiver_id);
        bean.setStatus(status);
        bean.setTitle(title);
        bean.setBody(body);
        bean.setItems(items);
        bean.setInfo(info);
        bean.setCreate_date(new Date(create_date.getTime()));
        bean.setModify_date(new Date(modify_date.getTime()));
        return bean;
    }
}
