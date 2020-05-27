package logic.gift;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class GiftCodeHelp {
    private static final GiftCodeHelp DEFAULT = new GiftCodeHelp();

    public static GiftCodeHelp getDefault() {
        return DEFAULT;
    }

    private static Map<String, InvitationCode> dataMap = new HashMap<>();

    public static void init() throws ClientProtocolException, IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String url = "http://192.168.20.182:9898/package/code/query";
        QueryInvitCodeSQL str = new QueryInvitCodeSQL();
        str.setLimitStart(1);
        str.setLimitLen(10000);
        RequestConfig requestConfig = getRequestConfig();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        String obj = JSONObject.toJSONString(str);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("queryInvitCodeSQL", obj);

        StringEntity stringEntity = new StringEntity(JSON.toJSONString(jsonObject, true), "UTF-8");
        httpPost.setEntity(stringEntity);
        CloseableHttpResponse response = httpClient.execute(httpPost);
        try {
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                return;
            }
            HttpEntity responseEntity = response.getEntity();
            String text = EntityUtils.toString(responseEntity, "UTF-8");
            JSONObject jj = JSONObject.parseObject(text);
            Object object = jj.get("contest");
            List<InvitationCode> list = JSON.parseArray(object.toString(), InvitationCode.class);
            for (InvitationCode code : list) {
                if (code.getGot() == null || code.getGot() == 1) {
                    continue;
                }
                dataMap.put(code.getId(), code);
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    protected static RequestConfig getRequestConfig() {
        return RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(10000).build();
    }

    public InvitationCode getCode(String id) {
        return dataMap.get(id);
    }

    public Set<String> ketSet() {
        return dataMap.keySet();
    }
}
