package UI.Listener;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import UI.window.frame.FunctionWindow;
import core.event.EventScanner;

/**
 * @function 功能选择监听器
 */
public class FunctionChooseListener implements ListSelectionListener {

    private FunctionWindow window;

    public FunctionChooseListener(FunctionWindow window) {
        this.window = window;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            JList<String> jList = (JList<String>) e.getSource();
            String fName = jList.getSelectedValue();
            if (null != fName) {
                DefaultListModel targetListModel = null;
                if (jList.getModel() == FunctionWindow.selectedList) {
                    targetListModel = FunctionWindow.waitingList;
                } else {
                    targetListModel = FunctionWindow.selectedList;
                }
                targetListModel.addElement(fName);
                DefaultListModel<String> sourceListMode =
                        (DefaultListModel<String>) jList.getModel();
                sourceListMode.removeElement(fName);
            }
            EventScanner.setFunctionMultpleEvents();
            window.showChooseMode();
        }
    }
}
