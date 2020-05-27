package core.robot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import com.google.protobuf.Message.Builder;
import UI.Listener.RunFunctionListener;
import UI.window.frame.FunctionWindow;
import UI.window.frame.MainWindow;
import UI.window.frame.MessageWindow;
import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventScanner;
import core.event.FunctionType;
import core.net.connect.GClient;
import core.net.kcp.KcpClient;
import core.net.message.SMessage;
import io.netty.channel.Channel;
import logic.robot.entity.RobotPlayer;
import utils.ProtoBufUtils;
import utils.ProtoBufUtils.ProtoClientMessage;
import utils.RandomEx;

/**
 * @function 客户端机器人线程(方法)
 */
public class RobotThread {

    public boolean init = true;

    public void increaseRequestOnceCount() {
        preRequestOnceCount++;
    }

    public boolean requestOnceDone() {
        return preRequestOnceCount >= EventScanner.requestOnceEventCount;
    }

    public volatile boolean block = true;

    public boolean cancelled() {
        return cancel;
    }

    public void cancel() {
        if (!cancel) {
            cancel = true;
        }
    }

    public MainWindow getWindow() {
        return window;
    }

    /**
     * 提取消息序列
     */
    public int getAndAddMagicNum() {
        return ++magicNum;
    }

    /**
     * 提取消息序列
     */
    public int getMagicNum() {
        return magicNum;
    }

    /**
     * 初始化机器人事件
     */
    void initEvents(Map<Integer, AbstractEvent> requireOnceEvents,
            Map<Integer, AbstractEvent> responseEvents,
            Map<FunctionType, Map<Integer, AbstractEvent>> requestMultipleEvents,
            Map<FunctionType, Map<Integer, AbstractEvent>> requestOnceFunctionEvents) {


        this.preReqQueue = new LinkedBlockingQueue<>();
        this.reqQueue = new LinkedBlockingQueue<>();
        this.requestMultipleEvents = new HashMap<>();
        this.responseEvents = new HashMap<>();


        Iterator<Entry<Integer, AbstractEvent>> iterator = requireOnceEvents.entrySet().iterator();
        iterator.forEachRemaining(event -> {
            preReqQueue.offer(event.getValue());
            // System.out.println("-preReqQueue---event=" + event.getValue());
        });

        Iterator<Entry<FunctionType, Map<Integer, AbstractEvent>>> iterator_out =
                requestOnceFunctionEvents.entrySet().iterator();
        iterator_out.forEachRemaining(action_out -> {
            Map<Integer, AbstractEvent> eventMap = action_out.getValue();
            Iterator<Entry<Integer, AbstractEvent>> iterator_in = eventMap.entrySet().iterator();
            iterator_in.forEachRemaining(action_in -> reqQueue.offer(action_in.getValue()));
        });

        this.requestMultipleEvents = requestMultipleEvents;
        this.responseEvents = responseEvents;
    }

    /**
     * 构造器人线程
     * 
     * @param window
     */
    RobotThread(String account, MainWindow window) {
        this.window = window;
        this.account = account;
        this.player = new RobotPlayer(this);
    }

    /**
     * 机器人账号
     */
    private String account;

    /**
     * 机器人通信用管道
     */
    public Channel channel;
    /** 机器人与战斗服通信是用tcp还是kcp通信 */
    private volatile boolean isKcp;
    /**
     * 机器人和战斗服通信管道
     */
    private KcpClient kcpClient;
    /**
     * 机器人和战斗服通信管道
     */
    private Channel fightChannel;


    /**
     * 机器人数据承载对象
     */
    private RobotPlayer player;


    /**
     * 测试用字段
     **/
    public long sendingTime = 0;

    public int sendingmsg;

    /**
     * 消息添加到队列的时间
     */
    public long addTime;

    /**
     * 进入游戏的次数
     */
    public int enterTimes;

    public String token;
    public ArrayList<Integer> sendList = new ArrayList<Integer>();

    public ArrayList<Integer> recvList = new ArrayList<Integer>();


    /**
     * 当前指令是否跳出等待执行
     */
    public volatile boolean isNowSkip;

    /**
     * 连续跳出次数
     */
    public volatile AtomicLong isSkipContiueCount = new AtomicLong();

    public volatile AbstractEvent currentEvent;

    private String lastFunName;

    /**
     * 创建机器人连接
     */
    public void initChannel() {
        try {
            GClient.run(window, this);
        } catch (Exception e) {
            Log4jManager.getInstance().error(window, e);
        }
    }

