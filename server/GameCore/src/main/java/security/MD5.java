package security;


import java.security.MessageDigest;

import org.apache.log4j.Logger;

import utils.ExceptionEx;

/**
 * 代码核对
 */
public class MD5 {

    public final static String MD5Encode(String s) {
        byte[] b = s.getBytes();
        return _md5(b);
    }

    public final static String MD5Bytes(byte[] v) {
        return _md5(v);
    }

    private final static String _md5(byte[] v) {
        char hexDigits[] =
                {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdAlgorithm = MessageDigest.getInstance("MD5");
            mdAlgorithm.update(v);
            byte[] mdCode = mdAlgorithm.digest();

            int mdCodeLength = mdCode.length;
            char strMd5[] = new char[mdCodeLength * 2];
            int k = 0;
            for (int i = 0; i < mdCodeLength; i++) {
                byte byte0 = mdCode[i];
                strMd5[k++] = hexDigits[byte0 >>> 4 & 0xf];
                strMd5[k++] = hexDigits[byte0 & 0xf];
            }
            mdCode = null;
            return new String(strMd5);
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
            return "";
        }
    }


    // public static String md5(String str) {
    // StringBuilder sb = new StringBuilder();
    // try {
    // MessageDigest m = MessageDigest.getInstance("MD5");
    // m.update(str.getBytes("UTF8"));
    // byte bytes[] = m.digest();
    //
    // for (int i = 0; i < bytes.length; i++) {
    // if ((bytes[i] & 0xff) < 0x10) {
    // sb.append("0");
    // }
    // sb.append(Long.toString(bytes[i] & 0xff, 16));
    // }
    // } catch (Exception e) {
    // }
    // return sb.toString();
    // }

    private static final Logger LOGGER = Logger.getLogger(MD5.class);

    public static void main(String[] args) {
        System.out.println(
                MD5Encode("52ba46c61902b124232330mt1232{\"serverId\":\"1\"}52ba46d5bdc7c"));
    }
}
