package db.game.bean;

import java.io.Serializable;

public class PlayerIdBean implements Serializable, Cloneable {
    private static final long serialVersionUID = 8408403884630780690L;

    private int minid;
    private int maxid;


    public int getMinid() {
        return minid;
    }


    public void setMinid(int minid) {
        this.minid = minid;
    }


    public int getMaxid() {
        return maxid;
    }


    public void setMaxid(int maxid) {
        this.maxid = maxid;
    }


    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
