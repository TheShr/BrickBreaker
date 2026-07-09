package com.brickbreaker.engine.core;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import javax.swing.JFrame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.brickbreaker.engine.resource.ConfigManager;

public class GameEngine implements Runnable {
    private static final Logger logger = LogManager.getLogger(GameEngine.class);

    private final JFrame frame;
    private final Canvas canvas;
    private final ScreenManager screenManager;

    private Thread gameThread;
    private volatile boolean running = false;

    public GameEngine(JFrame frame) {
        this.frame = frame;
        this.canvas = new Canvas();
        this.screenManager = new ScreenManager();

        var config = ConfigManager.getConfig().window();
        Dimension size = new Dimension(config.width(), config.height());
        canvas.setPreferredSize(size);
        canvas.setMinimumSize(size);
        canvas.setMaximumSize(size);
        canvas.setFocusable(true);

        frame.add(canvas);
        frame.pack();

        // Register ScreenManager in locator
        ServiceLocator.register(ScreenManager.class, this.screenManager);
    }

    public synchronized void start() {
        if (running) return;
        running = true;
        gameThread = new Thread(this, "GameLoopThread");
        gameThread.start();
        logger.info("GameEngine thread started.");
    }

    public synchronized void stop() {
        if (!running) return;
        running = false;
        try {
            gameThread.join(2000);
            logger.info("GameEngine thread joined and stopped.");
        } catch (InterruptedException e) {
            logger.error("Failed to join GameEngine thread", e);
            Thread.currentThread().interrupt();
        }
    }

    public Canvas getCanvas() {
        return canvas;
    }

    @Override
    public void run() {
        // Build BufferStrategy for active rendering (double-buffering)
        canvas.createBufferStrategy(2);
        BufferStrategy bs = canvas.getBufferStrategy();

        double timePerUpdate = 1000000000.0 / 60.0; // 60 updates/sec
        double timePerRender = 1000000000.0 / 60.0; // 60 frames/sec (v-synced/paced)
        double deltaUpdate = 0;
        double deltaRender = 0;
        long lastTime = System.nanoTime();

        logger.info("Beginning active render loop execution.");

        int frameCount = 0;
        boolean screenshotTaken = false;

        while (running) {
            long now = System.nanoTime();
            deltaUpdate += (now - lastTime) / timePerUpdate;
            deltaRender += (now - lastTime) / timePerRender;
            lastTime = now;

            if (deltaUpdate >= 1) {
                screenManager.update();
                deltaUpdate--;
            }

            if (deltaRender >= 1) {
                render(bs);
                deltaRender--;

                if (System.getProperty("takeScreenshot") != null && !screenshotTaken) {
                    frameCount++;
                    if (frameCount > 30) {
                        takeScreenshot();
                        screenshotTaken = true;
                        running = false;
                        System.exit(0);
                    }
                }
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                logger.warn("Game loop sleeping thread interrupted.");
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void takeScreenshot() {
        try {
            java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(canvas.getWidth(), canvas.getHeight(), java.awt.image.BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();
            screenManager.render(g2d);
            g2d.dispose();
            java.io.File outputFile = new java.io.File("screenshot.png");
            javax.imageio.ImageIO.write(image, "png", outputFile);
            logger.info("Auto-screenshot saved successfully to screenshot.png");
        } catch (Exception e) {
            logger.error("Failed to save auto-screenshot", e);
        }
    }

    private void render(BufferStrategy bs) {
        Graphics2D g = null;
        try {
            g = (Graphics2D) bs.getDrawGraphics();
            // Clear screen
            g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            
            // Delegate rendering
            screenManager.render(g);
            
            // Show frame buffer
            bs.show();
        } catch (Exception e) {
            logger.error("Active rendering pass encountered an error", e);
        } finally {
            if (g != null) {
                g.dispose();
            }
        }
    }
}
