package com.atang.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * 测试java-dsl路由设置中是否允许设置多个from
 */
public class SeparateEndpointRouteTest extends CamelTestSupport {

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
         * 允许设置多个from
         */
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("seda:a").log("receive message body ${body}");

                from("seda:b").log("receive message body ${body}");

                from("seda:c").log("receive message body ${body}");
            }
        };
    }

}
