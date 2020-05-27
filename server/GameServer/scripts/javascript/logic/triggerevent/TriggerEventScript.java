package javascript.logic.triggerevent;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import data.GameDataManager;
import data.bean.EventConditionCfgBean;
import data.bean.TriggerEventCfgBean;
import event.Event;
import event.IListener;
import logic.TriggerEvent.ITriggerEventScrip;
import logic.character.bean.Player;
import logic.constant.EEventType;
import logic.constant.EScriptIdDefine;
import logic.constant.ElementCollectionConstant;
import logic.task.bean.GameEventStack;
import utils.ExceptionEx;

/**
 * @author zhaojianbo
 * 
 */
public class TriggerEventScript extends ITriggerEventScrip {
    private static final Logger LOGGER = Logger.getLogger(TriggerEventScript.class);


    private static final Logger log = Logger.getLogger(TriggerEventScript.class);

    @Override
    public int getScriptId() {
        return EScriptIdDefine.TRIGGER_EVENT_SCRIPT.Value();
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected void eventPerformed(Player player, Event event) {
        int conditionId = event.getConditionId();
        if (conditionId == 0) {
            log.info("conditionId == 0  player:" + player.getPlayerName());
            return;
        }
        List<TriggerEventCfgBean> triggerEventList =
                GameDataManager.getTriggerEventCfgContainer().getList();
        List<TriggerEventCfgBean> triggerConditionList = triggerEventList.stream()
                .filter(e -> e.getEventConditionId() == conditionId).collect(Collectors.toList());
        for (TriggerEventCfgBean cfgBean : triggerConditionList) {
            EventConditionCfgBean eventConditionCgf =
                    GameDataManager.getEventConditionCfgBean(cfgBean.getEventConditionId());
            if (eventConditionCgf == null) {
                log.error("eventConditionCgf == null  id:" + cfgBean.getEventConditionId());
                return;
            }
            Map params = cfgBean.getParams();
            boolean trigger = (params == null || params.isEmpty());
            if (trigger) {
                eventResult(player, event, cfgBean.getResult(), null);
                // 图鉴收集
                player.getElementCollectionManager().recordElement(player,
                        ElementCollectionConstant.KEY_EVENT, cfgBean.getId(), false);
            } else {
                EEventType type = EEventType.gerEEventType(event.getEventId());
                IListener listener = null;
                try {
                    listener = type.getListener().newInstance();
                } catch (Exception e) {
                    LOGGER.error(ExceptionEx.e2s(e));
                }
                event.push(
                        new GameEventStack(event.getEventId(), conditionId, params, 0, 0, trigger));
                listener.onEvent(event, player);
                GameEventStack eventStack = event.peek();
                // 条件触发成功
                if (eventStack.isTrigger()) {
                    eventResult(player, event, cfgBean.getResult(), null);
                    // 图鉴收集
                    player.getElementCollectionManager().recordElement(player,
                            ElementCollectionConstant.KEY_EVENT, cfgBean.getId(), false);
                }
            }
        }
    }
}
