package logic.basecore;

import event.Event;
import event.IEventListener;
import logic.character.bean.Player;
import logic.constant.EEventType;

/***
 * 
 * 建筑建筑影响功能事件监听
 * 
 * @author lihongji
 *
 */
public interface IUnlockBuilding extends IEventListener {

    @Override
    public default void registerPerformed(Player player) {
        player.registerEventListener(EEventType.UNLOCK_BUILDING.value(), this);
    }

    @Override
    public default void eventPerformed(Event event) {
        final int eventId = event.getEventId();
        if (eventId == EEventType.UNLOCK_BUILDING.value()) {
            unlock(event.getParams());
        }
    }

    abstract void unlock(Object object);


}
