package logic.constant;

public enum ESkillType {
    /** 普攻 */
    ATTACK(1),
    /** 技能1 */
    SKILL_ONE(2),
    /** 技能2 */
    SKILL_TWO(3),
    /** 必杀技能 */
    KILLING_SKILL(4),
    /** 闪避 */
    EVA(5),
    /** 觉醒技 */
    AWAKEN_SKILL(6),
    /** 出场技 */
    APPEARANCE_SKILL(7),
    /** 下落技能 */
    FALL_SKILL(8),
    /** 起身技能 */
    ARISE_SKILL(9),
    /** 被动技能 */
    PASSIVE_SKILL(10);
    
    private final int value;

    ESkillType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
    
    public static ESkillType getESkillType(int value) {
        for (ESkillType rt : ESkillType.values()) {
            if (rt.compare(value)) {
                return rt;
            }
        }
        throw new IllegalArgumentException("unknown property type value:" + value);
    }

    public boolean compare(int value) {
        return this.value == value;
    }
}
