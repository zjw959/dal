package org;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : liu.jiang
 * @CreateDate : 2018年5月25日
 * @Description ：Proto java文件生成工具类
 */
public class ProtoBufBuilder {
	static final String LINE = File.separator;
    /** proto文件路径 */
	static final String INPUT_PATH = System.getProperty("user.dir") + LINE
			+ "proto";

    /** 生成的java文件路径 */
	static final String OUTPUT_PATH = System.getProperty("user.dir") + LINE
			+ "src" + LINE + "main" + LINE + "java";

    /** protoc.exe 路径 */
	static final String EXE_FILE = System.getProperty("user.dir") + LINE
			+ "proto" + LINE + "protoc.exe";

    /** 构建方法 */
    public static void buildOne(String fileName) throws IOException {
        build(INPUT_PATH, OUTPUT_PATH, EXE_FILE, fileName);
    }

    /** 构建方法 */
    public static void buildAll() throws IOException {
        build(INPUT_PATH, OUTPUT_PATH, EXE_FILE, null);
    }

    /** 构建方法 */
    private static void build(String inputPath, String outputPath, String exeFile, String fileName)
			throws IOException {
		System.out
				.println("##################### start build #####################");
		List<File> files = new ArrayList<File>();
		files = getAllFiles(files, ".proto", inputPath);
		for (File file : files) {
            if (fileName != null && !file.getName().equals(fileName)) {
                continue;
            }
            String command =
                    String.format("%s -I=%s --java_out=%s %s ", exeFile, inputPath, outputPath,
                            file.getPath());
            Runtime.getRuntime().exec(command);
            System.out.println(file.getPath());
		}
		System.out
				.println("##################### end build #####################");
	}

    /** 遍历制定目录下的所有指定后缀名文件（包含子目录） */
    private static List<File> getAllFiles(List<File> files, String suffix,
			String path) {
		File fileT = new File(path);
		if (fileT.exists()) {
			if (fileT.isDirectory()) {
				for (File f : fileT.listFiles()) {
					files = getAllFiles(files, suffix, f.getAbsolutePath());
				}
			} else {
				if ("*".equals(suffix) || fileT.getName().endsWith(suffix)) {
					files.add(fileT);
				}
			}
		}
		return files;
	}

	public static void main(String[] args) throws IOException {
        // buildAll();
        buildOne("S2CRechargeMsg.proto");
	}
}
