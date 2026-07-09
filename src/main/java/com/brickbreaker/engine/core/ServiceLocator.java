package com.brickbreaker.engine.core;

import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServiceLocator {
    private static final Logger logger = LogManager.getLogger(ServiceLocator.class);
    private static final Map<Class<?>, Object> services = new HashMap<>();

    public static synchronized <T> void register(Class<T> serviceClass, T serviceInstance) {
        services.put(serviceClass, serviceInstance);
        logger.info("Service registered: {}", serviceClass.getName());
    }

    @SuppressWarnings("unchecked")
    public static synchronized <T> T get(Class<T> serviceClass) {
        Object service = services.get(serviceClass);
        if (service == null) {
            logger.error("Requested service not found: {}", serviceClass.getName());
            throw new IllegalStateException("Service not registered: " + serviceClass.getName());
        }
        return (T) service;
    }

    public static synchronized void clear() {
        services.clear();
        logger.info("Service locator registry cleared.");
    }
}
