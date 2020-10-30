package com.atang.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class QuartzFtpScanner {

    public static void main(String args[]) throws Exception {
        // create CamelContext
        CamelContext context = new DefaultCamelContext();

        // add our route to the CamelContext
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("ftp://ftptest99@11.80.9.214:21/test1?" +
                        "passiveMode=true&" +
                        "password=ftptest99&" +
                        "scheduler=quartz&" +
                        "scheduler.cron=*/5+*+*+*+*+?&" +
                        "startScheduler=true")
                        .log("receive message body ${body}");

                from("ftp://ftptest99@11.80.9.214:21/test2?" +
                        "passiveMode=true&" +
                        "password=ftptest99&" +
                        "scheduler=quartz&" +
                        "scheduler.cron=*/5+*+*+*+*+?&" +
                        "startScheduler=true")
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
