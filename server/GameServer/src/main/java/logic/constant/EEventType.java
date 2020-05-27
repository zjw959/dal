package logic.constant;

import event.IListener;
import logic.listener.BuildingEventListenner;
import logic.listener.DupFinishListener;
import logic.listener.EquipEventListener;
import logic.listener.HeroChangeEventListenner;
import logic.listener.ItemListener;
import logic.listener.LoginListener;
import logic.listener.OtherEventListenner;
import logic.listener.PlayerChangeEventListenner;
import logic.listener.RoleEventListenner;
import logic.listener.TaskAcceptListener;

/**
 * 事件定义枚举
 */
public enum EEventType {
    LEVEL_UP(1), // 升级事件
    /** 物品 */
    ITEM_EVENT(2, ItemListener.class),
    /** 登陆 */
    LOGIN(3, LoginListener.class),
    /** 通关关卡 */
    EQUIP(8, EquipEventListener.class),
    /** 通关关卡 */
    PASS_DUP(9, DupFinishListener.class),
    /** 任务激活(接取) */
    TASK_ACCEPT(10, TaskAcceptListener.class),
    /** 玩家变化 */
    PLAYER_CHANGE(11, PlayerChangeEventListenner.class),
    /** 精灵变化 */
    ROLE_CHANGE(12, RoleEventListenner.class),
    /** 英雄变化 */
    HERO_CHANGE(13, HeroChangeEventListenner.class),
    /** 天使变化 */
    ANGEL_CHANGE(14),
    /** 建筑任务事件 */
    BUILDING_EVENT(16, BuildingEventListenner.class),
    /** 动作事件 */
    OTHER_EVENT(17, OtherEventListenner.class), UNLOCK_BUILDING(18), // 建筑解锁
    MEDIA_EVENT(19), CHECK_UNLOCK_BUILDING(20), // 建筑检测解锁
    MAINDATING_ACTIVE(21), // 主线章节激活
    ACTIVITY_EVENT(22), // 活动事件
    OPEN_ACTIVITY_EVENT(24),//开启活动
    TRIGGER_CHECK(23);// 时装和道具触发检测
    private final int value;

    private Class<? extends IListener> clazz;

    EEventType(int value) {
        this.value = value;
    }

    EEventType(int value, Class<? extends IListener> clazz) {
        this.value = value;
        this.clazz = clazz;
    }

    public int value() {
        return value;
    }

    public Class<? extends IListener> getListener() {
        return clazz;
    }

    public static EEventType gerEEventType(int type) {
        for (EEventType eeven : EEventType.values()) {
            if (eeven.value == type) {
                return eeven;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        EEventType type = EEventType.gerEEventType(2);
        System.out.println(type.value);
    }

}
