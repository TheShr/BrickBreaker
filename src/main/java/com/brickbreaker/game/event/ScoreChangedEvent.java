package com.brickbreaker.game.event;

import com.brickbreaker.engine.event.GameEvent;

public record ScoreChangedEvent(int newScore) implements GameEvent {}
