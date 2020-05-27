package logic.basecore;

import event.Event;
import event.IEventListener;
import logic.character.bean.Player;
import logic.constant.EEventType;

/***
 * 
 * 任务活动检测
 * @author lihongji
 *
 */
public interface IActivityListener extends IEventListener{
    
    
    @Override
    public default void registerPerformed(Player player) {
        player.registerEventListener(EEventType.ACTIVITY_EVENT.value(), this);
        player.registerEventListener(EEventType.OPEN_ACTIVITY_EVENT.value(), this);
    }

    @Override
    public default void eventPerformed(Event event) {
        final int eventId = event.getEventId();
        if (eventId == EEventType.ACTIVITY_EVENT.value()) {
            execute(event);
        }
        else if(eventId==EEventType.OPEN_ACTIVITY_EVENT.value()){
            open();
        }
    }

    abstract void execute(Event event);
    
    abstract void open();

}
