package com.atang.camel;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.net.ConnectException;

public class OnExceptionTest extends CamelTestSupport {

    @Override
    public boolean isUseRouteBuilder() {
        // each unit test include their own route builder
        return false;
    }

    /**
     * This test shows that a direct match will of course trigger the onException
     */
    @Test
    public void testOnExceptionDirectMatch() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                context.setTracing(true);

                onException(Exception.class).onWhen(exchange -> true).maximumRedeliveries(6).onWhen(exchange -> true).log("hi").end();
                onException(OrderFailedException.class).maximumRedeliveries(3);

                from("direct:order")
                        .onException(Exception.class).maximumRedeliveries(10).end()
                        .bean(OrderServiceBean.class, "handleOrder");
            }
        });

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                context.setTracing(true);

                onException(Exception.class).maximumRedeliveries(6);
                onException(OrderFailedException.class).maximumRedeliveries(3);


                from("direct:order1")
                        .onException(OrderFailedException.class).process(exchange -> System.out.println("error")).end()
                        .bean(OrderServiceBean.class, "handleOrder")
                        .bean(OrderServiceBean.class, "handleOrder1")
                        .onException(OrderFailedException.class).process(exchange -> System.out.println("error-1")).end()
                        .process(exchange -> {
                            System.out.println("not here!");
                        });
            }
        });
        context.start();

        try {
//            template.requestBody("direct:order", "ActiveMQ in Action");
            template.requestBody("direct:order1", "ActiveMQ in Action");
            fail("Should throw an exception");

            Thread.sleep(10000l);
        } catch (CamelExecutionException e) {
            assertIsInstanceOf(OrderFailedException.class, e.getCause());
        }
    }


    /**
     * This test shows that a wrapped connection exception in OrderFailedException will still
     * be triggered by the onException.
     */
    @Test
    public void testOnExceptionWrappedMatch() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                context.setTracing(true);

                onException(OrderFailedException.class).maximumRedeliveries(3);

                from("direct:order")
                        .bean(OrderServiceBean.class, "handleOrder")
                        .bean(OrderServiceBean.class, "saveToDB");
            }
        });
        context.start();

        try {
            template.requestBody("direct:order", "Camel in Action");
            fail("Should throw an exception");
        } catch (CamelExecutionException e) {
            assertIsInstanceOf(OrderFailedException.class, e.getCause());
            assertIsInstanceOf(ConnectException.class, e.getCause().getCause());
        }
    }

}
