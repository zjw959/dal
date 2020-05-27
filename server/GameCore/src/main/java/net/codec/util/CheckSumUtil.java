package net.codec.util;

import java.nio.ByteBuffer;

/**
 * 网络协议校验和工具类，用于快速比对网络协议的正确性
 * @author wk.dai
 */
public class CheckSumUtil {
	
	/**
     * 计算校验和
     * 
     * @param data
     *            完整的消息数据，包括包头和包体，计算将从第7个字节开始。
     * @return 校验和
     */
    public static short calculate(byte[] data)
    {
        return calculate(data, 0, data.length);
    }
    
    /**
     * 计算校验和
     * 
     * @param data
     *            完整的消息数据，包括包头和包体，计算将从第7个字节开始。
     * @return 校验和
     */
    public static short calculate(byte[] data,int startIndex,int length)
    {
        int val = 0x77;
        int index = startIndex;
        int endIndex = startIndex+length;
        while (index < endIndex)
        {
            val += (data[index++] & 0xFF);
        }
        return (short) (val & 0x7F7F);
    }
    
    /**
     * 计算校验和，计算完毕后回滚{@linkplain ByteBuffer#position(int)}当前坐标索引到调用方法之前
     * @param buffer 数据缓冲对象
     * @return 校验和
     */
    public static short calculate(ByteBuffer buffer){
    	return calculate(buffer,buffer.remaining());
    }
    
    /**
     * 计算校验和，计算完毕后回滚{@linkplain ByteBuffer#position(int)}当前坐标索引到调用方法之前
     * @param buffer 数据缓冲对象
     * @param startIndex 校验和检测内容的起始索引
     * @param length 检测的数据长度
     * @return 校验和
     */
    public static short calculate(ByteBuffer buffer,int startIndex,int length){
    	int bakIndex = buffer.position();
    	try{
    		buffer.position(startIndex);
    		return calculate(buffer,length);
    	}finally{
    		buffer.position(bakIndex);
    	}
    }
    
    /**
     * 计算校验和，计算完毕后回滚{@linkplain ByteBuffer#position(int)}当前坐标索引到调用方法之前
     * @param buffer 数据缓冲对象
     * @param length 检测的数据长度
     * @return 校验和
     */
    public static short calculate(ByteBuffer buffer,int length){
    	int bakIndex = buffer.position();
    	try{
	    	int val = 0x77;
	    	int i=0;
	        while (buffer.hasRemaining() && i++<length)
	        {
	            val += (buffer.get() & 0xFF);
	        }
	        return (short) (val & 0x7F7F);
    	}finally{
    		buffer.position(bakIndex);
    	}
    }
}
