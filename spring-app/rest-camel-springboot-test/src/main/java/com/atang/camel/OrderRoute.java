package com.atang.camel;

import org.apache.camel.builder.RouteBuilder;

public class OrderRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        // see the application.properties file for setup of the rest configuration
//        restConfiguration()
//                .component("servlet")
//
//                //Enable swagger endpoint.
//                .apiContextPath("/swagger") //swagger endpoint path
//                .apiContextRouteId("swagger") //id of route providing the swagger endpoint
//
//                //Swagger properties
//                .contextPath("/api") //base.path swagger property; use the mapping path set for CamelServlet
//                .apiProperty("api.title", "Swagger UI with Apache Camel-servlet")
//                .apiProperty("api.version", "1.0")
//                .apiProperty("api.contact.name", "Maytas Somsmai")
//                .apiProperty("api.contact.email", "maytas.som@gmail.com")
//                .apiProperty("api.contact.url", "https://github.com/maytas-som/DemoApacheCamel")
//                .apiProperty("host", "") //by default 0.0.0.0
//                .apiProperty("port", "8080")
//                .apiProperty("schemes", "")
//        ;

        restConfiguration()
                .component("jetty")
                .port(8080)
                //Enable swagger endpoint.
                .apiContextPath("/swagger") //swagger endpoint path
                .apiContextRouteId("swagger") //id of route providing the swagger endpoint

                //Swagger properties
                .contextPath("/api") //base.path swagger property; use the mapping path set for CamelServlet
                .apiProperty("api.title", "Swagger UI with Apache Camel-Jetty")
                .apiProperty("api.version", "1.0")
                .apiProperty("api.contact.name", "ATang")
                .apiProperty("api.contact.email", "ATang@atang.com")
                .apiProperty("api.contact.url", "https://atang.com/")
                .apiProperty("host", "") //by default 0.0.0.0
                .apiProperty("port", "8080")
                .apiProperty("schemes", "")
                .apiProperty("cors", "true");

        // rest services under the orders context-path
        rest("/orders")
                // need to specify the POJO types the binding is using (otherwise json binding defaults to Map based)
                .get("{id}").outType(Order.class)
                .to("bean:orderService?method=getOrder(${header.id})")
                // need to specify the POJO types the binding is using (otherwise json binding defaults to Map based)
                .post().type(Order.class)
                .to("bean:orderService?method=createOrder")
                // need to specify the POJO types the binding is using (otherwise json binding defaults to Map based)
                .put().type(Order.class)
                .to("bean:orderService?method=updateOrder")
                .delete("{id}")
                .to("bean:orderService?method=cancelOrder(${header.id})");
    }
}
