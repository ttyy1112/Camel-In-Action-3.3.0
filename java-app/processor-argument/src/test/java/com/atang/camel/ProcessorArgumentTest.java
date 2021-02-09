package com.atang.camel;

import com.alibaba.fastjson.JSON;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.ExtendedCamelContext;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.RoutesDefinition;
import org.apache.camel.spi.Registry;
import org.apache.camel.support.SimpleRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;

public class ProcessorArgumentTest extends CamelTestSupport {

    @Override
    protected Registry createCamelRegistry() throws Exception {
        final SimpleRegistry registry = new SimpleRegistry();
        registry.bind("processorService", new ProcessorService());
        return registry;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                errorHandler(defaultErrorHandler()
                        // enable async redelivery mode (pay attention to thread names in console output)
                        .asyncDelayedRedelivery()
                        .maximumRedeliveries(2)
                        .redeliveryDelay(1000)
                        .retryAttemptedLogLevel(LoggingLevel.WARN));

                // 参数定义
                final HashMap<String, Object> params = new HashMap<>();
                params.put("A", "A");
                params.put("B", "B");
                String paramsJson = JSON.toJSONString(params);
                final String encodedParams = new String(Base64.getEncoder().encode(paramsJson.getBytes()));


                from("direct:start").id("sample-route")
                        .errorHandler(deadLetterChannel("log:DLC")
                                .maximumRedeliveries(5).retryAttemptedLogLevel(LoggingLevel.INFO)
                                .redeliveryDelay(250).backOffMultiplier(2))
                        .log("User ${header.name} is calling us")
                        // 传递参数给bean
                        .bean("processorService", "process(${body}, " + encodedParams + ")")
                        // 传递参数给新创建的对象
                        .process(new Wrapper(params))
                        .filter(simple("${header.name} == 'Kaboom'"))
                        .end()
                        .to("mock:done");
            }
        };
    }

    @Test
    public void testNoError() throws Exception {
        getMockEndpoint("mock:done").expectedMessageCount(1);

        template.sendBodyAndHeader("direct:start", "Hello Camel", "name", "Camel");

        List<RouteDefinition> routes = context.getRouteDefinitions();
        RoutesDefinition def = new RoutesDefinition();
        def.setRoutes(routes);

        final String xml = context.adapt(ExtendedCamelContext.class).getModelToXMLDumper().dumpModelAsXml(context, def);
        System.out.println(xml);

        assertMockEndpointsSatisfied();
    }

    @Test
    public void testNewException() throws Exception {
        getMockEndpoint("mock:done").expectedMessageCount(0);

        try {
            template.sendBodyAndHeader("direct:start", "Hello Bomb", "name", "Kaboom");
            fail("Should have thrown exception");
        } catch (CamelExecutionException e) {
            // we expect a NullPointerException because that is what NotAllowedProcessor throws
            // while handling the first AuthorizationException which is thrown from the filter in the route
            assertIsInstanceOf(NullPointerException.class, e.getCause());
        }

        assertMockEndpointsSatisfied();
    }
}
