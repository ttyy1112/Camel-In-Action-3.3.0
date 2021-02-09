package com.atang.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Wrapper implements Processor {

    private Map<String, Object> params;

    public Wrapper(Map<String, Object> params) {
        this.params = params;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        final Iterator<Map.Entry<String, Object>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<String, Object> next = iterator.next();
            System.out.println(next.getKey() + ":" + next.getValue());
        }
    }
}
