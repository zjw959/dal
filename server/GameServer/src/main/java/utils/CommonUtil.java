package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.common.collect.Maps;

public class CommonUtil {
    private static final Logger LOGGER = Logger.getLogger(CommonUtil.class);

    public static void mergeMap(Map<Integer, Integer> from, Map<Integer, Integer> to) {
        for (Map.Entry<Integer, Integer> entry : from.entrySet()) {
            int value = entry.getValue();
            if (to.containsKey(entry.getKey())) {
                value += to.get(entry.getKey());
            }
            to.put(entry.getKey(), value);
        }
    }

    /**
     * 特殊掉落(必掉)计算
     * 
     * @param dropStrs 要求掉落的物品
     * @param dropCounts 已经掉落的物品
     * @param dropsCache 掉落缓存(要掉落的物品)
     */
    // public static Map<Integer, Integer> checkSpecialDrop(String dropStrs,
    // Map<Integer, Dungeon.DropCount> dropCounts) {
    // Map<Integer, Integer> dropsCache = new HashMap<>();
    // if (StringUtil.isNullConfigStr(dropStrs)) {
    // dropCounts.clear();
    // return dropsCache;
    // }
    // // 对特殊掉落数据 做最新数据更新
    // int[][] specialDrops = StrEx.buildIntDoubleArrayKV(dropStrs);
    // for (int i = 0; i < specialDrops.length; i++) {
    // // 当前特殊掉落 不存在 则加入记录中
    // Dungeon.DropCount dropCount = dropCounts.get(specialDrops[i][0]);
    // if (dropCount == null) {
    // dropCount = new Dungeon.DropCount(specialDrops[i][1]);
    // dropCounts.put(specialDrops[i][0], dropCount);
    // }
    // // 将所有需要参与特殊掉落的 进行掉落验证
    // if (dropCount.isDrop()) {
    // Map<Integer, Integer> specialDrop = DropService.getDropItems(specialDrops[i][0]);
    // if (specialDrop == null) {
    // LOGGER.error(ConstDefine.LOG_ERROR_CONFIG_PREFIX + "can not drop special items."
    // + specialDrops[i][0]);
    // } else {
    // dropsCache.putAll(specialDrop);
    // }
    // }
    // }
    //
    // _checkUpdateSpecialDrop(dropStrs, dropCounts);
    // return dropsCache;
    // }

    // private static void _checkUpdateSpecialDrop(String dropStrs,
    // Map<Integer, Dungeon.DropCount> dropCounts) {
    // // 当前副本已经不包含特殊掉落 清除掉 之前标记的特殊掉落
    // if (StringUtil.isNullConfigStr(dropStrs)) {
    // dropCounts.clear();
    // return;
    // }
    // Map<Integer, Integer> specialDrops = StrEx.buildIntKV(dropStrs);
    // for (Iterator<Map.Entry<Integer, Dungeon.DropCount>> ite =
    // dropCounts.entrySet().iterator();;) {
    // if (!ite.hasNext())
    // break;
    // Map.Entry<Integer, Dungeon.DropCount> entry = ite.next();
    // if (!specialDrops.containsKey(entry.getKey())) {
    // ite.remove();
    // continue;
    // }
    // // 提升当前权重修正值
    // entry.getValue().correctWeight();
    // // 检查是重置计数
    // entry.getValue().checkReset(specialDrops.get(entry.getKey()));
    // }
    // }


    public static List<String> buildCaserDatas() {
        List<String> caserDatas = new ArrayList<>();
        return caserDatas;
    }


    public static String getPlatformAccount(String userName) {
        String ret = userName;
        String[] split = userName.split("_");
        if (split.length == 0 || split.length == 1)
            ret = userName;
        else if (split.length == 2)
            ret = split[1];
        else {
            StringBuilder uidBuilder = new StringBuilder();
            for (int a = 1; a < split.length; ++a) {
                uidBuilder.append(split[a]);
            }
            ret = uidBuilder.toString();
        }
        return ret;
    }

    /**
     * 累加指定key的val
     * */
    public static void changeMap(Map<Integer, Integer> map, int key, int val) {
        Integer oldVal = map.get(key);
        if (oldVal == null) {
            oldVal = val;
            map.put(key, oldVal);
        } else {
            map.put(key, oldVal + val);
        }
    }

    public static void changeMap(Map<Integer, Integer> map, Map<Integer, Integer> param) {
        if (param.isEmpty()) {
            return;
        }
        param.forEach((k, v) -> {
            changeMap(map, k, v);
        });
    }

    public static Map<Integer, Integer> packageMap(int key, int value) {
        Map<Integer, Integer> map = Maps.newHashMap();
        map.put(key, value);
        return map;
    }

    private static String[] chars = new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N", "P", "Q", "R", "S",
            "T", "U", "V", "W", "X", "Y", "Z"};

    /**
     * 生成激活码字符串（8位字母及数字组合）
     *
     * @return
     */
    public static String generateShortUuid() {
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 34]);
        }
        return shortBuffer.toString();
    }

    /**
     * n选1
     * 
     * @param t_select(eg-> "a:10,b:20,c:70")
     * @return
     */
    public static String selectOneOfN(String t_select) {
        String ret = null;
        ArrayList<SelectOdd> selectOdds = new ArrayList<>();
        int total = 0;
        String[] select = StringUtils.split(t_select, ",");
        for (int i = 0; i < select.length; i++) {
            String[] strSelects = StringUtils.split(select[i], ":");
            if (strSelects.length < 2) {
                LOGGER.error("配置错误: " + t_select);
                return null;
            }
            String item = strSelects[0];
            int odd = Integer.parseInt(strSelects[1]);
            selectOdds.add(new SelectOdd(item, odd));
            total += odd;
        }
        for (SelectOdd odd : selectOdds) {
            odd.setOddReal((float) odd.getOdd() / (float) total);
        }
        float rand = new Random().nextFloat();
        float oddTmp = 0.0f;
        for (int a = 0; a < selectOdds.size(); a++) {
            SelectOdd dod = selectOdds.get(a);
            oddTmp += dod.getOddReal();
            if (rand <= oddTmp) {
                ret = dod.getItem();
                break;
            }
        }
        return ret;
    }

    /** 抽取概率封装 */
    private static class SelectOdd {
        // 元素
        private String item;
        // 权重
        private int odd;
        // 真实概率
        private float oddReal;

        public SelectOdd(String item, int odd) {
            this.item = item;
            this.odd = odd;
        }

        public String getItem() {
            return item;
        }

        public int getOdd() {
            return odd;
        }

        public float getOddReal() {
            return oddReal;
        }

        public void setOddReal(float oddReal) {
            this.oddReal = oddReal;
        }

    }
}
