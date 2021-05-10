package com.atang.camel.eip.jdbc;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.dataformat.CsvDataFormat;
import org.apache.camel.support.DefaultRegistry;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcDemo {
    private static Logger logger = LoggerFactory.getLogger(JdbcDemo.class);

    public static void main(String args[]) throws Exception {
        // create CamelContext
        final String url = "jdbc:mysql://gate.local.jed.jddb.com:3306/jd_eis?useUnicode=true&characterEncoding=UTF-8";

        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        basicDataSource.setUsername("jd_eis_admin");
        basicDataSource.setPassword("CSzwF2aDR9K1_LBW");
        basicDataSource.setUrl(url);
        //basicDataSource.setMaxActive(50); //最大活动数
        basicDataSource.setMaxIdle(20); //最大空闲数
        basicDataSource.setMinIdle(5); //最小空闲数
        basicDataSource.setInitialSize(10); //初始化个数

        DefaultRegistry simpleregistry = new DefaultRegistry();
        CamelContext context = new DefaultCamelContext(simpleregistry);

        context.getRegistry().bind("MySQLDataSource", basicDataSource);

        CsvDataFormat csv = new CsvDataFormat();
        csv.setQuoteMode("ALL");

        // add our route to the CamelContext
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("timer://queryUsers?period=60s")
                        .setBody(constant("select * from user where id > 300 "))
                        .to("jdbc:MySQLDataSource?outputType=SelectList")
                        .process(exchange -> {
                            logger.info("<------exchange-----> : exchange.toString()");
                            String str = exchange.getIn().getBody().toString();
                            logger.info("str : {}", str);
                            Object obj = exchange.getIn().getBody();
                            logger.info("obj : {}", obj.getClass());
                            logger.info("obj : {}", obj);
                        }).marshal(csv).to("file:///tmp?fileName=MY_TEST_FILE.csv");
                //outputType:CamelJdbcRowCount
                //If the query is a SELECT, query the row count is returned in this OUT header.
            }
        });

        // start the route and let it do its work
        context.start();
        Thread.sleep(100000);

        // stop the CamelContext
        context.stop();
    }


}
