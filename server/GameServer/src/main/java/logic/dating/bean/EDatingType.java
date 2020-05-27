package logic.dating.bean;

import logic.constant.ConstDefine;

/***
 * 
 * 约会类型
 * 
 * @author lihongji
 *
 */
public enum EDatingType {


    /**
     * 约会类型：主线约会
     */
    MAIN(1),
    /**
     * 约会类型：日常约会
     */
    DAILY(2),
    /**
     * 约会类型：预定约会
     */
    RESERVE(3, CityDatingBean.class),
    /**
     * 约会类型：事件触发约会
     */
    TRIGGER(4),
    /**
     * 约会类型：出游约会
     */
    OUT(6, CityDatingBean.class),
    /**
     * 约会类型：副本约会
     */
    DUNGEON(7, DungeonDatingBean.class),
    /***
     * 手机约会
     */
    PHONE(10);
    
    private int value;
    private Class<? extends CurrentDatingBean> clazz;

    EDatingType(int value) {
        _init(value, DatingBaseBean.class);
    }

    EDatingType(int value, Class<? extends CurrentDatingBean> clazz) {
        _init(value, clazz);
    }

    private void _init(int value, Class<? extends CurrentDatingBean> clazz) {
        this.value = value;
        this.clazz = clazz;
    }


    public Class<? extends CurrentDatingBean> getClazz() {
        return clazz;
    }


    public static EDatingType datingType(int typeValue) {
        for (EDatingType type : EDatingType.values()) {
            if (type.value == typeValue) {
                return type;
            }
        }
        throw new IllegalArgumentException(
                ConstDefine.LOG_ERROR_CONFIG_PREFIX + "undefine item type : " + typeValue);
    }
}
