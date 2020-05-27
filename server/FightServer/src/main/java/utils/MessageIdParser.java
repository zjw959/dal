package utils;

/**
 * 消息编号解析
 * 
 * 消息号定义:
 * 
 * 1.消息为10进制6位数 2.前三位为功能编号（100~999）
 * 
 * 3.第四位为来源（1:SC 2:CS 3:SS 4:CC） 4.后两位为具体功能来源的消息编号（01~99）
 * 
 * ------------------------------
 * 
 * 例如 消息号：100201
 * 
 * 前三位 100 xx功能 第四位 2 （1:SC 2:CS 3:SS 4:CC 5:CW 6:WC） 后两位 1 消息编号为1
 */
public class MessageIdParser {

    /**
     * 消息功能编号
     * 
     * @param msgId
     * @return
     */
    public static byte getFunction(short msgId) {
        return (byte) (msgId >> 8);
    }
}
