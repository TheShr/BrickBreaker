import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class MyApp {
    public static String playerName;

    public static void main(String[] args) {
        playerName = JOptionPane.showInputDialog("Enter your name:");
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Guest";
        }

        JFrame obj = new JFrame();
        GamePlay gameplay = new GamePlay(playerName);
        
        obj.setBounds(10, 10, 700, 600);
        obj.setTitle("Brick Breaker Deluxe 🎮");
        obj.setResizable(false);
        obj.setVisible(true);
        obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        obj.add(gameplay);
    }
}
