package utils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import org.apache.commons.compress.utils.Sets;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import security.MD5;
import server.FightServer;
import server.ServerConfig;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;

import data.GameDataManager;

/**
 * CSV校验插件
 * 
 * @author liujiang
 *
 */
public class CsvUtils {
	private static final Logger LOGGER = Logger.getLogger(CsvUtils.class);
	static final String CSV_DIR = CsvUtils.class.getClassLoader()
			.getResource("").getPath()
			+ "../csv/";
    static Set<String> rightFiles;
    static final String FILE_LIST = "filelist.txt";

	public static void init() throws Exception {
	    String url = ServerConfig.getInstance().getCsvUrl();
        if (url == null || url.trim().isEmpty() || FightServer.getInstance().isIDEMode()) {// 未配置远程获取地址，则使用本地配置文件
            LOGGER.info("load csv files from local");
            loadGameData();
        } else {
            LOGGER.info("load csv files from remote, url:" + url);
            reloadCsvFiles(url);
        }
	}

    /**
     * 重载csv文件
     * 
     * @param url
     * @throws Exception
     */
    public static void reloadCsvFiles(String url) throws Exception {
        if (url != null && !url.trim().isEmpty()) {
            url = StringUtils.endsWith(url, "/") ? url : url + "/";// 目录检查补全
            String localDir = CSV_DIR;
            File file = new File(localDir);
            if (!file.exists() || !file.isDirectory()) {
                file.mkdirs();
            }
            if (rightFiles == null) {
                GameDataManager temp = new GameDataManager();
                Field[] fields = temp.getClass().getDeclaredFields();
                Field.setAccessible(fields, true);
                String mark = "CfgContainer";
                Set<String> rFiles = Sets.newHashSet();
                for (int i = 0; i < fields.length; i++) {
                    Object obj = fields[i].get(temp);
                    if (obj == null) {
                        continue;
                    }
                    String name = obj.getClass().getSimpleName();
                    if (name.endsWith("CfgContainer")) {
                        String f = StringUtils.substringBefore(name, mark) + ".csv";
                        rFiles.add(f);
                    }
                }
                Field.setAccessible(fields, false);
                rightFiles = rFiles;
            }
            LOGGER.info("reloadCsvFiles begin.");
            HttpUtil.downloadFile(url + FILE_LIST, localDir);
            List<String> fileNames =
                    FileUtil.readLines(new File(CSV_DIR + FILE_LIST), StrEx.Charset_UTF8);
            for (String f : fileNames) {
                if (!rightFiles.contains(f)) {
                    LOGGER.error("====> invalide csv file:" + f);
                    continue;
                }
                LOGGER.info("====> reloadCsvFile:" + f);
                boolean isNewFile = true;
                String oldContent = null;
                String oldMd5 = null;
                File csvFile = new File(localDir + f);
                if (csvFile.exists()) {
                    oldContent = FileUtil.readString(csvFile, StrEx.Charset_UTF8);
                    oldMd5 = MD5.MD5Encode(oldContent);
                    isNewFile = false;
                }
                HttpUtil.downloadFile(url + f, localDir);
                if (!isNewFile) {
                    csvFile = new File(localDir + f);
                    String content = FileUtil.readString(csvFile, StrEx.Charset_UTF8);
                    String newMd5 = MD5.MD5Encode(content);
                    if (!newMd5.equals(oldMd5)) {// 日志记录有变化的文件
                        LOGGER.info("====> csvFileChanged:" + f + ",oldContent:" + oldContent
                                + " \n newContent:" + content);
                    }
                }
            }
            LOGGER.info("reloadCsvFiles end.");
        }
        loadGameData();
    }

    /**
     * 加载GameData
     */
    private static void loadGameData() {
        try {
            GameDataManager.Ainit(); // 加载配置数据
            FightServer.getInstance().setReloadGameDataTime(System.currentTimeMillis());
        } catch (Exception e) {
            LOGGER.error("load GameData fail!", e);
            throw new RuntimeException("加载GameData错误", e);
        }
        LOGGER.info("loadGameData finish");
    }

	public static void main(String[] args) {
	}
}
