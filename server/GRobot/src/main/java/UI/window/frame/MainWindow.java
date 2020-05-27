package UI.window.frame;


import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.concurrent.atomic.AtomicLong;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import UI.Listener.GmToolsListener;
import UI.Listener.HttpToolsListener;
import UI.Listener.TestMenuListener;
import UI.window.panel.ConsolePanel;
import UI.window.panel.RobotPanel;
import UI.window.panel.ServerPanel;
import core.robot.RobotThread;

public abstract class MainWindow {

    public MainWindow() {
        frame = new JFrame();

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBounds(0, 0, 1017, 21);
        frame.getContentPane().add(menuBar);

        JMenu testTypeMenu = new JMenu("测试类型");
        testTypeMenu.setFont(new Font("宋体", Font.PLAIN, 12));
        menuBar.add(testTypeMenu);

        JMenuItem functionMenuItem = new JMenuItem("功能测试");
        functionMenuItem.setFont(new Font("宋体", Font.PLAIN, 12));
        functionMenuItem.addActionListener(new TestMenuListener(frame));
        testTypeMenu.add(functionMenuItem);

        JMenuItem messageMenuItem = new JMenuItem("消息测试");
        messageMenuItem.setFont(new Font("宋体", Font.PLAIN, 12));
        messageMenuItem.addActionListener(new TestMenuListener(frame));
        testTypeMenu.add(messageMenuItem);

        JMenu toolsMenu = new JMenu("工具");
        toolsMenu.setFont(new Font("宋体", Font.PLAIN, 12));
        menuBar.add(toolsMenu);

        JMenuItem gmMenuItem = new JMenuItem("GM工具");
        gmMenuItem.setFont(new Font("宋体", Font.PLAIN, 12));
        gmMenuItem.addActionListener(new GmToolsListener(frame));
        toolsMenu.add(gmMenuItem);

        JMenuItem httpMenuItem = new JMenuItem("HTTP功能开关工具");
        httpMenuItem.setFont(new Font("宋体", Font.PLAIN, 12));
        httpMenuItem.addActionListener(new HttpToolsListener(frame));
        toolsMenu.add(httpMenuItem);

        topChose = new JCheckBox("是否置顶");
        topChose.setSelected(true);
        topChose.setBounds(10, 29, 87, 23);
        topChose.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                frame.setAlwaysOnTop(topChose.isSelected());
            }
        });

        frame.add(topChose);

        runButton = new JButton("开始运行");
        runButton.setName("runbutton");
        runButton.setFont(new Font("宋体", Font.PLAIN, 12));
        runButton.setBounds(27, 496, 120, 23);
        frame.getContentPane().add(runButton);

        getConsolePanel().setLocation(417, 113);
        frame.getContentPane().add(getConsolePanel());

        getServerPanel().setLocation(413, 31);
        frame.getContentPane().add(getServerPanel());

        getRobotPanel().setLocation(8, 112);
        frame.getContentPane().add(getRobotPanel());
    }

    /** 一堆计数器 **/
    protected final AtomicLong nowConnections = new AtomicLong();
    protected final AtomicLong nowLogined = new AtomicLong();
    protected final AtomicLong totConnections = new AtomicLong();
    protected final AtomicLong logicSendMsgs = new AtomicLong();
    protected final AtomicLong completeSendMsgs = new AtomicLong();
    protected final AtomicLong receiveMsgs = new AtomicLong();

    public final AtomicLong chasmSendMsgs = new AtomicLong();
    public final AtomicLong chasmCompleteSendMsgs = new AtomicLong();
    public final AtomicLong chasmReceiveMsgs = new AtomicLong();

    public void setVisible(boolean isVisible) {
        frame.setVisible(isVisible);
    }

    public void setAlltoZero() {
        nowConnections.set(0);
        nowLogined.set(0);
        totConnections.set(0);
        logicSendMsgs.set(0);
        completeSendMsgs.set(0);
        receiveMsgs.set(0);
    }

    public long getNowConnections() {
        return nowConnections.get();
    }

    /**
     * 建立新的连接
     */
    public void addConnection() {
        nowConnections.incrementAndGet();
    }

    public void addConnectionTots() {
        totConnections.incrementAndGet();
    }

    public long getConnectionTots() {
        return totConnections.get();
    }


    public void addLogined() {
        nowLogined.incrementAndGet();
    }

    public long getLogined() {
        return nowLogined.get();
    }


    /**
     * 连接断开
     * 
     * @param robot
     */
    public void closeConnection(RobotThread robot) {
        nowConnections.decrementAndGet();
        if (robot.getPlayer().getIsLogin()) {
            nowLogined.decrementAndGet();
        }
    }

    /**
     * 逻辑发送消息数据量
     */
    public void addSendLogicMsgs() {
        logicSendMsgs.incrementAndGet();
    }

    /**
     * 逻辑发送消息数据量
     */
    public long getSendLogicMsgs() {
        return logicSendMsgs.get();
    }

    /**
     * 刷新发送成功消息数据量
     */
    public void addCompleteSendMsgs() {
        completeSendMsgs.incrementAndGet();
    }

    /**
     * 获取发送成功消息数据量
     */
    public long getSendMsgs() {
        return completeSendMsgs.get();
    }

    /**
     * 刷新接收消息数据量
     */
    public void addReceiveMsgs() {
        receiveMsgs.incrementAndGet();
    }

    /**
     * 刷新接收消息数据量
     */
    public long getReceiveMsgs() {
        return receiveMsgs.get();
    }


    public RobotPanel getRobotPanel() {
        if (robotPanel == null) {
            robotPanel = new RobotPanel();
        }
        return robotPanel;
    }

    // TODO 取消直接获取
    public JButton getRunButton() {
        return runButton;
    }

    public void serBeginStart() {

    }

    public ConsolePanel getConsolePanel() {
        if (consolePanel == null) {
            consolePanel = new ConsolePanel();
        }
        return consolePanel;
    }

    public ServerPanel getServerPanel() {
        if (serverPanel == null) {
            serverPanel = new ServerPanel();
            // serverPanel.setSize(400, 80);
        }
        return serverPanel;
    }

    protected JFrame frame;
    protected RobotPanel robotPanel;
    protected JButton runButton;
    protected JCheckBox topChose;
    private ConsolePanel consolePanel;
    private ServerPanel serverPanel;

}
