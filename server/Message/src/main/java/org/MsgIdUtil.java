package org;

/**
 * 消息号转换工具类
 * @author liujiang
 *
 */
public class MsgIdUtil {
	
	/** 转换 */
	public static short code(byte classCode, byte functionCode){
		short ab = (short) ((classCode << 8) | functionCode);
		return ab;
	}
	
	public static void main(String[] args) {
		//系统编号（1-127）
		byte classCode = 12;
		//功能编号（1-127）
		byte functionCode = 127;
	
		short ab = code(classCode, functionCode);

		System.out.println("系统编号："+classCode+" 功能编号："+functionCode);
		System.out.println("--------转换结果-------");
		System.out.println("消息id（十进制）："+ab);
//		System.out.print("消息id（十六进制）：0x");
//		System.out.printf("%x\n", ab);// 按16进制输出
	}
}
