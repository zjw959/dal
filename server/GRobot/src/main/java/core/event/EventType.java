package core.event;

/**
 * @function 事件类型枚举
 */
public enum EventType {
    NOT_DEFINED,
    /** 只可请求一次的事件 (前提流程) **/
    REQUSET_ONCE,
    /** 可请求多次的事件 (模板) **/
    REQUSET_REPEAT,
    /** 响应事件 **/
    RESPONSE,
}
