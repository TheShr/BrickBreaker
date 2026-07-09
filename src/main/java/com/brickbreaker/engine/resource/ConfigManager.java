package com.brickbreaker.engine.resource;

import com.google.gson.Gson;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigManager {
    private static final Logger logger = LogManager.getLogger(ConfigManager.class);
    private static GameConfig config;

    public record WindowConfig(String title, int width, int height, boolean resizable) {}
    public record PaddleConfig(int startX, int startY, int width, int height, int speed, int minBound, int maxBound) {}
    public record BallConfig(int startX, int startY, int speedX, int speedY, int size, int deathY) {}
    public record BricksConfig(int rows, int cols, int totalCount, int offsetX, int offsetY, int widthBound, int heightBound) {}
    public record GameplayConfig(int initialDelay, int scorePerBrick, int levelThreshold, String scoresFile) {}

    public record GameConfig(
        WindowConfig window,
        PaddleConfig paddle,
        BallConfig ball,
        BricksConfig bricks,
        GameplayConfig gameplay
    ) {}

    public static void loadConfig() {
        try (InputStream is = ConfigManager.class.getResourceAsStream("/assets/configs/game_config.json")) {
            if (is == null) {
                logger.error("Configuration file /assets/configs/game_config.json not found in resources. Loading defaults.");
                loadDefaults();
                return;
            }
            Gson gson = new Gson();
            config = gson.fromJson(new InputStreamReader(is, StandardCharsets.UTF_8), GameConfig.class);
            logger.info("Configuration successfully loaded from JSON.");
        } catch (Exception e) {
            logger.error("Error loading configuration from JSON: " + e.getMessage() + ". Loading defaults.", e);
            loadDefaults();
        }
    }

    public static GameConfig getConfig() {
        if (config == null) {
            loadConfig();
        }
        return config;
    }

    private static void loadDefaults() {
        WindowConfig wc = new WindowConfig("Brick Breaker Deluxe 2.0 🎮", 700, 600, false);
        PaddleConfig pc = new PaddleConfig(310, 550, 100, 8, 30, 10, 600);
        BallConfig bc = new BallConfig(120, 350, -1, -2, 20, 570);
        BricksConfig brc = new BricksConfig(3, 7, 21, 80, 100, 540, 150);
        GameplayConfig gc = new GameplayConfig(8, 5, 15, "scores.txt");
        config = new GameConfig(wc, pc, bc, brc, gc);
    }
}
