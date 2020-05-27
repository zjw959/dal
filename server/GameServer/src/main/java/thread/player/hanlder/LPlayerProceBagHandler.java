package thread.player.hanlder;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import logic.bag.BagManager;
import logic.character.bean.Player;
import logic.constant.EReason;
import logic.item.bean.Item;
import thread.base.GameBaseProcessor;
import thread.base.LBaseCBHandler;
import thread.base.LProcessCBHandler;

/**
 * 在玩家线程进行背包操作handler
 */
public class LPlayerProceBagHandler extends LProcessCBHandler {
    private static final Logger LOGGER = Logger.getLogger(LPlayerProceBagHandler.class);

    private final EReason eReason;
    private final Map<Integer, Integer> addItems;
    private final Map<Integer, Integer> removeItems;

    public LPlayerProceBagHandler(Player player, Map<Integer, Integer> addItems,
            EReason eReason) {
        super(player, null, null);
        this.addItems = addItems;
        this.eReason = eReason;
        this.removeItems = null;
    }

    public LPlayerProceBagHandler(Player player, Map<Integer, Integer> addItems,
            GameBaseProcessor cp, LBaseCBHandler cbHandler, EReason eReason) {
        super(player, cp, cbHandler);
        this.addItems = addItems;
        this.eReason = eReason;
        this.removeItems = null;
    }

    public LPlayerProceBagHandler(Player player, Map<Integer, Integer> addItems,
            Map<Integer, Integer> removeItems, GameBaseProcessor cp,
            LBaseCBHandler cbHandler, EReason eReason) {
        super(player, cp, cbHandler);
        this.addItems = addItems;
        this.removeItems = removeItems;
        this.eReason = eReason;
    }


    @Override
    public void action() {
        boolean isSuccess = true;

        BagManager bag = this.player.getBagManager();
        if (this.removeItems != null) {
            boolean _is =
                    bag.removeItemsByTemplateIdWithCheck(this.removeItems, true, this.eReason);
            if (!_is) {
                LOGGER.error("removeItems faild. items:" + this.removeItems.toString() + " "
                        + this.player.logInfo());
                isSuccess = false;
            }
        }

        if (isSuccess) {
            if (this.addItems != null && !this.addItems.isEmpty()) {
                List<Item> _items = bag.addItems(this.addItems, true, this.eReason);
                if (_items.size() == 0) {
                    LOGGER.error("addItems faild. items:" + this.addItems.toString() + " "
                            + this.player.logInfo());
                    isSuccess = false;
                }
            }
        }

        _doCallBack(isSuccess);
    }

}
