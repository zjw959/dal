package UI.window.panel;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import conf.RunConf;
import conf.ServerConf;
import core.Log4jManager;
import utils.FileEx;

public class ServerPanel extends javax.swing.JPanel {
    public ServerPanel() {
        setLayout(null);

        this.setBounds(0, 0, 600, 80);

        JComboBox<ServerConf> comboBox = new JComboBox<ServerConf>();
        comboBox.setBounds(6, 6, 200, 30);
        comboBox.setFont(new Font("Dialog", Font.BOLD, 18));
        add(comboBox);

        JLabel serverInfoLabel = new JLabel("服务器信息: " + RunConf.choosedServerConf);
        serverInfoLabel.setBounds(16, 46, 372, 25);
        serverInfoLabel.setFont(new Font("宋体", Font.PLAIN, 18));
        add(serverInfoLabel);

        isRandomCheckBox = new JCheckBox("随机分发");
        isRandomCheckBox.setBounds(250, 10, 150, 23);
        isRandomCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isRandomCheckBox.isSelected()) {
                    comboBox.setEnabled(false);
                    groupBox.setEnabled(true);
                } else {
                    comboBox.setEnabled(true);
                    groupBox.setEnabled(false);
                }
            }
        });
        add(isRandomCheckBox);

        groupBox = new JComboBox<Object>();
        groupBox.setBounds(400, 6, 100, 30);
        groupBox.setFont(new Font("Dialog", Font.BOLD, 14));
        groupBox.addItem("week");
        groupBox.addItem("chengxu");
        groupBox.addItem("pub_test");
        groupBox.addItem("hw_test");
        groupBox.setEnabled(false);
        add(groupBox);


        RunConf.SERVERCONFS.forEach(action -> comboBox.addItem(action));
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RunConf.choosedServerConf = comboBox.getItemAt(comboBox.getSelectedIndex());
                serverInfoLabel.setText("服务器信息: " + RunConf.choosedServerConf);

                if (RunConf.choosedServerConf == null) {
                    // RunConf.choosedServerConf =
                    // new ServerConf("127", "127.0.0.1", 10086, 10087, -1, 10001);
                }

                try {
                    int index = comboBox.getSelectedIndex();
                    FileEx.writeAll("./config/serverIndex.txt", String.valueOf(index));
                } catch (IOException ex) {
                    Log4jManager.getInstance().error(ex);
                }
            }
        });
        if (RunConf.choosedServerConf != null) {
            comboBox.setSelectedIndex(RunConf.choosedServerConf.getSelectId());
        }
    }

    public boolean getIsRandomCheckBox() {
        return isRandomCheckBox.isSelected();
    }

    public String getSelectGroup() {
        return String.valueOf(groupBox.getSelectedItem());
    }



    private JComboBox<Object> groupBox;
    private JCheckBox isRandomCheckBox;
    private static final long serialVersionUID = 1L;
}
