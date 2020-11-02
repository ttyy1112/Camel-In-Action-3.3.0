package com.atang.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class TaskDemo {
    public static void main(String args[]) throws Exception {
        // create CamelContext
        CamelContext context = new DefaultCamelContext();

        // add our route to the CamelContext
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // for task example
                // =================================
                from("jmq:topic").process("bean:service?method=consumer(${body})").toD("direct:${body.channel_code}");

                // optional
                // ---------------------------------
                from("jmq:topic").process("bean:service?method=consumer(${body})").to("seda:task");

                from("seda:task").toD("direct:${body.channel_code}");
                // ---------------------------------

                from("direct:CHANNEL-001")
                        .bean("jssService", "download(${body.url})")
                        .process("bean:transform?method=transform(${body})")
                        .to("ftp:user@ip?passiveMode=true&password=xxx");

                from("direct:CHANNEL-002")
                        .bean("jssService", "download(${body.url})")
                        .process("bean:transform?method=transform(${body})")
                        .to("ftp:user@ip?passiveMode=true&password=xxx");
                // =================================
            }
        });

        // start the route and let it do its work
        context.start();

        Thread.sleep(10000);

        // stop the CamelContext
        context.stop();
    }


}
