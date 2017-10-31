package com.mrpg.service;

import java.sql.SQLException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.language.SimpleExpression;
import org.apache.camel.model.rest.RestBindingMode;

/**
 * Java DSL camel routes
 *
 */
public class MyRouteBuilder extends RouteBuilder {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger("MyRouteBuilder");

    @Override
    public void configure() throws SQLException {

        try {

            restConfiguration().component("servlet")
            .enableCORS(true)
              .corsHeaderProperty("Access-Control-Allow-Headers", "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers,CustomHeader1, CustomHeader2")
            .bindingMode(RestBindingMode.json)
            .dataFormatProperty("prettyPrint", "true");
            
//            response.setHeader("Access-Control-Allow-Headers", "accept, content-type");
//            response.setHeader("Access-Control-Allow-Methods", "GET,PUT,POST,HEAD,OPTIONS");
//            response.setHeader("Access-Control-Allow-Origin", "*");
//            response.setHeader("Content-Type", "application/json; charset=utf-8");
            
            from("servlet:annotations")
                    .setHeader("Access-Control-Allow-Headers", new SimpleExpression("accept, content-type"))
                    .setHeader("Access-Control-Allow-Methods", new SimpleExpression("GET,PUT,POST,HEAD,OPTIONS"))
                    .setHeader("Access-Control-Allow-Origin", new SimpleExpression("*"))
                    .to("bean:echoBean?method=echo");
                    
                    //.setHeader("Content-Type", new SimpleExpression("application/json; charset=utf-8"));

            from("servlet:query")
                    .setHeader("Access-Control-Allow-Headers", new SimpleExpression("accept, content-type"))
                    .setHeader("Access-Control-Allow-Methods", new SimpleExpression("GET,PUT,POST,HEAD,OPTIONS"))
                    .setHeader("Access-Control-Allow-Origin", new SimpleExpression("*"))
                    .to("bean:echoBean?method=query");
                    //.setHeader("Content-Type", new SimpleExpression("application/json; charset=utf-8"));
            
            from("servlet:search")
                    .setHeader("Access-Control-Allow-Headers", new SimpleExpression("accept, content-type"))
                    .setHeader("Access-Control-Allow-Methods", new SimpleExpression("GET,PUT,POST,HEAD,OPTIONS"))
                    .setHeader("Access-Control-Allow-Origin", new SimpleExpression("*"))
                    .to("bean:echoBean?method=search");
                    //.setHeader("Content-Type", new SimpleExpression("application/json; charset=utf-8"));

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }

    }
}
