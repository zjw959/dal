package UI.Listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import UI.window.frame.GMToolsWindow;

/**
 * @function GM工具监听器
 */
public class GmToolsListener implements ActionListener {

    public GmToolsListener(JFrame parent) {
        this.parent = parent;
    }

    private JFrame parent;

    @Override
    public void actionPerformed(ActionEvent e) {
        new GMToolsWindow(parent);
    }

}
