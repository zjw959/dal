package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import utils.lang.StrBuilderEx;


public class StrEx {
    private static final Logger LOGGER = Logger.getLogger(StrEx.class);


    public static final String Charset_UTF8 = "UTF-8";
    public static final String Charset_GBK = "GBK";
    public static final String Charset_GB2312 = "GB2312";
    public static final String SYMBOL_PLUS = "+";
    public static final String SYMBOL_SEMICOLON = ";";
    public static final String SYMBOL_COMMA = ",";
    public static final String SYMBOL_VERTICALLINE = "|";
    public static final String SYMBOL_COLON = ":";


    public static final String fix6Str(String s) {
        return String.format("%6s", s);
    }

    public static final String fixNStr(String s, int n) {
        return String.format("%" + n + "s", s);
    }

    public static final String left(String s, int len) {
        return s.substring(0, len);
    }

    public static final String right(String s, int len) {
        int length = s.length();
        return s.substring(length - len, length);
    }

    public static final String mid(String s, int begin, int end) {
        return s.substring(begin, end);
    }

    public static final String left(String s, String left) {
        int p1 = s.indexOf(left);
        p1 = p1 < 0 ? 0 : p1;
        return s.substring(0, p1);
    }

    public static final String right(String s, String right) {
        int p1 = s.lastIndexOf(right);
        p1 = p1 < 0 ? 0 : p1 + right.length();
        return s.substring(p1, s.length());
    }

    public static final String mid(String s, String begin, String end) {
        int p1 = s.indexOf(begin);
        p1 = p1 < 0 ? 0 : p1 + begin.length();
        int p2 = s.indexOf(end, p1 + begin.length());
        p2 = p2 < 0 ? s.length() : p2;
        return s.substring(p1, p2);
    }

    public static final boolean isByte(String s) {
        try {
            Byte.parseByte(s);
            return true;
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
            return false;
        }
    }

    public static final boolean isShort(String s) {
        try {
            Short.parseShort(s);
            return true;
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
            return false;
        }
    }