    /**
     * 执行机器人线程
     * 
     * @param isSkip 跳过等待执行间隔,立即执行下一条指令
     */
    public void run(boolean isSkip) {
        if (isSkip) {
            this.isNowSkip = isSkip;
        }
        if (isSkip) {

            if (requestMultipleEvents.size() != 1 && !(window instanceof MessageWindow)) {
                this.isSkipContiueCount.incrementAndGet();
            }
        } else {
            this.isSkipContiueCount.set(0);
        }

        try {
            if (window instanceof FunctionWindow) {
                while (FunctionWindow.isFunPause()) {
                    Thread.sleep(1000);
                }
            }

            if (channel == null) {
                return;
            }

            if (!channel.isOpen()) {
                if (kcpClient != null && kcpClient.isRunning()) {
                    kcpClient.close();
                }

                if (fightChannel != null && fightChannel.isOpen()) {
                    fightChannel.close();
                }
                channel.close();
                return;
            }

            if (window.getRunButton().getText().equals("开始运行")) {
                return;
            }

            // 安全关闭robot任务
            if (this.cancelled()) {
                Log4jManager.getInstance().info(window, this.getName() + " tasks are cancelled.");
                if (kcpClient != null && kcpClient.isRunning()) {
                    kcpClient.close();
                }
                if (fightChannel != null && fightChannel.isOpen()) {
                    fightChannel.close();
                }
                channel.close();
                return;
            }

            // 预执行事件
            if (!preReqQueue.isEmpty() && !this.cancelled()) {
                AbstractEvent event = preReqQueue.poll();
                this.currentEvent = event;
                event.doAction();
                return;
            }

            // 安全关闭robot任务
            if (this.cancelled()) {
                Log4jManager.getInstance().info(window, this.getName() + " tasks are cancelled.");
                if (kcpClient != null && kcpClient.isRunning()) {
                    kcpClient.close();
                }
                if (fightChannel != null && fightChannel.isOpen()) {
                    fightChannel.close();
                }
                channel.close();
                return;
            }

            if (window instanceof FunctionWindow) {
                if (reqQueue.isEmpty()) {
                    if (requestMultipleEvents.size() > 0) {
                        // all the function types
                        ArrayList<FunctionType> _functionEnumTypes = new ArrayList<FunctionType>();
                        // repeatable events
                        Object[] _repeatedEventsKeyCopy = requestMultipleEvents.keySet().toArray();
                        // copy all the repeatable functions proto into list
                        for (int i = 0; i < _repeatedEventsKeyCopy.length; i++) {
                            _functionEnumTypes.add((FunctionType) _repeatedEventsKeyCopy[i]);
                        }
                        // 打乱功能模块顺序
                        if (_functionEnumTypes.size() > 1) {
                            if (RandomEx.getRandomBoolean100(70)) {
                                Collections.shuffle(_functionEnumTypes);
                            }
                        }
                        Map<Integer, AbstractEvent> eventMap = null;
                        Iterator<Entry<Integer, AbstractEvent>> iterator_in = null;
                        boolean isAllOver = true;
                        for (FunctionType function : _functionEnumTypes) {
                            // 检查是否已经执行了指定次数
                            if (exceedLimitCount(function)) {
                                continue;
                            }
                            // 将选中的功能放入请求队列中
                            eventMap = requestMultipleEvents.get(function);
                            iterator_in = eventMap.entrySet().iterator();
                            while (iterator_in.hasNext()) {
                                reqQueue.offer(iterator_in.next().getValue());
                            }
                            // 添加功能模块已执行次数
                            addDoCount(function);
                            isAllOver = false;
                        }
                        if (isAllOver) {
                            isAllFunOver = true;
                        }
                    } else {
                        isAllFunOver = true;
                    }
                    if (isAllFunOver) {
                        // 换下一个机器人来执行
                        Log4jManager.getInstance().info(window, this.account + " 所有功能模块执行完毕!");
                        if (kcpClient != null && kcpClient.isRunning()) {
                            kcpClient.close();
                        }
                        if (fightChannel != null && fightChannel.isOpen()) {
                            fightChannel.close();
                        }
                        channel.close();
                        return;
                    }
                }

                if (!reqQueue.isEmpty()) {
                    AbstractEvent event = reqQueue.poll();
                    this.currentEvent = event;
                    if (this.lastFunName != null
                            && !this.currentEvent.getFunctionInfo().equals(this.lastFunName)) {
                        this.isSkipContiueCount.set(0);
                    }
                    this.lastFunName = this.currentEvent.getFunctionInfo();
                    event.doAction();
                }
            } else if (window instanceof MessageWindow) {
                MessageWindow mw = (MessageWindow) window;
                // 自己选的Clientmessage集合
                List<ProtoClientMessage> protoClientMessages = null;
                // 当请求的builder 为空值时 重新加入builder
                if (reqBuilder.isEmpty()) {
                    // TODO 现在不支持 随机协议
                    if (mw.getMessageId() == null) {
                        // 随机取出一个协议 并 填充 发送
                        ProtoClientMessage message = EventScanner.getClientMessages()
                                .get(RandomEx.nextInt(EventScanner.getClientMessages().size()));
                        if (protoClientMessages == null)
                            protoClientMessages = new ArrayList<ProtoClientMessage>();
                        protoClientMessages.add(message);
                    } else {
                        // 获取选择的协议号 根据输入的值重新封装builder
                        String[] messages = mw.getMessageId().split(",");
                        for (String message : messages) {
                            Map<Integer, ProtoClientMessage> map =
                                    EventScanner.getClientMessageMap();
                            ProtoClientMessage pmessage = map.get(Integer.parseInt(message));
                            if (protoClientMessages == null)
                                protoClientMessages = new ArrayList<ProtoClientMessage>();
                            protoClientMessages.add(pmessage);
                        }
                    }
                    // 创建builder 并设置值
                    int index = 0;
                    String value = mw.getMessageInfo();
                    for (ProtoClientMessage protoClientMessage : protoClientMessages) {
                        Builder builder = ProtoBufUtils
                                .createBuilder(protoClientMessage.getBuilder().getClass());
                        ProtoBufUtils.setBuilderVaules(builder, value, index);
                        Map<Integer, Builder> map = new HashMap<Integer, Builder>();
                        map.put(protoClientMessage.getMsgId(), builder);
                        reqBuilder.add(map);
                        index++;
                    }
                }
                Map<Integer, Builder> show = reqBuilder.poll();
                show.forEach((messageId, builder) -> {
                    // 发送请求
                    SMessage msg = new SMessage(messageId, builder.build().toByteArray());
                    RunFunctionListener.addSendMsgPool(this, msg, true);
                });
            } else {
                Log4jManager.getInstance().error(window, "未知的功能类型!!!!!!");
            }
        } catch (Exception e) {
            Log4jManager.getInstance().error(window, e);
        }
    }

