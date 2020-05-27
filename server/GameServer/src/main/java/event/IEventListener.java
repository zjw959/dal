package event;

import logic.character.bean.Player;

/**
 * 事件监听器
 */
public interface IEventListener {

    /**
     * 事件注册
     * 
     * @param event
     * @throws Exception
     */
    void registerPerformed(Player player);

    /**
     * 事件响应
     * 
     * @param event
     * @throws Exception
     */
    void eventPerformed(Event event);
}
