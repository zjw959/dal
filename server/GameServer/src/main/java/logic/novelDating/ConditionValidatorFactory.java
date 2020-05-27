package logic.novelDating;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import logic.character.bean.Player;
import logic.novelDating.structs.NovelDatingConst;
import logic.novelDating.structs.NovelDatingData;

public class ConditionValidatorFactory {
    protected ConditionValidatorFactory next = null;
    /** 建筑入口验证 */
    public static Map<String, ConditionValidator> entranceValidators;
    /** 建筑剧本验证 */
    public static Map<String, ConditionValidator> scriptValidators;

    static {
        ConditionValidatorFactory conditionFactory = new ConditionValidatorFactory();
        entranceValidators = conditionFactory.createDisplayValidators();
        scriptValidators = conditionFactory.createScriptEventValidators();
    }

    public Map<String, ConditionValidator> createDisplayValidators() {
        Map<String, ConditionValidator> validators = new HashMap<String, ConditionValidator>();
        validators.put(NovelDatingConst.SCRIPT_CONDITION_ITEM, createDefaultItemValidator());
        validators.put(NovelDatingConst.SCRIPT_CONDITION_MARK, createDefaultMarkValidator());
        validators.put(NovelDatingConst.SCRIPT_CONDITION_ANY_MARK, createDefaultAnyMarkValidator());
        if (next != null)
            validators.putAll(next.createDisplayValidators());
        return validators;
    }

    public Map<String, ConditionValidator> createScriptEventValidators() {
        Map<String, ConditionValidator> validators = new HashMap<String, ConditionValidator>();
        validators.put(NovelDatingConst.SCRIPT_CONDITION_ITEM, createDefaultItemValidator());
        validators.put(NovelDatingConst.SCRIPT_CONDITION_MARK, createDefaultMarkValidator());
        validators.put(NovelDatingConst.SCRIPT_CONDITION_ANY_MARK, createDefaultAnyMarkValidator());
        if (next != null)
            validators.putAll(next.createScriptEventValidators());
        return validators;
    }

    public ConditionValidatorFactory getNext() {
        return next;
    }

    public void setNext(ConditionValidatorFactory next) {
        this.next = next;
    }

    private ConditionValidator createDefaultMarkValidator() {
        return new ConditionValidator() {

            @SuppressWarnings("unchecked")
            @Override
            public boolean validate(Player player, NovelDatingData osd, Object markCondition) {

                if (osd != null && markCondition != null) {
                    List<Integer> marks = (List<Integer>) markCondition;
                    if (osd instanceof NovelDatingData) {
                        // 是否存在标记
                        if (!player.getNovelDatingManager().isMarksEnough(marks, osd))
                            return false;
                    }
                }
                // 默认通过检测
                return true;
            }
        };
    }

    private ConditionValidator createDefaultItemValidator() {
        return new ConditionValidator() {

            @SuppressWarnings("unchecked")
            @Override
            public boolean validate(Player player, NovelDatingData osd, Object itemCondition) {

                if (osd != null && itemCondition != null) {
                    Map<Integer, Integer> items = (Map<Integer, Integer>) itemCondition;
                    // 是否存在足够道具
                    if (!player.getNovelDatingManager().isItemsEnough(player, items, osd))
                        return false;
                }
                // 默认通过检测
                return true;
            }
        };
    }

    private ConditionValidator createDefaultAnyMarkValidator() {
        return new ConditionValidator() {

            @SuppressWarnings("unchecked")
            @Override
            public boolean validate(Player player, NovelDatingData osd, Object anyMarkCondition) {

                if (osd != null && anyMarkCondition != null) {
                    List<Object> marks = (List<Object>) anyMarkCondition;
                    // 是否存在标记
                    for (Object mark : marks) {
                        // 有任意一个
                        if (player.getNovelDatingManager().isMarksEnough((List<Integer>) mark, osd))
                            return true;
                    }
                    // 都没有
                    return false;
                }
                // 默认通过检测
                return true;
            }
        };
    }
}
