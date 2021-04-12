package com.atang.camel;

import org.apache.camel.CamelContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ContextInit implements ApplicationContextAware {
    @Autowired
    private CamelContext camelContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        try {
            camelContext.addRoutes(new OrderRoute());
            camelContext.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
