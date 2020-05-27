package event;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import utils.ExceptionEx;

/**
 * 简单的事件派发器
 */
public class SimpleEventDispatcher implements IEventDispatcher {

    public static final Logger LOGGER = Logger.getLogger(SimpleEventDispatcher.class);

    /**
     * 事件监听器列表 Key: 事件ID Value: 监听该事件的监听器列表
     */
    private Map<Integer, List<IEventListener>> eventListeners;


    public SimpleEventDispatcher() {
        this.eventListeners = new ConcurrentHashMap<>();
    }

    /**
     * 向指定的事件注册监听器，如果需要注册的 listener 已经存在则跳过
     * 
     * @param eventId 事件ID
     * @param listener 事件对应的监听器
     */
    public void registerEventListener(int eventId, IEventListener listener) {
        List<IEventListener> listeners = this.eventListeners.get(eventId);
        if (listeners == null) {
            listeners = Collections.synchronizedList(new LinkedList<IEventListener>());
            this.eventListeners.put(eventId, listeners);
        }
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * 移除指定事件{@param eventId} 下注册的所有监听器
     * 
     * @param eventId
     */
    public void removeAllEventListener(int eventId) {
        this.eventListeners.remove(eventId);
    }

    /**
     * 移除指定事件下注册的指定监听器
     * 
     * @param eventId 事件ID
     * @param listener 需要移除的事件监听器
     */
    public void removeEventListener(int eventId, IEventListener listener) {
        List<IEventListener> listeners = this.eventListeners.get(eventId);
        if (listeners != null) {
            if (listeners.contains(listener)) {
                listeners.remove(listener);
            }
        }
    }

    /**
     * 方法在 fireEvent 线程执行所有 监听器方法 如果涉及到跨线程调用,可以在listener 事件执行方法中执行任务分发
     * 
     * @param event
     * @note 目前框架中线程 基类的实现有多种，不合适在此处统一根据监听器 指定线程执行事件逻辑
     */
    public void fireEvent(Event event) {
        List<IEventListener> listeners = this.eventListeners.get(event.getEventId());
        if (listeners == null) {
            LOGGER.error(
                    String.format("can not find event listener for eventId:%s", event.getEventId())
                            + ExceptionEx.currentThreadTraces());
            return;
        }
        for (IEventListener listener : listeners) {
            try {
                listener.eventPerformed(event);
            } catch (Exception e) {
                LOGGER.error("fire event error. event type :" + event.getEventId()
                        + " event params :" + event.getParams() + ExceptionEx.e2s(e));
            }
        }
    }

    /**
     * 方法在 fireEvent 线程执行所有 监听器方法 如果涉及到跨线程调用 , 可以在listener 事件执行方法中执行任务分发
     * 
     * @param params
     * @param eventId
     * @throws Exception
     * @note 目前框架中线程 基类的实现有多种，不合适在此处统一根据监听器 指定线程执行事件逻辑
     */
    public void fireEvent(Object params, int eventId) {
        fireEvent(new Event(eventId, params));
    }


    /**
     * 判断指定事件是否存在{@param eventId}
     * 
     * @param eventId
     */
    public boolean isExistEventListener(int eventId, IEventListener listener) {
        List<IEventListener> listeners = this.eventListeners.get(eventId);
        if (listeners != null) {
            if (listeners.contains(listener)) {
                return true;
            }
        }
        return false;
    }
}
