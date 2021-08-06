package com.atang.camel.eip.choice;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.ProcessorDefinition;

/**
 * Notice this route is in Java code, but hawtio can visualize the route in the web browser
 * and you can add breakpoints and debug the route.
 */
public class MyRouteBuilder extends RouteBuilder {

    public void configure() {
        final ProcessorDefinition<?> end = from("timer:foo?period=5000&synchronous=true")
                .transform(simple("${random(1000)}"))
                .choice()
                .when(simple("${body} > 250"))
                .to("direct:ch-01")
                .otherwise()
                .to("direct:ch-02")
                .end();

        from("direct:ch-01").choice()
                .when(simple("${body} > 500"))
                .to("direct:ch-03")
                .otherwise()
                .to("direct:ch-04")
                .end();

        from("direct:ch-03").choice()
                .when(simple("${body} > 750"))
                .to("direct:ch-05")
                .otherwise()
                .to("direct:ch-06")
                .end();

        from("direct:ch-02").log("High number 0<${body}<=250");
        from("direct:ch-04").log("High number 250<${body}<=500");
        from("direct:ch-05").log("High number 750<${body}<=1000");
        from("direct:ch-06").log("High number 500<${body}<=750");
        System.out.println(end);
    }

}
