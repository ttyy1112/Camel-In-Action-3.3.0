package com.atang.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class DynamicAddRouteTest {

    public static void main(String args[]) throws Exception {
        // create CamelContext
        CamelContext context = new DefaultCamelContext();

        // start the route
        context.start();

        // add our route to the CamelContext
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("seda:a").log("receive message body ${body}");

                from("seda:b").log("receive message body ${body}");

                from("seda:c").log("receive message body ${body}");
            }
        });

        Thread.sleep(10000);

        // stop the CamelContext
        context.stop();
    }

}
