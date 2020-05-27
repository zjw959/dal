
package logic.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * 敏感词判断/过滤工具类
 */
public class SensitiveWordFilter {
    private final Logger log = Logger.getLogger(SensitiveWordFilter.class);
    private final List<String> allSensitiveWords = new ArrayList<>(); // 所有敏感词
    private final WordNode rootWordNode = new WordNode('R');
    private boolean isInitialized = false;

    /**
     * 初始化敏感词集
     */
    public synchronized void initSensitiveWords() {
        this.allSensitiveWords.clear();
//        Map<Integer, SensitiveData> maps = GameDataManager.getSensitiveDataKV();
//        for (SensitiveData value : maps.values()) {
//            allSensitiveWords.add(value.getName());
//        }
        buildWordTree();
        isInitialized = true;
    }

    /**
     * 检测敏感词
     * 
     * @param str
     * @return
     */
    public boolean check(String str) {
        return hasSensitiveWord(str) || hasDBSensitiveWord(str) || hasSpecialWord(str)
                || hasEmojiWord(str);
    }

    public boolean check(String str, boolean checkSpecialWord) {
        if (checkSpecialWord) {
            return check(str);
        } else {
            return hasSensitiveWord(str) || hasDBSensitiveWord(str) || hasEmojiWord(str);
        }
    }

