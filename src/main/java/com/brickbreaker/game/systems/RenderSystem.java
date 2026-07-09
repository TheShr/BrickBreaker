package com.brickbreaker.game.systems;

import com.brickbreaker.engine.ecs.GameSystem;
import com.brickbreaker.engine.ecs.Registry;
import com.brickbreaker.game.components.TransformComponent;
import com.brickbreaker.game.components.RenderComponent;
import java.awt.Graphics2D;
import java.util.Set;

public class RenderSystem implements GameSystem {
    @Override
    public void update(Registry registry) {
        // Rendering logic does not modify state
    }

    @Override
    public void render(Registry registry, Graphics2D g) {
        Set<Integer> entities = registry.getEntitiesWith(TransformComponent.class, RenderComponent.class);
        for (int entity : entities) {
            TransformComponent tc = registry.getComponent(entity, TransformComponent.class);
            RenderComponent rc = registry.getComponent(entity, RenderComponent.class);
            g.setColor(rc.color);
            if (rc.shape == RenderComponent.Shape.OVAL) {
                g.fillOval(tc.x, tc.y, tc.width, tc.height);
            } else {
                g.fillRect(tc.x, tc.y, tc.width, tc.height);
            }
        }
    }
}
