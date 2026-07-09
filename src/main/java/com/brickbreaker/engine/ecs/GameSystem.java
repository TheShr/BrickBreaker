package com.brickbreaker.engine.ecs;

import java.awt.Graphics2D;

public interface GameSystem {
    void update(Registry registry);
    void render(Registry registry, Graphics2D g);
}
