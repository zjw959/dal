package logic.constant;

/**
 * 属性枚举
 * 
 * @author ouyangcheng
 */
public enum EPropertyType {
    // 固定值属性（累加显示固定值）
    // 客户端显示固定值/100取整
    /** 最大生命值 */
    MAX_HP(1),
    /** 攻击力 */
    ATTACK(2),
    /** 防御力 */
    DEFENCE(3),
    /** 固定增伤 */
    INCREASE_DAMAGE_VALUE(4),
    /** 固定减伤 */
    DECREASE_DAMAGE_VALUE(5),
    /** 攻速值 */
    ATTACK_SPEED(6),
    /** 初始护盾值 */
    INIT_PROTECT(7),
    /** 最大怒气值 */
    MAX_ANGER(8),
    /** 怒气恢复值 */
    RECOVER_ANGER(9),
    /** 负重值 */
    BURDEN(10),
    /** 移动速度 */
    MOVE_SPEED(11),
    /** 破防值 */
    BREAK_PROTECT(12),
    /** 技能点 */
    SKIILL_POINT(13),

    /** 能量值上限 */
    POWER_LIMIT(14),
    /** 霸体上限 */
    MAX_SUPER_ARMOR(15),
    /** 基础生命恢复值 */
    BASE_HP_RECOVER(16),
    /** 基础sp回复值 */
    BASE_SP_RECOVER(17),
    /** 撕裂点上限 */
    MAX_TEAR(18),

    // 战斗用属性（需服务器记录）
    /** 当前怒气值 */
    ANGER(51),
    /** 当前生命（战斗使用, 原始值, 不进行倍率等计算, 原始值与被倍率值均可） */
    HP(52),
    
    /** 当前护盾值 */
    PROTECT(53),
    /** 当前霸体 */
    SUPER_ARMOR(54),
    /** 能量初始值 */
    POWER_INITIAL(56),
    /** 当前撕裂点 */
    TEAR(57),
    /** 硬直时间 */
    STRONG_TIME(60),

    // 固定百分比属性（累加显示百分比）
    // 客户端显示百分比/10000
    /** 增伤率 */
    INCREASE_DAMAGE_RATE(501),
    /** 减伤率 */
    DECREASE_DAMAGE_RATE(502),
    /** 穿透率 */
    PIERCE_RATE(503),
    /** 格挡率 */
    PARRY_RATE(504),
    /** 命中率 */
    HIT_RATE(505),
    /** 闪避率 */
    DODGE_RATE(506),
    /** 暴击率 */
    CRIT_RATE(507),
    /** 抗暴率 */
    UNCRIT_RATE(508),
    /** 暴击伤害率 */
    CRIT_HURT_RATE(509),
    /** 暴击减伤率 */
    CRIT_SUB_HURT_RATE(510),
    /** 控制时间加成 */
    CONTROL_TIME_RATE(511),
    /** 受控时间减免 */
    REDUCE_CONTROL_TIME_RATE(512),
    /** 增益时间加成 */
    BUFF_RATE(513),
    /** 减益时间减免 */
    DEBUFF_RATE(514),

    /** CD减少率 */
    CURE_RATE(515),
    /** CD减少率 */
    CD_REDUCE_RATE(517),

    /** 物理伤害加成 */
    PHYSICS_DAMAGE_ADDITION(601),
    /** 冰伤害加成 */
    ICE_DAMAGE_ADDITION(602),
    /** 火伤害加成 */
    FIRE_DAMAGE_ADDITION(603),
    /** 风伤害加成 */
    WIND_DAMAGE_ADDITION(604),
    /** 雷伤害加成 */
    THUNDER_DAMAGE_ADDITION(605),
    /** 毒伤害加成 */
    POISON_DAMAGE_ADDITION(606),
    /** 光伤害加成 */
    LIGHT_DAMAGE_ADDITION(607),
    /** 暗伤害加成 */
    DARK_DAMAGE_ADDITION(608),
    /** 精神伤害加成 */
    SPIRIT_DAMAGE_ADDITION(609),
    /** 伤害加成（针对人类） */
    DAMAGE_ADDITION_TO_HUMAN(651),
    /** 伤害加成（针对能量） */
    DAMAGE_ADDITION_TO_ENERGY(652),
    /** 伤害加成（针对机械） */
    DAMAGE_ADDITION_TO_MACHINE(653),
    /** 伤害加成（针对精灵） */
    DAMAGE_ADDITION_TO_HERO(654),
    /** 伤害加成（针对怪物） */
    DAMAGE_ADDITION_TO_MONSTER(661),
    /** 伤害加成（针对精英） */
    DAMAGE_ADDITION_TO_ELITE(662),
    /** 伤害加成（针对BOSS） */
    DAMAGE_ADDITION_TO_BOSS(663),
    
    
    /** 物理伤害减免 */
    PHYSICS_DAMAGE_REDUCTION(701),
    /** 冰伤害减免 */
    ICE_DAMAGE_REDUCTION(702),
    /** 火伤害减免 */
    FIRE_DAMAGE_REDUCTION(703),
    /** 风伤害减免 */
    WIND_DAMAGE_REDUCTION(704),
    /** 雷伤害减免 */
    THUNDER_DAMAGE_REDUCTION(705),
    /** 毒伤害减免 */
    POISON_DAMAGE_REDUCTION(706),
    /** 光伤害减免 */
    LIGHT_DAMAGE_REDUCTION(707),
    /** 暗伤害减免 */
    DARK_DAMAGE_REDUCTION(708),
    /** 精神伤害减免 */
    SPIRIT_DAMAGE_REDUCTION(709),
    
    /** 伤害减免（针对人类） */
    DAMAGE_REDUCTION_TO_HUMAN(751),
    /** 伤害减免（针对能量） */
    DAMAGE_REDUCTION_TO_ENERGY(752),
    /** 伤害减免（针对机器） */
    DAMAGE_REDUCTION_TO_MACHINE(753),
    /** 伤害减免（针对精灵） */
    DAMAGE_REDUCTION_TO_HERO(754),


    // 固定值加成属性（累加）
    // 客户端显示百分比/10000
    /** 生命加成 */
    HP_RATE(1001),
    /** 攻击加成 */
    ATTACK_RATE(1002),
    /** 防御加成 */
    DEFENCE_RATE(1003),
    /** 攻速加成 */
    ATTACK_SPEED_RATE(1006),
    /** 护盾加成 */
    PROTECT_RATE(1007),
    /** 最大怒气值加成 */
    MAX_ANGER_RATE(1008),
    /** 怒气回复加成 */
    RECOVER_ANGER_RATE(1009),
    /** 移速加成 */
    MOVE_SPEED_RATE(1011),
    /** 破防值加成 */
    BREAK_PROTECT_RATE(1012);

    private final int value;

    EPropertyType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static EPropertyType getEPropertyType(int value) {
        for (EPropertyType rt : EPropertyType.values()) {
            if (rt.compare(value)) {
                return rt;
            }
        }
        throw new IllegalArgumentException("unknown property type value:" + value);
    }

    public static boolean isExistPropertyType(int value) {
        for (EPropertyType rt : EPropertyType.values()) {
            if (rt.compare(value)) {
                return true;
            }
        }
        return false;
    }

    public boolean compare(int value) {
        return this.value == value;
    }
}
