package UI.Listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;

import core.event.EventScanner;

/**
 * @function 功能确认事件
 */
public class FunctionConfirmListener implements ActionListener {

    public FunctionConfirmListener(JDialog chooseFunctionDialog) {
        this.chooseFunctionDialog = chooseFunctionDialog;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        EventScanner.setFunctionMultpleEvents();
        chooseFunctionDialog.dispose();
    }

    private JDialog chooseFunctionDialog;
}
