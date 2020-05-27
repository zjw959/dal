package core.event;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import UI.window.frame.FunctionWindow;
import core.robot.GRobotManager;
import utils.MapEx;
import utils.PackageScanner;
import utils.ProtoBufUtils;
import utils.ProtoBufUtils.ProtoClientMessage;

/**
 * @function 事件扫描器
 */
public final class EventScanner {

    public static int requestOnceEventCount = 0;

    /**
     * 响应事件类模板集合
     */
    private static List<Class<? extends AbstractEvent>> respEventClazzs = new ArrayList<>();

    /**
     * 可请求多次的事件类模板集合
     */
    private static Map<FunctionType, List<Class<? extends AbstractEvent>>> reqMultipleEventClazzs =
            new HashMap<>();

    /**
     * 只可请求一次的事件类模板集合
     */
    private static List<Class<? extends AbstractEvent>> reqOnceEventClazzs = new ArrayList<>();


    /**
     * 客户端消息
     */
    private static Map<Integer, ProtoClientMessage> clientMessageMap =
            new HashMap<Integer, ProtoClientMessage>();

    /**
     * 客户端消息
     */
    private static List<ProtoClientMessage> clientMessages = new ArrayList<ProtoClientMessage>();

    /**
     * 响应消息
     */
    private static Map<Integer, Class<?>> respMessageMap =
            new ConcurrentHashMap<Integer, Class<?>>();
    
    /**
     * 已选择的多次请求事件类模板集合
     */
    private static Map<FunctionType, List<Class<? extends AbstractEvent>>> selectedReqMultipleEventClazzs =
            new HashMap<>();

    /**
     * 已选择的一次请求事件类模板集合
     */
    private static Map<FunctionType, List<Class<? extends AbstractEvent>>> selectedReqOnceEventClazzs =
            new HashMap<>();

    public static List<Class<? extends AbstractEvent>> getResponseEventClazzs() {
        return respEventClazzs;
    }

    public static Map<FunctionType, List<Class<? extends AbstractEvent>>> getRequestMultipleEventClazzs() {
        return reqMultipleEventClazzs;
    }

    public static List<Class<? extends AbstractEvent>> getRequestOnceEventClazzs() {
        return reqOnceEventClazzs;
    }

    public static Map<Integer, ProtoClientMessage> getClientMessageMap() {
        return clientMessageMap;
    }

    public static List<ProtoClientMessage> getClientMessages() {
        return clientMessages;
    }
    
    public static Class<?> getRespMessageClass(int msgId) {
        return respMessageMap.get(msgId);
    }

    public static Map<FunctionType, List<Class<? extends AbstractEvent>>> getSelectedRequestMultipleEventClazzs() {
        return selectedReqMultipleEventClazzs;
    }

    public static Map<FunctionType, List<Class<? extends AbstractEvent>>> getSelectedRequestOnceEventClazzs() {
        return selectedReqOnceEventClazzs;
    }