    /**
     * 检测特殊字符
     * 
     * @param str
     * @return
     */
    private boolean hasSpecialWord(String str) {

        for (int i = 0; i < str.length(); i++) {
            String tmp = str.substring(i, i + 1);
            if (!tmp.matches("[\\u4e00-\\u9fbf]+") && !tmp.matches("\\w+")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否包含数据库敏感词
     * 
     * @return true:包含 false:未包含
     */
    private boolean hasDBSensitiveWord(String str) {
        if (str == null || str.equals("")) {
            return false;
        }

//        List<DatabasefilterData> datas = GameDataManager.getDatabasefilterDatas();
//        for (DatabasefilterData bean : datas) {
//            if (bean.getTContent().equals(""))
//                continue;
//            if (str.toUpperCase().indexOf(bean.getTContent()) != -1)
//                return true;
//        }
        return false;
    }

    //
    // 默认构建方式
    //
    // /**
    // * 初始化敏感词集
    // *
    // * @param allSensitiveWords
    // */
    // public synchronized void initSensitiveWords(List<String> allSensitiveWords)
    // {
    // this.allSensitiveWords.clear();
    // this.allSensitiveWords.addAll(allSensitiveWords);
    // buildWordTree();
    // isInitialized = true;
    // }

    /**
     * 是否有敏感词
     * 
     * @param content
     * @return
     */
    private synchronized boolean hasSensitiveWord(String content) {
        if (!isInitialized)
            throw new RuntimeException("SensitiveWordFilter has not initialized");
        char[] chars = content.toCharArray();
        WordNode node = rootWordNode;
        StringBuilder buffer = new StringBuilder();
        List<String> word = new ArrayList<>();
        int a = 0;
        while (a < chars.length) {
            node = findNode(node, chars[a]);
            if (node == null) {
                node = rootWordNode;
                a = a - word.size();
                buffer.append(chars[a]);
                word.clear();
            } else if (node.endFlag == 1) {
                node = null;
                return true;
            } else {
                word.add(String.valueOf(chars[a]));
            }
            a++;
        }
        chars = null; // help GC ?
        word.clear();
        word = null; // help GC ?
        return false;
    }

    /**
     * 是否包含不同的敏感词n个,n又配置表决定
     * 
     * @return
     */
    public boolean hasDifferentWords(String content) {

        if (!isInitialized)
            throw new RuntimeException("SensitiveWordFilter has not initialized");

        try {

            char[] chars = content.toCharArray();
            WordNode node = rootWordNode;
            StringBuilder buffer = new StringBuilder();
            List<String> badList = new ArrayList<>();
            List<String> sensitives = new ArrayList<>();
            int a = 0;
            int flag = -1;
            while (a < chars.length) {
                node = findNode(node, chars[a]);
                if (node == null) {

                    // if(badList.size() > 1) {
                    // for (String str : badList) {
                    // buffer.append(str);
                    // }
                    // if(!sensitives.contains(buffer.toString()))
                    // sensitives.add(buffer.toString());
                    // buffer.delete(0, buffer.length());
                    // }

                    node = rootWordNode;
                    a = a - badList.size();
                    flag = -1;
                    if (badList.size() > 0) {
                        badList.clear();
                    }
                } else if (node.endFlag == 1) {
                    if (badList.isEmpty())
                        flag = a;
                    badList.add(String.valueOf(chars[a]));
                    if (badList.size() == 1) {
                        buffer.append(String.valueOf(chars[a]));
                        if (!sensitives.contains(buffer.toString()))
                            sensitives.add(buffer.toString());
                        buffer.delete(0, buffer.length());
                    } else {
                        for (String str : badList) {
                            buffer.append(str);
                        }
                        if (!sensitives.contains(buffer.toString()))
                            sensitives.add(buffer.toString());
                        buffer.delete(0, buffer.length());
                    }
                    if (a == chars.length - 1) {
                        for (String str : badList) {
                            buffer.append(str);
                        }
                        if (!sensitives.contains(buffer.toString()))
                            sensitives.add(buffer.toString());
                        if (flag != -1) {
                            a = flag;
                            flag = -1;
                            node = rootWordNode;
                            badList.clear();
                            buffer.delete(0, buffer.length());
                        }
                    }
                } else {
                    if (badList.isEmpty())
                        flag = a;
                    badList.add(String.valueOf(chars[a]));
                    // if (a == chars.length - 1) {
                    // if(badList.size() > 1) {
                    // for (String str : badList) {
                    // buffer.append(str);
                    // }
                    // if(!sensitives.contains(buffer.toString()))
                    // sensitives.add(buffer.toString());
                    // }
                    // }
                }
                a++;
            }

            // return sensitives.size() >=
            // GameDataManager.getGlobalconfigData(1268).getT_int_param();
            return sensitives.size() >= 3;

        } catch (Exception e) {
            log.error("检测重复敏感词异常", e);
            return false;
        }

    }

    /**
     * 过滤敏感词
     * 
     * @param content
     * @return
     */
    public synchronized String filterSensitiveWord(String content) {
        if (!isInitialized)
            throw new RuntimeException("SensitiveWordFilter has not initialized");
        char[] chars = content.toCharArray();
        WordNode node = rootWordNode;
        StringBuilder buffer = new StringBuilder();
        List<String> badList = new ArrayList<>();
        int a = 0;
        while (a < chars.length) {
            node = findNode(node, chars[a]);
            if (node == null) {
                node = rootWordNode;
                a = a - badList.size();
                if (badList.size() > 0) {
                    badList.clear();
                }
                buffer.append(chars[a]);
            } else if (node.endFlag == 1) {
                badList.add(String.valueOf(chars[a]));
                for (@SuppressWarnings("unused")
                String badList1 : badList) {
                    buffer.append("*");
                }
                node = rootWordNode;
                badList.clear();
            } else {
                badList.add(String.valueOf(chars[a]));
                if (a == chars.length - 1) {
                    for (String badList1 : badList) {
                        buffer.append(badList1);
                    }
                }
            }
            a++;
        }
        return buffer.toString();
    }

    private void buildWordTree() {
        rootWordNode.wordNodes.clear();
        for (String str : allSensitiveWords) {
            char[] chars = str.toCharArray();
            if (chars.length > 0)
                insertNode(rootWordNode, chars, 0);
        }
    }

    private void insertNode(WordNode node, char[] cs, int index) {
        WordNode n = findNode(node, cs[index]);
        if (n == null) {
            n = new WordNode(cs[index]);
            node.wordNodes.put(String.valueOf(cs[index]), n);
        }

        if (index == (cs.length - 1))
            n.endFlag = 1;

        index++;
        if (index < cs.length)
            insertNode(n, cs, index);
    }

    private WordNode findNode(WordNode node, char word) {
        return node.wordNodes.get(String.valueOf(word));
    }

    Pattern emoji = Pattern.compile(
            "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
            Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

    private boolean hasEmojiWord(String str) {
        return emoji.matcher(str).find();
    }

    private static class WordNode {
        // public char word;
        public int endFlag;
        public HashMap<String, WordNode> wordNodes = new HashMap<>();

        public WordNode(char word) {
            // this.word = word;
            this.endFlag = 0;
        }
    }

    public static SensitiveWordFilter getInstance() {
        return Singleton.INSTANCE.getService();
    }

    private enum Singleton {
        INSTANCE;
        SensitiveWordFilter filter;

        Singleton() {
            this.filter = new SensitiveWordFilter();
        }

        SensitiveWordFilter getService() {
            return filter;
        }
    }
}
