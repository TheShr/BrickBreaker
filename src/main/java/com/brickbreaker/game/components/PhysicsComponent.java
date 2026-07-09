package com.brickbreaker.game.components;

import com.brickbreaker.engine.ecs.Component;

public class PhysicsComponent implements Component {
    public int vx;
    public int vy;

    public PhysicsComponent(int vx, int vy) {
        this.vx = vx;
        this.vy = vy;
    }
}
