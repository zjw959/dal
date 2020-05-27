package event;

/**
 * 时间分发器
 */
public interface IEventDispatcher {

    /**
     * 向指定的事件注册监听器，如果需要注册的 listener 已经存在则跳过
     * 
     * @param eventId 事件ID
     * @param listener 事件对应的监听器
     */
    public void registerEventListener(int eventId, IEventListener listener);

    /**
     * 移除指定事件{@param eventId} 下注册的所有监听器
     * 
     * @param eventId
     */
    public void removeAllEventListener(int eventId);

    /**
     * 移除指定事件下注册的指定监听器
     * 
     * @param eventId 事件ID
     * @param listener 需要移除的事件监听器
     */
    public void removeEventListener(int eventId, IEventListener listener);

    /**
     * 方法在 fireEvent 线程执行所有 监听器方法 如果涉及到跨线程调用 ，可以在listener 事件执行方法中执行任务分发
     * 
     * @param event
     * @throws Exception
     * @note 目前框架中线程 基类的实现有多种，不合适在此处统一根据监听器 指定线程执行事件逻辑
     */
    public void fireEvent(Event event);

    /**
     * 方法在 fireEvent 线程执行所有 监听器方法 如果涉及到跨线程调用 ，可以在listener 事件执行方法中执行任务分发
     * 
     * @param params
     * @param eventId
     * @throws Exception
     * @note 目前框架中线程 基类的实现有多种，不合适在此处统一根据监听器 指定线程执行事件逻辑
     */
    public void fireEvent(Object params, int eventId);
}
