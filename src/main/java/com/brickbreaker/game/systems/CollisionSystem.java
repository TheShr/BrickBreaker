package com.brickbreaker.game.systems;

import com.brickbreaker.engine.ecs.GameSystem;
import com.brickbreaker.engine.ecs.Registry;
import com.brickbreaker.engine.core.ServiceLocator;
import com.brickbreaker.engine.event.EventBus;
import com.brickbreaker.engine.resource.ConfigManager;
import com.brickbreaker.game.components.TransformComponent;
import com.brickbreaker.game.components.PhysicsComponent;
import com.brickbreaker.game.components.BallComponent;
import com.brickbreaker.game.components.PaddleComponent;
import com.brickbreaker.game.components.BrickComponent;
import com.brickbreaker.game.event.BrickBrokenEvent;

import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.util.Set;

public class CollisionSystem implements GameSystem {
    @Override
    public void update(Registry registry) {
        var config = ConfigManager.getConfig();
        var windowCfg = config.window();
        var ballCfg = config.ball();
        var paddleCfg = config.paddle();
        var eventBus = ServiceLocator.get(EventBus.class);

        Set<Integer> balls = registry.getEntitiesWith(BallComponent.class, TransformComponent.class, PhysicsComponent.class);
        Set<Integer> paddles = registry.getEntitiesWith(PaddleComponent.class, TransformComponent.class);
        Set<Integer> bricks = registry.getEntitiesWith(BrickComponent.class, TransformComponent.class);

        for (int ball : balls) {
            TransformComponent ballTrans = registry.getComponent(ball, TransformComponent.class);
            PhysicsComponent ballPhys = registry.getComponent(ball, PhysicsComponent.class);

            // 1. Boundary check: Walls (left & right)
            if (ballTrans.x < 0) {
                ballTrans.x = 0;
                ballPhys.vx = -ballPhys.vx;
            } else if (ballTrans.x > windowCfg.width() - ballTrans.width - 20) {
                ballTrans.x = windowCfg.width() - ballTrans.width - 20;
                ballPhys.vx = -ballPhys.vx;
            }

            // 2. Boundary check: Ceiling (top)
            if (ballTrans.y < 0) {
                ballTrans.y = 0;
                ballPhys.vy = -ballPhys.vy;
            }

            // 3. Collision with Paddle
            Rectangle ballRect = new Rectangle(ballTrans.x, ballTrans.y, ballTrans.width, ballTrans.height);
            for (int paddle : paddles) {
                TransformComponent padTrans = registry.getComponent(paddle, TransformComponent.class);
                Rectangle padRect = new Rectangle(padTrans.x, padTrans.y, padTrans.width, padTrans.height);
                if (ballRect.intersects(padRect)) {
                    ballPhys.vy = -Math.abs(ballPhys.vy); // Always bounce up
                    ballTrans.y = padTrans.y - ballTrans.height; // Avoid sticking
                }
            }

            // 4. Collision with Bricks
            for (int brick : bricks) {
                BrickComponent bc = registry.getComponent(brick, BrickComponent.class);
                if (bc == null || bc.value == 0) continue; // Already broken

                TransformComponent brickTrans = registry.getComponent(brick, TransformComponent.class);
                Rectangle brickRect = new Rectangle(brickTrans.x, brickTrans.y, brickTrans.width, brickTrans.height);

                if (ballRect.intersects(brickRect)) {
                    bc.value = 0; // mark broken
                    registry.destroyEntity(brick); // Remove entity from registry
                    eventBus.publish(new BrickBrokenEvent(brick));

                    // Reflection physics
                    if (ballTrans.x + (ballTrans.width - 1) <= brickTrans.x || ballTrans.x + 1 >= brickTrans.x + brickTrans.width) {
                        ballPhys.vx = -ballPhys.vx;
                    } else {
                        ballPhys.vy = -ballPhys.vy;
                    }
                    break; // Bounce off only one brick per update
                }
            }
        }
    }

    @Override
    public void render(Registry registry, Graphics2D g) {
        // No rendering
    }
}
