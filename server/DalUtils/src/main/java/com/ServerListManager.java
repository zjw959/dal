package com;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

public class ServerListManager {
    private static final Logger LOGGER = Logger.getLogger(ServerListManager.class);
    private static String server_init = "/server/server_init";
    private static String user_center_url;
    public static ServerInfo myServerInfo = null;
    /** 本服所在分组下游戏服数量 */
    public static int gameServerCount;

    /**
     * 初始化
     * 
     * @param url
     * @return
     */
    public static boolean initServerListManager(String url, String internalIp, int tcpPort) {
        user_center_url = url;
        try {
            return requestRemote(internalIp, tcpPort);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return false;
    }

    /**
     * 获取本服务器信息
     * 
     */
    public static ServerInfo getMyServerInfo() {
        return myServerInfo;
    }

    private static boolean requestRemote(String internalIp, int tcpPort) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String url =
                user_center_url + server_init + "?internalIp=" + internalIp + "&tcpPort=" + tcpPort;
        RequestConfig requestConfig = getRequestConfig();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        try {
            LOGGER.info("req server init----response.StatusCode="
                    + response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                return false;
            }
            HttpEntity responseEntity = response.getEntity();
            String text = EntityUtils.toString(responseEntity, "UTF-8");
            JSONObject resultJO = JSONObject.parseObject(text);
            String status = resultJO.getString("status");
            LOGGER.info("req server init----status=" + status);
            if (!status.equals("0")) {
                return false;
            }
            JSONObject serverData = resultJO.getJSONObject("data");
            myServerInfo = serverData.toJavaObject(ServerInfo.class);
            gameServerCount = resultJO.getIntValue("gameServerCount");
            LOGGER.info("-----myServerInfo=" + myServerInfo);
            LOGGER.info("-----gameServerCount=" + gameServerCount);
            // String allServer = serverData.getString("all_server");
            // allServerList = JSON.parseArray(allServer, ServerInfo.class);
            // String usingServer = serverData.getString("using_server");
            // usingServerList = JSON.parseArray(usingServer, ServerInfo.class);
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return true;
    }


    private static RequestConfig getRequestConfig() {
        return RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(10000).build();
    }
}
