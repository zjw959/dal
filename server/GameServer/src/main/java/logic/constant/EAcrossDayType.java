package logic.constant;

/**
 * 
 * @Description 跨天类型枚举
 * @author LiuJiang
 * @date 2018年7月13日 下午3:00:37
 *
 */
public enum EAcrossDayType {
    /** 系统跨天（0点） */
    SYS_ACROSS_DAY(1),
    /** 游戏内跨天（6点） */
    GAME_ACROSS_DAY(2),
    /** 系统跨周（周一0点） */
    SYS_ACROSS_WEEK(3),
    /** 游戏内跨周（周一6点） */
    GAME_ACROSS_WEEK(4),
    /** 系统跨月（1号0点） */
    SYS_ACROSS_MONTH(5),
    /** 游戏内跨月（1号6点） */
    GAME_ACROSS_MONTH(6);

    int value;

    private EAcrossDayType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }


}
