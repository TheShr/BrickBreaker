package com.brickbreaker.engine.core;

import java.awt.Graphics2D;

public class ScreenManager {
    private Screen currentScreen;

    public synchronized void setScreen(Screen screen) {
        this.currentScreen = screen;
    }

    public synchronized Screen getScreen() {
        return currentScreen;
    }

    public synchronized void update() {
        if (currentScreen != null) {
            currentScreen.update();
        }
    }

    public synchronized void render(Graphics2D g) {
        if (currentScreen != null) {
            currentScreen.render(g);
        }
    }
}
