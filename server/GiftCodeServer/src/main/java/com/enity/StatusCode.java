/**
 * 
 */
package com.enity;

/**
 * @author zjw
 *
 */
public interface StatusCode {
    /**
     * OK
     */
    int OK = 0;
    /**
     * 未知错误
     */
    int ERROR = -1;
    /**
     * 礼包码已经使用过
     */
    int ALREADY_USE = 1;
    /**
     * 已经使用过同一个礼包
     */
    int ALREADY_USE_SAME_PACK = 2;
    /**
     * 使用次数超过了上限
     */
    int TIME_LESS_THAN = 3;

    /**
     * 找不到邀请码
     */
    int CODE_NOT_FOUND = 4;
    /**
     * 找不到礼包
     */
    int PACK_NOT_FOUND = 5;

    /**
     * 礼包仍未开放兑换
     */
    int PACK_NOT_START = 6;

    /**
     * 礼包过期
     */
    int PACK_TIME_OUT = 7;

    /**
     * 无效渠道号
     */
    int INVALID_CHANNEL = 8;
}
