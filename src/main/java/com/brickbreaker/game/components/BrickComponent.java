package com.brickbreaker.game.components;

import com.brickbreaker.engine.ecs.Component;

public class BrickComponent implements Component {
    public int row;
    public int col;
    public int value; // 1 = active, 0 = broken

    public BrickComponent(int row, int col, int value) {
        this.row = row;
        this.col = col;
        this.value = value;
    }
}
