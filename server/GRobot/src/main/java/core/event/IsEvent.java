package core.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @function 事件注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface IsEvent {
    /** 事件类型 **/
    public EventType eventT();

    /** 功能类型(只有当事件类型为重复请求类型时，才加入此参数，否则为空) **/
    public FunctionType functionT() default FunctionType.NULL;

    /** 事件编号 请求事件order为请求顺序 返回事件order为返回消息的msgid **/
    public int order();
}
