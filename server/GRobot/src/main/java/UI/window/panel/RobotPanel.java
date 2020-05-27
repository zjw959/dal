package UI.window.panel;

import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import conf.RunConf;
import utils.StrEx;

public class RobotPanel extends javax.swing.JPanel {
    public RobotPanel() {
        setLayout(null);
        this.setBounds(0, 0, 180, 300);

        parent = this.getParent();

        robotCountLabel = new JLabel("最大机器人数量:");
        robotCountLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        robotCountLabel.setBounds(6, 6, 107, 15);
        add(robotCountLabel);

        robotMaxTextField = new JTextField("10");
        robotMaxTextField.setFont(new Font("Dialog", Font.PLAIN, 12));
        robotMaxTextField.setColumns(10);
        robotMaxTextField.setBounds(6, 27, 66, 21);
        robotMaxTextField.setText(String.valueOf(-1));
        // robotMaxTextField.addFocusListener(new FocusListener() {
        // @Override
        // public void focusLost(FocusEvent e) {
        // JTextField textField = (JTextField) e.getSource();
        // if (!textField.getText().matches(
        // "^[1-9][0-9][0-9][0-9]$|^[1-9][0-9][0-9]$|^[1-9][0-9]$|^[1-9]$|^[1-9][0-9]$|^[1-9]$")) {
        // JOptionPane.showMessageDialog(parent, "数量只能输入1-99999");
        // textField.setText("1000");
        // }
        // }
        //
        // @Override
        // public void focusGained(FocusEvent e) {
        //
        // }
        // });
        add(robotMaxTextField);

        JLabel holdClientlabel = new JLabel("链接保持数：");
        holdClientlabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        holdClientlabel.setBounds(6, 60, 107, 15);
        add(holdClientlabel);

        robotHoldTextField = new JTextField("1");
        robotHoldTextField.setFont(new Font("Dialog", Font.PLAIN, 12));
        robotHoldTextField.setColumns(10);
        robotHoldTextField.setBounds(6, 88, 66, 21);
        robotHoldTextField.addFocusListener(new FocusListener() {

            @Override
            public void focusLost(FocusEvent e) {
                JTextField textField = (JTextField) e.getSource();
                if (!textField.getText()
                        .matches("^[1-9][0-9][0-9][0-9]$|^[1-9][0-9][0-9]$|^[1-9][0-9]$|^[1-9]$")) {
                    JOptionPane.showMessageDialog(parent, "数量只能输入1-9999");
                    textField.setText("1");
                }
            }

            @Override
            public void focusGained(FocusEvent e) {

            }
        });
        add(robotHoldTextField);

        JButton reloadbutton = new JButton("更新机器人数量");
        reloadbutton.setName("reloadbutton");
        reloadbutton.setFont(new Font("Dialog", Font.PLAIN, 12));
        reloadbutton.setBounds(6, 123, 120, 23);
        reloadbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reloadRobotConf();
            }

        });
        add(reloadbutton);

        JLabel functionDelayTimeTextLabel = new JLabel("发送间隔(毫秒)");
        functionDelayTimeTextLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        functionDelayTimeTextLabel.setBounds(16, 161, 107, 15);
        add(functionDelayTimeTextLabel);

        functionDelayTimeTextField = new JTextField("1000");
        functionDelayTimeTextField.setFont(new Font("Dialog", Font.PLAIN, 12));
        functionDelayTimeTextField.setColumns(10);
        functionDelayTimeTextField.setBounds(6, 188, 66, 21);
        functionDelayTimeTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                JTextField textField = (JTextField) e.getSource();
                if (!textField.getText().matches("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$")) {
                    JOptionPane.showMessageDialog(parent, "只能是数字且不能以0开头");
                    textField.setText("1000");
                }
            }

            @Override
            public void focusGained(FocusEvent e) {

            }
        });
        add(functionDelayTimeTextField);

        JLabel robotNameLabel = new JLabel("机器人名称:");
        robotNameLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        robotNameLabel.setBounds(6, 256, 107, 15);
        add(robotNameLabel);

        String name;
        try {
            name = InetAddress.getLocalHost().getHostName();
            int index = name.indexOf(".");
            if (index != -1) {
                name = StrEx.removeRight(name, name.length() - index);
            }
        } catch (UnknownHostException e) {
            name = "robot";
        }
        robotNameTextField = new JTextField(name);
        robotNameTextField.setFont(new Font("Dialog", Font.PLAIN, 12));
        robotNameTextField.setColumns(10);
        robotNameTextField.setBounds(6, 273, 151, 21);
        robotNameTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                JTextField textField = (JTextField) e.getSource();
                if (StrEx.isEmpty(textField.getText())) {
                    JOptionPane.showMessageDialog(parent, "请填写机器人名称");
                    textField.setText("test");
                }
            }

            @Override
            public void focusGained(FocusEvent e) {

            }
        });
        add(robotNameTextField);

        isSingleCheckBox = new JCheckBox("固定用户名");
        isSingleCheckBox.setBounds(6, 221, 128, 23);
        isSingleCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isSingleCheckBox.isSelected()) {
                    robotHoldTextField.setText("1");
                    robotHoldTextField.setEnabled(false);
                    robotMaxTextField.setEnabled(false);
                } else {
                    robotHoldTextField.setEnabled(true);
                    robotMaxTextField.setEnabled(true);
                }
            }
        });
        add(isSingleCheckBox);
    }

    public boolean getIsSingle() {
        return isSingleCheckBox.isSelected();
    }

    public JTextField getRobotMaxTextField() {
        return robotMaxTextField;
    }

    public JLabel getRobotCountLabel() {
        return robotCountLabel;
    }

    /**
     * 初始化运行机器人配置
     */
    public void reloadRobotConf() {
        int robotCount = Integer.valueOf(robotMaxTextField.getText());
        long msgDelayTime = Long.valueOf(functionDelayTimeTextField.getText());
        String robotName = robotNameTextField.getText();
        int holdNum = Integer.valueOf(robotHoldTextField.getText());
        RunConf.robotConf.setRobotMaxNum(robotCount);
        RunConf.robotConf.setSendDelayTime(msgDelayTime);
        RunConf.robotConf.setPrefixName(robotName);
        RunConf.robotConf.setRobotHoldNum(holdNum);
    }

    private static final long serialVersionUID = 1L;
    private JTextField robotMaxTextField;
    private JTextField robotHoldTextField;
    private JTextField functionDelayTimeTextField;
    private JTextField robotNameTextField;
    private Container parent;
    private JLabel robotCountLabel;
    private JCheckBox isSingleCheckBox;

}
