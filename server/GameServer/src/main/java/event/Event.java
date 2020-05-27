package event;

import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;

import logic.constant.EventConditionKey;
import logic.task.bean.GameEventStack;

/**
 * 事件对象
 */
public class Event {
    public static final Logger LOGGER = Logger.getLogger(Event.class);
    /**
     * 事件ID
     */
    private int eventId;
    /**
     * 事件执行需要的参数对象
     */
    private Object params;
    // 触发条件栈
    private Stack<GameEventStack> eventStack;

    /**
     * @param eventId 事件ID
     * @param params 事件参数
     */
    public Event(int eventId, Object params) {
        this.eventId = eventId;
        this.params = params;
        this.eventStack = new Stack<>();
    }

    public void push(GameEventStack eventStack) {
        this.eventStack.push(eventStack);
    }

    public GameEventStack peek() {
        if (eventStack.empty()) {
            return null;
        }
        return eventStack.peek();
    }

    /**
     * 事件ID
     * 
     * @return
     */
    public int getEventId() {
        return eventId;
    }

    /**
     * 执行事件的事件对象
     * 
     * @return
     */
    public Object getParams() {
        return params;
    }
	/**
	 * 获取事件条件Id
	 */
	@SuppressWarnings("unchecked")
    public int getConditionId(){
		Map<String, Object> in =  (Map<String, Object>) params;
		if(in == null){
			return 0;
		}
		Integer conditionType =(Integer) in.get(EventConditionKey.CONDITION_TYPE);
		if(conditionType == null){
			return 0;
		}
		return conditionType;
	}
}
