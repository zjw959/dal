package logic.mail;

public interface ReqMailOrder {
    /** 请求获取邮件 */
    public static final int REQ_GET_MAILS = 1;
    /** 请求创建邮件 */
    public static final int REQ_CREATE_MAILS = 2;
    /** 请求读取邮件 */
    public static final int REQ_READ_MAILS = 3;
    /** 请求领取邮件附件 */
    public static final int REQ_RECEIVE_MAILS = 4;
    /** 请求删除邮件 */
    public static final int REQ_DELETE_MAILS = 5;
}
