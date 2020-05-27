package utils;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 敏感词汇工具类
 * 
 * @author wk.dai
 */
public class SensitiveWordsUtil {


    private static Logger logger = LoggerFactory.getLogger(SensitiveWordsUtil.class);
    /** 敏感词汇文件名 */
    private static String SENSITIVE_WORDS_FILE_PATH = "./config/sensitive_words.txt";
    /** 敏感词汇列表 */
    private static List<String> SENSITIVE_WORD_LIST = new ArrayList<String>(1000000);

    static {
        SENSITIVE_WORD_LIST = getSensitiveWordFromFile(SENSITIVE_WORDS_FILE_PATH, "\n");
    }

    public static String filterAndReplace(String word) {
        for (String item : SENSITIVE_WORD_LIST) {
            word = word.replace(item, "*");
        }
        return word;
    }

    /**
     * 过滤敏感词汇
     * 
     * @param word 待过滤的词
     * @return String 敏感词汇
     */
    public static String filter(String word) {
        for (String item : SENSITIVE_WORD_LIST) {
            if (word.contains(item)) {
                logger.info("words[{}] contains sensitive word[{}]", word, item);
                return item;
            }
        }
        return null;
    }

    /**
     * 从文本文件获取敏感词汇列表
     * 
     * @param filePath 文本文件路径
     * @param splitSymbol 分词符号
     * @return List<String> 词汇列表
     */
    private static List<String> getSensitiveWordFromFile(String filePath, String splitSymbol) {
        InputStream in = null;
        List<String> wordList = new ArrayList<String>();
        try {
            in = FileEx.openInputStream(filePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
            StringBuffer buffer = new StringBuffer(100000);
            String str = "";
            while ((str = reader.readLine()) != null) {
                buffer.append(str.trim()).append("\n");
            }
            String allCount = buffer.toString();
            String words[] = allCount.split(splitSymbol);
            for (String word : words) {
                String _word = word.trim();
                if (_word != null && _word.length() > 0 && !_word.contains("#")) {// #是注释标记
                    wordList.add(word);
                }
            }
        } catch (IOException ex) {
            logger.error("read sensitive word error", ex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error("close sensitive word reader error", e);
                }
            }
        }
        return wordList;
    }

}


