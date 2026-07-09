package com.brickbreaker.game.components;

import com.brickbreaker.engine.ecs.Component;

public class TransformComponent implements Component {
    public int x;
    public int y;
    public int width;
    public int height;

    public TransformComponent(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
