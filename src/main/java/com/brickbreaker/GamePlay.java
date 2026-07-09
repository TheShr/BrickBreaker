package com.brickbreaker;

import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.awt.event.KeyEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.brickbreaker.engine.resource.ConfigManager;
import com.brickbreaker.engine.core.Screen;
import com.brickbreaker.engine.core.ServiceLocator;
import com.brickbreaker.engine.event.EventBus;
import com.brickbreaker.engine.input.InputManager;
import com.brickbreaker.engine.ecs.Registry;
import com.brickbreaker.game.entities.EntityFactory;
import com.brickbreaker.game.components.TransformComponent;
import com.brickbreaker.game.components.PhysicsComponent;
import com.brickbreaker.game.components.BallComponent;
import com.brickbreaker.game.components.PaddleComponent;
import com.brickbreaker.game.components.BrickComponent;
import com.brickbreaker.game.event.BrickBrokenEvent;
import com.brickbreaker.game.systems.MovementSystem;
import com.brickbreaker.game.systems.CollisionSystem;
import com.brickbreaker.game.systems.RenderSystem;

public class GamePlay implements Screen {
    private static final Logger logger = LogManager.getLogger(GamePlay.class);

    private final Registry registry;
    private final MovementSystem movementSystem;
    private final CollisionSystem collisionSystem;
    private final RenderSystem renderSystem;

    private boolean play = false;
    private int score = 0;
    private int level = 1;
    private int totalBricks;

    private int paddleEntity;
    private int ballEntity;

    private final String playerName;
    private int highScore;

    public GamePlay(String playerName, Canvas canvas) {
        this.playerName = playerName;
        this.registry = ServiceLocator.get(Registry.class);
        this.movementSystem = new MovementSystem();
        this.collisionSystem = new CollisionSystem();
        this.renderSystem = new RenderSystem();

        var config = ConfigManager.getConfig();
        var gameplayCfg = config.gameplay();

        this.highScore = loadHighScore(playerName);

        // Initialize Level/Bricks
        resetGame();

        // Register to event bus
        ServiceLocator.get(EventBus.class).subscribe(BrickBrokenEvent.class, event -> {
            totalBricks--;
            score += gameplayCfg.scorePerBrick();
            int newLevel = (score / gameplayCfg.levelThreshold()) + 1;
            if (newLevel > level) {
                level = newLevel;
                // Speed up ball
                for (int ball : registry.getEntitiesWith(BallComponent.class, PhysicsComponent.class)) {
                    PhysicsComponent pc = registry.getComponent(ball, PhysicsComponent.class);
                    if (pc != null) {
                        if (pc.vx > 0) pc.vx += 1; else pc.vx -= 2;
                        if (pc.vy > 0) pc.vy += 1; else pc.vy -= 2;
                    }
                }
                logger.info("Player leveled up via Event: {}", level);
            }
            if (totalBricks <= 0) {
                regenerateBricks();
            }
        });

        // Key bindings
        var input = ServiceLocator.get(InputManager.class);
        input.clearBindings();
        input.bindKey(KeyEvent.VK_RIGHT, () -> {
            var paddleCfg = ConfigManager.getConfig().paddle();
            TransformComponent tc = registry.getComponent(paddleEntity, TransformComponent.class);
            if (tc != null) {
                if (tc.x >= paddleCfg.maxBound()) {
                    tc.x = paddleCfg.maxBound();
                } else {
                    play = true;
                    tc.x += paddleCfg.speed();
                }
            }
        });
        input.bindKey(KeyEvent.VK_LEFT, () -> {
            var paddleCfg = ConfigManager.getConfig().paddle();
            TransformComponent tc = registry.getComponent(paddleEntity, TransformComponent.class);
            if (tc != null) {
                if (tc.x <= paddleCfg.minBound()) {
                    tc.x = paddleCfg.minBound();
                } else {
                    play = true;
                    tc.x -= paddleCfg.speed();
                }
            }
        });
        input.bindKey(KeyEvent.VK_ENTER, () -> {
            if (!play) {
                resetGame();
                play = true;
                logger.info("Game restarted by player: {}", playerName);
            }
        });

        logger.info("GamePlay screen fully initialized.");
    }

    private void resetGame() {
        var config = ConfigManager.getConfig();
        var paddleCfg = config.paddle();
        var ballCfg = config.ball();
        var bricksCfg = config.bricks();

        registry.clear();
        score = 0;
        level = 1;
        totalBricks = bricksCfg.totalCount();

        // Create Paddle
        paddleEntity = EntityFactory.createPaddle(registry, paddleCfg.startX(), paddleCfg.startY(), paddleCfg.width(), paddleCfg.height(), new Color(0, 255, 255));

        // Create Ball
        ballEntity = EntityFactory.createBall(registry, ballCfg.startX(), ballCfg.startY(), ballCfg.speedX(), ballCfg.speedY(), ballCfg.size(), new Color(50, 255, 50));

        // Generate Bricks
        int brickWidth = bricksCfg.widthBound() / bricksCfg.cols();
        int brickHeight = bricksCfg.heightBound() / bricksCfg.rows();
        for (int i = 0; i < bricksCfg.rows(); i++) {
            for (int j = 0; j < bricksCfg.cols(); j++) {
                int brickX = j * brickWidth + bricksCfg.offsetX();
                int brickY = i * brickHeight + bricksCfg.offsetY();
                EntityFactory.createBrick(registry, brickX, brickY, brickWidth, brickHeight, i, j, Color.white);
            }
        }
    }

