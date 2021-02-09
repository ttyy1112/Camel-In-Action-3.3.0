package com.atang.camel;

import com.alibaba.fastjson.JSON;

import java.util.Base64;
import java.util.Iterator;
import java.util.Map;

public class ProcessorService {
    public void process(String body, String encodedParams) {
        // 打印表体
        System.out.println(body);

        // 参数解码并打印输出
        final String decodedParms = new String(Base64.getDecoder().decode(encodedParams.getBytes()));
        final Map<String, String> params = (Map<String, String>) JSON.parse(decodedParms);
        final Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<String, String> next = iterator.next();
            System.out.println(next.getKey() + ":" + next.getValue());
        }
    }
}
