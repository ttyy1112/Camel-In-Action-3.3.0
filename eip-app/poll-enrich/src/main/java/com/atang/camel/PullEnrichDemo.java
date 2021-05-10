package com.atang.camel;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.quartz.QuartzMessage;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultMessage;

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
                from("quartz://report?cron=0/5+*+*+*+*+?")
                        .process(exchange -> {
                            exchange.getIn().setBody("1111");

//                            final DefaultMessage out = new DefaultMessage(exchange);
//                            out.setBody("2222");
//                            exchange.setOut(out);
                        }).process(exchange -> {
                            final Message in = exchange.getIn();
                            final Message message = exchange.getMessage();
                            System.out.println(in.getBody());
                            System.out.println(message.getBody());
                        }
                );

//                from("quartz://report?cron=0/5+*+*+*+*+?")
//                        .process(new Processor() {
//                            @Override
//                            public void process(Exchange exchange) throws Exception {
//                                log.info("come");
//                                System.out.println("=======" + exchange.getIn(QuartzMessage.class).getJobExecutionContext().getScheduledFireTime());
//                            }
//
//                        });

//                from("file:data/inbox?noop=true").to("file:data/outbox");
//
//                from("quartz2://report?cron=0+0+6+*+*+?")
//                        .to("http://riders.com/orders/cmd=received&date=yesterday")
//                        .process(new OrderToCsvProcessor())
//                        .pollEnrich("ftp://riders.com/orders/?username=rider&password=secret",
//                                new AggregationStrategy() {
//                                    @Override
//                                    public Exchange aggregate(Exchange oldExchange,
//                                                              Exchange newExchange) {
//                                        if (newExchange == null) {
//                                            return oldExchange;
//                                        }
//                                        String http = oldExchange.getIn().getBody(String.class);
//                                        String ftp = newExchange.getIn().getBody(String.class);
//                                        String body = http + "\n" + ftp;
//                                        oldExchange.getIn().setBody(body);
//                                        return oldExchange;
//                                    }
//                                })
//                        .to("file://riders/orders");
            }
        });

        // start the route and let it do its work
        context.start();
        Thread.sleep(100000);

        // stop the CamelContext
        context.stop();
    }

}
