package UI.window.frame;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import UI.Listener.RunFunctionListener;
import UI.Listener.SelecetFunctionListener;
import core.event.AbstractEvent;
import core.event.EventScanner;
import core.event.FunctionType;
import utils.MiscUtils;

/**
 * 
 * @function 功能测试窗口
 */
public class FunctionWindow extends MainWindow {

    public static DefaultListModel<String> waitingList = new DefaultListModel<>();
    public static DefaultListModel<String> selectedList = new DefaultListModel<>();

    public static FunctionWindow getInstance() {
        return Singleton.INSTANCE.getProcessor();
    }

    public JTextField getRobotMaxTextField() {
        return robotMaxTextField;
    }

    private FunctionWindow() {
        super();

        frame.setTitle("功能测试");
        frame.getContentPane().setFont(new Font("宋体", Font.PLAIN, 12));
        frame.setResizable(false);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds((int) (dimension.getWidth() / 2 - (1023 >> 1)),
                (int) (dimension.getHeight() / 2 - (576 >> 1)), 1023, 576);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBounds(0, 0, 1017, 21);
        frame.getContentPane().add(menuBar);

        JLabel titleLabel = new JLabel("功能测试");
        titleLabel.setFont(new Font("宋体", Font.BOLD, 20));
        titleLabel.setBounds(230, 29, 107, 26);
        frame.getContentPane().add(titleLabel);

        JLabel desLabel = new JLabel("");
        desLabel.setFont(new Font("宋体", Font.PLAIN, 18));
        desLabel.setBounds(198, 33, 186, 23);
        frame.getContentPane().add(desLabel);

        JButton chooseFunctionButton = new JButton("增删运行功能");
        chooseFunctionButton.setFont(new Font("宋体", Font.PLAIN, 12));
        chooseFunctionButton.setBounds(27, 444, 120, 23);
        chooseFunctionButton.addActionListener(new SelecetFunctionListener(this));
        frame.getContentPane().add(chooseFunctionButton);

        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane_1.setBounds(200, 159, 187, 382);
        frame.getContentPane().add(scrollPane_1);

        functionChooseArea = new JTextArea();
        scrollPane_1.setViewportView(functionChooseArea);
        functionChooseArea.setLineWrap(true);
        functionChooseArea.setEditable(false);
        functionChooseArea.setFont(new Font("宋体", Font.PLAIN, 12));
        initFunctionMode();


        JLabel label = new JLabel("已选功能列表:");
        label.setFont(new Font("宋体", Font.PLAIN, 12));
        label.setBounds(200, 65, 122, 15);
        frame.getContentPane().add(label);

        pauseCheckBox.setName("pauseCheckBox");
        pauseCheckBox.setBounds(200, 122, 93, 23);
        frame.getContentPane().add(pauseCheckBox);

        unlimitCheckBox.setName("unlimitCheckBox");
        unlimitCheckBox.setBounds(200, 92, 107, 23);
        frame.getContentPane().add(unlimitCheckBox);

        runButton.addActionListener(new RunFunctionListener(this));

        frame.setVisible(true);
    }

    /**
     * 显示已选择的功能
     */
    public void showChooseMode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < selectedList.size(); i++) {
            sb.append(selectedList.get(i) + "\r\n");
        }
        functionChooseArea.setText(sb.toString());
        frame.repaint();
    }

    public static boolean isFunPause() {
        return pauseCheckBox.isSelected();
    }

    public static boolean isUnLimit() {
        return unlimitCheckBox.isSelected();
    }

    private JTextField robotMaxTextField;
    private static JTextArea functionChooseArea;
    private static JCheckBox pauseCheckBox = new JCheckBox("暂停开关");
    private static JCheckBox unlimitCheckBox = new JCheckBox("强制无限次");

    /**
     * 用枚举来实现单例
     */
    private enum Singleton {

        INSTANCE;

        FunctionWindow processor;

        Singleton() {
            this.processor = new FunctionWindow();
        }

        FunctionWindow getProcessor() {
            return processor;
        }
    }

    /**
     * 初始化功能模块选项
     */
    private void initFunctionMode() {
        Map<FunctionType, List<Class<? extends AbstractEvent>>> multipleEventmap =
                EventScanner.getRequestMultipleEventClazzs();
        Iterator<FunctionType> multipleIterator = multipleEventmap.keySet().iterator();

        multipleIterator.forEachRemaining(item -> {
            String name = item.fName;
            if (name.contains("TODO BUG")) {
                return;
            }

            // 可重复执行的模块显示设定的次数
            if (item.fNum > 0) {
                name += "_" + item.fNum + "次";
            } else {
                name += "_无限次";
            }
            if (MiscUtils.isIDEEnvironment()) {
                waitingList.addElement(name);
            } else {
                selectedList.addElement(name);
            }
        });

        EventScanner.setFunctionMultpleEvents();

        showChooseMode();
    }
}
