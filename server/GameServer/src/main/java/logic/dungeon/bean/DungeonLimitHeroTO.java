package logic.dungeon.bean;


/**
 * 关卡限定英雄传输对象
 * 
 * @author Alan
 *
 */
public class DungeonLimitHeroTO {

    /**
     * 关卡英雄列表类型
     * 
     * @see logic.constant.DungeonConstant#DUNGEON_HERO_LIST_NORMAL DUNGEON_HERO_LIST_NORMAL
     * @see logic.constant.DungeonConstant#DUNGEON_HERO_LIST_LIMITED DUNGEON_HERO_LIST_LIMITED
     */
    int type;
    /**
     * 英雄id
     * <p>
     * 根据类型分布于不同配置表的数据
     */
    int cid;


    public DungeonLimitHeroTO(int type, int cid) {
        super();
        this.type = type;
        this.cid = cid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }


}
