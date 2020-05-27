package logic.constant;

/**
 * 提示信息的类型
 */
public enum ENotifyType {
    /** 一般提示（无需确认） */
    TIP(0),
    /** 弹框提示（确认后关闭） */
    POPUP(1),
    /** 弹框提示（确认后返回登录界面） */
    POPUP_RELOGIN(2),
    /** 弹框提示（确认后关闭游戏） */
    POPUP_QUIT(3);

    private int val;

    ENotifyType(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }
}
