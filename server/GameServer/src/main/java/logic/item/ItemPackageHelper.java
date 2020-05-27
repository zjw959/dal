package logic.item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import logic.constant.GameErrorCode;
import logic.support.MessageUtils;

import org.apache.commons.lang.math.RandomUtils;

import utils.CommonUtil;
import utils.ToolMap;
import cn.hutool.core.util.RandomUtil;

/**
 * @author :
 * @CreateDate : 2017年8月22日 上午11:31:11
 * @Description ：Please describe this document { { "roll":{ "count":5 // 随机次数(非必填默认为1) "items": //
 *              条目 [ {"id":1,"min":50,"max":100,"weight":50},
 *              {"id":1,"min":50,"max":100,"weight":50}, {"id":1,"min":50,"max":100,"weight":50},
 *              {"id":1,"min":50,"max":100,"weight":50}, {"id":1,"min":50,"max":100,"weight":50}, ]
 *              }, "fix":{ "items": [ {"id":1,"num":50}, {"id":1,"num":50}, {"id":1,"num":50},
 *              {"id":1,"num":50}, {"id":1,"num":50} ] }, "notBis":{ "count":5 // 随机次数(非必填默认为1)
 *              "items": [ {"id":1,"min":50,"max":100,"weight":50},
 *              {"id":1,"min":50,"max":100,"weight":50}, {"id":1,"min":50,"max":100,"weight":50},
 *              {"id":1,"min":50,"max":100,"weight":50}, {"id":1,"min":50,"max":100,"weight":50}, ]
 *              }, "custom":{ "count":1, [ {"id":1,"num":50}, {"id":1,"num":50}, {"id":1,"num":50},
 *              {"id":1,"num":50}, {"id":1,"num":50} ] } } //--------------------------------说明 roll
 *              :为简单随机 fix :固定全给 notBis :随机不重复 custom :自选
 * 
 * 
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ItemPackageHelper {

    /** 为简单随机 */
    private static final String ROLL = "roll";
    /** 固定全给 */
    private static final String FIX = "fix";
    /** 随机不重复 */
    private static final String NOT_BIS = "notBis";
    /** 逐条保底掉落 */
    private static final String BASIC = "basic";
    /** 自选 */
    private static final String CUSTOM = "custom";
    /** 随机次数 */
    private static final String RANDOM_COUNT = "count";
    private static final String ITEMS = "items";
    private static final String ITEM_ID = "id";
    private static final String ITEM_NUM = "num";
    private static final String ITEM_MIN = "min";
    private static final String ITEM_MAX = "max";
    private static final String ITEM_WEIGHT = "weight";

    /** 默认最大权重 */
    // private static final int DEFAULT_MAX_WEIGHT = 10000;
    /** 默认随机次数 */
    private static final int DEFAULT_RANDOM_COUNT = 1;

    /**
     * 解包
     * 
     * @param context 礼包内容
     * @param customParame 自选类型下标
     * @return
     */
    public static Map<Integer, Integer> unpack(Map<String, Map> in, List<Integer> customParame,
            Map<Integer, Integer> out) {
        // 有自选类型,没有选择参数
        if (in.get(CUSTOM) != null && (customParame == null || customParame.isEmpty())) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR, "有自选类型,没有选择参数");
        }
        in.forEach((k, v) -> {
            switch (k) {
                case ROLL:
                    roll(v, out);
                    break;
                case FIX:
                    fix(v, out);
                    break;
                case NOT_BIS:
                    notBis(v, out);
                    break;
                case BASIC:
                    basic(v, out);
                    break;
                case CUSTOM:
                    custom(v, out, customParame);
                    break;
                default:
                    break;
            }
        });
        return out;
    }

    /**
     * 简单随机
     * 
     * @param in
     * @param out
     */
    private static void roll(Map<String, Object> in, Map<Integer, Integer> out) {
        Integer rcount = (Integer) in.get(RANDOM_COUNT);
        int randomCount = rcount != null ? rcount.intValue() : DEFAULT_RANDOM_COUNT;
        List<Map> items = (List<Map>) in.get(ITEMS);
        if (items != null) {
            // 累加最大权重
            int maxWeight = 0;
            for (Map map : items) {
                maxWeight += (Integer) map.get(ITEM_WEIGHT);
            }
            for (int i = 0; i < randomCount; i++) {
                int randomWeight = RandomUtils.nextInt(maxWeight);
                int totalWeight = 0;
                for (Map map : items) {
                    int id = ToolMap.getInt(ITEM_ID, map, 0);
                    int min = ToolMap.getInt(ITEM_MIN, map, 0);
                    int max = ToolMap.getInt(ITEM_MAX, map, 0);
                    int num = RandomUtil.randomInt(min, max + 1);
                    int weight = ToolMap.getInt(ITEM_WEIGHT, map, 0);
                    if (id == 0 || num == 0 || weight == 0) {
                        continue;
                    }
                    totalWeight += weight;
                    if (totalWeight >= randomWeight) {
                        CommonUtil.changeMap(out, id, num);
                        break;
                    }
                }
            }
        }
    }



    /**
     * 全给
     * 
     * @param in
     * @param out
     */
    private static void fix(Map<String, Object> in, Map<Integer, Integer> out) {
        List<Map> items = ToolMap.getList(ITEMS, in, new ArrayList<>(0));
        for (Map map : items) {
            int id = ToolMap.getInt(ITEM_ID, map, 0);
            if (id == 0) {
                continue;
            }
            int num = 0;
            if (map.containsKey(ITEM_NUM)) {// 固定值
                num = ToolMap.getInt(ITEM_NUM, map, 0);
            } else {// 范围随机
                int min = ToolMap.getInt(ITEM_MIN, map, 0);
                int max = ToolMap.getInt(ITEM_MAX, map, 0);
                num = RandomUtil.randomInt(min, max + 1);
            }
            CommonUtil.changeMap(out, id, num);
        }
    }

    /**
     * 随机不重复
     * 
     * @param in
     * @param out
     */
    private static void notBis(Map<String, Object> in, Map<Integer, Integer> out) {
        int randomCount = ToolMap.getInt(RANDOM_COUNT, in, DEFAULT_RANDOM_COUNT);
        List<Map> items = ToolMap.getList(ITEMS, in, new ArrayList<>(0));
        // 累加最大权重
        int maxWeight = 0;
        for (Map map : items) {
            maxWeight += ToolMap.getInt(ITEM_WEIGHT, map, 0);
        }
        Set<Integer> set = new HashSet<Integer>(5);
        // 避免随机次数多余条目数
        randomCount = Math.min(randomCount, items.size());

        for (int i = 0; i < randomCount; i++) {
            int randomWeight = RandomUtil.randomInt(0, maxWeight + 1);
            int totalWeight = 0;
            // 奖励下标
            int index = 0;

            for (Map map : items) {
                int id = ToolMap.getInt(ITEM_ID, map, 0);
                int min = ToolMap.getInt(ITEM_MIN, map, 0);
                int max = ToolMap.getInt(ITEM_MAX, map, 0);
                int num = RandomUtil.randomInt(min, max + 1);
                int weight = ToolMap.getInt(ITEM_WEIGHT, map, 0);
                index++;
                if (id == 0 || num == 0 || weight == 0 || set.contains(index)) {
                    continue;
                }
                totalWeight += weight;
                if (totalWeight >= randomWeight) {
                    CommonUtil.changeMap(out, id, num);
                    maxWeight -= weight;
                    set.add(index);
                    break;
                }
            }
        }
    }

    /**
     * 逐条保底掉落
     * 
     * @param in
     * @param out
     */
    private static void basic(Map<String, Object> in, Map<Integer, Integer> out) {
        List<Map> items = ToolMap.getList(ITEMS, in, new ArrayList<>(0));
        for (Map map : items) {
            int id = ToolMap.getInt(ITEM_ID, map, 0);
            if (id == 0) {
                continue;
            }
            int num = ToolMap.getInt(ITEM_NUM, map, 0);// 保底掉落数量
            int weight = ToolMap.getInt(ITEM_WEIGHT, map, 0);
            if (RandomUtil.randomInt(0, 10000) < weight) {// 额外掉落
                int min = ToolMap.getInt(ITEM_MIN, map, 0);
                int max = ToolMap.getInt(ITEM_MAX, map, 0);
                num += RandomUtil.randomInt(min, max + 1);
            }
            if (num == 0) {
                continue;
            }
            CommonUtil.changeMap(out, id, num);
        }
    }


    /**
     * 自选
     * 
     * @param in
     * @param out
     */
    private static void custom(Map<String, Object> in, Map<Integer, Integer> out,
            List<Integer> customParame) {
        List<Map> items = ToolMap.getList(ITEMS, in, new ArrayList<>(0));
        int itemCount = items.size();

        int customCount = ToolMap.getInt(RANDOM_COUNT, in, DEFAULT_RANDOM_COUNT);
        // 最大自选个数
        customCount = Math.min(customParame.size(), customCount);

        for (int i = 0; i < customCount; i++) {
            int index = customParame.get(i);
            if (index >= itemCount) {
                index = itemCount;
            }
            if (i >= 0) {
                Map item = items.get(index - 1);
                int id = ToolMap.getInt(ITEM_ID, item, 0);
                int num = ToolMap.getInt(ITEM_NUM, item, 0);
                if (id == 0 || num == 0) {
                    continue;
                }
                CommonUtil.changeMap(out, id, num);
            }
        }
    }

    public static void main(String[] args) {
        // String json =
        // "{\"fix\":{\"items\":[{\"id\":500007,\"num\":10},{\"id\":500008,\"num\":10},{\"id\":530111,\"num\":5},{\"id\":540001,\"num\":1}]},\"cc\":1001}";
        // Map out = new HashMap<>();
        // unpack(JSON.parseObject(json,Map.class), null, out);
        // System.out.println(out);


        for (int i = 0; i < 10; i++) {
            System.out.println("---" + RandomUtils.nextInt(10));
        }
    }
}
