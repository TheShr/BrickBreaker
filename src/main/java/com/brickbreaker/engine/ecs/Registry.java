package com.brickbreaker.engine.ecs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Registry {
    private static final Logger logger = LogManager.getLogger(Registry.class);
    
    private int nextEntityId = 1;
    private final Set<Integer> entities = new HashSet<>();
    private final Map<Class<? extends Component>, Map<Integer, Component>> componentStores = new HashMap<>();

    public synchronized int createEntity() {
        int entity = nextEntityId++;
        entities.add(entity);
        logger.debug("Created Entity with ID: {}", entity);
        return entity;
    }

    public synchronized void destroyEntity(int entity) {
        if (!entities.contains(entity)) return;
        
        // Remove all components associated with this entity
        for (Map<Integer, Component> store : componentStores.values()) {
            store.remove(entity);
        }
        entities.remove(entity);
        logger.debug("Destroyed Entity with ID: {}", entity);
    }

    public synchronized <T extends Component> void addComponent(int entity, T component) {
        if (!entities.contains(entity)) {
            throw new IllegalArgumentException("Entity " + entity + " does not exist.");
        }
        componentStores.computeIfAbsent(component.getClass(), k -> new HashMap<>())
                       .put(entity, component);
    }

    @SuppressWarnings("unchecked")
    public synchronized <T extends Component> T getComponent(int entity, Class<T> componentClass) {
        Map<Integer, Component> store = componentStores.get(componentClass);
        if (store == null) return null;
        return (T) store.get(entity);
    }

    public synchronized <T extends Component> boolean hasComponent(int entity, Class<T> componentClass) {
        Map<Integer, Component> store = componentStores.get(componentClass);
        return store != null && store.containsKey(entity);
    }

    public synchronized <T extends Component> void removeComponent(int entity, Class<T> componentClass) {
        Map<Integer, Component> store = componentStores.get(componentClass);
        if (store != null) {
            store.remove(entity);
        }
    }

    // Get all entities that possess a specific component
    public synchronized Set<Integer> getEntitiesWith(Class<? extends Component> componentClass) {
        Map<Integer, Component> store = componentStores.get(componentClass);
        if (store == null) return new HashSet<>();
        return new HashSet<>(store.keySet());
    }

    // Get all entities that possess a set of components (System queries)
    @SafeVarargs
    public final synchronized Set<Integer> getEntitiesWith(Class<? extends Component>... componentClasses) {
        if (componentClasses.length == 0) return new HashSet<>();
        Set<Integer> result = getEntitiesWith(componentClasses[0]);
        for (int i = 1; i < componentClasses.length; i++) {
            result.retainAll(getEntitiesWith(componentClasses[i]));
        }
        return result;
    }

    public synchronized void clear() {
        entities.clear();
        componentStores.clear();
        nextEntityId = 1;
        logger.info("ECS Registry cleared.");
    }
}
