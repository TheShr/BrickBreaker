package com.brickbreaker.game.event;

import com.brickbreaker.engine.event.GameEvent;

public record LevelChangedEvent(int newLevel) implements GameEvent {}