    public static final boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
            return false;
        }
    }

    public static final boolean isLong(String s) {
        try {
            Long.parseLong(s);
            return true;
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
            return false;
        }
    }

    public static final boolean isFloat(String s) {
        try {
            Float.parseFloat(s);
            return true;
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
            return false;
        }
    }

    public static final boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
            return false;
        }
    }

    public static final String f(String s, Object... args) {
        return String.format(s, args);
    }

    public static final String upperFirstLowerOther(String s) {
        int len = s.length();
        if (len <= 0)
            return "";
        StrBuilderEx sb = StrBuilderEx.builder();
        sb.a(s.substring(0, 1).toUpperCase());
        sb.a(s.substring(1, len).toLowerCase());
        return sb.toString();
    }

    public static final String upperFirst(String s) {
        int len = s.length();
        if (len <= 0)
            return "";
        StrBuilderEx sb = StrBuilderEx.builder();
        sb.a(s.substring(0, 1).toUpperCase());
        sb.a(s.substring(1, len));
        return sb.toString();
    }

    public static final String lowerFirst(String s) {
        int len = s.length();
        if (len <= 0)
            return "";
        StrBuilderEx sb = StrBuilderEx.builder();
        sb.a(s.substring(0, 1).toLowerCase());
        sb.a(s.substring(1, len));
        return sb.toString();
    }

    public static final String upperAll(String s) {
        int len = s.length();
        if (len <= 0)
            return "";
        StrBuilderEx sb = StrBuilderEx.builder();
        sb.a(s.substring(0, len).toUpperCase());
        return sb.toString();
    }

    public static final String package2Path(String pkg) {
        return pkg.replaceAll("\\.", "/");
    }

    public static final String mapToString(Map<?, ?> m) {
        StrBuilderEx sb = StrBuilderEx.builder();
        Iterator<?> it = m.keySet().iterator();
        while (it.hasNext()) {
            Object k = it.next();
            Object v = m.get(k);
            sb.a(k).a("=").a(v).a("\n");
        }
        return sb.toString();
    }

    public static final int cNum(String s) {
        try {
            byte[] b = s.getBytes(StrEx.Charset_UTF8);
            return b.length;
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
        return 0;
    }

    public static final String toString(List<?> l) {
        StrBuilderEx sb = StrBuilderEx.builder();
        Iterator<?> it = l.iterator();
        while (it.hasNext()) {
            Object v = it.next();
            String var = String.valueOf(v);
            sb.a(var).a("\n");
        }
        return sb.toString();
    }

    public static final byte[] toByteArray(String s, String charset)
            throws UnsupportedEncodingException {
        return s.getBytes(charset);
    }

    public static final String createString(byte[] b, String charset)
            throws UnsupportedEncodingException {
        return new String(b, charset);
    }

    public static final List<String> toLines(String s) throws IOException {
        List<String> ret = new Vector<String>();
        StringReader sr = new StringReader(s);
        BufferedReader br = new BufferedReader(sr);
        while (true) {
            String line = br.readLine();
            if (line == null)
                break;
            ret.add(line);
        }
        return ret;
    }

    /** 全角字符 **/
    public static final String toW(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);
            }
        }
        return new String(c);
    }

    /** 半角字符 **/
    public static final String toC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);
            }
        }
        return new String(c);
    }

    public static final String fmtCrLf(String fmt, Object... args) {
        return fmt(fmt, args) + "\r\n";
    }

    public static final String fmt(String fmt, Object... args) {
        try {
            Map<String, String> params = new HashMap<String, String>();
            int length = args.length;
            for (int i = 1; i < length + 1; i++) {
                params.put(String.valueOf(i), String.valueOf(args[i - 1]));
            }
            return EasyTemplate.make(fmt, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fmt;
    }

    public static int hashCode(String s) {
        char[] value = s.toCharArray();
        return hashCode(value);
    }

    public static int hashCode(char[] value) {
        int hash = 0;
        int count = value.length;
        int offset = 0;

        int i = hash;
        if (i == 0 && count > 0) {
            int j = offset;
            char ac[] = value;
            int k = count;
            for (int l = 0; l < k; l++)
                i = 31 * i + ac[j++];

            hash = i;
        }
        return i;
    }

    public static int nextInt(List<Integer> list) {
        int index = NumEx.nextInt(list.size());
        return list.get(index);
    }

    public static String removeLeft(String s, int n) {
        int len = s.length();
        if (len < n)
            return "";
        return s.substring(n, len);
    }

    public static String removeRight(String s, int n) {
        int len = s.length();
        if (len < n)
            return "";
        return s.substring(0, len - n);
    }

    public static boolean isEmpty(String type) {
        return type == null || type.isEmpty();
    }

    public static final void println(Object... args) {
        print(args);
        System.out.println();
    }

    public static final void print(Object... args) {
        StrBuilderEx sb = StrBuilderEx.builder();
        int length = args.length;
        int p = 0;
        for (Object o : args) {
            sb.a(o);
            p++;
            if (p < length)
                sb.a(", ");
        }
        System.out.print(sb.toString());
    }

    public static final String removeUnderline(String str) {
        return str.replace("_", "");
    }

    public static final String removeUnderlineUpFirstSingleWordLowerOther(String str) {
        return StrEx.upperFirstLowerOther(str.replace("_", ""));
    }

    public static final String removeUnderlineUpFirstAllWordLowerOther(String str)
            throws IOException {
        StrBuilderEx sb = StrBuilderEx.builder();
        String[] strs = str.split("_");
        for (int i = 0; i < strs.length; i++) {
            sb.a(StrEx.upperFirstLowerOther(strs[i]));
        }
        return sb.toString();
    }


    public static List<Integer> buildIntList(String str) {
        List<Integer> ret = new ArrayList<>();
        String[] arr = str.split(SYMBOL_COMMA);
        for (int i = 0; i < arr.length; ++i) {
            ret.add(Integer.parseInt(arr[i]));
        }
        return ret;
    }


    public static int[] buildIntArray(String str) {
        return buildIntArray(str, SYMBOL_COMMA);
    }

    /**
     * 二维数组
     * 
     * @param str
     * @return 注意:不存在元素则返回null
     * 
     */
    public static int[][] buildIntDoubleArrayKV(String str) {
        if (str == null || str.isEmpty()) {
            return new int[0][0];
        }
        String[] strs = str.split(SYMBOL_COMMA);
        int[][] arrays = new int[strs.length][2];
        for (int i = 0; i < strs.length; i++) {
            String _str = strs[i];
            int[] _array = buildIntArray(_str, SYMBOL_COLON);
            for (int j = 0; j < _array.length; j++) {
                arrays[i][j] = _array[j];
            }
        }
        return arrays;
    }

    /**
     * 确定只会存在一组数据的键值对
     * 
     * @param str
     * @return
     */
    public static int[] buildIntArrayKV(String str) {
        return buildIntArray(str, SYMBOL_COLON);
    }

    private static int[] buildIntArray(String str, String symbol) {
        if (str.isEmpty()) {
            return new int[0];
        }
        String[] strs = str.split(symbol);
        int[] result = new int[strs.length];
        for (int i = 0; i < strs.length; i++) {
            result[i] = Integer.parseInt(strs[i]);
        }
        return result;
    }

    public static Set<Integer> buildIntSet(String str) {
        Set<Integer> ret = new HashSet<>();
        String[] arr = str.split(SYMBOL_COMMA);
        for (int i = 0; i < arr.length; ++i) {
            ret.add(Integer.parseInt(arr[i]));
        }
        return ret;
    }


    public static List<Long> buildLongList(String str) {
        List<Long> ret = new ArrayList<>();

        String[] arr = str.split(SYMBOL_COMMA);
        for (int i = 0; i < arr.length; ++i) {
            ret.add(Long.parseLong(arr[i]));
        }

        return ret;
    }

    /**
     * 分割双重list字符串
     * 
     * @param kvStr
     * @return
     */
    public static ArrayList<List<Integer>> buildListInList(String dataStr) {
        String[] strs = dataStr.split(SYMBOL_SEMICOLON);
        ArrayList<List<Integer>> rets = new ArrayList<>();
        for (String str : strs) {
            String[] _str = str.split(SYMBOL_COMMA);
            List<Integer> _rets = new ArrayList<>();

            for (int i = 0; i < _str.length; i++) {
                _rets.add(Integer.parseInt(_str[i]));
            }
            rets.add(_rets);
        }
        return rets;
    }

    /**
     * 分割双重list字符串
     * 
     * @param kvStr
     * @return
     */
    public static ArrayList<int[]> buildArrayInListKV(String dataStr) {
        String[] strs = dataStr.split(SYMBOL_COMMA);
        ArrayList<int[]> rets = new ArrayList<>();
        for (String str : strs) {
            String[] _str = str.split(SYMBOL_COLON);
            int[] _rets = new int[_str.length];

            for (int i = 0; i < _str.length; i++) {
                _rets[i] = Integer.parseInt(_str[i]);
            }
            rets.add(_rets);
        }
        return rets;
    }

    /**
     * 分割双重list字符串 内部为键值对形式
     *
     * @param dataStr
     * @return
     */
    public static ArrayList<Map<Integer, Integer>> buildListInListKV(String dataStr) {
        String[] strs = dataStr.split(SYMBOL_SEMICOLON);
        ArrayList<Map<Integer, Integer>> rets = new ArrayList<>();
        for (String str : strs) {
            String[] _str = str.split(SYMBOL_COMMA);
            Map<Integer, Integer> _rets = new HashMap<Integer, Integer>();
            for (int i = 0; i < _str.length; i++) {
                String[] __str = _str[i].split(SYMBOL_COLON);
                _rets.put(Integer.parseInt(__str[0]), Integer.parseInt(__str[1]));
            }
            rets.add(_rets);
        }
        return rets;
    }

    /**
     * 注意键值重复覆盖问题
     * 
     * 1:50;2:20
     * 
     * @param strData
     * @return
     */
    public static Map<Integer, Integer> buildIntKV(String strData) {
        Map<Integer, Integer> map = new HashMap<>();
        String[] strs = strData.split(SYMBOL_COMMA);
        for (String _strData : strs) {
            String[] _strs = _strData.split(SYMBOL_COLON);
            map.put(Integer.parseInt(_strs[0].trim()), Integer.parseInt(_strs[1].trim()));
        }
        return map;
    }


    /**
     * 根据模板类容构造邮件内容
     *
     * @param templet
     * @param params
     * @return
     */
    public static String constructContent(String templet, List<String> params) {
        StringBuilder content = new StringBuilder();
        String[] strs = templet.split("\\{\\d+\\}");
        if (strs.length == 1) {
            if (params == null || params.isEmpty()) {
                content.append(strs[0]);
                return content.toString();
            }
            if (strs.length == params.size()) {
                return content.append(strs[0]).append(params.get(0)).toString();
            } else {
                LOGGER.error("给定参数和模板参数不匹配，将不生成相应邮件");
                return null;
            }
        } else {
            if (params == null || params.isEmpty()) {
                LOGGER.error("给定参数和模板参数不匹配，将不生成相应邮件");
                return null;
            } else {
                if (strs.length == params.size()) {
                    for (int i = 0; i < strs.length; i++) {
                        content.append(strs[i]);
                        content.append(params.get(i));
                    }
                    return content.toString();
                } else {
                    if (strs.length > params.size() + 1) {
                        LOGGER.error("给定参数和模板参数不匹配，将不生成相应邮件");
                        return null;
                    } else {
                        for (int i = 0; i < strs.length; i++) {
                            content.append(strs[i]);
                            if (i + 1 >= strs.length) {
                                break;
                            }
                            content.append(params.get(i));
                        }
                        return content.toString();
                    }
                }
            }
        }
    }



    public static void main(String[] args) {
        System.out.println(removeLeft("你好:${2}, 我oo${1}", 3));
        System.out.println(removeRight("你好:${2}, 我oo${1}", 3));
    }

}
