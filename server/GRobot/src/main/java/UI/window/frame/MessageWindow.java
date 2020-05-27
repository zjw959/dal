package UI.window.frame;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import org.apache.commons.lang.StringUtils;
import com.google.protobuf.Descriptors.FieldDescriptor;
import UI.Listener.RunFunctionListener;
import UI.window.panel.MessagePanel;
import core.Log4jManager;
import core.event.EventScanner;
import utils.ProtoBufUtils;
import utils.ProtoBufUtils.ProtoClientMessage;

/**
 * @function 单接口测试
 */
public class MessageWindow extends MainWindow {

    public static MessageWindow getInstance() {
        return Singleton.INSTANCE.getProcessor();
    }

    private MessageWindow() {
        super();

        MessageWindow my = this;

        frame.setTitle("消息测试");
        frame.getContentPane().setFont(new Font("宋体", Font.PLAIN, 12));
        frame.setResizable(false);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds((int) (dimension.getWidth() / 2 - (1023 >> 1)),
                (int) (dimension.getHeight() / 2 - (576 >> 1)), 1023, 576);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel titlelLabel = new JLabel("消息测试");
        titlelLabel.setFont(new Font("宋体", Font.BOLD, 20));
        titlelLabel.setBounds(230, 33, 107, 26);
        frame.getContentPane().add(titlelLabel);
        
        // 显示demo消息信息
        String demo="<html>单接口参数填写demo5122,5125,.....(逗号)两个接口参数之间以|$|分割</html>";
        showDemoLable = new JLabel(demo);
        showDemoLable.setFont(new Font("宋体", Font.PLAIN, 13));
        showDemoLable.setBounds(230, 110, 161, 50);
        frame.add(showDemoLable);       
        
        String msgId = "5122,5125";
        messageIdLabel = new JLabel("单接口id填写");
        messageIdLabel.setFont(new Font("宋体", Font.PLAIN, 12));
        messageIdLabel.setBounds(230, 170, 161, 23);
        frame.add(messageIdLabel);

        robotHoldTextField = new JTextField(msgId);
        robotHoldTextField.setFont(new Font("Dialog", Font.PLAIN, 12));
        robotHoldTextField.setColumns(10);
        robotHoldTextField.setBounds(230, 190, 161, 23);

        // 显示的消息信息
        showMessagelabel = new JLabel("单接口参数填写");
        showMessagelabel.setFont(new Font("宋体", Font.PLAIN, 13));
//        showMessagelabel.setSize(20, 100);
        showMessagelabel.setBounds(230, 210, 161, 23);
        frame.add(showMessagelabel);

        // 消息信息
        robotMessageTextField = new JTextField("1<>2|&|3<>true|$|10<>1<>true");
        robotMessageTextField.setFont(new Font("Dialog", Font.PLAIN, 12));
        robotMessageTextField.setColumns(20);
        robotMessageTextField.setBounds(230, 230, 161, 23);

        showMessagelabel.setVisible(false);
        showDemoLable.setVisible(false);

        msgIdButton = new JButton("确定id");
        msgIdButton.setFont(new Font("宋体", Font.PLAIN, 12));
        msgIdButton.setBounds(230, 270, 120, 23);
        frame.add(msgIdButton);

        getMessagePanel().setLocation(230, 200);
        frame.getContentPane().add(getMessagePanel());
        
        
        allTest = new JCheckBox("全接口随机测试");
        allTest.setSelected(true);
        allTest.setBounds(210, 75, 161, 23);
        allTest.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                if (allTest.isSelected()) {
                    messageIdLabel.setVisible(false);
                    robotHoldTextField.setVisible(false);
                    messageIdLabel.setVisible(false);
                    msgIdButton.setVisible(false);
                    robotMessageTextField.setVisible(false);
                    showMessagelabel.setVisible(false);
                    showDemoLable.setVisible(false);
                } else {
                    // showTitleInfo(showMessagelabel);
                    messageIdLabel.setVisible(true);
                    robotHoldTextField.setVisible(true);
                    messageIdLabel.setVisible(true);
                    msgIdButton.setVisible(true);
                    robotMessageTextField.setVisible(true);
                    showMessagelabel.setVisible(true);
                    showDemoLable.setVisible(true);
                }
            }
        });
        frame.add(allTest);

        robotHoldTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                JTextField textField = (JTextField) e.getSource();
                String text = textField.getText();
                if (text == null) {
                    JOptionPane.showMessageDialog(frame, "数量只能输入4位数字");
                }
                String[] confims = text.split(",");
                for (String confim : confims) {
                    if (!confim.matches("^[0-9]{4}$")) {
                        JOptionPane.showMessageDialog(frame, "数量只能输入4位数字");
                        textField.setText(msgId);
                    }
                }
                // 获取该消息ID的协议类型
                Map<Integer, ProtoClientMessage> messages = EventScanner.getClientMessageMap();
                List<ProtoClientMessage> protoClientMessages = new ArrayList<ProtoClientMessage>();
                for (String confim : confims) {
                    ProtoClientMessage clientMessage = messages.get(Integer.valueOf(confim));
                    if (clientMessage == null) {
                        JOptionPane.showMessageDialog(frame, "找不到对应的消息id:" + confim);
                        textField.setText(msgId);
                        return;
                    }
                    protoClientMessages.add(clientMessage);
                }
                Log4jManager.getInstance().info(my, "当前消息 " + textField.getText() + " 对应的参数规范:");
                // 是否在界面上显示
                // showTitleInfo(showMessagelabel);
                for (ProtoClientMessage clientMessage : protoClientMessages) {
                    Map<FieldDescriptor, Object> map = clientMessage.getBuilder().getAllFields();
                    Set<FieldDescriptor> key = map.keySet();
                    boolean create = false;
                    for (FieldDescriptor fieldDescriptor : key) {
                        if (!create) {
                            Log4jManager.getInstance().info(my,
                                    fieldDescriptor.getContainingType().getName());
                            create = true;
                        }
                        Log4jManager.getInstance().info(my, "index:" + fieldDescriptor.getIndex()
                                + ",name:" + fieldDescriptor.getName() + ",type:"
                                + fieldDescriptor.getJavaType().toString().toLowerCase() + ","
                                + (StringUtils.substringAfter(
                                        fieldDescriptor.toProto().getLabel().toString(), "LABEL_"))
                                                .toLowerCase());
                    }
                }
            }

            @Override
            public void focusGained(FocusEvent e) {

            }
        });
        frame.getContentPane().add(robotHoldTextField);
        frame.getContentPane().add(robotMessageTextField);

