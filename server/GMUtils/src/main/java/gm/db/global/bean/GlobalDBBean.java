package gm.db.global.bean;


/**
 * 
 * @Description 全局Bean
 * @author LiuJiang
 * @date 2018年8月24日 上午10:24:07
 *
 */
public class GlobalDBBean {
    private int id;
    private long longValue;
    private String stringValue;

    public GlobalDBBean() {}


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getLongValue() {
        return longValue;
    }

    public void setLongValue(long longValue) {
        this.longValue = longValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }
}
