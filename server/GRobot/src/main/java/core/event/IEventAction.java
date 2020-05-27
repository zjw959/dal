package core.event;

/**
 * @function 事件执行接口
 */
public interface IEventAction {
    /**
     * 执行事件
     * 
     * @param channel 通信管道
     * @param actionArgs 事件执行参数
     */
    public void action(Object... obj) throws Exception;
}
