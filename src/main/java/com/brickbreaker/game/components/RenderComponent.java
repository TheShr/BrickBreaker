package com.brickbreaker.game.components;

import com.brickbreaker.engine.ecs.Component;
import java.awt.Color;

public class RenderComponent implements Component {
    public Color color;
    public enum Shape { RECTANGLE, OVAL }
    public Shape shape;

    public RenderComponent(Color color, Shape shape) {
        this.color = color;
        this.shape = shape;
    }
}
