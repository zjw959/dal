package logic.elementCollection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.GameDataManager;
import data.bean.DiscreteDataCfgBean;
import logic.basecore.IRoleJsonConverter;
import logic.basecore.PlayerBaseFunctionManager;
import logic.character.bean.Player;
import logic.constant.DiscreteDataID;
import logic.constant.DiscreteDataKey;
import logic.support.LogicScriptsUtils;

/**
 * 
 * @Description 图鉴管理器
 * @author ZhaoJianBo
 *
 */
public class ElementCollectionManager extends PlayerBaseFunctionManager
        implements IRoleJsonConverter {
    // 图鉴收集的数据
    private Map<Integer, List<Integer>> elementDatas = new HashMap<Integer, List<Integer>>();

    /**
     * 获取所有图鉴
     */
    public void reqGetElementCollection() {
        LogicScriptsUtils.getIElementCollectionScript().reqGetElementCollection(player,
                elementDatas);
    }

    /**
     * 记录收集到的元素
     * 
     * @param player 玩家
     * @param type 元素类型
     * @param cid 元素cid
     */
    public void recordElement(Player player, int type, int cid, boolean check) {
        boolean canRecord = true;
        if (check) {
            canRecord = checkRecord(type);
        }
        if (!canRecord) {
            return;
        }
        LogicScriptsUtils.getIElementCollectionScript().recordElement(player, type, cid,
                elementDatas);
    }

    @SuppressWarnings("unchecked")
    private boolean checkRecord(int type) {
        DiscreteDataCfgBean discreteDataCfgBean =
                GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.COLLECT_ITEM_TYPES);
        Map<String, List<Integer>> data = discreteDataCfgBean.getData();
        if (data == null) {
            return false;
        }
        List<Integer> recordTypes = data.get(DiscreteDataKey.COLLECT_ITEM_TYPES);
        return recordTypes.contains(type);
    }
}
