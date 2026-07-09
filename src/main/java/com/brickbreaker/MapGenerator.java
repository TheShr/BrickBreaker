package com.brickbreaker;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import com.brickbreaker.engine.resource.ConfigManager;

public class MapGenerator {
   public int[][] map;
   public int brickWidth;
   public int brickHeight;

   // Constructor to initialize map and brick dimensions
   public MapGenerator(int rows, int cols) {
      this.map = new int[rows][cols];

      // Initialize all brick values to 1
      for(int r = 0; r < rows; ++r) {
         for(int c = 0; c < cols; ++c) {
            this.map[r][c] = 1;
         }
      }

      var brickConfig = ConfigManager.getConfig().bricks();
      // Calculate brick width and height based on bounds
      this.brickWidth = brickConfig.widthBound() / cols;
      this.brickHeight = brickConfig.heightBound() / rows;
   }

   // Method to draw the map of bricks
   public void draw(Graphics2D g) {
      var brickConfig = ConfigManager.getConfig().bricks();
      for (int r = 0; r < this.map.length; ++r) {
          for (int c = 0; c < this.map[0].length; ++c) {
              if (this.map[r][c] > 0) {
                  g.setColor(Color.white);
                  g.fillRect(c * this.brickWidth + brickConfig.offsetX(), r * this.brickHeight + brickConfig.offsetY(), this.brickWidth, this.brickHeight);
                  g.setStroke(new BasicStroke(3.0F));
                  g.setColor(Color.black);
                  g.drawRect(c * this.brickWidth + brickConfig.offsetX(), r * this.brickHeight + brickConfig.offsetY(), this.brickWidth, this.brickHeight);
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
