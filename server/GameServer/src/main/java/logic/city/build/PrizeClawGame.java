package logic.city.build;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.time.DateUtils;
import org.game.protobuf.c2s.C2SNewBuildingMsg.ReqStartGashapon;
import org.game.protobuf.s2c.S2CNewBuildingMsg;
import org.game.protobuf.s2c.S2CShareMsg.RewardsMsg;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;

import data.GameDataManager;
import data.bean.DiscreteDataCfgBean;
import data.bean.GashaponCfgBean;
import exception.AbstractLogicModelException;
import logic.character.bean.Player;
import logic.city.build.bean.PrizeClawRecord;
import logic.constant.DiscreteDataID;
import logic.constant.EReason;
import logic.constant.GameErrorCode;
import logic.item.ItemPackageHelper;
import logic.msgBuilder.NewBuildingMsgBuilder;
import logic.support.MessageUtils;
import utils.ToolMap;

/***
 * 抓娃娃游戏
 * 
 * @author lihongji
 *
 */
public class PrizeClawGame {

    public static PrizeClawGame getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private enum Singleton {
        INSTANCE;

        PrizeClawGame instance;

        private Singleton() {
            instance = new PrizeClawGame();
        }

        PrizeClawGame getInstance() {
            return instance;
        }
    }

//    Random random = new Random();

    /** 随机抓娃娃蛋池 */
    public JSONArray randomGashaponPool(String eggItems, String lineMin, String allMax) {
        String[] lines = eggItems.split("\\|");
        JSONArray arr = new JSONArray();
        // 蛋池最多出现
        Map<Integer, Integer> maxMap = new HashMap<Integer, Integer>();
        if (allMax != null && !"".equals(allMax)) {
            String[] allMaxs = allMax.split("\\|");

            for (String str : allMaxs) {
                String[] strs = str.split(":");
                int eggId = Integer.parseInt(strs[0]);
                int maxNum = Integer.parseInt(strs[1]);
                maxMap.put(eggId, maxNum);
            }
        }
        // 每行最少出现
        Map<Integer, Map<Integer, Integer>> lineMinMap =
                new HashMap<Integer, Map<Integer, Integer>>();
        if (lineMin != null && !"".equals(lineMin)) {
            String[] lineMins = lineMin.split("\\|");
            for (String str : lineMins) {
                String[] strs = str.split(":");
                int eggId = Integer.parseInt(strs[0]);
                Integer line = Integer.valueOf(strs[1]);
                int minNum = Integer.parseInt(strs[2]);
                Map<Integer, Integer> map = lineMinMap.get(line);
                if (map == null) {
                    map = new HashMap<Integer, Integer>();
                    lineMinMap.put(line, map);
                }
                map.put(eggId, minNum);
            }
        }
        // 每行按权重抽取
        Map<Integer, Integer> totalMap = new HashMap<Integer, Integer>();
        for (int i = 0; i < lines.length; i++) {
            String linestr = lines[i];
            JSONArray arrLine = new JSONArray();
            arr.add(arrLine);
            String[] line = linestr.split("\\+");
            int num = Integer.parseInt(line[0]);
            List<Integer> temp = new ArrayList<Integer>();
            Map<Integer, Integer> lineNumMap = new HashMap<Integer, Integer>();
            for (int j = 0; j < num; j++) {
                int v = Integer.parseInt(selectOneOfN(line[1]));
                temp.add(v);
                Integer value = lineNumMap.get(v);
                lineNumMap.put(v, value == null ? 1 : value + 1);
            }
            if (lineMinMap.containsKey(i + 1)) {// 本行有最少出现数量限制
                Map<Integer, Integer> tarMap = lineMinMap.get(i + 1);
                for (Integer id : tarMap.keySet()) {
                    int lineNum = lineNumMap.containsKey(id) ? lineNumMap.get(id) : 0;// 本行出现数量
                    int dis = tarMap.get(id) - lineNum;// 与目标数量差值
                    // 数量未达标,倒序替换
                    if (dis > 0) {
                        for (int j = temp.size() - 1; dis > 0 && j >= 0; j--) {
                            if (!tarMap.containsKey(temp.get(j))) {
                                temp.set(j, id);
                                dis--;
                            }
                        }
                    }
                }
            }
            for (int k = 0; k < temp.size(); k++) {
                int id = temp.get(k);
                if (maxMap.containsKey(id) && totalMap.containsKey(id)
                        && totalMap.get(id) >= maxMap.get(id)) {// 已达上限
                    // 重新组合随机抽取池，排除掉已达上限的id
                    String[] ss = line[1].split(",");
                    String tempStr = null;
                    for (int j = 0; j < ss.length; j++) {
                        String[] tt = ss[j].split(":");
                        if (maxMap.containsKey(Integer.valueOf(tt[0]))) {
                            continue;
                        }
                        tempStr = tempStr == null ? ss[j] : tempStr + "," + ss[j];
                    }
                    if (tempStr == null) {
                        continue;
                    }
                    id = Integer.parseInt(selectOneOfN(tempStr));
                }
                int uid = (i + 1) * 100 + (k + 1);// 唯一编号
                arrLine.add(uid + "_" + id);
                Integer value = totalMap.get(id);
                totalMap.put(id, value == null ? 1 : value + 1);
            }
        }
        return arr;
    }


