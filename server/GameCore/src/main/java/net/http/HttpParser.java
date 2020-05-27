package net.http;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


public class HttpParser {
    public static HashMap<String, String> parse(FullHttpRequest request) throws IOException {
        HashMap<String, String> parmMap = new HashMap<>();
        HttpMethod method = request.getMethod();

        QueryStringDecoder uriDecoder = new QueryStringDecoder(request.getUri());
        uriDecoder.parameters().entrySet().forEach(entry -> {
            // entry.getValue()是一个List, 只取第一个元素
            parmMap.put(entry.getKey(), entry.getValue().get(0));
        });

        if (HttpMethod.POST == method) {
            // 是POST请求
            HttpPostRequestDecoder contentDecoder = new HttpPostRequestDecoder(request);
            contentDecoder.offer(request);

            List<InterfaceHttpData> parmList = contentDecoder.getBodyHttpDatas();
            for (InterfaceHttpData parm : parmList) {
                Attribute data = (Attribute) parm;
                parmMap.put(data.getName(), data.getValue());
            }
        }
        // 释放ByteBuff
        request.content().release();
        return parmMap;
    }

}
