/**
 * 
 */
package com.controller.abs;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

import com.alibaba.fastjson.JSON;

/**
 * @author zjw
 *
 */
public abstract class AbstractHandler {
    protected static final int OK = 0;
    protected static final int ERROR = -1;
    protected static final String KEY_STATUS = "status";
    protected static final String KEY_DATA = "data";
    protected static final String LOG_ERROR_PROGRAMMER_PREFIX = "[程序错误] ";

    /**
     * @param request
     * @return
     */
    protected String getRequestJson(HttpServletRequest request) throws IOException {
        InputStream in = request.getInputStream();
        int length = 0;
        ByteArrayOutputStream bos = null;
        DataOutputStream output = null;
        try {
            bos = new ByteArrayOutputStream();
            output = new DataOutputStream(bos);
            byte[] data = new byte[4096];
            // int totalLen = 0;
            while ((length = in.read(data, 0, data.length)) != -1) {
                output.write(data, 0, length);
                // totalLen +=length;
            }
            byte[] bytes = bos.toByteArray();
            String text = new String(bytes, "UTF-8");
            return text;
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    /**
     * @param request
     * @param _class
     * @return
     */
    protected <T> T getPostObject(HttpServletRequest request, Class<T> _class) throws IOException {
        InputStream in = request.getInputStream();
        int length = 0;
        ByteArrayOutputStream bos = null;
        DataOutputStream output = null;
        try {
            bos = new ByteArrayOutputStream();
            output = new DataOutputStream(bos);
            byte[] data = new byte[4096];
            // int totalLen = 0;
            while ((length = in.read(data, 0, data.length)) != -1) {
                output.write(data, 0, length);
                // totalLen +=length;
            }
            byte[] bytes = bos.toByteArray();
            String text = new String(bytes, "UTF-8");
            if (text == null || text.length() < 1) {
                return null;
            }
            return JSON.parseObject(text, _class);
        } finally {
            if (output != null) {
                output.close();
            }
        }

    }

    /**
     * 获取流中的字符串
     * 
     * @param is
     * @return
     */
    protected String stream2String(InputStream is) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new java.io.InputStreamReader(is));
            String line = "";
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tryClose(br);
        }
        return "";
    }

    /**
     * 向客户端应答结果
     * 
     * @param response
     * @param content
     */
    protected void sendToClient(HttpServletResponse response, String content) {
        response.setContentType("text/plain;charset=utf-8");
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.write(content);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tryClose(writer);
        }
    }

    /**
     * 关闭输出流
     * 
     * @param os
     */
    protected void tryClose(OutputStream os) {
        try {
            if (null != os) {
                os.close();
                os = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭writer
     * 
     * @param writer
     */
    protected void tryClose(java.io.Writer writer) {
        try {
            if (null != writer) {
                writer.close();
                writer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭Reader
     * 
     * @param reader
     */
    protected void tryClose(java.io.Reader reader) {
        try {
            if (null != reader) {
                reader.close();
                reader = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final SimpleDateFormat datetimeFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    protected boolean after(Date dateTime, String dateExpression) {
        if (dateTime == null)
            return true;
        if (dateExpression != null && dateExpression.length() > 0
                && (dateExpression = dateExpression.trim()).length() > 0) {
            try {
                Date date = datetimeFormat.parse(dateExpression);
                return dateTime.after(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    protected boolean before(Date dateTime, String dateExpression) {
        if (dateTime == null)
            return true;
        if (dateExpression != null && dateExpression.length() > 0
                && (dateExpression = dateExpression.trim()).length() > 0) {
            try {
                Date date = datetimeFormat.parse(dateExpression);
                return dateTime.before(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    protected boolean contains(String str, String expression) {
        if (expression != null && expression.length() > 0) {
            if (expression.contains("%")) {
                String value = expression.replace("%", "");
                if (!str.contains(value)) {
                    return false;
                }
            } else {
                if (!expression.equals(str)) {
                    return false;
                }
            }
        }
        return true;
    }

    public String sendClient(HttpServletResponse response, String path) throws Exception {
        response.setCharacterEncoding("UTF-8");
        File file = new File(path);
        if (!file.exists()) {
            // 让浏览器用UTF-8解析数据
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            response.getWriter().write("文件不存在或已过期,请重新生成");
            return "";
        }
        String fileName = URLEncoder.encode(path.substring(path.lastIndexOf("/") + 1), "UTF-8");
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition",
                String.format("attachment; filename=\"%s\"", fileName));
        byte[] data = compress(loadFile(path, file));
        String json = new String(Base64.encodeBase64(data));
        return json;
    }

    public byte[] loadFile(String path, File file) throws IOException {

        InputStream is = null;
        ByteArrayOutputStream baos = null;
        byte[] data = null;
        try {
            is = new FileInputStream(path);
            byte[] buffer = new byte[1024];
            baos = new ByteArrayOutputStream((int) file.length());
            int len;
            while ((len = is.read(buffer)) > 0) {
                baos.write(buffer, 0, len);
            }
            data = baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (is != null)
                    is.close();
                if (baos != null)
                    baos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return data;
    }

    /**
     * 对byte[]进行压缩
     * 
     * @param 要压缩的数据
     * @return 压缩后的数据
     */
    public static byte[] compress(byte[] data) {
        System.out.println("before:" + data.length);

        GZIPOutputStream gzip = null;
        ByteArrayOutputStream baos = null;
        byte[] newData = null;

        try {
            baos = new ByteArrayOutputStream();
            gzip = new GZIPOutputStream(baos);

            gzip.write(data);
            gzip.finish();
            gzip.flush();

            newData = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                gzip.close();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("after:" + newData.length);
        return newData;
    }

    /**
     * GZIP解压缩
     * 
     */
    public static byte[] gzipUncompress(byte[] source) throws Exception {
        if (source == null || source.length == 0) {
            return null;
        }
        InputStream bais = new ByteArrayInputStream(source);

        byte[] buffer = new byte[1024];
        int n;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPInputStream gin = null;
        try {
            gin = new GZIPInputStream(bais);
            while ((n = gin.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }

            out.close();
            bais.close();
            return out.toByteArray();
        } finally {
            gin.close();
        }
    }

}
