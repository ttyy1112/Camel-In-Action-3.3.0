package com.atang.camel;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class PullEnrichDemo {

    public static void main(String args[]) throws Exception {
        // create CamelContext
        CamelContext context = new DefaultCamelContext();

        // add our route to the CamelContext
        context.addRoutes(new RouteBuilder() {
            class OrderToCsvProcessor implements org.apache.camel.Processor {
                @Override
                public void process(Exchange exchange) throws Exception {

                }
            }

            @Override
            public void configure() {
                from("file:data/inbox?noop=true").to("file:data/outbox");

                from("quartz2://report?cron=0+0+6+*+*+?")
                        .to("http://riders.com/orders/cmd=received&date=yesterday")
                        .process(new OrderToCsvProcessor())
                        .pollEnrich("ftp://riders.com/orders/?username=rider&password=secret",
                                new AggregationStrategy() {
                                    @Override
                                    public Exchange aggregate(Exchange oldExchange,
                                                              Exchange newExchange) {
                                        if (newExchange == null) {
                                            return oldExchange;
                                        }
                                        String http = oldExchange.getIn().getBody(String.class);
                                        String ftp = newExchange.getIn().getBody(String.class);
                                        String body = http + "\n" + ftp;
                                        oldExchange.getIn().setBody(body);
                                        return oldExchange;
                                    }
                                })
                        .to("file://riders/orders");
            }
        });

        // start the route and let it do its work
        context.start();
        Thread.sleep(10000);

        // stop the CamelContext
        context.stop();
    }

}
