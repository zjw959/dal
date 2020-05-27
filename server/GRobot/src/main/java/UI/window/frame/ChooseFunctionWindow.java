package UI.window.frame;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;

import UI.Listener.AllFunctionChooseListener;
import UI.Listener.FunctionChooseListener;
import UI.Listener.FunctionConfirmListener;

/**
 * @function 选择功能窗口
 */
public class ChooseFunctionWindow extends JDialog {
    public ChooseFunctionWindow(FunctionWindow window) {
        super(window.frame, true);

        this.setLocation(window.frame.getX() + 200, window.frame.getY() + 200);

        getContentPane().setLayout(null);

        FunctionChooseListener chooseListener = new FunctionChooseListener(window);
        JList<String> waitingList = new JList<>(FunctionWindow.waitingList);
        waitingList.setFont(new Font("宋体", Font.PLAIN, 13));
        waitingList.setBounds(10, 113, 229, 311);
        waitingList.addListSelectionListener(chooseListener);
        getContentPane().add(waitingList);

        JList<String> selectedList = new JList<String>(FunctionWindow.selectedList);
        selectedList.setFont(new Font("宋体", Font.PLAIN, 13));
        selectedList.setBounds(315, 113, 229, 311);
        selectedList.addListSelectionListener(chooseListener);
        getContentPane().add(selectedList);

        JLabel waitingLabel = new JLabel("待选功能:");
        waitingLabel.setFont(new Font("宋体", Font.PLAIN, 18));
        waitingLabel.setBounds(10, 29, 118, 32);
        getContentPane().add(waitingLabel);

        JLabel selectedLabel = new JLabel("已选功能:");
        selectedLabel.setFont(new Font("宋体", Font.PLAIN, 18));
        selectedLabel.setBounds(315, 29, 118, 32);
        getContentPane().add(selectedLabel);


        JButton waitingAllButton = new JButton("全选");
        waitingAllButton.setFont(new Font("宋体", Font.PLAIN, 16));
        waitingAllButton.setBounds(10, 71, 101, 32);
        waitingAllButton.addActionListener(new AllFunctionChooseListener(window,
                FunctionWindow.waitingList, FunctionWindow.selectedList));
        getContentPane().add(waitingAllButton);

        JButton selectedAllButton = new JButton("取消");
        selectedAllButton.setFont(new Font("宋体", Font.PLAIN, 16));
        selectedAllButton.setBounds(315, 71, 101, 32);
        selectedAllButton.addActionListener(new AllFunctionChooseListener(window,
                FunctionWindow.selectedList, FunctionWindow.waitingList));
        getContentPane().add(selectedAllButton);

        JButton confirmButton = new JButton("确定");
        confirmButton.setFont(new Font("宋体", Font.PLAIN, 18));
        confirmButton.setBounds(232, 436, 100, 32);


        FunctionConfirmListener confirmListener = new FunctionConfirmListener(this);
        confirmButton.addActionListener(confirmListener);
        getContentPane().add(confirmButton);



        setTitle("功能选择");
        this.setBounds(window.frame.getX() + 100, window.frame.getY() + 50, 600, 500);
        this.setResizable(false);
        this.setVisible(true);
    }

    private static final long serialVersionUID = -3700216543117408921L;
}
