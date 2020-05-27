package logic.favor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.GameDataManager;
import logic.character.bean.Player;
import logic.favor.structs.FavorDatingConst;
import logic.favor.structs.FavorDatingData;


public class ConditionValidatorFactory {
    protected ConditionValidatorFactory next = null;
    /** 建筑入口验证 */
    public static Map<String, ConditionValidator> entranceValidators;
    /** 事件信息处理 */
    static Map<Integer, EventMessageHandler> eventMessageHandlers;

    static {
        ConditionValidatorFactory conditionFactory = new ConditionValidatorFactory();
        entranceValidators = conditionFactory.createDisplayValidators();
        eventMessageHandlers = new EventMessageHandlerFactory().createEventMessageHandlers();
    }
    public Map<String, ConditionValidator> createDisplayValidators() {
        Map<String, ConditionValidator> validators = new HashMap<String, ConditionValidator>();
        validators.put(FavorDatingConst.OWN_ITEM,
                createDefaultItemValidator());
        validators.put(FavorDatingConst.NOT_ITEM, createNotItemValidator());
        validators.put(FavorDatingConst.OWN_SIGN,
                createDefaultMarkValidator());
        validators.put(FavorDatingConst.NOT_SIGN, createNotMarkValidator());
        validators.put(FavorDatingConst.CHECK_POINT, createNotMarkValidator());
        validators.put(FavorDatingConst.OWN_DRESS, createOwnDressValidator());
        validators.put(FavorDatingConst.NOT_DRESS, createNotOwnDressValidator());
        validators.put(FavorDatingConst.OWN_END, createOwnEndValidator());
        validators.put(FavorDatingConst.NOT_END, createNotOwnEndValidator());
        validators.put(FavorDatingConst.SCRIPT_CONDITION_ANY_MARK,
                        createDefaultAnyMarkValidator());
        validators.put(FavorDatingConst.DATING_CONDITION_WON_ROLE, createDefaultNotRoleValidator());
        validators.put(FavorDatingConst.DATING_CONDITION_NOT_WON_ROLE,
                createDefaultNotRoleValidator());
        validators.put(FavorDatingConst.DATING_CONDITION_MUCH_VARIABLE,
                createMuchVariableValidator());
        validators.put(FavorDatingConst.DATING_CONDITION_LESS_VARIABLE,
                createLessVariableValidator());
        validators.put(FavorDatingConst.MUCH_ROLE_FAVOR, createMuchRoleFavorValidator());
        validators.put(FavorDatingConst.LESS_ROLE_FAVOR, createLessRoleFavprValidator());
        validators.put(FavorDatingConst.MUCH_ROLE_MOOD, createMuchRoleMoodValidator());
        validators.put(FavorDatingConst.LESS_ROLE_MOOD, createLessRoleMoodValidator());
        validators.put(FavorDatingConst.CHECK_POINT, createPointValidator());
        validators.put(FavorDatingConst.CHANCE, createChanceValidator());
        validators.put(FavorDatingConst.OWN_JUMP, createJumpValidator());
        validators.put(FavorDatingConst.NOT_JUMP, createNotJumpValidator());
        if (next != null)
            validators.putAll(next.createDisplayValidators());
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
            public boolean validate(Player player, FavorDatingData osd, Object markCondition,
                    int favorDatingId) {

                if (osd != null && markCondition != null) {
                    List<Integer> marks = (List<Integer>) markCondition;
                    if (osd instanceof FavorDatingData) {
                        // 是否存在标记
                        if (!player.getFavorDatingManager().isMarksEnough(marks, osd))
                            return false;
                    }
                }
                // 默认通过检测
                return true;
            }
        };
    }

    private ConditionValidator createNotMarkValidator() {
        return new ConditionValidator() {

            @SuppressWarnings("unchecked")
            @Override
            public boolean validate(Player player, FavorDatingData osd, Object markCondition,
                    int favorDatingId) {

                if (osd != null && markCondition != null) {
                    List<Integer> marks = (List<Integer>) markCondition;
                    if (osd instanceof FavorDatingData) {
                        // 是否存在标记
                        if (!player.getFavorDatingManager().notMarksEnough(marks, osd))
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
            public boolean validate(Player player, FavorDatingData osd, Object itemCondition,
                    int favorDatingId) {

                if (osd != null && itemCondition != null) {
                    Map<Integer, Integer> items = (Map<Integer, Integer>) itemCondition;
                    // 是否存在足够道具
                    if (!player.getFavorDatingManager().isItemsEnough(player, items, osd))
                        return false;
                }
                // 默认通过检测
                return true;
            }
        };
    }

    private ConditionValidator createNotItemValidator() {
        return new ConditionValidator() {

            @SuppressWarnings("unchecked")
            @Override
            public boolean validate(Player player, FavorDatingData osd, Object itemCondition,
                    int favorDatingId) {

                if (osd != null && itemCondition != null) {
                    Map<Integer, Integer> items = (Map<Integer, Integer>) itemCondition;
                    if (!player.getFavorDatingManager().notItemsEnough(player, items, osd))
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
            public boolean validate(Player player, FavorDatingData osd, Object anyMarkCondition,
                    int favorDatingId) {

                if (osd != null && anyMarkCondition != null) {
                    List<Object> marks = (List<Object>) anyMarkCondition;
                    // 是否存在标记
                    for (Object mark : marks) {
                        // 有任意一个
                        if (player.getFavorDatingManager()
                                .isMarksEnough((List<Integer>) mark, osd))
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

    private ConditionValidator createDefaultNotRoleValidator() {
        return new ConditionValidator() {

            @SuppressWarnings("unchecked")
            @Override
            public boolean validate(Player player, FavorDatingData osd, Object condition,
                    int favorDatingId) {

                if (osd != null && condition != null) {
                    List<Integer> condi = (List<Integer>) condition;
                    // 是否满足条件
                    if (condi.stream().filter(e -> player.getRoleManager().getRole(e) != null)
                            .findAny().isPresent()) {
                        return false;
                    }
                }
                // 默认通过检测
                return true;
            }
        };
    }

    private ConditionValidator createMuchVariableValidator() {
        return new ConditionValidator() {

            @SuppressWarnings("unchecked")
            @Override
            public boolean validate(Player player, FavorDatingData osd, Object condition,
                    int favorDatingId) {

                if (osd != null && condition != null) {
                    Map<Integer, Integer> muchVariable = (Map<Integer, Integer>) condition;
                    // 是否满足条件
                    if (!checkVariable(player, favorDatingId, muchVariable)) {
                        return false;
                    }
                }
                // 默认通过检测
                return true;
            }
        };
    }

    private ConditionValidator createLessVariableValidator() {
        return new ConditionValidator() {

            @SuppressWarnings("unchecked")
            @Override
            public boolean validate(Player player, FavorDatingData osd, Object condition,
                    int favorDatingId) {

                if (osd != null && condition != null) {
                    Map<Integer, Integer> muchVariable = (Map<Integer, Integer>) condition;
                    // 是否满足条件
                    if (!checkNotVariable(player, favorDatingId, muchVariable)) {
                        return false;
                    }
                }
                // 默认通过检测
                return true;
            }
        };
    }

    private boolean checkVariable(Player player, int favorDatingId,
            Map<Integer, Integer> condition) {
        int roleId = GameDataManager.getFavorCfgBean(favorDatingId).getRole();
        FavorDatingData favorData =
                player.getFavorDatingManager().getDataMap().get(roleId).get(favorDatingId);
        Map<Integer, Integer> qualityMap = favorData.getQualityMap();
        for (Map.Entry<Integer, Integer> condi : condition.entrySet()) {
            int value = qualityMap.get(condi.getKey()) == null ? 0 : qualityMap.get(condi.getKey());
            if (value < condi.getValue()) {
                return false;
            }
        }
        return true;
    }

    private boolean checkNotVariable(Player player, int favorDatingId,
            Map<Integer, Integer> condition) {
        int roleId = GameDataManager.getFavorCfgBean(favorDatingId).getRole();
        FavorDatingData favorData =
                player.getFavorDatingManager().getDataMap().get(roleId).get(favorDatingId);
        Map<Integer, Integer> qualityMap = favorData.getQualityMap();
        for (Map.Entry<Integer, Integer> condi : condition.entrySet()) {
            int value = qualityMap.get(condi.getKey()) == null ? 0 : qualityMap.get(condi.getKey());
            if (value >= condi.getValue()) {
                return false;
            }
        }
        return true;
    }

    private ConditionValidator createMuchRoleFavorValidator() {
        return new ConditionValidator() {

            @SuppressWarnings("unchecked")
            @Override
            public boolean validate(Player player, FavorDatingData osd, Object condition,
                    int favorDatingId) {

                if (osd != null && condition != null) {
                    Map<Integer, Integer> condi = (Map<Integer, Integer>) condition;
                    // 是否满足条件
                    for (Map.Entry<Integer, Integer> condiEntry : condi.entrySet()) {
                        int roleId = condiEntry.getKey();
                        if (player.getRoleManager().getRole(roleId) == null) {
                            return false;
                        }
                        int favor = player.getRoleManager().getRole(roleId).getFavor();
                        if (favor < condiEntry.getValue()) {
                            return false;
                        }
                    }
                }
                // 默认通过检测
                return true;
            }
        };
    }

    private ConditionValidator createLessRoleFavprValidator() {
        return new ConditionValidator() {

            @SuppressWarnings("unchecked")
            @Override
            public boolean validate(Player player, FavorDatingData osd, Object condition,
                    int favorDatingId) {

                if (osd != null && condition != null) {
                    Map<Integer, Integer> condi = (Map<Integer, Integer>) condition;
                    // 是否满足条件
                    for (Map.Entry<Integer, Integer> condiEntry : condi.entrySet()) {
                        int roleId = condiEntry.getKey();
                        if (player.getRoleManager().getRole(roleId) != null) {
                            int favor = player.getRoleManager().getRole(roleId).getFavor();
                            if (favor >= condiEntry.getValue()) {
                                return false;
                            }
                        }

                    }
                }
                return true;
            }
        };
    }

    private ConditionValidator createMuchRoleMoodValidator() {
        return new ConditionValidator() {

            @SuppressWarnings("unchecked")
            @Override
            public boolean validate(Player player, FavorDatingData osd, Object condition,
                    int favorDatingId) {

                if (osd != null && condition != null) {
                    Map<Integer, Integer> condi = (Map<Integer, Integer>) condition;
                    // 是否满足条件
                    for (Map.Entry<Integer, Integer> condiEntry : condi.entrySet()) {
                        int roleId = condiEntry.getKey();
                        if (player.getRoleManager().getRole(roleId) == null) {
                            return false;
                        }
                        int mood = player.getRoleManager().getRole(roleId).getMood();
                        if (mood < condiEntry.getValue()) {
                            return false;
                        }
                    }
                }
                return true;
            }
        };
    }

    private ConditionValidator createLessRoleMoodValidator() {
        return new ConditionValidator() {

            @SuppressWarnings("unchecked")
            @Override
            public boolean validate(Player player, FavorDatingData osd, Object condition,
                    int favorDatingId) {

                if (osd != null && condition != null) {
                    Map<Integer, Integer> condi = (Map<Integer, Integer>) condition;
                    // 是否满足条件
                    for (Map.Entry<Integer, Integer> condiEntry : condi.entrySet()) {
                        int roleId = condiEntry.getKey();
                        if (player.getRoleManager().getRole(roleId) != null) {
                            int mood = player.getRoleManager().getRole(roleId).getMood();
                            if (mood > condiEntry.getValue()) {
                                return false;
                            }
                        }
                    }
                }
                return true;
            }
        };
    }

    private ConditionValidator createOwnDressValidator() {
        return new ConditionValidator() {

            @SuppressWarnings("unchecked")
            @Override
            public boolean validate(Player player, FavorDatingData osd, Object condition,
                    int favorDatingId) {

                if (osd != null && condition != null) {
                    List<Integer> condi = (List<Integer>) condition;
                    if (condi.stream().filter(e -> player.getBagManager().getItemOrigin(e) == null)
                            .findAny().isPresent()) {
                        return false;
                    }
                }
                // 默认通过检测
                return true;
            }
        };
    }

    private ConditionValidator createNotOwnDressValidator() {
        return new ConditionValidator() {

            @SuppressWarnings("unchecked")
            @Override
            public boolean validate(Player player, FavorDatingData osd, Object condition,
                    int favorDatingId) {

                if (osd != null && condition != null) {
                    List<Integer> condi = (List<Integer>) condition;
                    if (condi.stream().filter(e -> player.getBagManager().getItemOrigin(e) != null)
                            .findAny().isPresent()) {
                        return false;
                    }
                }
                // 默认通过检测
                return true;
            }
        };
    }

    private ConditionValidator createOwnEndValidator() {
        return new ConditionValidator() {
            @SuppressWarnings("unchecked")
            @Override
            public boolean validate(Player player, FavorDatingData osd, Object condition,
                    int favorDatingId) {

                if (osd != null && condition != null) {
                    List<Integer> condi = (List<Integer>) condition;

                    if (condi.stream().filter(e -> !osd.getEndings().contains(e)).findAny()
                            .isPresent()) {
                        return false;
                    }
                }
                // 默认通过检测
                return true;
            }
        };
    }

    private ConditionValidator createNotOwnEndValidator() {
        return new ConditionValidator() {
            @SuppressWarnings("unchecked")
            @Override
            public boolean validate(Player player, FavorDatingData osd, Object condition,
                    int favorDatingId) {

                if (osd != null && condition != null) {
                    List<Integer> condi = (List<Integer>) condition;

                    if (condi.stream().filter(e -> osd.getEndings().contains(e)).findAny()
                            .isPresent()) {
                        return false;
                    }
                }
                // 默认通过检测
                return true;
            }
        };
    }

    private ConditionValidator createPointValidator() {
        return new ConditionValidator() {
            @SuppressWarnings("unchecked")
            @Override
            public boolean validate(Player player, FavorDatingData osd, Object condition,
                    int favorDatingId) {

                if (osd != null && condition != null) {
                    List<Integer> condi = (List<Integer>) condition;
                    if (condi.stream().filter(e -> !player.getDungeonManager().checkDungeonPass(e))
                            .findAny().isPresent()) {
                        return false;
                    }
                }
                // 默认通过检测
                return true;
            }
        };
    }

    private ConditionValidator createChanceValidator() {
        return new ConditionValidator() {
            @SuppressWarnings("unchecked")
            @Override
            public boolean validate(Player player, FavorDatingData osd, Object condition,
                    int favorDatingId) {

                if (osd != null && condition != null) {
                    List<Integer> condi = (List<Integer>) condition;
                    int value = condi.get(0);
                    int randomNumber = (int) (Math.random() * 100) + 1;
                    if (randomNumber > value) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    private ConditionValidator createJumpValidator() {
        return new ConditionValidator() {
            @SuppressWarnings("unchecked")
            @Override
            public boolean validate(Player player, FavorDatingData osd, Object condition,
                    int favorDatingId) {

                if (osd != null && condition != null) {
                    List<Integer> condi = (List<Integer>) condition;
                    if (condi.stream().filter(e -> !osd.getTempChoose().contains(e)).findAny()
                            .isPresent()) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    private ConditionValidator createNotJumpValidator() {
        return new ConditionValidator() {
            @SuppressWarnings("unchecked")
            @Override
            public boolean validate(Player player, FavorDatingData osd, Object condition,
                    int favorDatingId) {

                if (osd != null && condition != null) {
                    List<Integer> condi = (List<Integer>) condition;
                    if (condi.stream().filter(e -> osd.getTempChoose().contains(e)).findAny()
                            .isPresent()) {
                        return false;
                    }
                }
                return true;
            }
        };
    }
}
