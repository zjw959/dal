package logic.basecore;

import event.Event;
import event.IEventListener;
import logic.character.bean.Player;
import logic.constant.EEventType;

/**
 * 建筑解锁检测
 */
public interface ICheckUnlockBuilding extends IEventListener {

    @Override
    public default void registerPerformed(Player player) {
        player.registerEventListener(EEventType.CHECK_UNLOCK_BUILDING.value(), this);
    }

    @Override
    public default void eventPerformed(Event event) {
        final int eventId = event.getEventId();
        if (eventId == EEventType.CHECK_UNLOCK_BUILDING.value()) {
            check(event.getParams());
        }
    }

    abstract void check(Object object);
}
