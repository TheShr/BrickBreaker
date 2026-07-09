package com.brickbreaker;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import com.brickbreaker.engine.resource.ConfigManager;
import com.brickbreaker.engine.core.ServiceLocator;
import com.brickbreaker.engine.core.GameEngine;
import com.brickbreaker.engine.core.ScreenManager;
import com.brickbreaker.engine.event.EventBus;
import com.brickbreaker.engine.input.InputManager;
import com.brickbreaker.engine.ecs.Registry;

public class MyApp {
    public static String playerName;

    public static void main(String[] args) {
        // Load configuration
        ConfigManager.loadConfig();
        var config = ConfigManager.getConfig().window();

        // Register core services
        ServiceLocator.register(EventBus.class, new EventBus());
        ServiceLocator.register(Registry.class, new Registry());
        
        InputManager inputManager = new InputManager();
        ServiceLocator.register(InputManager.class, inputManager);

        if (System.getProperty("takeScreenshot") != null) {
            playerName = "Autopilot";
        } else {
            playerName = JOptionPane.showInputDialog("Enter your name:");
            if (playerName == null || playerName.trim().isEmpty()) {
                playerName = "Guest";
            }
        }

        JFrame obj = new JFrame();
        obj.setTitle(config.title());
        obj.setResizable(config.resizable());
        obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize GameEngine
        GameEngine engine = new GameEngine(obj);
        engine.getCanvas().addKeyListener(inputManager);
        
        // Create Gameplay Screen
        GamePlay gameplay = new GamePlay(playerName, engine.getCanvas());
        
        // Register current screen
        ServiceLocator.get(ScreenManager.class).setScreen(gameplay);

        // Show window and start game loop thread
        obj.setVisible(true);
        engine.start();
    }
}
