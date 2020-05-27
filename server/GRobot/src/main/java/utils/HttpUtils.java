package utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpUtils {

    private static final Log LOGGER = LogFactory.getLog(HttpUtils.class);

    /**
     * http get
     *
     * @param uri
     * @return
     */
    public static String get(String uri) {
        return get(uri, StrEx.Charset_UTF8);
    }

    /**
     * http get 自定编码
     *
     * @param uri
     * @param charset 字符集
     * @return
     */
    public static String get(String uri, String charset) {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(uri);
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64)"
                + " AppleWebKit/537.36 (KHTML, like Gecko)" + " Chrome/46.0.2490.80 Safari/537.36");
        CloseableHttpResponse response;
        try {
            response = httpClient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            String res = EntityUtils.toString(httpEntity, charset);
            return res;
        } catch (IOException e) {
            LOGGER.error(e);
        }
        return null;
    }

    /**
     * http post
     *
     * @param uri
     * @param parameters map<String,String>
     * @return
     * @throws Exception
     */
    public static String post(String uri, Map<String, String> parameters) {
        return post(uri, parameters, StrEx.Charset_UTF8);
    }

    /**
     * http post
     *
     * @param uri
     * @param parameters map<String,String>
     * @param charset 字符集
     * @return
     * @throws Exception
     */
    public static String post(String uri, Map<String, String> parameters, String charset) {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(uri);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        if (parameters != null) {
            for (Entry<String, String> entry : parameters.entrySet()) {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        try {
            post.setEntity(new UrlEncodedFormEntity(nvps, charset));
            CloseableHttpResponse response = httpClient.execute(post);
            HttpEntity entity = response.getEntity();
            String res = EntityUtils.toString(entity, charset);
            return res;
        } catch (Exception e) {
            LOGGER.error(e);
            return null;
        }
    }

    /**
     * http post
     *
     * @param uri
     * @return
     * @throws Exception
     */
    public static String post(String uri) throws Exception {
        return post(uri, null);
    }

}
