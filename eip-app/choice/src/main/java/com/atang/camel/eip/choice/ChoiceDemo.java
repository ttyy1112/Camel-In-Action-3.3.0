package com.atang.camel.eip.choice;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

public class ChoiceDemo {

    public static void main(String args[]) throws Exception {
        CamelContext context = new DefaultCamelContext();

        // add our route to the CamelContext
        context.addRoutes(new MyRouteBuilder());

        // start the route and let it do its work
        context.start();
        Thread.sleep(100000);

        // stop the CamelContext
        context.stop();
    }


}
