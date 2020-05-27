package logic.novelDating.structs;

public interface NovelDatingConst {
    /** 剧本条件类型：标记 */
    String SCRIPT_CONDITION_MARK = "sign";
    /** 剧本条件类型：道具 */
    String SCRIPT_CONDITION_ITEM = "items";
    /** 剧本条件类型：属性 */
    String SCRIPT_CONDITION_QUALITY = "quality";
    /** 剧本条件类型：任意标记 */
    String SCRIPT_CONDITION_ANY_MARK = "orsign";
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
}
