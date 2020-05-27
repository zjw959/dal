package com.utils;


import com.alibaba.fastjson.JSONObject;
import com.enity.ServerInfo;

public class HttpToolTest {
	public static void main(String[] args) throws Throwable {
//        //发送 GET 请求
//        String s=HttpTool.sendGet("http://localhost:8080/server/login", "accountId=1s121345611&channelId=ht");
//        System.out.println(s);

//        //发送 POST 请求
//        String sr=HttpTool.sendPost("http://localhost:8080/server/login", "accountId=1s121345611&channelId=ht");
//        System.out.println(sr);
        
        String url = "http://localhost:8080/server/add";
        JSONObject jo = new JSONObject();
        
//        jo.put("playerId", 12345);
//        jo.put("type", 1);
//        HttpBaseRequest.getDefault().requestRemoteWithPost(url, jo.toString());
        
        ServerInfo info = new ServerInfo();
        info.setName("dkw");
        info.setId(90);
        jo.put("serverInfo", info);
        HttpBaseRequest.getDefault().requestRemoteWithPost(url, jo.toString());
	}
}
