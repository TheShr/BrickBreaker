// nishant part
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class GamePlay extends JPanel implements KeyListener, ActionListener {
    private boolean play = false;
    private int score = 0;
    private int level = 1;
    private int totalBricks = 21;

    private Timer timer;
    private int delay = 8;

    private int playerX = 310;
    private int ballposX = 120;
    private int ballposY = 350;
    private int ballXdir = -1;
    private int ballYdir = -2;

    private MapGenerator map;

    private String playerName;
    private int highScore;

    public GamePlay(String playerName) {
        this.playerName = playerName;
        this.highScore = loadHighScore(playerName);

        map = new MapGenerator(3, 7);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(delay, this);
        timer.start();
    }

    public void paint(Graphics g) {
        // Gradient Background
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gp = new GradientPaint(0, 0, new Color(25, 25, 112), 0, getHeight(), Color.BLACK);
        g2d.setPaint(gp);
        g2d.fillRect(1, 1, 692, 592);

        map.draw((Graphics2D) g);

        // Neon Borders
        g.setColor(Color.cyan);
        g.fillRect(0, 0, 3, 592);
        g.fillRect(0, 0, 692, 3);
        g.fillRect(691, 0, 3, 592);

        // Player Info
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Player: " + playerName, 20, 30);
        g.drawString("Score: " + score, 560, 30);
        g.drawString("High Score: " + highScore, 20, 60);
        g.drawString("Level: " + level, 560, 60);

        // Paddle
        g.setColor(new Color(0, 255, 255)); // Neon Blue
        g.fillRect(playerX, 550, 100, 8);

        // Ball
        g.setColor(new Color(50, 255, 50)); // Neon Green
        g.fillOval(ballposX, ballposY, 20, 20);

        // Messages
        if (totalBricks <= 0) {
            play = false;
            ballXdir = 0;
            ballYdir = 0;

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

        if (ballposY > 570) {
            play = false;
            ballXdir = 0;
            ballYdir = 0;

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

        g.dispose();
    }
//// anuj part
    @Override
    public void actionPerformed(ActionEvent e) {
        timer.start();

        if (play) {
            if (new Rectangle(ballposX, ballposY, 20, 20)
                    .intersects(new Rectangle(playerX, 550, 100, 8))) {
                ballYdir = -ballYdir;
            }

            A: for (int i = 0; i < map.map.length; i++) {
                for (int j = 0; j < map.map[0].length; j++) {
                    if (map.map[i][j] > 0) {
                        int brickX = j * map.brickWidth + 80;
                        int brickY = i * map.brickHeight + 100;
                        int brickWidth = map.brickWidth;
                        int brickHeight = map.brickHeight;

                        Rectangle rect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
                        Rectangle ballRect = new Rectangle(ballposX, ballposY, 20, 20);

                        if (ballRect.intersects(rect)) {
                            map.setBrickValue(0, i, j);
                            totalBricks--;
                            score += 5;

                            int newLevel = (score / 15) + 1;
                            if (newLevel > level) {
                                level = newLevel;
                                if (ballXdir > 0) ballXdir += 1; else ballXdir -= 2;
                                if (ballYdir > 0) ballYdir += 1; else ballYdir -= 2;
                            }

                            if (ballposX + 19 <= rect.x || ballposX + 1 >= rect.x + rect.width) {
                                ballXdir = -ballXdir;
                            } else {
                                ballYdir = -ballYdir;
                            }

                            break A;
                        }
                    }
                }
            }

            ballposX += ballXdir;
            ballposY += ballYdir;
            if (ballposX < 0) ballXdir = -ballXdir;
            if (ballposY < 0) ballYdir = -ballYdir;
            if (ballposX > 670) ballXdir = -ballXdir;

            if (totalBricks == 0) {
                map = new MapGenerator(3, 7);
                totalBricks = 21;
            }
        }

        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (playerX >= 600) {
                playerX = 600;
            } else {
                moveRight();
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (playerX <= 10) {
                playerX = 10;
            } else {
                moveLeft();
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!play) {
                play = true;
                ballposX = 120;
                ballposY = 350;
                ballXdir = -1;
                ballYdir = -2;
                playerX = 310;
                score = 0;
                level = 1;
                totalBricks = 21;
                map = new MapGenerator(3, 7);

                repaint();
            }
        }
    }

    public void moveRight() {
        play = true;
        playerX += 30;
    }

    public void moveLeft() {
        play = true;
        playerX -= 30;
    }

    private int loadHighScore(String name) {
        File file = new File("scores.txt");
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
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void saveHighScore(String name, int score) {
        File file = new File("scores.txt");
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        scores.put(name, score);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (Map.Entry<String, Integer> entry : scores.entrySet()) {
                bw.write(entry.getKey() + ":" + entry.getValue());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
