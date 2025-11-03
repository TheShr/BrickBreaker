import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class MapGenerator {
   public int[][] map;
   public int brickWidth;
   public int brickHeight;

   // Constructor to initialize map and brick dimensions
   public MapGenerator(int var1, int var2) {
      this.map = new int[var1][var2];

      // Initialize all brick values to 1
      for(int var3 = 0; var3 < var1; ++var3) {
         for(int var4 = 0; var4 < var2; ++var4) {
            this.map[var3][var4] = 1;
         }
      }

      // Calculate brick width and height based on number of rows and columns
      this.brickWidth = 540 / var2;
      this.brickHeight = 150 / var1;
   }

   // Method to draw the map of bricks
   public void draw(Graphics2D var1) {
      for (int var2 = 0; var2 < this.map.length; ++var2) {
          for (int var3 = 0; var3 < this.map[0].length; ++var3) {
              if (this.map[var2][var3] > 0) {
                  var1.setColor(Color.white);
                  var1.fillRect(var3 * this.brickWidth + 80, var2 * this.brickHeight + 100, this.brickWidth, this.brickHeight); // shifted down
                  var1.setStroke(new BasicStroke(3.0F));
                  var1.setColor(Color.black);
                  var1.drawRect(var3 * this.brickWidth + 80, var2 * this.brickHeight + 100, this.brickWidth, this.brickHeight); // shifted down
              }
          }
      }
  }
  

   // Method to set a brick value at specific coordinates
   public void setBrickValue(int value, int row, int col) {
      if (row >= 0 && row < map.length && col >= 0 && col < map[0].length) {
         this.map[row][col] = value;
      }
   }
}
