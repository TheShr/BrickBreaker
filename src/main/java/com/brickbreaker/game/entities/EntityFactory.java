package com.brickbreaker.game.entities;

import java.awt.Color;
import com.brickbreaker.engine.ecs.Registry;
import com.brickbreaker.game.components.TransformComponent;
import com.brickbreaker.game.components.PhysicsComponent;
import com.brickbreaker.game.components.RenderComponent;
import com.brickbreaker.game.components.BrickComponent;
import com.brickbreaker.game.components.BallComponent;
import com.brickbreaker.game.components.PaddleComponent;

public class EntityFactory {
    public static int createBall(Registry registry, int x, int y, int vx, int vy, int size, Color color) {
        int ball = registry.createEntity();
        registry.addComponent(ball, new TransformComponent(x, y, size, size));
        registry.addComponent(ball, new PhysicsComponent(vx, vy));
        registry.addComponent(ball, new RenderComponent(color, RenderComponent.Shape.OVAL));
        registry.addComponent(ball, new BallComponent());
        return ball;
    }

    public static int createPaddle(Registry registry, int x, int y, int width, int height, Color color) {
        int paddle = registry.createEntity();
        registry.addComponent(paddle, new TransformComponent(x, y, width, height));
        registry.addComponent(paddle, new RenderComponent(color, RenderComponent.Shape.RECTANGLE));
        registry.addComponent(paddle, new PaddleComponent());
        return paddle;
    }

    public static int createBrick(Registry registry, int x, int y, int width, int height, int row, int col, Color color) {
        int brick = registry.createEntity();
        registry.addComponent(brick, new TransformComponent(x, y, width, height));
        registry.addComponent(brick, new RenderComponent(color, RenderComponent.Shape.RECTANGLE));
        registry.addComponent(brick, new BrickComponent(row, col, 1));
        return brick;
    }
}
