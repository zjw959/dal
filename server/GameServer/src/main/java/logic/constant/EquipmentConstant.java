package logic.constant;

/**
 * 灵装常量
 * Created by fxf on 2017/9/26.
 */
public interface EquipmentConstant {

    /** 特殊属性数量   */
    int[] SPECIAL_ATTR_NUM = {0,0,1,2,3,4};

    /** 装备位置个数 */
    int EQUIP_POSITION_NUM = 3;
    
    /**
     * 未装备时位置为0
     */
    public static final int NO_EQUIP_POSITION = 0;
    
    public static int SPECIAL_ATTR_INDEX_TYPE = 0;
    public static int SPECIAL_ATTR_INDEX_VALUE = 1;
    public static int SPECIAL_ATTR_INDEX_CFG_ID = 2;
}
