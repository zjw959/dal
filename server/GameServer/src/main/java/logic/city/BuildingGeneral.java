package logic.city;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import logic.character.bean.Player;
import logic.city.build.BuildingConstant;
import logic.city.build.bean.BuildEvent;
import logic.constant.ItemConstantId;

/***
 * 建筑通用方法
 * 
 * @author lihongji
 */
public class BuildingGeneral {


    /**
     * 通用随机方法(返回数据的当前位置)
     * 
     * @param list 随机数据集合
     * @param parm 循环增加i
     * @param index 如果没有默认取值的位置
     * @param location 概率的位置
     * @return
     */
    public static int random(ArrayList<Integer> list, int parm, int index, int location) {
        if (list == null || list.size() == 0)
            return 0;
        int probability = (int) (Math.random() * list.get(list.size() - 1));
        int startrPobability = 0;
        for (int i = 0; i < list.size(); i += parm) {
            if (startrPobability <= probability && probability < list.get(i + location)) {
                return i;
            }
            startrPobability = list.get(i + location);
        }
        // 如果没有随机到 则默认写第一个
        return index;
    }


    /***
     * 
     * @param list 随机数据集合
     * @param parm 循环增加i
     * @param index 概率的位置
     * @return
     */
    public static void resetProbability(ArrayList<Integer> list, int parm, int index) {
        int weight = 0;
        for (int i = 0; i < list.size(); i += parm) {
            weight += list.get(i + index);
            list.set(i + index, weight);
        }
    }


    /** 生成建筑事件 **/
    public static BuildEvent createBuildingEvent(int buildingId, int eventType, int exeId,
            int funId, Player player) {
        BuildEvent event = new BuildEvent(buildingId, funId, exeId, eventType);
        ArrayList<BuildEvent> events =
                player.getNewBuildingManager().getBuildingEvent().get(buildingId);
        if (events == null) {
            events = new ArrayList<BuildEvent>();
            player.getNewBuildingManager().getBuildingEvent().put(buildingId, events);
        }
        events.add(event);
        return event;
    }

    /**
     * 开放检测检查条件
     * 
     * @param condition
     * @return
     */
    public static boolean checkBuildingCondition(Map<Integer, Object> condition, Player player) {
        if (condition == null)
            return true;
        if (condition.isEmpty())
            return true;
        for (Map.Entry<Integer, Object> entry : condition.entrySet()) {
            switch (entry.getKey()) {
                // 玩家等级
                case BuildingConstant.BUILDING_UP_CONDITION_1:
                    int level = (int) condition.get(BuildingConstant.BUILDING_UP_CONDITION_1);
                    if (player.getLevel() < level) {
                        return false;
                    }
                    break;
                // 检测玩家管卡等级
                case BuildingConstant.BUILDING_UP_CONDITION_2:
                    int dungeonId = (int) condition.get(BuildingConstant.BUILDING_UP_CONDITION_2);
                    if (!player.getDungeonManager().checkDungeonPass(dungeonId)) {
                        return false;
                    }
                    break;
                case BuildingConstant.BUILDING_UP_CONDITION_3:
                    break;
                case BuildingConstant.BUILDING_UP_CONDITION_4:
                    break;
                default:
                    break;
            }
        }
        return true;
    }



    /** 检测5属性能力 **/
    public static boolean checkAbility(Map<Integer, Integer> ability, Player player) {
        if (ability == null || ability.isEmpty())
            return true;
        for (Map.Entry<Integer, Integer> entry : ability.entrySet()) {
            switch (entry.getKey()) {
                // 专注
                case ItemConstantId.PLAYER_ABSORBED:
                    if (player.getAbsorbed() < entry.getValue()) {
                        return false;
                    }
                    break;
                // 魅力
                case ItemConstantId.PLAYER_GLAMOUR:
                    if (player.getGlamour() < entry.getValue()) {
                        return false;
                    }
                    break;
                // 温柔
                case ItemConstantId.PLAYER_TENDER:
                    if (player.getTender() < entry.getValue()) {
                        return false;
                    }
                    break;
                // 知识
                case ItemConstantId.PLAYER_KNOWLEDGE:
                    if (player.getKnowledge() < entry.getValue()) {
                        return false;
                    }
                    break;
                case ItemConstantId.PLAYER_FORTUNE:
                    if (player.getFortune() < entry.getValue()) {
                        return false;
                    }
                    break;
                default:
                    break;
            }
        }
        return true;
    }


    /** 获取功能中的所对应的建筑 **/
    public static Set<Integer> getBuildingByFunId(int funId, Player player) {
        Set<Integer> buildingIds = new HashSet<Integer>();
        player.getNewBuildingManager().getValidFunIds().forEach((buildingId, funIds) -> {
            funIds.forEach((fid) -> {
                if (fid == funId)
                    buildingIds.add(buildingId);
            });
        });
        return buildingIds;
    }
}
