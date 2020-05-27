package logic.constant;

/**
 * 
 * @Description 好友操作类型
 * @author LiuJiang
 * @date 2018年6月12日 上午11:42:06
 *
 */
public class FriendConstant {
    /** 申请好友 */
    public static final int OPERATE_APPLY_FRIEND = 1;
    /** 删除好友 */
    public static final int OPERATE_DELETE_FRIEND = 2;
    /** 屏蔽玩家 */
    public static final int OPERATE_SHIELD_PLAYER = 3;
    /** 取消屏蔽 */
    public static final int OPERATE_LIFTED_SHIELD = 4;
    /** 同意申请 */
    public static final int OPERATE_AGREE_APPLY = 5;
    /** 拒绝申请 */
    public static final int OPERATE_REFUSE_APPLY = 6;
    /** 赠送礼品 */
    public static final int OPERATE_GIVE_GIFT = 7;
    /** 收取礼品 */
    public static final int OPERATE_RECEIVE_GIFT = 8;


    // ************************************分割线************************************
    /** 好友 */
    public static final int STATUS_FRIEND = 1;
    /** 屏蔽 */
    public static final int STATUS_SHIELD = 2;
    /** 申请 */
    public static final int STATUS_APPLY = 3;
    // ************************************分割线************************************
    /** 获取 */
    public static final int CT_GET = 0;
    /** 添加 */
    public static final int CT_ADD = 1;
    /** 删除 */
    public static final int CT_DELETE = 2;
    /** 修改 */
    public static final int CT_UPDATE = 3;
    // ************************************分割线************************************
    /** 获取 */
    public static final int FRIEND_GET_EVENT = 0;
    /** 好友增加 */
    public static final int FRIEND_ADD_EVENT = 1;
    /** 好友删除 */
    public static final int FRIEND_DEL_EVENT = 2;
}
