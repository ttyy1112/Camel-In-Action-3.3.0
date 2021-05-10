package com.atang.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class JettyRestService {

    public static void main(String args[]) throws Exception {
        // create CamelContext
        CamelContext context = new DefaultCamelContext();

        // add our route to the CamelContext
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // configure rest-dsl
                restConfiguration()
                        // to use jetty component and run on port 8080
                        .component("jetty").port(8080)
                        // use a smaller thread pool in jetty as we do not have so high demand yet
                        .componentProperty("minThreads", "1")
                        .componentProperty("maxThreads", "8")
                        .apiContextPath("/api-doc")
                        .apiProperty("api.path", "/a")
                ;
                // to setup jetty to use the security handler
//                        .endpointProperty("handlers", "#securityHandler");

                // rest services under the orders context-path
                rest("/orders").get()
                        .to("direct:CHANNEL-0001");

                from("direct:CHANNEL-0001").process(exchange -> {
                    final Message message = exchange.getMessage();
                    message.setBody("<xml><body>Here?</body></xml>");
                    exchange.setMessage(message);
                });
            }
        });

        // start the route and let it do its work
        context.start();

        Thread.sleep(1000000);

        // stop the CamelContext
        context.stop();
    }
}