    /**
     * n选1
     * 
     * @param t_select(eg-> "a:10,b:20,c:70")
     * @return
     */
    public String selectOneOfN(String t_select) {
        String ret = null;
        ArrayList<SelectOdd> selectOdds = new ArrayList<>();
        int total = 0;
        String[] select = t_select.split(",");
        for (int i = 0; i < select.length; i++) {
            String[] strSelects = select[i].split(":");
            if (strSelects.length < 2) {
                System.out.println("配置错误");
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


    public S2CNewBuildingMsg.RespStartGashapon.Builder reqStartGashapon(ReqStartGashapon req,
            PrizeClawRecord ga) throws AbstractLogicModelException {
        DiscreteDataCfgBean cfg = GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.GASHAPON);
        long now = System.currentTimeMillis();
        int cd = ToolMap.getInt("time", cfg.getData(), 20) * 1000;
        long endTime = now + cd;
        ga.setEndTime(endTime);
        return buildRespStartGashaponMsg(endTime).toBuilder();
    }


    public S2CNewBuildingMsg.RespStartGashapon buildRespStartGashaponMsg(long endTime) {
        S2CNewBuildingMsg.RespStartGashapon.Builder builder =
                S2CNewBuildingMsg.RespStartGashapon.newBuilder();
        builder.setCatchEndTime(endTime);
        return builder.build();
    }


    /** 重置抓娃娃蛋池 */
    public void resetGashaponPool(PrizeClawRecord prizeClawRecord) {
        JSONArray array = (JSONArray) GameDataManager
                .getDiscreteDataCfgBean(DiscreteDataID.GASHAPON).getData().get("eggs");
        List<Integer> ids = new ArrayList<Integer>();
        int poolId = prizeClawRecord.getPoolId();
        for (int i = 0; i < array.size(); i++) {
            String str = array.getString(i);
            int id = Integer.parseInt(str);
            if (id != poolId) {
                ids.add(id);
            }
        }
        if (ids.size() == 0) {
            ids.add(poolId);
        }
        // 新蛋池id
        int newPoolId = ids.get(new Random().nextInt(ids.size()));
        prizeClawRecord.setPoolId(newPoolId);
        GashaponCfgBean gcfg = GameDataManager.getGashaponCfgBean(newPoolId);
        // 新蛋池
        JSONArray arr = randomGashaponPool(gcfg.getEggItems(), gcfg.getLineMin(), gcfg.getAllMax());
        prizeClawRecord.setPool(arr.toString());
    }



    /** 刷新娃娃池 **/
    public void reqRefreshGashaponPool(PrizeClawRecord prizeClawRecord)
            throws AbstractLogicModelException {
        DiscreteDataCfgBean bean = GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.GASHAPON);
        // CD时间
        long cd = ToolMap.getInt("cd", bean.getData(), 24) * DateUtils.MILLIS_PER_HOUR;
        long endTime = System.currentTimeMillis() + cd;
        prizeClawRecord.setRefreshCD(endTime);
        // 刷新蛋池
        resetGashaponPool(prizeClawRecord);
    }



    @SuppressWarnings("unchecked")
    public S2CNewBuildingMsg.RespCheckGashaponResult.Builder reqCheckGashaponResult(List<Integer> cids,
            PrizeClawRecord prizeClawRecord, Player player) throws AbstractLogicModelException {
        int dis = 15000;// 容错时间
        // 超时检查
        if (System.currentTimeMillis() > prizeClawRecord.getEndTime() + dis)
            MessageUtils.throwCondtionError(GameErrorCode.ITEM_NOT_ENOUGH_CAN_NOT_PLAYER_GAME,
                    "道具不足不能玩游戏");
        String pool = prizeClawRecord.getPool();
        JSONArray arr = JSONArray.parseArray(pool);
        // 抓到的蛋
        List<Integer> eggIds = new ArrayList<Integer>();
        cids.forEach(cid -> {
            int line = cid / 100 - 1;
            JSONArray lineArr = arr.getJSONArray(line);
            String eggStr = null;
            if (lineArr != null) {
                for (int i = 0; i < lineArr.size(); i++) {
                    if (lineArr.getString(i).startsWith(String.valueOf(cid))) {
                        eggStr = lineArr.getString(i);
                        lineArr.remove(i);
                        break;
                    }
                }
                if (eggStr != null) {
                    eggIds.add(Integer.parseInt(eggStr.split("_")[1]));
                }
            }
        });
        // 封装掉落信息
        List<RewardsMsg> rewards = Lists.newArrayList();
        if (eggIds.size() > 0) {
            // 发放奖励
            Map<Integer, Integer> out = new HashMap<Integer, Integer>();
            eggIds.forEach(id -> {
                ItemPackageHelper.unpack(GameDataManager.getEggCfgBean(id).getUseProfit(), null, out);
            });
            // 添加道具
            // ItemManager.addGoodsAndPush(player, null, null, out, logDsp);
            out.forEach((k, v) -> {
                rewards.add(RewardsMsg.newBuilder().setId(k).setNum(v).build());
            });
            player.getBagManager().addItems(out, true, EReason.GIVE_BUILDING_GAME_PRIZECLAW);
            prizeClawRecord.setPool(arr.toString());
            prizeClawRecord.setEndTime(0);
        }
        // 蛋池被抓完后自动刷新蛋池
        boolean isEmpty = true;
        for (Object obj : arr.toArray()) {
            JSONArray lineArr = (JSONArray) obj;
            if (lineArr.size() > 0) {
                isEmpty = false;
                break;
            }
        }
        if (isEmpty) {
            resetGashaponPool(prizeClawRecord);// 自动刷新蛋池
        }
        return NewBuildingMsgBuilder.buildRespCheckGashaponResultMsg(prizeClawRecord.getPool(), eggIds,
                rewards);
    }

    /** 抽取概率封装 */
    private class SelectOdd {
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
