package com.brickbreaker.game.event;

import com.brickbreaker.engine.event.GameEvent;

public record BrickBrokenEvent(int brickEntity) implements GameEvent {}
