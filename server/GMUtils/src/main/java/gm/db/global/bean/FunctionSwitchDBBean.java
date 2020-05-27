package gm.db.global.bean;


/**
 * 
 * @Description 功能开关
 * @author LiuJiang
 * @date 2018年8月8日 下午5:33:59
 *
 */
public class FunctionSwitchDBBean {
    /** 功能类型 */
    private int type;
    /** 状态（0-关闭 1-开启） */
    private int status;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
