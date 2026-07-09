package com.brickbreaker.engine.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EventBus {
    private static final Logger logger = LogManager.getLogger(EventBus.class);
    
    @FunctionalInterface
    public interface EventListener<T extends GameEvent> {
        void onEvent(T event);
    }

    private final Map<Class<? extends GameEvent>, List<EventListener<?>>> listeners = new HashMap<>();

    public synchronized <T extends GameEvent> void subscribe(Class<T> eventType, EventListener<T> listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
        logger.debug("Subscribed listener for event type: {}", eventType.getSimpleName());
    }

    @SuppressWarnings("unchecked")
    public synchronized <T extends GameEvent> void publish(T event) {
        Class<? extends GameEvent> eventType = event.getClass();
        List<EventListener<?>> list = listeners.get(eventType);
        if (list != null) {
            for (EventListener<?> listener : list) {
                try {
                    ((EventListener<T>) listener).onEvent(event);
                } catch (Exception e) {
                    logger.error("Error dispatching event " + eventType.getSimpleName() + " to listener", e);
                }
            }
        }
    }
}
