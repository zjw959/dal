package logic.basecore;

import event.Event;
import event.IEventListener;
import logic.character.bean.Player;
import logic.constant.EEventType;

/***
 * 约会(时装，道具)事件触发
 * 
 * @author lihongji
 *
 */
public interface ITriggerEvent extends IEventListener {


    @Override
    public default void registerPerformed(Player player) {
        player.registerEventListener(EEventType.TRIGGER_CHECK.value(), this);
    }

    @Override
    public default void eventPerformed(Event event) {
        final int eventId = event.getEventId();
        if (eventId == EEventType.TRIGGER_CHECK.value()) {
            trigger(event.getParams());
        }
    }

    abstract void trigger(Object object);

}