    /**
     * 添加响应消息
     */
    public void addRespMsg(SMessage receivMsg) {
        this.response(receivMsg);
    }

    /**
     * 机器人响应消息事件
     */
    private void response(SMessage msg) {

        AbstractEvent responseEvent = responseEvents.get(msg.getId());
        if (msg.getId() < 0) {
            return;
        }
        if (null == responseEvent) {
            return;
        }

        responseEvent.robot.recvList.add(msg.getId());
        // responseEvent.robot.currentEvent = responseEvent;
        responseEvent.doAction(msg.getData(), msg.getStatus());
        // 通过回调执行数据驱动
        int resOrder = responseEvent.robot.getResOrder();
        if ((resOrder != -1) && (resOrder != -2) && (resOrder == msg.getId())) {
            responseEvent.robot.run(false);
        }
    }

    /**
     * 检查某个功能模块是否已经达到限定次数
     */
    private boolean exceedLimitCount(FunctionType type) {

        if (window instanceof FunctionWindow) {
            if (FunctionWindow.isUnLimit()) {
                return false;
            }
        }

        // 如果包含该模块就继续执行，直接设置成执行次数达到上限,已达到类似移除了该功能的效果，
        // 下次再增加进来的时候会继续执行没有执行完的次数
        if (!GRobotManager.instance().containsAction(type)) {
            Log4jManager.getInstance().info(window, this.getName() + ":" + "停止执行消息: " + type.fName);
            if (functionExeucteStatistics.containsKey(type)) {
                functionExeucteStatistics.get(type).setPaused(true);
            }
            return true;
        }

        if (GRobotManager.instance().containsAction(type)) {
            if (functionExeucteStatistics.containsKey(type)) {
                functionExeucteStatistics.get(type).isPaused();
                // Log4jManager.getInstance().info(this.getName() + ":" + "继续执行消息: " + type.fName);
                functionExeucteStatistics.get(type).setPaused(false);
            }
        }

        if (type.fNum <= 0) {
            return false;
        }
        if (!functionExeucteStatistics.containsKey(type)) {
            return false;
        }
        Integer count = functionExeucteStatistics.get(type).getExecuteAmount();

        return count.intValue() >= type.fNum;
    }

