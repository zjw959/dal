package utils;

import java.io.*;
import java.util.List;
import java.util.zip.*;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.log4j.Logger;

public class Compress {
    private static final Logger LOGGER = Logger.getLogger(Compress.class);

    /**
     * GZIP压缩
     * 
     * @param data
     * @return
     */
    public static byte[] compressWithGZip(byte[] data) {
        try {
            try (ByteArrayOutputStream output = new ByteArrayOutputStream();
                    GzipCompressorOutputStream gzip = new GzipCompressorOutputStream(output)) {
                gzip.write(data);
                IOUtils.closeQuietly(gzip);
                return output.toByteArray();
            }
        } catch (IOException e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
        return null;
    }

    /**
     * GZIP 解压
     * 
     * @param data
     * @return
     */
    public static byte[] uncompressWithGZip(byte[] data) {
        try {
            try (GzipCompressorInputStream gzip =
                    new GzipCompressorInputStream(new ByteArrayInputStream(data))) {
                byte[] ret = IOUtils.toByteArray(gzip);
                IOUtils.closeQuietly(gzip);
                return ret;
            }
        } catch (IOException ex) {
            LOGGER.error(ex, ex);
        }
        return null;
    }

    /**
     * 对将单个文件进行压缩
     * 
     * @param source 源文件
     * @param target 目标文件
     * @throws IOException
     */
    public static void GZIPFile(String source, String target) throws IOException {
        FileInputStream fin = null;
        FileOutputStream fout = null;
        GZIPOutputStream gzout = null;
        try {
            fin = new FileInputStream(source);
            fout = new FileOutputStream(target);
            gzout = new GZIPOutputStream(fout);
            byte[] buf = new byte[1024];
            int num;
            while ((num = fin.read(buf)) != -1) {
                gzout.write(buf, 0, num);
            }
        } finally {
            if (gzout != null)
                gzout.close();
            if (fout != null)
                fout.close();
            if (fin != null)
                fin.close();
        }
    }

    /** 多个文件压缩成Zip文件 **/
    public static void mutileFileToZip(List<String> filePaths, String targetFileName) {
        try {
            File file = new File(targetFileName);
            FileOutputStream fout = new FileOutputStream(file);
            BufferedInputStream bin = null;
            ZipOutputStream zout = new ZipOutputStream(fout);
            for (String fileSource : filePaths) {
                String[] fileNames = fileSource.split("/");
                zout.putNextEntry(new ZipEntry(fileNames[fileNames.length - 1]));
                int c;
                bin = new BufferedInputStream(new FileInputStream(fileSource));
                while ((c = bin.read()) != -1) {
                    zout.write(c);
                }
                bin.close();
            }
            zout.close();
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
        LOGGER.info("压缩成功");
    }

    /**
     * 执行压缩.
     * 
     * @param data 待压缩数据
     * @return 压缩后的数据
     */
    public static byte[] compress(byte[] data) {
        byte[] output = new byte[0];

        Deflater compresser = new Deflater();
        compresser.reset();
        compresser.setInput(data);
        compresser.finish();

        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        try {
            byte[] buf = new byte[1024];
            while (!compresser.finished()) {
                int i = compresser.deflate(buf);
                bos.write(buf, 0, i);
            }
            output = bos.toByteArray();
        } catch (Exception e) {
            output = data;
            LOGGER.error(e, e);
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                LOGGER.error(e, e);
            }
        }
        compresser.end();

        return output;
    }

    /**
     * 执行压缩.
     * 
     * @param data 待压缩数据
     * @param os 输出流
     */
    public static void compress(byte[] data, OutputStream os) {
        DeflaterOutputStream dos = new DeflaterOutputStream(os);

        try {
            dos.write(data, 0, data.length);
            dos.finish();
            dos.flush();
        } catch (IOException e) {
            LOGGER.error(e, e);
        }
    }

    /**
     * 执行解压.
     * 
     * @param data 待压缩的数据
     * @return 解压后的数据
     */
    public static byte[] decompress(byte[] data) {
        byte[] output = new byte[0];

        Inflater decompresser = new Inflater();
        decompresser.reset();
        decompresser.setInput(data);

        ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);
        try {
            byte[] buf = new byte[1024];
            while (!decompresser.finished()) {
                int i = decompresser.inflate(buf);
                if (i == 0)
                    break;
                o.write(buf, 0, i);
            }
            output = o.toByteArray();
        } catch (DataFormatException e) {
            output = null;
            LOGGER.error(e, e);
            return null;
        } finally {
            try {
                o.close();
            } catch (IOException e) {
                LOGGER.error(e, e);
            }
        }
        decompresser.end();

        return output;
    }

    /**
     * 执行解压.
     * 
     * @param is 输入流
     * @return 解压后的数据
     */
    public static byte[] decompress(InputStream is) {
        InflaterInputStream iis = new InflaterInputStream(is);
        ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
        try {
            int i = 1024;
            byte[] buf = new byte[i];

            while ((i = iis.read(buf, 0, i)) > 0) {
                os.write(buf, 0, i);
            }
        } catch (IOException e) {
            LOGGER.error(e, e);
        }

        return os.toByteArray();
    }


    public static byte[] decodeBase64(byte[] base64) {
        return Base64.decodeBase64(base64);
    }

    public static String encodeBase64(byte[] bytes) {
        return new String(Base64.encodeBase64(bytes));
    }
}
