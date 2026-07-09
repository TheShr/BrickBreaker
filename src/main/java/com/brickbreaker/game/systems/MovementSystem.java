package com.brickbreaker.game.systems;

import com.brickbreaker.engine.ecs.GameSystem;
import com.brickbreaker.engine.ecs.Registry;
import com.brickbreaker.game.components.TransformComponent;
import com.brickbreaker.game.components.PhysicsComponent;
import java.awt.Graphics2D;
import java.util.Set;

public class MovementSystem implements GameSystem {
    @Override
    public void update(Registry registry) {
        Set<Integer> entities = registry.getEntitiesWith(TransformComponent.class, PhysicsComponent.class);
        for (int entity : entities) {
            TransformComponent tc = registry.getComponent(entity, TransformComponent.class);
            PhysicsComponent pc = registry.getComponent(entity, PhysicsComponent.class);
            tc.x += pc.vx;
            tc.y += pc.vy;
        }
    }

    @Override
    public void render(Registry registry, Graphics2D g) {
        // No rendering
    }
}