    private void regenerateBricks() {
        var bricksCfg = ConfigManager.getConfig().bricks();
        totalBricks = bricksCfg.totalCount();

        int brickWidth = bricksCfg.widthBound() / bricksCfg.cols();
        int brickHeight = bricksCfg.heightBound() / bricksCfg.rows();
        for (int i = 0; i < bricksCfg.rows(); i++) {
            for (int j = 0; j < bricksCfg.cols(); j++) {
                int brickX = j * brickWidth + bricksCfg.offsetX();
                int brickY = i * brickHeight + bricksCfg.offsetY();
                EntityFactory.createBrick(registry, brickX, brickY, brickWidth, brickHeight, i, j, Color.white);
            }
        }
        logger.info("Bricks regenerated.");
    }

    @Override
    public void render(Graphics2D g) {
        var config = ConfigManager.getConfig();
        var windowCfg = config.window();
        var ballCfg = config.ball();

        // Gradient Background
        GradientPaint gp = new GradientPaint(0, 0, new Color(25, 25, 112), 0, windowCfg.height(), Color.BLACK);
        g.setPaint(gp);
        g.fillRect(0, 0, windowCfg.width(), windowCfg.height());

        // Delegate Entity Rendering to RenderSystem
        renderSystem.render(registry, g);

        // Draw neon borders
        g.setColor(Color.cyan);
        g.fillRect(0, 0, 3, windowCfg.height());
        g.fillRect(0, 0, windowCfg.width(), 3);
        g.fillRect(windowCfg.width() - 19, 0, 3, windowCfg.height());

        // Player Info HUD
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Player: " + playerName, 20, 30);
        g.drawString("Score: " + score, 540, 30);
        g.drawString("High Score: " + highScore, 20, 60);
        g.drawString("Level: " + level, 540, 60);

        // Message HUD overlays
        if (totalBricks <= 0) {
            play = false;
            g.setColor(Color.green);
            g.setFont(new Font("Impact", Font.BOLD, 50));
            g.drawString("You Won!", 220, 250);

            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.setColor(Color.white);
            g.drawString("Your Score: " + score, 240, 310);

            g.setFont(new Font("Comic Sans MS", Font.BOLD, 25));
            g.setColor(Color.orange);
            g.drawString("Press Enter to Restart", 210, 370);
        }

        TransformComponent ballTrans = registry.getComponent(ballEntity, TransformComponent.class);
        if (ballTrans != null && ballTrans.y > ballCfg.deathY()) {
            play = false;
            g.setColor(Color.red);
            g.setFont(new Font("Impact", Font.BOLD, 50));
            g.drawString("Game Over", 210, 250);

            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.setColor(Color.white);
            g.drawString("Your Score: " + score, 240, 310);

            g.setFont(new Font("Comic Sans MS", Font.BOLD, 25));
            g.setColor(Color.orange);
            g.drawString("Press Enter to Restart", 210, 370);

            if (score > highScore) {
                highScore = score;
                saveHighScore(playerName, highScore);
            }
        }
    }

    @Override
    public void update() {
        var config = ConfigManager.getConfig();
        var ballCfg = config.ball();

        if (play) {
            // Update physical movement and collisions using the ECS systems
            movementSystem.update(registry);
            collisionSystem.update(registry);

            TransformComponent ballTrans = registry.getComponent(ballEntity, TransformComponent.class);
            if (ballTrans != null && ballTrans.y > ballCfg.deathY()) {
                play = false;
                PhysicsComponent ballPhys = registry.getComponent(ballEntity, PhysicsComponent.class);
                if (ballPhys != null) {
                    ballPhys.vx = 0;
                    ballPhys.vy = 0;
                }
            }
        }
    }

    private int loadHighScore(String name) {
        String fileName = ConfigManager.getConfig().gameplay().scoresFile();
        File file = new File(fileName);
        if (!file.exists()) {
            return 0;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            Map<String, Integer> scores = new HashMap<>();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    scores.put(parts[0], Integer.parseInt(parts[1]));
                }
            }
            return scores.getOrDefault(name, 0);
        } catch (IOException | NumberFormatException e) {
            logger.error("Failed to load high score for " + name, e);
            return 0;
        }
    }

    private void saveHighScore(String name, int score) {
        String fileName = ConfigManager.getConfig().gameplay().scoresFile();
        File file = new File(fileName);
        Map<String, Integer> scores = new HashMap<>();

        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        scores.put(parts[0], Integer.parseInt(parts[1]));
                    }
                }
            } catch (IOException | NumberFormatException e) {
                logger.error("Failed to parse scores list file during save operation", e);
            }
        }

        scores.put(name, score);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (Map.Entry<String, Integer> entry : scores.entrySet()) {
                bw.write(entry.getKey() + ":" + entry.getValue());
                bw.newLine();
            }
            logger.info("Successfully saved high score for {}: {}", name, score);
        } catch (IOException e) {
            logger.error("Failed to save high score file", e);
        }
    }
}