//        List<ProtoClientMessage> clientMessages = EventScanner.getClientMessages();
//        for (ProtoClientMessage protoClientMessage : clientMessages) {
//            Log4jManager.getInstance().info(my, protoClientMessage.getMsgId() + ","
//                    + protoClientMessage.getBuilder().getClass().getName());
//        }
        messageIdLabel.setVisible(false);
        robotHoldTextField.setVisible(false);
        robotMessageTextField.setVisible(false);
        messageIdLabel.setVisible(false);
        msgIdButton.setVisible(false);

        runButton.addActionListener(new RunFunctionListener(this));

        robotPanel.getRobotCountLabel().setVisible(false);
        robotPanel.getRobotMaxTextField().setText("99999999");
        robotPanel.getRobotMaxTextField().setVisible(false);

        frame.setVisible(true);
    }

    /**
     * 取得当前设置的消息id
     * 
     * @return
     */
    public String getMessageId() {
        if (allTest.isSelected()) {
            return null;
        } else {
            return robotHoldTextField.getText();
        }
    }

    /** 取的当前设置的消息信息 **/
    public String getMessageInfo() {
        if (allTest.isSelected()) {
            return null;
        } else {
            return robotMessageTextField.getText();
        }
    }


    JCheckBox allTest;
    JTextField robotHoldTextField;
    JTextField robotMessageTextField;
    JLabel messageIdLabel;
    JButton msgIdButton;
    JLabel showMessagelabel;
    //demo
    JLabel showDemoLable;
    MessagePanel messagePanel;
    

    /**
     * 用枚举来实现单例
     */
    private enum Singleton {

        INSTANCE;

        MessageWindow processor;

        Singleton() {
            this.processor = new MessageWindow();
        }

        MessageWindow getProcessor() {
            return processor;
        }
    }


    /** 在界面上显示message信息 **/
    void showTitleInfo(JLabel showMessagelabel) {
        List<ProtoClientMessage> exeMessage = new ArrayList<ProtoClientMessage>();
        String text = robotHoldTextField.getText();
        // 设置默认值
        String[] message_ids = text.split(",");
        StringBuffer showTitle = new StringBuffer();
        showTitle.append("<html>");
        List<ProtoClientMessage> clientMessages = EventScanner.getClientMessages();
        for (ProtoClientMessage protoClientMessage : clientMessages) {
            // Log4jManager.getInstance().info(my, protoClientMessage.getMsgId() + ","
            // + protoClientMessage.getBuilder().getClass().getName());
            for (String messageId : message_ids) {
                if (protoClientMessage.getMsgId() == Integer.parseInt(messageId)) {
                    exeMessage.add(protoClientMessage);
                    if (!protoClientMessage.getBuilder().getAllFields().isEmpty()
                            && protoClientMessage.getBuilder().getAllFields().size() > 0) {
                        FieldDescriptor f = (FieldDescriptor) protoClientMessage.getBuilder()
                                .getAllFields().keySet().toArray()[0];
                        showTitle.append(f.getContainingType().getName() + ":<br/>");
                    }
                    try {
                        ProtoBufUtils.createBuilder(protoClientMessage.getBuilder().getClass());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    for (Object o : protoClientMessage.getBuilder().getAllFields().keySet()) {
                        FieldDescriptor fieldDescriptor = (FieldDescriptor) o;
                        showTitle.append(
                                fieldDescriptor.getNumber() + ":" + fieldDescriptor.getName() + "("
                                        + fieldDescriptor.getJavaType().toString().toLowerCase()
                                        + ")<br/>");
                    }
                    showTitle.append("<br/>");
                }
            }
        }
        showTitle.append("</html>");
        showMessagelabel.setText(showTitle.toString());
    }

    /**获取下来协议**/
    public MessagePanel getMessagePanel() {
        if (messagePanel == null) {
            messagePanel = new MessagePanel();
        }
        return messagePanel;
    }
}
