package com.atang.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * module task related rule:
 * 1. set routeId for all route definitions
 * 2. consider task retry strategy for every protocol
 * 3. middleware adaptor
 * 4. create temporary route, add to camel context dynamically, consider haw and when delete them
 */
public class RouteLifecycle {

    public static void main(String args[]) throws Exception {
        // create CamelContext
        CamelContext context = new DefaultCamelContext();

        // add our route to the CamelContext
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("seda:a").log("receive message body ${body}").routeId("A");

                from("seda:b").log("receive message body ${body}").routeId("B");

                from("seda:c").log("receive message body ${body}").routeId("C");
            }
        });

        // start the route and let it do its work
        context.start();

        // suspend route
        context.getRouteController().suspendRoute("C");
        // resume route
        context.getRouteController().resumeRoute("C");
        // stop route
        context.getRouteController().stopRoute("C");

        // remove route from camel context
        context.removeRoute("C");

        Thread.sleep(10000);

        // stop the CamelContext
        context.stop();
    }

}
