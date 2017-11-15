package com.mrpg.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.naming.NamingException;
import org.apache.camel.component.servletlistener.CamelContextLifecycle;
import org.apache.camel.component.servletlistener.ServletCamelContext;
import org.apache.camel.impl.JndiRegistry;

/**
 * CamelContextLifecycle listener (see web.xml) Adds Beans to the JNDI tree .
 * Removes on shutdown
 *
 * Depreicated in favour of normal servlets
 *
 */
public class JNDIService implements CamelContextLifecycle<JndiRegistry> {

    /**
     * Logger entry.
     */
    private static final org.apache.log4j.Logger LOG
            = org.apache.log4j.Logger.getLogger("JNDIService");

    /**
     * Map of beans available to camel routes.
     */
    private static final Map<String, Class<?>> JNDI_MAP;

    static {
        JNDI_MAP = new HashMap<>();
    }

    /**
     * Before starting the camel JNDI service
     * @param camelContext Context
     * @param registry Provided registry
     * @throws Exception 
     */
    @Override
    public void beforeStart(ServletCamelContext camelContext, JndiRegistry registry) throws Exception {
        // register beans in the registry
        for (Entry<String, Class<?>> entry : JNDI_MAP.entrySet()) {
            String key = entry.getKey();
            Class value = entry.getValue();

            try {
                Object bean = registry.lookup(key);
                if (bean == null) {
                    LOG.debug("$$ Adding " + key + " to the JNDI tree");
                    registry.bind(key, value.newInstance());
                }

            } catch (IllegalAccessException | InstantiationException ex) {
                LOG.error("@@ Error adding bean to jndi tree\n"
                        + ex.getMessage(), ex);
            }
        }

    }

    /**
     * Before stopping the JNDI service
     * @param camelContext Context
     * @param registry Provided registry
     * @throws Exception
     */
    @Override
    public void beforeStop(ServletCamelContext camelContext, JndiRegistry registry) throws Exception {

        for (Entry<String, Class<?>> entry : JNDI_MAP.entrySet()) {
            String key = entry.getKey();

            try {
                registry.getContext().unbind(key);
            } catch (NamingException ex) {
                LOG.error("@@ Error removing bean from jndi tree\n"
                        + ex.getMessage(), ex);
            }
        }

    }

    @Override
    public void afterStop(ServletCamelContext camelContext, JndiRegistry registry) throws Exception {
        // noop
    }

    @Override
    public void beforeAddRoutes(ServletCamelContext camelContext, JndiRegistry registry) throws Exception {
        // noop
    }

    @Override
    public void afterAddRoutes(ServletCamelContext camelContext, JndiRegistry registry) throws Exception {
        // noop
    }

    @Override
    public void afterStart(ServletCamelContext camelContext, JndiRegistry registry) throws Exception {

    }

}