    /**
     * 添加某个功能模块已执行次数
     */
    private void addDoCount(FunctionType type) {
        functionExeucteStatistics.putIfAbsent(type, new FunctionExecuteStatistics(type));
        functionExeucteStatistics.get(type).addExecuteAmount();
        functionExeucteStatistics.get(type).setLastExecuteTimestamp(System.currentTimeMillis());
    }

    /**
     * 删除机器人的当前功能,并清空当前的发送队列.
     */
    public void removeCurrentFun() {
        FunctionType currentType = currentEvent.getFunctionType();
        this.requestMultipleEvents.remove(currentType);
        this.reqQueue.clear();
    }

    /**
     * 数据驱动指定回调
     **/
    public void setResOrder(int resOrder) {
        this.resOrder = resOrder;
    }

    /**
     * 数据驱动指定回调
     **/
    public int getResOrder() {
        return this.resOrder;
    }

    public Queue<AbstractEvent> getReqQueue() {
        return reqQueue;
    }

    public Map<Integer, AbstractEvent> getResponseEvents() {
        return responseEvents;
    }

    public void setResponseEvents(Map<Integer, AbstractEvent> responseEvents) {
        this.responseEvents = responseEvents;
    }

    public String getName() {
        return account;
    }

    public void setName(String name) {
        this.account = name;
    }

    public Channel getChannel() {
        return channel;
    }

    public boolean isKcp() {
        return isKcp;
    }

    public void setKcp(boolean isKcp) {
        this.isKcp = isKcp;
    }

    public KcpClient getKcpClient() {
        return kcpClient;
    }

    public void setKcpClient(KcpClient kcpClient) {
        this.kcpClient = kcpClient;
    }

    public Channel getFightChannel() {
        return fightChannel;
    }

    public void setFightChannel(Channel fightChannel) {
        this.fightChannel = fightChannel;
    }

    public RobotPlayer getPlayer() {
        return player;
    }

    public void setPlayer(RobotPlayer player) {
        this.player = player;
    }

    /**
     * 是否所有功能执行完毕
     */
    public boolean isAllFunOver() {
        return isAllFunOver;
    }

    /**
     * 机器人各个功能块已经执行的次数
     */
    private class FunctionExecuteStatistics {

        @SuppressWarnings("unused")
        private FunctionType functionType;
        private Integer executeAmount;
        @SuppressWarnings("unused")
        private Long lastExecuteTimestamp;

        public boolean isPaused() {
            return paused;
        }

        public void setPaused(boolean paused) {
            this.paused = paused;
        }

        private boolean paused;

        public FunctionExecuteStatistics(FunctionType functionType) {
            this.functionType = functionType;
            this.executeAmount = 0;
            this.lastExecuteTimestamp = 0L;
        }


        public Integer getExecuteAmount() {
            return executeAmount;
        }

        public void addExecuteAmount() {
            this.executeAmount += 1;
        }

        public void setLastExecuteTimestamp(Long lastExecuteTimestamp) {
            this.lastExecuteTimestamp = lastExecuteTimestamp;
        }
    }

    private Map<FunctionType, FunctionExecuteStatistics> functionExeucteStatistics =
            new HashMap<>();

    /**
     * 是否所有功能模块已执行完毕
     */
    private boolean isAllFunOver;

    private MainWindow window;

    private volatile int preRequestOnceCount = 0;
    /**
     * 停止任务标记
     */
    private volatile boolean cancel = false;

    /**
     * 注册的当前等待回调
     */
    private int resOrder;

    /**
     * 机器人自身消息序列号(协议用)
     */
    private int magicNum = 0;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     * 机器人可重复请求事件
     */
    public Map<FunctionType, Map<Integer, AbstractEvent>> requestMultipleEvents = new HashMap<>();

    /**
     * 请求事件队列
     */
    private Queue<AbstractEvent> reqQueue = new LinkedBlockingQueue<>();

    /**
     * 预先(登陆流程)需要执行事件的队列
     */
    private Queue<AbstractEvent> preReqQueue = new LinkedBlockingQueue<>();

    /**
     * 机器人响应事件Map
     */
    private Map<Integer, AbstractEvent> responseEvents = new HashMap<>();


    /**
     * 单接口执行事件列队
     */

    private Queue<Map<Integer, Builder>> reqBuilder =
            new LinkedBlockingQueue<Map<Integer, Builder>>();

    public int shortTimeCount;

    public boolean isInCleanBag = false;

}
