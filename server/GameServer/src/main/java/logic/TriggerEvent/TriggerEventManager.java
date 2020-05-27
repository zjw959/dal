package logic.TriggerEvent;

import event.Event;
import event.IEventListener;
import logic.basecore.PlayerBaseFunctionManager;
import logic.character.bean.Player;
import logic.support.LogicScriptsUtils;

/**
 * 
 * @Description 事件触发与结果(此功能所有的触发事件,已在其他功能里面有处理,registerPerformed不用注册事件)
 * @author ZhaoJianBo
 * 
 */
public class TriggerEventManager extends PlayerBaseFunctionManager implements IEventListener {
    protected transient Player player;

    @Override
    public void registerPerformed(Player player) {

    }

    @Override
    public void eventPerformed(Event event) {
        LogicScriptsUtils.getITriggerEventScript().eventPerformed(player, event);
    }

}
