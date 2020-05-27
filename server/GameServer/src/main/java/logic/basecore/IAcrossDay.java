package logic.basecore;

import java.util.Date;

import logic.constant.EAcrossDayType;

/**
 * 跨天/跨周
 * 
 * 当前时间大于跨天时间则执行跨天逻辑
 * 
 * 当前有0点与6点(配置)两种跨天
 * 
 * 23点系统关闭,1点开服.那么0点跨天.用户登陆后会执行.6点跨天需要6点以后才会执行.第3天以后的任意一天登陆,都只会执行一次.
 * 
 * 注意.
 * 
 * 当前系统的登陆执行依赖于ILoginDataLazyPull. 当前客户端还不支持ILoginDataLazyPull.由服务器自发推送处理一次登录初始化.
 *
 * 主要是用于默认第一次获取该manager中的信息时,默认已经执行了跨天.
 */
public interface IAcrossDay extends ITick, ILoginInit {
    @Override
    default void loginInit() {

    }

    @Override
    default void tick() {}

    default boolean isAcrossSysDay(PlayerBaseFunctionManager manager, long currentTime) {
        return currentTime >= manager.getSysAcrossDay();
    }

    default boolean isAcrossGameDay(PlayerBaseFunctionManager manager, long currentTime) {
        return currentTime >= manager.getGameAcrossDay();
    }

    default boolean isAcrossSysWeek(PlayerBaseFunctionManager manager, long currentTime) {
        return currentTime >= manager.getSysAcrossWeek();
    }

    default boolean isAcrossGameWeek(PlayerBaseFunctionManager manager, long currentTime) {
        return currentTime >= manager.getGameAcrossWeek();
    }

    default boolean isAcrossSysMonth(PlayerBaseFunctionManager manager, long currentTime) {
        return currentTime >= manager.getSysAcrossMonth();
    }

    default boolean isAcrossGameMonth(PlayerBaseFunctionManager manager, long currentTime) {
        return currentTime >= manager.getGameAcrossMonth();
    }

    public default void tickAcrossDay(PlayerBaseFunctionManager manager, long currentTime,
            boolean isNotify) {
        if (isAcrossSysDay(manager, currentTime)) {
            this.acrossDay(EAcrossDayType.SYS_ACROSS_DAY, isNotify);
            manager.setSysAcrossDay(manager.getSysNextAcrossDay(new Date(currentTime)));
        }
        if (isAcrossGameDay(manager, currentTime)) {
            this.acrossDay(EAcrossDayType.GAME_ACROSS_DAY, isNotify);
            manager.setGameAcrossDay(manager.getGameNextAcrossDay(new Date(currentTime)));
        }
        if (isAcrossSysWeek(manager, currentTime)) {
            this.acrossDay(EAcrossDayType.SYS_ACROSS_WEEK, isNotify);
            manager.setSysAcrossWeek(manager.getSysNextAcrossWeek(new Date(currentTime)));
        }
        if (isAcrossGameWeek(manager, currentTime)) {
            this.acrossDay(EAcrossDayType.GAME_ACROSS_WEEK, isNotify);
            manager.setGameAcrossWeek(manager.getGameNextAcrossWeek(new Date(currentTime)));
        }
        if (isAcrossSysMonth(manager, currentTime)) {
            this.acrossDay(EAcrossDayType.SYS_ACROSS_MONTH, isNotify);
            manager.setSysAcrossMonth(manager.getSysNextAcrossMonth(new Date(currentTime)));
        }
        if (isAcrossGameMonth(manager, currentTime)) {
            this.acrossDay(EAcrossDayType.GAME_ACROSS_MONTH, isNotify);
            manager.setGameAcrossMonth(manager.getGameNextAcrossMonth(new Date(currentTime)));
        }
    }

    abstract void acrossDay(EAcrossDayType type, boolean isNotify);
}
