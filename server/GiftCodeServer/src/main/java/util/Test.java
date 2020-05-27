package util;

import java.io.IOException;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.controller.helper.CreateInvitCodeStruct;
import com.controller.helper.QueryChineseCode;
import com.controller.helper.QueryPackage;
import com.enity.PackageInfo;
import com.enity.chinese.ChineseCode;

public class Test {
    private static ExecutorService backPool = Executors.newFixedThreadPool(200);

    public static void main(String[] args) throws ClientProtocolException, IOException {
        // app();
        // testInv();
        // queryPack();
        // test1();
        queryChinese();
    }

    public static void app() {
        for (int i = 0; i < 50; i++) {
            backPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        use();
                    } catch (ClientProtocolException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static void test1() throws ClientProtocolException, IOException {
        Random random = new Random();
        int id = random.nextInt(100000);
        PackageInfo info = new PackageInfo(112, "测试礼包1", "LOCAL_TEST", "0", new Date(),
                new Date(new Date().getTime() + 1000000000), "500001:1000,500002:20000", 100, 0);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String url = "http://192.168.20.182:9898/package/create";


        RequestConfig requestConfig = getRequestConfig();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        String obj = JSONObject.toJSONString(info);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("packageInfo", obj);

        StringEntity stringEntity = new StringEntity(JSON.toJSONString(jsonObject, true), "UTF-8");
        httpPost.setEntity(stringEntity);
        CloseableHttpResponse response = httpClient.execute(httpPost);
        String status = "";
        try {
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                return;
            }
            HttpEntity responseEntity = response.getEntity();
            String text = EntityUtils.toString(responseEntity, "UTF-8");
            // JSONObject resultJO = JSONObject.parseObject(text);
            // status = resultJO.getString("status");
            // System.err.println(text);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public static void test2() throws ClientProtocolException, IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String url = "http://127.0.0.1:8080/package/delete";
        RequestConfig requestConfig = getRequestConfig();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        JSONObject obj = new JSONObject();
        obj.put("id", 1);
        String result = JSON.toJSONString(obj, true);
        StringEntity stringEntity = new StringEntity(result);
        httpPost.setEntity(stringEntity);
        CloseableHttpResponse response = httpClient.execute(httpPost);
        String status = "";
        try {
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                return;
            }
            HttpEntity responseEntity = response.getEntity();
            String text = EntityUtils.toString(responseEntity, "UTF-8");
            // JSONObject resultJO = JSONObject.parseObject(text);
            // status = resultJO.getString("status");
            if (!status.equals("success")) {
                return;
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public static void testInv() throws ClientProtocolException, IOException {
        // Random random = new Random();
        // int id = random.nextInt(100000);
        CreateInvitCodeStruct str = new CreateInvitCodeStruct();
        str.setCount(100);
        str.setPackageId(1);
        str.setType(1);
        // QueryChineseUse chineseUse = new QueryChineseUse();
        // chineseUse.setId("江湖333");
        // chineseUse.setLimitStart(1);
        // chineseUse.setLimitLength(50);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String url = "http://127.0.0.1:9898/package/code/create";

        RequestConfig requestConfig = getRequestConfig();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        String obj = JSONObject.toJSONString(str);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("struct", obj);

        StringEntity stringEntity = new StringEntity(JSON.toJSONString(jsonObject, true), "UTF-8");
        httpPost.setEntity(stringEntity);
        CloseableHttpResponse response = httpClient.execute(httpPost);

        try {
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                return;
            }
            HttpEntity responseEntity = response.getEntity();
            String text = EntityUtils.toString(responseEntity, "UTF-8");
            // JSONObject resultJO = JSONObject.parseObject(text);
            // status = resultJO.getString("status");
            System.err.println(text);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public static void testChin(int i) throws ClientProtocolException, IOException {
        ChineseCode str = new ChineseCode();
        str.setId("江湖" + i);
        str.setPackageId(1);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String url = "http://192.168.20.182:9898/create/chinese/code";

        RequestConfig requestConfig = getRequestConfig();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        String obj = JSONObject.toJSONString(str);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("chineseCode", obj);

        StringEntity stringEntity = new StringEntity(JSON.toJSONString(jsonObject, true), "UTF-8");
        httpPost.setEntity(stringEntity);
        CloseableHttpResponse response = httpClient.execute(httpPost);
        String status = "";
        try {
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                return;
            }
            HttpEntity responseEntity = response.getEntity();
            String text = EntityUtils.toString(responseEntity, "UTF-8");
            // JSONObject resultJO = JSONObject.parseObject(text);
            // status = resultJO.getString("status");
            System.err.println(text);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    class RunT implements Runnable {
        @Override
        public void run() {
            try {
                test1();
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static void queryPack() throws ClientProtocolException, IOException {
        QueryPackage q = new QueryPackage();
        // q.setId("1");
        // q.setName("测试礼包king");
        // q.setStartTime("2018-01-08 11:56:32");
        // q.setEndTime("2018-08-02 11:29:15");
        // q.setChannel("1");
        q.setLimitStart(1);
        q.setLimitLen(50);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // String url = "http://127.0.0.1:9088/package/create.do";
        String url = "http://192.168.20.182:9898/package/query";
        RequestConfig requestConfig = getRequestConfig();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        String obj = JSONObject.toJSONString(q);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("queryPackage", obj);

        StringEntity stringEntity = new StringEntity(JSON.toJSONString(jsonObject, true), "UTF-8");
        httpPost.setEntity(stringEntity);

        CloseableHttpResponse response = httpClient.execute(httpPost);
        String status = "";
        try {
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                return;
            }
            HttpEntity responseEntity = response.getEntity();
            String text = EntityUtils.toString(responseEntity, "UTF-8");
            // JSONObject resultJO = JSONObject.parseObject(text);
            // status = resultJO.getString("status");
            System.err.println(text);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public static void use() throws ClientProtocolException, IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // String url = "http://127.0.0.1:9088/package/create.do";
        Random random = new Random();
        int id = random.nextInt(100000);

        String url =
                "http://127.0.0.1:9898/package/use?id=ae677008uc&channelId=LOCAL_TEST&roleId=" + id;

        RequestConfig requestConfig = getRequestConfig();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);

        CloseableHttpResponse response = httpClient.execute(httpGet);
        String status = "";
        try {
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                return;
            }
            HttpEntity responseEntity = response.getEntity();
            String text = EntityUtils.toString(responseEntity, "UTF-8");
            // JSONObject resultJO = JSONObject.parseObject(text);
            // status = resultJO.getString("status");
            System.err.println(text);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public static void queryChinese() throws ClientProtocolException, IOException {
        QueryChineseCode q = new QueryChineseCode();

        q.setLimitStart(1);
        q.setLimitLength(50);
        q.setPackId(9527);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        String url = "http://127.0.0.1:9898/file/expload";
        RequestConfig requestConfig = getRequestConfig();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        String obj = JSONObject.toJSONString(q);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "queryCCode");
        jsonObject.put("condtion", obj);
        StringEntity stringEntity = new StringEntity(JSON.toJSONString(jsonObject, true), "UTF-8");
        httpPost.setEntity(stringEntity);

        CloseableHttpResponse response = httpClient.execute(httpPost);
        String status = "";
        try {
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                return;
            }
            HttpEntity responseEntity = response.getEntity();
            String text = EntityUtils.toString(responseEntity, "UTF-8");
            System.err.println(text);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    protected static RequestConfig getRequestConfig() {
        return RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(10000).build();
    }
}
