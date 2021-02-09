package com.atang.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class FtpScanner {

    public static void main(String args[]) throws Exception {
        // create CamelContext
        CamelContext context = new DefaultCamelContext();

        // add our route to the CamelContext
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // FTP消费者限制：同一用户的同一目录只能有一个消费者

                // 启用默认ScheduledPollConsumerScheduler
                from("ftp://ftptest99@11.80.9.214:21/test1?" +
                        "passiveMode=true&" +
                        "password=ftptest99&" +
                        "initialDelay=1000&" +
                        "delay=2000&" +
                        "startScheduler=true")
                        .log("receive message body ${body}");

                final String path = "/Users/tanghonggang1/IdeaProjects/Camel-In-Action-3.3.0/java-app/ftp-component/src/main/resources/";
                // 启用默认ScheduledPollConsumerScheduler
                from("ftp://ftptest99@11.80.9.214:21/test2?" +
                        "passiveMode=true&" +
                        "password=ftptest99&" +
                        "initialDelay=1000&" +
                        "delay=2000&" +
                        "startScheduler=true&" +
                        "localWorkDirectory=" + path)
                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                final Object body = exchange.getIn().getBody();
                                System.out.println(body);
                            }
                        })
                        .log("receive message body ${body}");
            }
        });

        // start the route and let it do its work
        context.start();

        Thread.sleep(10000);

        // stop the CamelContext
        context.stop();
    }



}
