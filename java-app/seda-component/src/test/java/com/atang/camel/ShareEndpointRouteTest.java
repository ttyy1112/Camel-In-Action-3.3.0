package com.atang.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class ShareEndpointRouteTest extends CamelTestSupport {

    @Test
    public void testMultiFrom() throws InterruptedException {
        template.sendBody("seda:a", "a");
        template.sendBody("seda:b", "b");
        template.sendBody("seda:c", "c");
        template.sendBody("seda:c", "cccc");
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        /**
         * 两个路由共用一个endpoint
         */
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("seda:a").log("receive message body ${body}");
                from("seda:a").log("receive message body ${body}");
                from("seda:b").log("receive message body ${body}");
                from("seda:c").log("receive message body ${body}");
            }
        };
    }

}
