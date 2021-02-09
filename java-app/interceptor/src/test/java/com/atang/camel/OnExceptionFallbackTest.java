package com.atang.camel;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.InterceptStrategy;
import org.apache.camel.support.processor.DelegateAsyncProcessor;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.junit4.TestSupport;
import org.junit.Assert;
import org.junit.Test;

import java.net.ConnectException;

public class OnExceptionFallbackTest extends CamelTestSupport {

    @Override
    public boolean isUseRouteBuilder() {
        // each unit test include their own route builder
        return false;
    }

    /**
     * This tests shows no match for onException and therefore fallback on the error handler
     * and by which there are no explicit configured. Therefore default error handler will be
     * used which by default does NO redelivery attempts.
     */
    @Test
    public void testOnExceptionFallbackToErrorHandler() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                context.setTracing(true);

                interceptFrom().process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        return;
                    }
                });

                onException(IllegalArgumentException.class).maximumRedeliveries(3);
                intercept().process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        return;
                    }
                });
                from("direct:order")
                        .bean(OrderServiceBean.class, "handleOrder")
                        .bean(OrderServiceBean.class, "saveToDB");
            }
        });


        context.adapt(ExtendedCamelContext.class).addInterceptStrategy(new InterceptStrategy() {
            @Override
            public Processor wrapProcessorInInterceptors(CamelContext context, NamedNode definition, Processor target, Processor nextTarget) throws Exception {
                return new DelegateAsyncProcessor() {
                    @Override
                    public boolean process(Exchange exchange, AsyncCallback callback) {
                        return super.process(exchange, new AsyncCallback() {
                            @Override
                            public void done(boolean doneSync) {
                                log.info("interceptor 1");
                            }
                        });
                    }
                };
            }
        });

        context.adapt(ExtendedCamelContext.class).addInterceptStrategy(new InterceptStrategy() {
            @Override
            public Processor wrapProcessorInInterceptors(CamelContext context, NamedNode definition, Processor target, Processor nextTarget) throws Exception {
                return new DelegateAsyncProcessor() {
                    @Override
                    public boolean process(Exchange exchange, AsyncCallback callback) {
                        return super.process(exchange, new AsyncCallback() {
                            @Override
                            public void done(boolean doneSync) {
                                log.info("interceptor 2");
                            }
                        });
                    }
                };
            }
        });

        context.adapt(ExtendedCamelContext.class).addInterceptStrategy(new InterceptStrategy() {
            @Override
            public Processor wrapProcessorInInterceptors(CamelContext context, NamedNode definition, Processor target, Processor nextTarget) throws Exception {
                return new DelegateAsyncProcessor() {
                    @Override
                    public boolean process(Exchange exchange, AsyncCallback callback) {
                        return super.process(exchange, new AsyncCallback() {
                            @Override
                            public void done(boolean doneSync) {
                                log.info("interceptor 3");
                            }
                        });
                    }
                };
            }
        });

        context.start();

        try {
            template.requestBody("direct:order", "Camel in Action");
            Assert.fail("Should throw an exception");
            Thread.sleep(100000);
        } catch (CamelExecutionException e) {
            TestSupport.assertIsInstanceOf(OrderFailedException.class, e.getCause());
            TestSupport.assertIsInstanceOf(ConnectException.class, e.getCause().getCause());
        }
    }

}
