package util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestParam;

import com.enity.PackageInfo;
import com.google.common.collect.Maps;

/**
 * csv文件读写工具类
 *
 */
public class CsvUtil {

    private static final Logger logger = LoggerFactory.getLogger("CsvUtils.class");

    /**
     * 写csv文件 (一次性写 数据不宜过大)
     *
     * @param objectList 对象
     * @param fileHeader 头说明
     * @param fileName 文件名称
     * @return File 文件
     * @throws Exception
     */
    public static File writeCsv(List<Object> objectList, String[] fileHeader, String fileName,
            String outPutPath) {
        // 这里显式地配置一下CSV文件的Header，然后设置跳过Header（要不然读的时候会把头也当成一条记录）
        CSVFormat format = CSVFormat.DEFAULT.withHeader(fileHeader).withRecordSeparator("\n");
        // 这个是定位 判断某个字段的数据应该放在records数组中的那个位子
        Map<String, Integer> map = Maps.newHashMap();
        for (int i = 0; i < fileHeader.length; i++) {
            map.put(fileHeader[i], i);
        }
        File file = new File(outPutPath);
        if (!file.exists()) {
            file.mkdir();
        } else {
            delteFile(file);
        }
        // 定义文件名格式并创建
        File csvFile = null;
        try {
            csvFile = File.createTempFile(fileName, ".csv", new File(outPutPath));
            // 获取对象的PropertyDescriptor
            Map<String, PropertyDescriptor> descriptorMap = null;
            // 附加
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(csvFile), "UTF-8"));
            CSVPrinter printer = new CSVPrinter(bw, format);
            for (Object object : objectList) {
                if (descriptorMap == null || descriptorMap.isEmpty()) {
                    descriptorMap = CsvUtil.getCsvFieldMapPropertyDescriptor(object.getClass());
                }
                String[] records = new String[fileHeader.length];
                for (Map.Entry<String, Integer> stringIntegerEntry : map.entrySet()) {
                    if (descriptorMap.containsKey(stringIntegerEntry.getKey())) {
                        records[map.get(stringIntegerEntry.getKey())] = String.valueOf(descriptorMap
                                .get(stringIntegerEntry.getKey()).getReadMethod().invoke(object));
                    }
                }
                printer.printRecord(Arrays.asList(records));
            }
            bw.flush();
            bw.close();
            printer.close();
            logger.info("生成csv成功");
        } catch (Exception e) {
            logger.error("CsvUtils.writeCsv,写csv文件失败,message:{}", e.getMessage(), e);
        }
        return csvFile;
    }

    /**
     * 获取对应对象中包含CsvCsvField字段的 PropertyDescriptor
     *
     * @param tClass 对象的class
     * @return Map
     * @throws Exception 异常
     */
    public static Map<String, PropertyDescriptor> getCsvFieldMapPropertyDescriptor(Class tClass)
            throws Exception {
        Map<String, PropertyDescriptor> descriptorMap = Maps.newHashMap();
        BeanInfo beanInfo = Introspector.getBeanInfo(tClass);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            // 获取该字段赋值过来的 字段名称
            if (propertyDescriptor.getWriteMethod() == null) {
                continue;
            }
            Field field = tClass.getDeclaredField(propertyDescriptor.getName());
            CsvField csvField = field.getAnnotation(CsvField.class);
            if (csvField == null) {
                continue;
            }
            String fieldMetaName = csvField.fileName();
            if ("".equals(fieldMetaName)) {
                continue;
            }
            descriptorMap.put(fieldMetaName, propertyDescriptor);
        }
        return descriptorMap;
    }

    /**
     * 读取csv文件 (一次性读取文件不宜过大)
     *
     * @param filePath 文件路径
     * @param headers csv列头
     * @param tClass 返回对象的类型
     * @return CSVRecord 列表
     * @throws BizException 异常
     **/
    public static <T> List<T> readCSV(String filePath, String[] headers, Class<T> tClass) {
        // 创建CSVFormat
        CSVFormat format = CSVFormat.DEFAULT.withHeader(headers);
        // 获取对象的PropertyDescriptor
        List<T> tList = new ArrayList<>();
        try {
            Map<String, PropertyDescriptor> descriptorMap =
                    CsvUtil.getCsvFieldMapPropertyDescriptor(tClass);
            FileReader fileReader = new FileReader(filePath);
            // 创建CSVParser对象
            CSVParser parser = new CSVParser(fileReader, format);
            Map<String, Integer> map = parser.getHeaderMap();
            for (CSVRecord record : parser) {
                T t = tClass.newInstance();
                for (Map.Entry<String, Integer> stringIntegerEntry : map.entrySet()) {
                    if (descriptorMap.containsKey(stringIntegerEntry.getKey())
                            && record.size() > stringIntegerEntry.getValue()) {
                        descriptorMap.get(stringIntegerEntry.getKey()).getWriteMethod().invoke(t,
                                record.get(stringIntegerEntry.getValue()));
                    }
                }
                tList.add(t);
            }
            parser.close();
            fileReader.close();
        } catch (Exception e) {
            logger.error("CsvUtils.readCSV,读取csv文件,message:{}", e.getMessage(), e);
        }
        return tList;
    }

    public static void main(String[] args) {
        List<Object> exportData = new ArrayList<Object>();
        PackageInfo info =
                new PackageInfo(1, "测试", "test", "100", new Date(), new Date(), "aaa", 100, 0);
        exportData.add(info);
        PackageInfo inf2 =
                new PackageInfo(2, "测试2", "test2", "100", new Date(), new Date(), "aaa", 100, 0);
        exportData.add(inf2);
        // 反射获取类属性，用于csv的表头
        Field[] fields = PackageInfo.class.getDeclaredFields();
        String[] csvHeaders = new String[fields.length];
        for (short i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String fieldName = field.getName();
            csvHeaders[i] = fieldName;
        }
        String path = "./export";
        String fileName = "文件导出";
        writeCsv(exportData, csvHeaders, fileName, path);
        // readCSV(path + "文件导出6882525703746982801.csv", csvHeaders, PackageInfo.class);
        // delteFile(new File("./export"));
    }

    public static void download(HttpServletResponse response, @RequestParam String path)
            throws Exception {
        // 让servlet用UTF-8转码，默认为ISO8859
        response.setCharacterEncoding("UTF-8");

        File file = new File(path);
        if (!file.exists()) {
            // 让浏览器用UTF-8解析数据
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            response.getWriter().write("文件不存在或已过期,请重新生成");
            return;
        }

        String fileName = URLEncoder.encode(path.substring(path.lastIndexOf("/") + 1), "UTF-8");
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition",
                String.format("attachment; filename=\"%s\"", fileName));
        InputStream is = null;
        OutputStream os = null;

        try {
            is = new FileInputStream(path);
            byte[] buffer = new byte[1024];
            os = response.getOutputStream();
            int len;
            while ((len = is.read(buffer)) > 0) {
                os.write(buffer, 0, len);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (is != null)
                    is.close();
                if (os != null)
                    os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    // 递归删除文件及文件夹
    public static void delteFile(File file) {
        File[] filearray = file.listFiles();
        long now = System.currentTimeMillis();
        if (filearray != null) {
            for (File f : filearray) {
                if (f.isDirectory()) {
                    delteFile(f);
                } else {
                    // 删除超过10分钟的文件
                    if ((now - f.lastModified()) >= 10 * 60 * 1000) {
                        f.delete();
                    }

                }
            }
        }
    }

}