    /**
     * 初始化加载事件类模板
     */
    public static void initEventClazzs() {
        requestOnceEventCount = 0;
        Set<Class<?>> clazzs = PackageScanner.getClasses("logic");
        Iterator<Class<?>> it = clazzs.iterator();
        Class<?> clazz = null;
        FunctionType functionType = null;
        while (it.hasNext()) {
            clazz = it.next();
            IsEvent isEventClazz = clazz.getAnnotation(IsEvent.class);
            if (null != isEventClazz) {
                List<Class<? extends AbstractEvent>> clazzList = null;
                switch (isEventClazz.eventT()) {
                    case RESPONSE:
                        respEventClazzs.add((Class<? extends AbstractEvent>) clazz);
                        break;
                    case REQUSET_REPEAT:
                        functionType = isEventClazz.functionT();
                        clazzList = reqMultipleEventClazzs.get(functionType);
                        if (null == clazzList) {
                            clazzList = new ArrayList<>();
                            reqMultipleEventClazzs.put(functionType, clazzList);
                        }
                        clazzList.add((Class<? extends AbstractEvent>) clazz);
                        break;
                    case REQUSET_ONCE:
                        requestOnceEventCount++;
                        reqOnceEventClazzs.add((Class<? extends AbstractEvent>) clazz);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 初始化消息
     * 
     * @throws Exception
     */
    public static void initMessageClazzs() throws Exception {
        Set<Class<?>> clazzs = PackageScanner.getClasses("org.game.protobuf.c2s");
        Iterator<Class<?>> it = clazzs.iterator();
        Class<?> clazz = null;
        while (it.hasNext()) {
            clazz = it.next();
            //TODO 过滤登陆相关协议

            Map<Integer, ProtoClientMessage> _clazzs = ProtoBufUtils.parseClientMessage(clazz);
            //TODO 根据协议ID过滤
            clientMessageMap.putAll(_clazzs);
        }
        clientMessages = MapEx.valueToList(clientMessageMap);
    }

    /**
     * 初始化消息
     * 
     * @throws Exception
     */
    public static void initRespMessageClazzs() throws Exception {
        Set<Class<?>> clazzs = PackageScanner.getClasses("org.game.protobuf.s2c");
        Iterator<Class<?>> it = clazzs.iterator();
        Class<?> clazz = null;
        while (it.hasNext()) {
            clazz = it.next();
            // 解析此proto中所有message
            for (Class<?> msgClass : clazz.getClasses()) {
                // 解析message中的对象
                for (Class<?> innerClass : msgClass.getClasses()) {
                    // 如果该message中包含枚举类 MsgID, 就证明是属于交互数据的对象,而非单独的消息体
                    if (innerClass.getSimpleName().equals("MsgID")) {
                        // 获取 CS 类型的消息体
                        for (Object obj : innerClass.getEnumConstants()) {
                            // protobuf的枚举类型包含 value 和 index
                            // 获取"private"类型的方法"values"
                            Method m = obj.getClass().getDeclaredMethod("values", null);
                            Object[] result = (Object[]) m.invoke(obj, null);
                            List<?> list = Arrays.asList(result);
                            Iterator<?> temp = list.iterator();
                            while (temp.hasNext()) {
                                Object objOne = temp.next();
                                Field value = objOne.getClass().getDeclaredField("value");

                                // 设置为可强制访问
                                value.setAccessible(true);
                                respMessageMap.put(value.getInt(objOne), msgClass);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public static void setFunctionMultpleEvents() {
        List<String> fNames = new ArrayList<>();
        for (int i = 0; i < FunctionWindow.selectedList.size(); i++) {
            fNames.add(FunctionWindow.selectedList.get(i));
        }
        selectedReqMultipleEventClazzs = new HashMap<>();
        if (null != fNames && fNames.size() > 0) {
            fNames.forEach(fName -> {
                Iterator<Entry<FunctionType, List<Class<? extends AbstractEvent>>>> multipleIterator =
                        reqMultipleEventClazzs.entrySet().iterator();
                multiple_in_loop: while (multipleIterator.hasNext()) {
                    Entry<FunctionType, List<Class<? extends AbstractEvent>>> entry =
                            multipleIterator.next();
                    if (entry.getKey().fName.equals(fName.split("_")[0])) {
                        selectedReqMultipleEventClazzs.putIfAbsent(entry.getKey(),
                                entry.getValue());
                        break multiple_in_loop;
                    }
                }
            });
        }
        Set<FunctionType> functionTypes = new HashSet<FunctionType>();
        for (String s : fNames) {
            for (FunctionType functionType : FunctionType.values()) {
                if (s.split("_")[0].equals(functionType.fName)) {
                    functionTypes.add(functionType);
                }
            }
        }
        GRobotManager.instance().refill(functionTypes);
    }
}
