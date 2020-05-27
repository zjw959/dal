package logic.basecore;

import db.game.bean.PlayerDBBean;

public interface IRoleBeanConverter {
    /**
     * 数据存储
     * 
     * 这里的数据储存是指把数据寄存在roleBean中
     * 
     * @return
     */
    void toData(PlayerDBBean roleBean);

    /**
     * 对象初始化
     *
     * 这里是指从内存数据转化,和fromJson类似
     * 
     * 并不是指从DB中获取
     * 
     * @param roleBean
     */
    void fromData(PlayerDBBean roleBean);

    /** 单玩家的数据库加载 **/
    static void loadFromDB(PlayerDBBean roleBean) {}

    /** 单玩家的数据库回存 **/
    static void saveToDB(PlayerDBBean roleBean) {}
}

