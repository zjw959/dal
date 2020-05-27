package util;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

public class HTRandomString {

	private static final String NUMBER = "0123456789";
//	private static final String ENGLISH_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String ENGLISH_CHARS_LOWER = "abcdefghijklmnopqrstuvwxyz";

	/**
	 * 生产一个指定长度的随机字符串
	 * 
	 * @param length
	 *            字符串长度
	 * @return
	 */
	public static String generateRandomString(int length) {
		StringBuilder sb = new StringBuilder(length);
		SecureRandom random = new SecureRandom();
		
		for (int i = 0; i < length; i++) {
			if(i < 2){
				sb.append(ENGLISH_CHARS_LOWER.charAt(random.nextInt(ENGLISH_CHARS_LOWER
						.length())));
			}else if(i<8){
				sb.append(NUMBER.charAt(random.nextInt(NUMBER
						.length())));
			}else{
				sb.append(ENGLISH_CHARS_LOWER.charAt(random.nextInt(ENGLISH_CHARS_LOWER
						.length())));
			}
		}
		return sb.toString();
	}

	public static String generateString8() {
		return generateRandomString(10);
	}

	/**
	 * 默认长度
	 */
	private static final int DEFAULT_LENGHT = 16;

	/**
	 * 生产一个16位长度的随机字符串
	 * 
	 * @return
	 */
	public static String generateRandomString() {
		return generateRandomString(DEFAULT_LENGHT);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Set<String> check = new HashSet<String>();
		
		int dumpNum = 0;
		// 生成1000个随机字符串，检查是否出现重复
		for (int i = 0; i < 10; i++) {
//			String s = HTRandomString.generateRandomString();
			String s = HTRandomString.generateString8();
			if (check.contains(s)) {
				dumpNum +=1;
//				throw new IllegalStateException("Repeated string found : " + s);
			} else {
				if (i % 1000 == 0)
					System.out
							.println("generated " + i / 1000 + "000 strings.");
				check.add(s);
			}
			System.out.println(s);
		}
		System.err.println("dump number : "+dumpNum);
	}
}