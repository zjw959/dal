package logic.store;

import java.util.List;

import org.game.protobuf.c2s.C2SStoreMsg.SellGoods;

import data.bean.CommodityCfgBean;
import logic.character.bean.Player;
import logic.constant.EAcrossDayType;
import script.IScript;

public abstract class IStoreScript implements IScript {

    protected abstract void buy(Player player, StoreManager stortManager, int cid, int num);

    protected abstract void refreshStore(Player player, StoreManager stortManager, int cid);

    protected abstract void getCommodityBuyLog(Player player, StoreManager stortManager);

    protected abstract void getStoreInfo(Player player, StoreManager stortManager,
            List<Integer> cids);

    protected abstract void autoRefresh(Player player, StoreManager stortManager);

    protected abstract void sell(Player player, List<SellGoods> sellList, int goodCount);

    protected abstract void checkAcrossDMY(Player player, StoreManager stortManager,
            EAcrossDayType type);

    protected abstract List<CommodityCfgBean> randomCommodity(Player player,
            StoreManager stortManager, int shopType, boolean isLevelUp);
}
