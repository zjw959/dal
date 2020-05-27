package logic.favor.structs;

public interface FavorDatingConst {
    /** 剧本条件类型：标记 */
    String SCRIPT_CONDITION_MARK = "sign";
    /** 剧本条件类型：道具 */
    String SCRIPT_CONDITION_ITEM = "items";
    /** 剧本条件类型：属性 */
    String SCRIPT_CONDITION_QUALITY = "quality";
    /** 剧本条件类型：任意标记 */
    String SCRIPT_CONDITION_ANY_MARK = "orsign";
    /** 选项类型：拥有精灵 */
    String DATING_CONDITION_WON_ROLE = "ownRole";
    /** 选项类型： 未拥有精灵 */
    String DATING_CONDITION_NOT_WON_ROLE = "notRole";
    /** 选项类型：指定参数值 */
    String DATING_CONDITION_MUCH_VARIABLE = "muchVariable";
    /** 选项类型：指定参数值 */
    String DATING_CONDITION_LESS_VARIABLE = "lessVariable";
    /** 选项类型：精灵好感度大于 等于 */
    String MUCH_ROLE_FAVOR = "muchRoleFavor";
    /** 选项类型：精灵好感度小于 */
    String LESS_ROLE_FAVOR = "lessRoleFavor";
    /** 选项类型：精灵心情值大于 等于 */
    String MUCH_ROLE_MOOD = "muchRoleMood";
    /** 选项类型：精灵心情值小于 */
    String LESS_ROLE_MOOD = "lessRoleMood";
    /** 选项类型：拥有物品 */
    String OWN_ITEM = "ownItem";
    /** 选项类型：没有物品 */
    String NOT_ITEM = "notItem";
    /** 选项类型：拥有时装 */
    String OWN_DRESS = "ownDress";
    /** 选项类型：没有时装 */
    String NOT_DRESS = "notDress";
    /** 选项类型：拥有结局 */
    String OWN_END = "ownEnd1";
    /** 选项类型：没有结局 */
    String NOT_END = "notEnd1";
    /** 选项类型：拥有标记 */
    String OWN_SIGN = "ownSign";
    /** 选项类型：没有标记 */
    String NOT_SIGN = "notSign";
    /** 选项类型：通关主线章节 */
    String CHECK_POINT = "checkpoint";
    /** 选项类型：概率出现 */
    String CHANCE = "chance";
    /** 选项类型：选择过选项 */
    String OWN_JUMP = "ownJump";
    /** 选项类型:没有择过选项 */
    String NOT_JUMP = "notJump";
    /** 事件信息类型：提示 */
    int EVENT_MESSAGE_TYPE_TIPS = 1;
    /** 事件信息类型：选择 */
    int EVENT_MESSAGE_TYPE_CHOICE = 2;
    /** 事件信息类型：直接奖励 */
    int EVENT_MESSAGE_TYPE_REWARD = 3;

    /** 事件信息选择1 */
    int EVENT_MESSAGE_CHOICE_1 = 1;
    /** 事件信息选择2 */
    int EVENT_MESSAGE_CHOICE_2 = 2;
    
    /** 建筑事件类型：剧本 */
    int BUILDING_EVENT_TYPE_SCRIPT = 1;
    /** 建筑事件类型：信息 */
    int BUILDING_EVENT_TYPE_MSG = 2;

    String FAVOR_DATING = "FavorDating";
    /** 主线类型 1 主线 */
    int FAVOR_TYPE = 1;
    /** 主线类型 2 奖励 */
    int FAVOR_TYPE_REWARD = 2;
    /** 条件类型 1 好感度 */
    int CONDI_TYPE = 1;
    /** 条件类型 2 关卡 */
    int CONDI_DUNGEON = 2;

    /** 主线约会类型 */
    int DATING_TYPE_FAVOR = 2;
    /** 副本约会类型 */
    int DATING_TYPE_NOVEL = 3;
}
