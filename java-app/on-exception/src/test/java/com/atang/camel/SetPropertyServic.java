package com.atang.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class SetPropertyServic implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        System.out.println(exchange);
    }
}
